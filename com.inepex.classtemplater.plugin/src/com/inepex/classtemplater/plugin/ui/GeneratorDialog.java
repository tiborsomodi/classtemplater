package com.inepex.classtemplater.plugin.ui;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
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

import com.inepex.classtemplater.plugin.logic.Attribute;
import com.inepex.classtemplater.plugin.logic.TemplateGen;

public class GeneratorDialog extends Dialog {

	private Shell shell;
	private Label lbl_selection;
	private ResourceSelector select_template;
	private Composite comp_result;
	private Label lbl_result;
	private Text text_result;
	private FolderSelector select_folder;
	private Text text_outfile;
	private Composite comp_buttons;
	private Button btn_savetofile;
	private Button btn_close;

	private String selectionName = "";
	private ArrayList<Attribute> attrs = new ArrayList<Attribute>();
	private String className = "";
	private String packageName = "";
	
	
	private LogiSelectionListener listener = new LogiSelectionListener() {
		
		@Override
		public void onSelection(LogiSelectionEvent event) {
			generate((IResource)event.getSelected());
		}
	};
	
	private Listener saveListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			save();
		}
	};
	
	private Listener closeListener = new Listener() {
		
		@Override
		public void handleEvent(Event event) {
			close();
		}
	};
	
	public GeneratorDialog(Shell parent) {
		super(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}
	
	public void open(ISelection selection) throws Exception {
		TreeSelection sel = (TreeSelection) selection;
		if (ICompilationUnit.class.isInstance(sel.getFirstElement())){	
			ICompilationUnit compunit = ((ICompilationUnit) sel.getFirstElement());
			selectionName = compunit.getElementName();
			attrs = getAttrs(compunit);
			className = compunit.findPrimaryType().getTypeQualifiedName();
			packageName = compunit.getPackageDeclarations()[0].getElementName();
			
		} else if (IField.class.isInstance(sel.getFirstElement())){
			Iterator<IField> iter = sel.iterator();
			while (iter.hasNext()){
				IField field =  iter.next();
				attrs.add(getAttr(field));
			}
			selectionName = "custom fields";
		}
		shell = new Shell(getParent(), getStyle());
		shell.setText("Classtemplater: " + selectionName);
		RowLayout layout = new RowLayout(SWT.VERTICAL);
		layout.pack = true;
		layout.wrap = false;
		layout.fill = true;
	 	shell.setLayout(layout);
	 	
	 	Rectangle displayBounds = getParent().getBounds();
	 	shell.setLocation(displayBounds.x + 200 , displayBounds.y + 200);
	 	shell.setSize(new Double(displayBounds.width * 0.7).intValue()
	 			, 585);
	 	
	 	lbl_selection = new Label(shell, SWT.LEFT);
	 	Font bold = new Font(shell.getDisplay(), new FontData("Arial", 10, SWT.BOLD));
	 	lbl_selection.setFont(bold);
	 	lbl_selection.setText("Selected: " + selectionName);
	 	select_template = new ResourceSelector(shell, "Select template (by typing its name and click on it in the list)");
	 	select_template.setWidth(250);
	 	select_template.setExtensionFilter(".vt");
	 	select_template.addSelectionListener(listener);
	 	comp_result = new Composite(shell, SWT.BORDER);
	 	comp_result.setLayout(new RowLayout());
	 	comp_result.setLayoutData(new RowData(shell.getBounds().width - 20, 325));
	 	lbl_result = new Label(comp_result, SWT.LEFT);
	 	lbl_result.setText("Result of code generation:");
	 	text_result = new Text(comp_result, SWT.MULTI);
	 	text_result.setLayoutData(new RowData(shell.getBounds().width - 33, 300));
//	 	select_folder = new FolderSelector(shell, "Select output folder");
//	 	select_folder.setWidth(250);
	 	text_outfile = new Text(shell, SWT.SINGLE);
	 	comp_buttons = new Composite(shell, SWT.NONE);
	 	comp_buttons.setLayout(new RowLayout(SWT.HORIZONTAL));
	 	btn_savetofile = new Button(comp_buttons, SWT.PUSH);
	 	btn_savetofile.addListener(SWT.Selection, saveListener);
	 	btn_savetofile.setText("Save to file");
	 	btn_close = new Button(comp_buttons, SWT.PUSH);
	 	btn_close.setText("Close");
	 	btn_close.addListener(SWT.Selection, closeListener);
	 	shell.open();
	}
	
	private ArrayList<Attribute> getAttrs(ICompilationUnit unit) throws Exception {
		ArrayList<Attribute> attrs = new ArrayList<Attribute>();
		IType[] allTypes = unit.getAllTypes();
		for (IType type : allTypes) {
			for (IField field : type.getFields()) {
				attrs.add(getAttr(field));
			}
		}		
		return attrs;
	}
	
	private Attribute getAttr(IField field) throws Exception {
		String sign = field.getTypeSignature();

		if (sign.startsWith("Q")) {
			if (sign.indexOf("<") == -1) sign = sign.substring(1, sign.length()-1);
			else {
				//generic type
				String basetype = sign.substring(1, sign.indexOf("<"));
				String gentype = sign.substring(sign.indexOf("<") + 1, sign.indexOf(">"));
				String[] parts = gentype.split(";");
				String partString = "";
				for (String s : parts){
					partString += s.substring(1) + ", ";
				}
				partString = partString.substring(0, partString.length() - 2);
				sign = basetype + "<" + partString + ">";				
			}
		}
		else if (sign.equals("I")) sign = "int";
		else if (sign.equals("J")) sign = "long";
		else if (sign.equals("Z")) sign = "boolean";
		else if (sign.equals("D")) sign = "double";
		else if (sign.equals("B")) sign = "byte";
		else if (sign.equals("S")) sign = "short";
		else if (sign.equals("F")) sign = "float";
		else if (sign.equals("C")) sign = "char";
		String visibility = "";
		if (Flags.isPublic(field.getFlags())) visibility = "public";
		else if (Flags.isPrivate(field.getFlags())) visibility = "private";
		else if (Flags.isProtected(field.getFlags())) visibility = "protected";
		else visibility = "public";
		Attribute attr = new Attribute(
				field.getElementName()
				, sign
				, visibility
				, Flags.isStatic(field.getFlags())
				, Flags.isAbstract(field.getFlags())
				, Flags.isFinal(field.getFlags()));
		return attr;
	}
	
	private String readFile(IFile file) throws Exception{
		BufferedReader breader = new BufferedReader(new InputStreamReader(file.getContents(), "UTF-8"));
		String content = "";
		String strLine = "";
		while ((strLine = breader.readLine()) != null) {
			content += strLine;
		}
		return content;
	}
	
	private void save(){
		
	}
	
	private void close(){
		shell.close();
	}

	private void generate(IResource template) {
		
		try {
			String templateContent = readFile((IFile)template);
			String generated = TemplateGen.generate(
					templateContent
					, packageName
					, className				
					, attrs);
			text_result.setText(generated);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
