/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.eclipse.core.resources.IResource;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

//TODO: selection history
public class ItemSelector extends Composite implements HasSelection{
	private ArrayList<IResource> resources = new ArrayList<IResource>();
	private ArrayList<IResource> filtered = new ArrayList<IResource>();
	private String extensionFilter;
	private Label lbl_title;
	private Text tb_filter;
	private List list_items;
	
	private ArrayList<LogiSelectionListener> listeners = new ArrayList<LogiSelectionListener>();
	
	public ItemSelector(Shell shell, String title) {
		super(shell, SWT.BORDER);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.pack = true;
		layout.fill = true;
		setLayout(layout);
		lbl_title = new Label(this, SWT.LEFT);
		lbl_title.setText(title);
		tb_filter = new Text(this, SWT.SINGLE);
		tb_filter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				filter();				
			}
		});
		
		list_items = new List(this, SWT.MULTI | SWT.V_SCROLL);
		list_items.addListener (SWT.Selection, new Listener () {
			public void handleEvent (Event e) {
				fireSelection(getSelectedObjects());
			}
		});
		list_items.setLayoutData(new RowData(400, 200));
		filter();

	}
	
	public String getExtensionFilter() {
		return extensionFilter;
	}

	public void setExtensionFilter(String extensionFilter) {
		this.extensionFilter = extensionFilter;
		filter();
	}

	public void setItems(ArrayList<IResource> items){
		this.resources = items;
		filter();
	}
	
	private void filter(){
		filtered.clear();
		list_items.removeAll();
		String filtertext = tb_filter.getText();
		for(IResource res : resources){
			String name = res.getName().toLowerCase();
			if (res.getProject() != null) name = (res.getProject().getName() + res.getName()).toLowerCase();
			if (((extensionFilter != null && res.getName().endsWith(extensionFilter))
				|| extensionFilter == null)	&& name.contains(filtertext.toLowerCase()))
			{
				filtered.add(res);
			}
		}
		
		ArrayList<String> itemNames = new ArrayList<String>();
		for(IResource res : filtered){
			if (res.getProject() != null){
				itemNames.add(res.getProject().getName() + " - " + res.getName());
			} 
		}
		
		Collections.sort(filtered, new Comparator<IResource>() {

			@Override
			public int compare(IResource o1, IResource o2) {
				if (o1.getProject() != null && o2.getProject() != null){
					String o1Item = o1.getProject().getName() + " - " + o1.getName();
					String o2Item = o2.getProject().getName() + " - " + o2.getName();
				return o1Item.compareTo(o2Item);
				}
				return 0;
			}
		});
		Collections.sort(itemNames);
		
		for(String name : itemNames){
			list_items.add(name);
		}
		
	}


	@Override
	public void addSelectionListener(LogiSelectionListener listener) {
		if (!listeners.contains(listener)) listeners.add(listener);
		
	}

	@Override
	public void removeSelectionListener(LogiSelectionListener listener) {
		if (!listeners.contains(listener)) listeners.remove(listener);		
	}
	
	private void fireSelection(java.util.List<Object> selected){
		LogiSelectionEvent e = new LogiSelectionEvent(selected);
		for(LogiSelectionListener l : listeners){
			l.onSelection(e);
		}
	}
	
	public void setWidth(int width){
		tb_filter.setLayoutData(new RowData(width, 20));
		list_items.setLayoutData(new RowData(width, 100));
	}
	
	public java.util.List<Object> getSelectedObjects(){
		int [] selection = list_items.getSelectionIndices ();
		java.util.List<Object> selectedObjects = new ArrayList<Object>();
		for (int sel : selection){
			selectedObjects.add(filtered.get(sel));
		}
		return selectedObjects;
	}

}
