package com.cocomonkey.trace;

import java.util.List;

import com.cocomonkey.type.Node;
import com.cocomonkey.type.UIElement;

/**
 * 
 * @author shiyl
 * @date 2015年12月16日
 */
public interface ElementFileter {

	public List<UIElement> filterClickElement(Node node,
			List<UIElement> elements);
}
