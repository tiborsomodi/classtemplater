package com.inepex.classtemplater.plugin.popup.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IField;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.inepex.classtemplater.plugin.Log;
import com.inepex.classtemplater.plugin.codegeneration.GenerationType;
import com.inepex.classtemplater.plugin.logic.Attribute;
import com.inepex.classtemplater.plugin.logic.TemplateGen;
import com.inepex.classtemplater.plugin.ui.CodeGenarator;
import com.inepex.classtemplater.plugin.ui.GeneratorDialog;
import com.inepex.classtemplater.plugin.ui.LogiSelectionEvent;
import com.inepex.classtemplater.plugin.ui.LogiSelectionListener;

public class AttributeGenerator implements IObjectActionDelegate {

	private Shell shell;
	private IWorkbenchPart targetPart;
	private ISelection selection;
	
	private GeneratorUI ui;
	
	private List<Attribute> models = new ArrayList<Attribute>();
	
	private CodeGenarator codeGenarator;
	
	private TemplateGen templateGen;
	
	private LogiSelectionListener filterListener = new LogiSelectionListener() {
		
		@Override
		public void onSelection(LogiSelectionEvent event) {
			ui.clearText();
			ui.addText("Click Save to file to generate code!");
		}
	};
	
	private Listener saveListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			save(false);
		}
	};
	
	@Override
	public void run(IAction action) {
		try {
			templateGen = new TemplateGen(GenerationType.ATTRIBUTE);
			codeGenarator = new CodeGenarator(GenerationType.ATTRIBUTE, targetPart, templateGen);
			codeGenarator.setFormat(ui.formatCode());
			processSelection((IStructuredSelection)selection);
			ui = new GeneratorDialog(shell, "Attribute templater");
			ui.open();
			ui.addTemplateSelectionListener(filterListener);
			ui.addSaveListener(saveListener);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.log(sw.toString());
		}
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}
	
	private void processSelection(IStructuredSelection selection) throws Exception {
		models.clear();

		if (IField.class.isInstance(selection.getFirstElement())){
			for (Object field : selection.toList()){
				models.add(new Attribute((IField)field));
			}
		} 
	}
	
	private void save(boolean organize){
		try {
			ui.clearText();
			for (IResource template : ui.getSelectedTemplates()){
				for (Attribute attr : models){
					ui.addText("Generate from " + attr.getName() + " with " + template.getName());
					codeGenarator.generate(organize, template, attr);
				}
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ui.addText(sw.toString());
		}
	}

}
