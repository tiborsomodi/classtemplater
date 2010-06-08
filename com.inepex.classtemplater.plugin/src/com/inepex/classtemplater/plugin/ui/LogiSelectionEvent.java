package com.inepex.classtemplater.plugin.ui;

import java.util.List;

public class LogiSelectionEvent {

	private List<Object> selected;

	public LogiSelectionEvent(List<Object> selected) {
		super();
		this.selected = selected;
	}

	public List<Object> getSelected() {
		return selected;
	}

	public void setSelected(List<Object> selected) {
		this.selected = selected;
	}

	
	
}
