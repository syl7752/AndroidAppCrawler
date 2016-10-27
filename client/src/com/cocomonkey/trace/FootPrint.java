package com.cocomonkey.trace;

import com.cocomonkey.command.Command;
import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;
import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 用于纪录走过的操作及node节点
 * @author shiyl
 * @date 2015年11月30日
 */
public class FootPrint {
	private Node sourceView;
	
	private Node targetView;
	
	private Command command;
	
	
	@Override
	public int hashCode() {
		if(command.getElement()!=null)
			return Objects.hashCode(this.sourceView.getId()+this.targetView.getId()+this.command.getElement().getCompareId());
		else
		    return Objects.hashCode(this.sourceView.getId()+this.targetView.getId()+this.command.getCommandContent());
	}

	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		FootPrint fp=(FootPrint) obj;
		if(sourceView.getId().equals(fp.sourceView.getId())&&targetView.getId().equals(fp.targetView.getId())&&command.getCommandContent().equals(fp.command.getCommandContent()))
		{
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public FootPrint(Node oldView,Node newView,Command command)
	{
		this.sourceView=oldView;
		this.targetView=newView;
		this.command=command;
	}
	
	/**
	 * 
	 * @author shiyl
	 * @date 2015年11月30日
	 */
	public Optional<UIElement> getUIElement()
	{
		if(this.command.getElement()==null)
			return Optional.absent();
		else
		    return Optional.of(this.command.getElement());
	}
	
	/**
	 * 获取目标view
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public Node getTarget()
	{
		return this.targetView;
	}
	
	/**
	 * 获取目标view
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public Node getSource()
	{
		return this.sourceView;
	}
	
	/**
	 * 获取操作过的指令
	 * @author shiyl
	 * @date 2015年12月4日
	 */
	public Command getCommand()
	{
		return this.command;
	}
}
