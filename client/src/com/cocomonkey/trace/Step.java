package com.cocomonkey.trace;


import com.cocomonkey.command.Command;
import com.cocomonkey.crawler.View;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

/**
 * 
 * @author shiyl
 * @date 2015年11月25日
 */
public class Step {
	public Optional<View> oldView = Optional.absent();
	public Optional<View> newView = Optional.absent();
	public Optional<Command> command = Optional.absent();

	public void applyOldView(View view) {
		this.oldView = Optional.of(view);
	}

	public void applyNewView(View view) {
		this.newView = Optional.of(view);
	}

	public void applyCommand(Command command) {
		this.command = Optional.of(command);
	}
	
//	public boolean isPresent() {
//		return oldView.isPresent() && newView.isPresent() && command.isPresent();
//	}
	
}
