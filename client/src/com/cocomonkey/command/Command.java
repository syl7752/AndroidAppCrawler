package com.cocomonkey.command;

import com.cocomonkey.type.UIElement;

/**
 * UI操作指令类
 * 
 * @author shiyl
 * @date 2015年11月25日
 */
public interface Command {
	public String getCommandContent();

	public UIElement getElement();

	// 点击操作
	class ClickEvent implements Command {
		private UIElement uiElement;

		public ClickEvent(UIElement uiElement) {
			this.uiElement = uiElement;
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return this.uiElement;
		}

		@Override
		public String getCommandContent() {
			//如果坐标不为空，则优先点击坐标
			if (!uiElement.getBounds().equals(""))
				return "click;bounds:" + uiElement.getBounds();
			else
				return "click;" + uiElement.getId();
			
		}

	}

	// 获取当前activity操作
	class GetActivity implements Command {

		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	// 退出应用操作
	class ExitEvent implements Command {
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// 返回应用主界面操作
	class HomeEvent implements Command {
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// 回退操作
	class BackEvent implements Command {
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	// 导出当前的view操作
	class DumpWindow implements Command {
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	class InputText implements Command {
		
		private UIElement uiElement;

		public InputText(UIElement uiElement) {
			this.uiElement = uiElement;
		}
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + ";"+uiElement.getId();
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return this.uiElement;
		}
	}
	
	class ScrollEvent implements Command {
		@Override
		public String getCommandContent() {
			// TODO Auto-generated method stub
			return this.getClass().getSimpleName() + "; ";
		}

		@Override
		public UIElement getElement() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
