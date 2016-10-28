package com.cocomonkey.mode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.cocomonkey.command.Command;
import com.cocomonkey.command.CommandHelper;
import com.cocomonkey.crawler.View;
import com.cocomonkey.device.AndroidDevice;
import com.cocomonkey.reporter.HtmlReporter;
import com.cocomonkey.trace.ElementFileter;
import com.cocomonkey.trace.FootPrint;
import com.cocomonkey.trace.Recorder;
import com.cocomonkey.trace.Step;
import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;
import com.cocomonkey.util.Utils;
import com.google.common.base.Optional;

/**
 * 遍历模式
 * 
 * @author shiyl
 * @date 2015年11月24日
 */
public class TraceMode implements Mode, ElementFileter {

	private final Recorder recorder = new Recorder();

	private Optional<Step> step = Optional.absent();

	private CommandHelper helper;

	private View lastView;

	private Command lastCommand;

	private final String pkgName;

	private String activityName;

	private boolean isFirstRun = true;

	private boolean isScrolled = false;

	private List<String> visitedNodes = new LinkedList<>();

	public TraceMode(CommandHelper hepler, AndroidDevice deviceInfo) {
		this.helper = hepler;
		this.pkgName = deviceInfo.getPackageName();
		this.activityName = deviceInfo.getActivityName();
	}

	/**
	 * 运行遍历模式
	 * 
	 * @author shiyl
	 * @date 2015年12月3日
	 */
	@Override
	public String run(View view) {
		// TODO Auto-generated method stub
		if (step.isPresent()) {
			step.get().applyNewView(view);
			recorder.addStep(step);
		}

		step = Optional.of(new Step());

		if (isFirstRun && view.getId().contains(pkgName)) {
			activityName = view.getId();
			isFirstRun = false;
		}

		if (!view.getId().contains(pkgName)
				&& lastView.getId().equals(activityName)
				&& (lastCommand instanceof Command.ExitEvent || lastCommand instanceof Command.HomeEvent)) { // 当前view不在目标包内且上一个view不为主界面
																												// 且上一个操作为退出事件，则运行完毕
			Utils.log("return test complete");
			return "complete";
		}

		recordView(view);

		Command command = getNextCommand(view);

		String result = helper.sendConmand(command); // 发送本次操作到uiautomator server，由uiautomator执行

		step.get().applyOldView(view);
		step.get().applyCommand(command);

		lastCommand = command; // 纪录上个操作和view
		lastView = view;

		return result;
	}

	private void recordView(View view) {
		// 将当前view加入到访问过的view列表中
		if (visitedNodes.size() > 0) {
			addNode(view.getId());
		} else {
			visitedNodes.add(view.getId());
		}
	}

	public void addNode(String nodeId) {
		if (visitedNodes.contains(nodeId)) {
			for (int i = visitedNodes.size() - 1; i >= 0; i--) {
				if (!visitedNodes.get(i).equals(nodeId)) {
					Utils.log("remove node " + visitedNodes.get(i));
					visitedNodes.remove(i);
				} else {
					Utils.log("break remove " + visitedNodes.get(i));
					break;
				}
			}
		} else {
			visitedNodes.add(nodeId);
		}
	}

	private Command getNextCommand(View view) {
		Command command = null;
		if (lastView == null || lastCommand == null)
			command = nextCommand(view);

		if (lastCommand instanceof Command.BackEvent
				&& view.getId().equals(activityName)) {
			visitedNodes.clear();
			visitedNodes.add(view.getId());
		}

		
		if (lastView.getId().equals(view.getId())  // 如果上一个操作为回退操作，且当前view与上一个view相同，则执行home操作，避免在一个界面死循环
				&& lastCommand instanceof Command.BackEvent)
			command = new Command.HomeEvent();
		else
			command = nextCommand(view);
		return command;
	}

	/**
	 * 获取下一个操作指令
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public Command nextCommand(View view) {
		Utils.log("next Command");
		if (isInvalidView(view)) {
			return goBackOrExit(view);
		}

		List<UIElement> elements = filterClickElement(view.getNode(),
				new ArrayList<UIElement>());
		if (elements.isEmpty()) {
			Utils.log("node don't have any element");
			return goBackOrExit(view);
		}

		List<UIElement> traceableElements = getTraceableElement(view, elements);

		if (traceableElements.isEmpty()) {
			if (view.getNode().canScroll()
					&& !(lastCommand instanceof Command.ScrollEvent))
				return new Command.ScrollEvent();
			else
				return goBackOrExit(view);
		}

		UIElement targetElement = traceableElements.get(0);

		if (targetElement.getType().equals(UIElement.Type.EDIT_TEXT)
				&& !targetElement.getId().equals("")) {
			Utils.writeEventLog("InputText :" + targetElement.getId());
			return new Command.InputText(targetElement);
		}

		if (!targetElement.getText().equals(""))
			Utils.writeEventLog("Click :" + targetElement.getText()
					+ " Activity = " + view.getId() + "\r\n");
		else if (!targetElement.getId().equals(""))
			Utils.writeEventLog("Click :" + targetElement.getId()
					+ " Activity = " + view.getId() + "\r\n");
		else
			Utils.writeEventLog("Click :" + targetElement.getBounds()
					+ " Activity = " + view.getId() + "\r\n");
		return new Command.ClickEvent(targetElement);

	}

	/**
	 * 是否已跳出应用
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	private boolean isInvalidView(View view) {
		if (!view.getId().contains(pkgName)) {
			return true;
		}
		return false;
	}

	/**
	 * 执行后退或者退出操作
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	private Command goBackOrExit(View view) {
		if (view.getId().equals(activityName)) {
			return new Command.ExitEvent();
		} else {
			return new Command.BackEvent();
		}
	}

	/**
	 * 获取可遍历得元素
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	private List<UIElement> getTraceableElement(View view,
			List<UIElement> clickables) {
		List<UIElement> traceableElements = new ArrayList<UIElement>();
		Optional<Node> node = recorder.getNodeById(view);

		// 获取纪录过的node，如果node为空，则此view为没遍历过的view
		if (!node.isPresent()) {
			Utils.log("found new view");
			return clickables;
		} else {
			Utils.log("old view");
			updateElements(node.get(), view);
		}

		// 遍历所有可点击
		for (int i = 0; i < clickables.size(); i++) {
			UIElement uiElement = clickables.get(i);
			if (isTraceable(node.get(), uiElement)) {
				traceableElements.add(uiElement);
			}
		}

		return traceableElements;

	}

	/**
	 * 元素是否值得遍历
	 * 
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public boolean isTraceable(Node node, UIElement uiElement) {
		Set<FootPrint> fps = node.getFootPrint();
		for (FootPrint fp : fps) {
			if (!fp.getUIElement().isPresent()) {
				continue;
			}
			if (fp.getUIElement().get().getCompareId()
					.equals(uiElement.getCompareId())) {

				Utils.log("element equals " + uiElement.getCompareId());
				uiElement.setTraced(true);

				Node targetNode = fp.getTarget();

				boolean traceable = isTracedTraceable(fp, node, targetNode, 2);
				return traceable;
			}
		}
		return true;
	}

	/**
	 * 元素是否值得遍历
	 * 
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public boolean isTracedTraceable(FootPrint footPrint, Node sourceNode,
			Node targetNode, int depth) {
		UIElement uiElement = footPrint.getUIElement().get();

		// 如果目标view和上一个view相同或当前view与目标view相同,返回false,避免重复点击

		if (sourceNode.getId().equals(targetNode.getId())) {
			Utils.log("don't click loop element " + sourceNode.getId());
			return false;
		}

		Set<FootPrint> fps = targetNode.getFootPrint();
		// 如果目标view中有过back或home操作，则不进入
		for (FootPrint fp : fps) {
			if (fp.getCommand() instanceof Command.BackEvent
					|| fp.getCommand() instanceof Command.HomeEvent) {
				Utils.log("fp has back event or home event");
				return false;
			}
		}

		if (visitedNodes.contains(targetNode.getId())) {
				return false;
		}

		// 如果目标view有没遍历过的元素，则返回true
		// List<Node> nodes = targetNode.getChilds();
		List<UIElement> elements = filterClickElement(targetNode,
				new ArrayList<UIElement>());
		for (UIElement element : elements) {
			if (element.getType().equals(UIElement.Type.UNKNOWN)) {
				continue;
			}
			for (FootPrint fp : fps) {
				if (!fp.getUIElement().isPresent())
					continue;
				if (!fp.getUIElement().get().equals(element)) {
					Utils.log("has no trace view "
							+ element.getCompareId());
					return true;
				} else {
					if (isTracedTraceable(fp, targetNode, fp.getTarget(),
							depth + 1)) {
						Utils.log("level " + depth + " has no trace view");
						return true;
					}
				}
			}
		}

		return false;

	}

	@Override
	public void generateReport() {
		// TODO Auto-generated method stub
		HtmlReporter reporter = new HtmlReporter(pkgName, recorder, this);
		reporter.generate();
	}

	/**
	 * 过滤可点击的元素
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	@Override
	public List<UIElement> filterClickElement(Node node,
			List<UIElement> elements) {
		// TODO Auto-generated method stub
		UIElement element = node.getElement();

		// 元素类型不为unkown且元素的id或bounds不为空视为可点击
		if (!element.getType().equals(UIElement.Type.UNKNOWN)
				&& (!element.getBounds().equals("") || !element.getId().equals(
						""))) {
			elements.add(element);
		}

		// 递归过滤每个元素
		for (Node childNode : node.getChilds()) {
			filterClickElement(childNode, elements);
		}
		return elements;
	}

	/**
	 * 如果ui中有新的元素出现，纪录此元素
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	private void updateElements(Node node, View view) {
		Node newNode = view.getNode();
		List<UIElement> newElements = filterClickElement(newNode,
				new ArrayList<UIElement>());
		List<UIElement> oldElements = filterClickElement(node,
				new ArrayList<UIElement>());
		for (UIElement newElement : newElements) {
			if (!oldElements.contains(newElement)) {
				Utils.log("add new node " + newElement.getCompareId());
				node.addNode(new Node(newElement, new ArrayList<Node>()));
			}
		}
		recorder.updateNode(view.getId(), node);
	}

}
