/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.widgets.Shell;

public class ResourceSelector extends ItemSelector {
	
	private ArrayList<IResource> ress = new ArrayList<IResource>();

	public ResourceSelector(Shell shell, String title) {
		super(shell, title);
		
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		
		try {
			workspaceRoot.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					ress.add(resource);
					
					return true;
				}
				
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		setItems(ress);
	}

}
