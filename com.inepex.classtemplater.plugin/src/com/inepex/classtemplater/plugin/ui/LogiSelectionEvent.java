package com.inepex.classtemplater.plugin.ui;

public class LogiSelectionEvent {

	private Object selected;

	public LogiSelectionEvent(Object selected) {
		super();
		this.selected = selected;
	}

	public Object getSelected() {
		return selected;
	}

	public void setSelected(Object selected) {
		this.selected = selected;
	}
	
	
}
