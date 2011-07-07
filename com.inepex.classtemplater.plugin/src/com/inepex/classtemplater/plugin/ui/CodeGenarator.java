package com.inepex.classtemplater.plugin.ui;

import java.io.StringBufferInputStream;
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
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.actions.OrganizeImportsAction;
import org.eclipse.ui.IWorkbenchPart;

import com.inepex.classtemplater.plugin.codegeneration.GenerationType;
import com.inepex.classtemplater.plugin.logic.Attribute;
import com.inepex.classtemplater.plugin.logic.Class;
import com.inepex.classtemplater.plugin.logic.FileUtil;
import com.inepex.classtemplater.plugin.logic.ResourceUtil;
import com.inepex.classtemplater.plugin.logic.StringUtil;
import com.inepex.classtemplater.plugin.logic.TemplateGen;

public class CodeGenarator {

	private GenerationType type;
	private IWorkbenchPart targetPart;
	private TemplateGen templateGen;
	
	private List<IFile> modifiedFiles = new ArrayList<IFile>();
	
	public CodeGenarator(GenerationType type, IWorkbenchPart targetPart, TemplateGen templateGen) throws Exception {
		this.type = type;
		this.targetPart = targetPart;
		this.templateGen = templateGen;		
	}
	
	
	public String generate(boolean organize, IResource template, Attribute attr) throws Exception {
		String templateContent = FileUtil.readFile((IFile)template);
		String outpath = "";
		if (templateContent.startsWith("outpath")){
			outpath = templateContent.substring(8, templateContent.indexOf('\n'));
			outpath = outpath.replace("${attr.name}", attr.getName());
			outpath = outpath.replace("${attr.nameU1}", StringUtil.getU1(attr.getName()));
			outpath = outpath.replace("${attr.workspaceRelativePath}", attr.getWorkspaceRelativePath());
			templateContent = templateContent.substring(templateContent.indexOf('\n') + 1);
		}
		String generated = templateGen.generate(
				template.getName()
				, true
				, attr
				);

		if (!outpath.equals("")){
			String actual = readActualVersion(outpath);
			String merged = mergeHandWrittenCode(actual, generated);
			generated = merged;
			saveToFile(organize, outpath, merged);
		} 
		return generated;	
	}

	public String generate(boolean simpleMode
			, boolean organize
			, IResource template
			, Class classModel
			, boolean ignoreHcContent
			, boolean dontRenderHc) throws Exception {
		String templateContent = FileUtil.readFile((IFile)template);
		String outpath = "";
		if (templateContent.startsWith("outpath")){
			outpath = templateContent.substring(8, templateContent.indexOf('\n'));
			outpath = outpath.replace("${classname}", classModel.getName());
			outpath = outpath.replace("${class.workspaceRelativePath}", classModel.getWorkspaceRelativePath());
			outpath = outpath.replace("${class.getParentRelativePath(1)}", classModel.getParentRelativePath(1));
			outpath = outpath.replace("${class.getParentRelativePath(2)}", classModel.getParentRelativePath(2));
			outpath = outpath.replace("${class.getParentRelativePath(3)}", classModel.getParentRelativePath(3));
			outpath = outpath.replace("${class.getParentRelativePath(4)}", classModel.getParentRelativePath(4));
			outpath = outpath.replace("${class.getParentRelativePath(5)}", classModel.getParentRelativePath(5));
			outpath = outpath.replace("${class.getParentRelativePath(6)}", classModel.getParentRelativePath(6));
			outpath = outpath.replace("${class.getParentRelativePath(7)}", classModel.getParentRelativePath(7));
		}
		String generated = templateGen.generate(
				template.getName()
				, !dontRenderHc
				, classModel
				);

		if (!dontRenderHc && !outpath.equals("")){
			if (!ignoreHcContent){
				String actual = readActualVersion(outpath);
				String merged = mergeHandWrittenCode(actual, generated);
				generated = merged;
			}
			if (!simpleMode) saveToFile(organize, outpath, generated);
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
			content = FileUtil.readFile(file);
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
		if (organize) modifiedFiles.add(file);
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
	
	public void organizeAll() throws Exception {
		for (IFile file : modifiedFiles){
			organizeImports(file);
		}
	}
	
	public static String removeHcs(String content){
		int pos = 0;
		
		do {
			StringBuffer sb = new StringBuffer(content);
			int start = content.indexOf("/*hc:", pos);
			if (start != -1){
				int startEnd = content.indexOf("/", start + 1);
				
				int end = content.indexOf("/*hc*/", startEnd);
				pos = start;
				String defaultContent = sb.substring(startEnd + 1, end);
				sb.replace(start, end + 6, defaultContent);
			} else {
				pos = -1;
			}
			content = sb.toString();
		} while (pos != -1);
		return content;
	}

}
