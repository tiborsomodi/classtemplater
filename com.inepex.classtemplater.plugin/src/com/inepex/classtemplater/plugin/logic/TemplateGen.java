/*
 * Copyright:
 * 2010 Tibor Somodi, Inepex, Hungary, http://www.inepex.com
 * License:
 * EPL: http://www.eclipse.org/legal/epl-v10.html
 */

package com.inepex.classtemplater.plugin.logic;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.apache.velocity.tools.generic.AlternatorTool;
import org.apache.velocity.tools.generic.ClassTool;
import org.apache.velocity.tools.generic.ComparisonDateTool;
import org.apache.velocity.tools.generic.ContextTool;
import org.apache.velocity.tools.generic.ConversionTool;
import org.apache.velocity.tools.generic.DateTool;
import org.apache.velocity.tools.generic.DisplayTool;
import org.apache.velocity.tools.generic.EscapeTool;
import org.apache.velocity.tools.generic.FieldTool;
import org.apache.velocity.tools.generic.IteratorTool;
import org.apache.velocity.tools.generic.LinkTool;
import org.apache.velocity.tools.generic.ListTool;
import org.apache.velocity.tools.generic.LoopTool;
import org.apache.velocity.tools.generic.MarkupTool;
import org.apache.velocity.tools.generic.MathTool;
import org.apache.velocity.tools.generic.NumberTool;
import org.apache.velocity.tools.generic.RenderTool;
import org.apache.velocity.tools.generic.ResourceTool;
import org.apache.velocity.tools.generic.SortTool;
import org.apache.velocity.tools.generic.XmlTool;

import com.inepex.classtemplater.plugin.codegeneration.GenerationType;

public class TemplateGen {
	private GenerationType type;
	private Attribute attrModel;
	private Class classModel;
	private String loggerName = "Classtempletar velocity logger";
	StringResourceRepository repo;
	VelocityEngine ve;

	
	public TemplateGen(GenerationType type) throws Exception {
		Thread thread = Thread.currentThread();
		ClassLoader loader = thread.getContextClassLoader();
		thread.setContextClassLoader(this.getClass().getClassLoader());
		try {
			this.type = type;
			ve = new VelocityEngine();
			ve.setProperty("string.resource.loader.description",
				"Velocity StringResource loader");
			ve.setProperty("string.resource.loader.class",
					"org.apache.velocity.runtime.resource.loader.StringResourceLoader");
			ve.setProperty("string.resource.loader.repository.class",
					"org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
			ve.setProperty("string.resource.loader.repository.name", "repo");
	
			ve.setProperty("resource.loader", "string");
			ve.setProperty("input.encoding", "UTF-8");
			ve.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
		      "org.apache.velocity.runtime.log.Log4JLogChute" );
			ve.setProperty("runtime.log.logsystem.log4j.logger",
					loggerName);
	
			ve.init();
			repo = StringResourceLoader.getRepository("repo");
		} finally {
			  thread.setContextClassLoader(loader);
		}

	}
	
	private String generateBase(String templateName, boolean renderHc) throws Exception {
		String res = "";
		VelocityContext context = new VelocityContext();

		if (type == GenerationType.CLASS){
			context.put("class", classModel);
			context.put("classname", classModel.getName());
			context.put("classnameL1", StringUtil.getL1(classModel.getName()));
			context.put("package", classModel.packageName);
			context.put("attrs", classModel.getAttributes());
			context.put("methods", classModel.getMethods());
			context.put("parentPackage", classModel.getParentPackage());
			//velocity tools			
			context.put("alternatorTool", new AlternatorTool());
			context.put("classTool", new ClassTool());
			context.put("comparisonDateTool", new ComparisonDateTool());
			context.put("contextTool", new ContextTool());
			context.put("conversionTool", new ConversionTool());
			context.put("dateTool", new DateTool());
			context.put("displayTool", new DisplayTool());
			context.put("escapeTool", new EscapeTool());
			context.put("fieldTool", new FieldTool());
			context.put("iteratorTool", new IteratorTool());
			context.put("linkTool", new LinkTool());
			context.put("listTool", new ListTool());
			context.put("loopTool", new LoopTool());
			context.put("markupTool", new MarkupTool());
			context.put("mathTool", new MathTool());
			context.put("numberTool", new NumberTool());
			context.put("renderTool", new RenderTool());
			context.put("resourceTool", new ResourceTool());
			context.put("sortTool", new SortTool());
			
			//imports
			Set<Importable> imports = new HashSet<Importable>();
			for (Attribute attr : classModel.getAttributes()){
				if (!attr.isStatic){
					if (!attr.getType().equals("Long")
							&& !attr.getType().equals("Boolean")
							&& !attr.getType().equals("String")
							&& !attr.getType().equals("Double")
							&& !attr.isEnum){
						
						if (attr.getType().contains("List")){
							imports.addAll(attr.getTypesInGenerics());
						} else if (attr.getType().contains("Map")){
							//TODO handle maps
						} else {
							imports.add(new Importable(attr.getType()));
						}
					}
				}
			}
			
			context.put("importables", imports);
		} else if (type == GenerationType.ATTRIBUTE){
			context.put("attr", attrModel);
			context.put("package", attrModel.getPackageName());
		}
		context.put("delimiter", '\n');
		context.put("nl", '\n');
		context.put("utils", new StringUtil());
		
		Template template = null;

		if (!renderHc){
			templateName = "withoutHc_" + templateName;
		}
		template = ve.getTemplate(templateName);

		StringWriter sw = new StringWriter();

		template.merge(context, sw);

		res = sw.toString();
		return clearNewLinesFromBeginning(res);
	}
	
	private String clearNewLinesFromBeginning(String s){
		return s.trim();
	}
	
	public String generate(
			String templateName
			, boolean renderHc
			, Attribute model
			) throws Exception {
		attrModel = model;
		return generateBase(templateName, renderHc);
	}
	

	public String generate(
			String templateName
			, boolean renderHc
			, Class classModel
			) throws Exception {
		this.classModel = classModel;
		
		return generateBase(templateName, renderHc);

	}
	
	public void putTemplate(String name, String templateAsString){
		repo.putStringResource(name, templateAsString);
	}
}
