package com.inepex.classtemplater.plugin.logic;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.ICompilationUnit;

public class ResourceUtil {

	public static void createPath(IPath path) throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		
		for (int i=path.segmentCount()-2; i>=0; i--){
			if (!root.getFolder(path.removeLastSegments(i)).exists()) root.getFolder(path.removeLastSegments(i)).create(true, true, null);	
		}		
		
	}
	
	public static String getWorkspaceRelativePath(ICompilationUnit compunit){
		return compunit.getPath().removeLastSegments(1).addTrailingSeparator().toString();
	}
	
}
