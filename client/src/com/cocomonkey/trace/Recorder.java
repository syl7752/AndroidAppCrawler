package com.cocomonkey.trace;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.cocomonkey.command.Command;
import com.cocomonkey.crawler.View;
import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;
import com.google.common.base.Optional;

/**
 * 纪录step类
 * @author shiyl
 * @date 2015年11月30日
 */
public class Recorder {
	private final Map<String, Node> nodes = new HashMap<>();
	
	/**
	 * 纪录执行过得step及view
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public void addStep(Optional<Step> step)
	{
		Node sourceNode=addNode(step.get().oldView);
		Node newNode=addNode(step.get().newView);
//		pathList.add(step.get().oldView.get().getId()); 
		addCmd(sourceNode, new FootPrint(sourceNode, newNode, step.get().command.get()));
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public void addCmd(Node node,FootPrint footPrint)
	{
		node.addFootPrint(footPrint);
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public Node addNode(Optional<View> view)
	{
	    Node node=nodes.get(view.get().getId());
	    if(node==null)
	    {
	    	nodes.put(view.get().getId(), view.get().getNode());
	    	return view.get().getNode();
	    }
	    else
	    	return node;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年12月16日
	 */
	public void updateNode(String id,Node node)
	{
		nodes.put(id, node);
	}
	
	/**
	 * 根据view获取node
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public Optional<Node> getNodeById(View view)
	{
		Node node=nodes.get(view.getId());
		if(node!=null)
			return Optional.of(node);
		else
			return Optional.absent();
	}


	public Map<String, Node> getSteps()
	{
		return this.nodes;
	}
	
}
