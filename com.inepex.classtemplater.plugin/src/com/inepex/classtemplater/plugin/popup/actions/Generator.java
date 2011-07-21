/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.popup.actions;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

import com.inepex.classtemplater.plugin.Log;
import com.inepex.classtemplater.plugin.codegeneration.GenerationType;
import com.inepex.classtemplater.plugin.logic.Class;
import com.inepex.classtemplater.plugin.logic.FileUtil;
import com.inepex.classtemplater.plugin.logic.TemplateGen;
import com.inepex.classtemplater.plugin.ui.CodeGenarator;
import com.inepex.classtemplater.plugin.ui.GeneratorDialog;
import com.inepex.classtemplater.plugin.ui.LogiSelectionEvent;
import com.inepex.classtemplater.plugin.ui.LogiSelectionListener;

public class Generator implements IObjectActionDelegate {

	private GeneratorUI ui;
	private Shell shell;
	private IWorkbenchPart targetPart;
	private ISelection selection;
	private List<Class> classModels = new ArrayList<Class>();
	private String mode = "";
	private TemplateGen templateGen;
	
	private LogiSelectionListener filterListener = new LogiSelectionListener() {
		
		@Override
		public void onSelection(LogiSelectionEvent event) {
			if (mode.equals("One class") || mode.equals("Attribute list") || mode.equals("Method list")){
				save(true, false);
			} else {
				ui.clearText();
				ui.addText("Click Save to file to generate code!");
			}
			
//			if (event.getSelected().size() == 1) save(true);
//			else save(false);
		}
	};
	
	private Listener saveListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			save(false, false);
		}
	};
	
	private Listener saveAndOrganizeListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			save(false, true);
		}
	};
	
	/**
	 * Constructor for Action1.
	 */
	public Generator() {
	}
	
	public Generator(IWorkbenchPart activePart, IStructuredSelection selection) {
		super();
		shell = activePart.getSite().getShell();
		this.targetPart = activePart;
		this.selection = selection;
		
		
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
		this.targetPart = targetPart;
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		try {
			templateGen = new TemplateGen(GenerationType.CLASS);
			readTemplatesFromWorkspace();
			processSelection((IStructuredSelection)selection);
			ui = new GeneratorDialog(shell, mode);
			ui.open();
			ui.addTemplateSelectionListener(filterListener);
			ui.addSaveListener(saveListener);
			ui.addSaveAndOrganizeListener(saveAndOrganizeListener);
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			Log.log(sw.toString());
		}
	}
	
	private void processSelection(IStructuredSelection selection) throws Exception {
		classModels.clear();
		if (ICompilationUnit.class.isInstance(selection.getFirstElement())){
			for (Object o : selection.toList()){
				ICompilationUnit compunit = ((ICompilationUnit)o);
				classModels.add(new Class(compunit));
			}
			if (selection.size() == 1) mode = "One class";
			else mode = "Multiple class";
		} else if (IField.class.isInstance(selection.getFirstElement())){
			classModels.add(new Class(selection.toList())); 
			mode = "Attribute list";
		} else if (IMethod.class.isInstance(selection.getFirstElement())){
			classModels.add(new Class(selection.toList(), true)); 
			mode = "Method list";
		}
	}
	
	private void save(boolean simpleMode, boolean organize){
		try {
			CodeGenarator generator = new CodeGenarator(GenerationType.CLASS, targetPart, templateGen);
			generator.setFormat(ui.formatCode());
			ui.clearText();
			for (IResource template : ui.getSelectedTemplates()){
				for (Class classModel : classModels){
					if (simpleMode){
						String generated = generator.generate(
								simpleMode, organize, template, classModel
								, ui.ignoreHcContent(), ui.dontRenderHc());
						ui.addText(generated);
						ui.selectTextAndFocus();
					} else {
						ui.addText("Generate from " + classModel.getName() + " with " + template.getName());
						generator.generate(simpleMode, organize, template, classModel
								, ui.ignoreHcContent(), ui.dontRenderHc());
					}
				}
				generator.organizeAll();
			}
		} catch (Exception e) {
			StringWriter sw = new StringWriter();
			e.printStackTrace(new PrintWriter(sw));
			ui.addText(sw.toString());
		}
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.selection = selection;
	}

	private void readTemplatesFromWorkspace() throws Exception {
		final ArrayList<IResource> ress = new ArrayList<IResource>();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		workspaceRoot.accept(new IResourceVisitor() {
			
			@Override
			public boolean visit(IResource resource) throws CoreException {
				if (resource.getName().endsWith(".vm"))
					ress.add(resource);
				return true;
			}
			
		});
		
		for (IResource res : ress){
			String templateContent = FileUtil.readFile((IFile)res);
			if (templateContent.startsWith("outpath")){
				templateContent = templateContent.substring(templateContent.indexOf('\n') + 1);
			}
			
			templateGen.putTemplate(res.getName(), templateContent);
			templateGen.putTemplate("withoutHc_" + res.getName(), CodeGenarator.removeHcs(templateContent));			
		}
	}
}
