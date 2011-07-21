/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.inepex.classtemplater.plugin.popup.actions.GeneratorUI;

public class GeneratorDialog extends Dialog implements GeneratorUI{

	private Shell shell;
	private ResourceSelector select_template;
	private Composite comp_result;
	private Label lbl_result;
	private Text text_result;
//	private FolderSelector select_folder;
//	private Text text_outfile;
	private Composite comp_buttons;
	private Button ignoreHcContent;
	private Button dontRenderHc;
	private Button formatGeneratedCode;
	private Button btn_savetofile;
	private Button btn_savetofileAndOrganize;
	private Button btn_close;

	private String titleText;
	
	private Listener closeListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			close();
		}
	};
	
	public GeneratorDialog(Shell parent, String titleText) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
		this.titleText = titleText;
	}
	
	public void open() {
		shell = new Shell(getParent(), getStyle());
		shell.setText("Classtemplater: " + titleText);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.pack = true;
		layout.wrap = false;
		layout.fill = true;
	 	shell.setLayout(layout);
	 	
	 	Rectangle displayBounds = getParent().getBounds();
	 	shell.setLocation(displayBounds.x + 200 , displayBounds.y + 200);
	 	shell.setSize(new Double(displayBounds.width * 0.7).intValue()
	 			, 585);
	 	
	 	select_template = new ResourceSelector(shell, "Select template (by typing its name and click on it in the list)");
	 	select_template.setExtensionFilter(".vm");
	 	comp_result = new Composite(shell, SWT.BORDER);
	 	comp_result.setLayout(new RowLayout());
	 	comp_result.setLayoutData(new RowData(shell.getBounds().width - 20, 225));
	 	lbl_result = new Label(comp_result, SWT.LEFT);
	 	lbl_result.setText("Result of code generation:");
	 	text_result = new Text(comp_result, SWT.MULTI | SWT.V_SCROLL);
	 	text_result.setLayoutData(new RowData(shell.getBounds().width - 45, 200));
//	 	select_folder = new FolderSelector(shell, "Select output folder");
//	 	select_folder.setWidth(250);
//	 	text_outfile = new Text(shell, SWT.SINGLE);
	 	comp_buttons = new Composite(shell, SWT.NONE);
	 	comp_buttons.setLayout(new RowLayout(SWT.HORIZONTAL));
	 	dontRenderHc = new Button(comp_buttons, SWT.CHECK);
	 	dontRenderHc.setText("Don't render hc's");
	 	dontRenderHc.setToolTipText("Remove handwritten code parts from generated code (also /*hc../ notation)");
	 	ignoreHcContent = new Button(comp_buttons, SWT.CHECK);
	 	ignoreHcContent.setText("Ignore hc's content");
	 	ignoreHcContent.setToolTipText("Ignore text written in hc part of generated code and replace " +
	 			"it with the default value from template.");
	 	formatGeneratedCode = new Button(comp_buttons, SWT.CHECK);
	 	formatGeneratedCode.setText("Format code");
	 	formatGeneratedCode.setToolTipText("Execute eclipse code formatter for the generated code. (only with Save and organize!)");
	 	btn_savetofile = new Button(comp_buttons, SWT.PUSH);
	 	btn_savetofile.setText("Save to file");
	 	btn_savetofileAndOrganize = new Button(comp_buttons, SWT.PUSH);
	 	btn_savetofileAndOrganize.setText("Save and organize (experimental)");
	 	btn_close = new Button(comp_buttons, SWT.PUSH);
	 	btn_close.setText("Close");
	 	btn_close.addListener(SWT.Selection, closeListener);
	 	shell.open();
	}
	
	
	private void close(){
		shell.close();
	}

	@Override
	public void addTemplateSelectionListener(LogiSelectionListener listener) {
		select_template.addSelectionListener(listener);
	}

	@Override
	public void addSaveListener(Listener listener) {
		btn_savetofile.addListener(SWT.Selection, listener);
	}
	
	@Override
	public void addSaveAndOrganizeListener(Listener listener) {
		btn_savetofileAndOrganize.addListener(SWT.Selection, listener);
	}

	@Override
	public void addText(String text) {
		String newText = 
			text_result.getText()
			+ ((text_result.getText().equals("")) ? "" : "\r\n") 
			+ text.replace("\n", "\r\n");
		
		text_result.setText(newText);
	}

	@Override
	public void clearText() {
		text_result.setText("");
	}

	public List<IResource> getSelectedTemplates() {
		List<IResource> res = new ArrayList<IResource>();
		for (Object o : select_template.getSelectedObjects()){
			res.add((IResource)o);
		}
		return res;
	}

	@Override
	public void selectTextAndFocus() {
		text_result.setFocus();
		text_result.selectAll();
		text_result.copy();
	}

	@Override
	public boolean ignoreHcContent() {
		return ignoreHcContent.getSelection();
	}

	@Override
	public boolean dontRenderHc() {
		return dontRenderHc.getSelection();
	}

	@Override
	public boolean formatCode() {
		return formatGeneratedCode.getSelection();
	}


	
}
