/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.ui;

public interface HasSelection {

	public void addSelectionListener(LogiSelectionListener listener);
	public void removeSelectionListener(LogiSelectionListener listener);
	
}
