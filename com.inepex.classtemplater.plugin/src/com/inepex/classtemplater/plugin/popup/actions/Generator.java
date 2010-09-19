/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.popup.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringBufferInputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
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
import com.inepex.classtemplater.plugin.logic.Class;
import com.inepex.classtemplater.plugin.logic.ResourceUtil;
import com.inepex.classtemplater.plugin.logic.TemplateGen;
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
	
	private String readFile(IFile file) throws Exception{
		BufferedReader breader = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"));
		String content = "";
		String strLine = "";
		while ((strLine = breader.readLine()) != null) {
			content += strLine + "\n";
		}
		breader.close();
		return content;
	}
	
	private String generate(boolean simpleMode, boolean organize, IResource template, Class classModel) throws Exception {
		String templateContent = readFile((IFile)template);
		String outpath = "";
		if (templateContent.startsWith("outpath")){
			outpath = templateContent.substring(8, templateContent.indexOf('\n'));
			outpath = outpath.replace("${classname}", classModel.getName());
			outpath = outpath.replace("${class.workspaceRelativePath}", classModel.getWorkspaceRelativePath());
			templateContent = templateContent.substring(templateContent.indexOf('\n') + 1);
		}
		String generated = TemplateGen.generate(
				templateContent
				, classModel
				);

		if (!outpath.equals("")){
			String actual = readActualVersion(outpath);
			String merged = mergeHandWrittenCode(actual, generated);
			generated = merged;
			if (!simpleMode) saveToFile(organize, outpath, merged);
		} 
		return generated;		

	}
	
	private String mergeHandWrittenCode(String actual, String generated) {
		String merged = "";
		Map<String, String> actualCodes = getHandWrittenCodes(actual);
		Map<String, String> codesInTemplate = getHandWrittenCodes(generated);
		merged = putHandWrittenCodes(generated, actualCodes, codesInTemplate);
		return merged;
	}
	
	private Map<String, String> getHandWrittenCodes(String content){
		Map<String, String> codes = new HashMap<String, String>();
		int pos = 0;
		do {
			int start = content.indexOf("/*hc:", pos);
			if (start != -1){
				int startend = content.indexOf("*/", start);
				int end = content.indexOf("/*hc*/", startend);
				pos = end;
				
				String id = content.substring(start + 5, startend);
				String text = content.substring(startend + 2, end);
				codes.put(id, text);
			} else {
				pos = -1;
			}
		} while (pos != -1);
		
		return codes;
	}
	
	private String putHandWrittenCodes(String content
			, Map<String, String> actualCodes
			, Map<String, String> codesInTemplate
			){
		int pos = 0;
		
		do {
			StringBuffer sb = new StringBuffer(content);
			int start = content.indexOf("/*hc:", pos);
			if (start != -1){
				int startend = content.indexOf("*/", start);
				int end = content.indexOf("/*hc*/", startend);
				pos = end;
				String id = content.substring(start + 5, startend);
				
				if (actualCodes.get(id) != null){
					sb.replace(startend + 2, end, "");
					sb.insert(startend + 2, actualCodes.get(id));
					
					if (codesInTemplate.get(id) != null){
						int lengthDifference = codesInTemplate.get(id).length() - actualCodes.get(id).length();
						pos -= lengthDifference;
					}
				}
			} else {
				pos = -1;
			}
			content = sb.toString();
		} while (pos != -1);
		return content;
	}

	private String readActualVersion(String path) throws Exception {
		String content = "";
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IFile file = root.getFile(new Path(path));
		if (file.exists()){
			content = readFile(file);
		}
		return content;
	}
	
	private void saveToFile(boolean organize, String path, String content) throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		Path filePath = new Path(path);
		ResourceUtil.createPath(filePath.removeLastSegments(1));
		IFile file = root.getFile(filePath);
		if (file.exists()) {
			file.setContents(new StringBufferInputStream(content), true, true, null);
		} else {	
			file.create(new StringBufferInputStream(content), true, null);
		}
//		file.refreshLocal(IProject.DEPTH_ONE, null);
		if (organize) organizeImports(file);
	}

	private void organizeImports(IFile file) throws Exception {
		// only need this if you do not already have a ICompilatioinUnit
		ICompilationUnit cu = JavaCore.createCompilationUnitFrom(file);

		ICompilationUnit[] cus = new ICompilationUnit[1];
		cus[0] = cu;
		OrganizeImportsAction a = new OrganizeImportsAction(targetPart.getSite());
//		a.runOnMultiple(cus);
		a.run(cu);
	}
	
	private void save(boolean simpleMode, boolean organize){
		try {
			ui.clearText();
			for (IResource template : ui.getSelectedTemplates()){
				for (Class classModel : classModels){
					if (simpleMode){
						String generated = generate(simpleMode, organize, template, classModel);
						ui.addText(generated);
						ui.selectTextAndFocus();
					} else {
						ui.addText("Generate from " + classModel.getName() + " with " + template.getName());
						generate(simpleMode, organize, template, classModel);
					}
				}
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

}
