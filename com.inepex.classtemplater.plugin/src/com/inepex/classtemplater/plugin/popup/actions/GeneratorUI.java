package com.inepex.classtemplater.plugin.popup.actions;

import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.widgets.Listener;

import com.inepex.classtemplater.plugin.ui.LogiSelectionListener;

public interface GeneratorUI {

	void addTemplateSelectionListener(LogiSelectionListener listener);
	void addSaveListener(Listener listener);
	void addText(String text);
	void clearText();
	void open();
	List<IResource> getSelectedTemplates();
	void selectTextAndFocus();
}
