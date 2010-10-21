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
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import com.inepex.classtemplater.plugin.codegeneration.GenerationType;

public class TemplateGen {
	private GenerationType type;
	private Attribute attrModel;
	private Class classModel;
	
	public TemplateGen(GenerationType type){
		this.type = type;
	}
	
	private String generateBase(String templateAsString) throws Exception {
		String res = "";
		Velocity.addProperty("resource.loader", "string");
		Velocity.addProperty("string.resource.loader.description",
				"Velocity StringResource loader");
		Velocity
				.addProperty("string.resource.loader.class",
						"org.apache.velocity.runtime.resource.loader.StringResourceLoader");
		Velocity
				.addProperty("string.resource.loader.repository.class",
						"org.apache.velocity.runtime.resource.util.StringResourceRepositoryImpl");
		Velocity.addProperty("string.resource.loader.repository.name", "repo");
		Velocity.addProperty("input.encoding", "UTF-8");

		Velocity.init();
		StringResourceRepository repo = StringResourceLoader.getRepository("repo");

		String templateName = "/templates/thetemplate.vm";
		repo.putStringResource(templateName, templateAsString);


		VelocityContext context = new VelocityContext();

		if (type == GenerationType.CLASS){
			context.put("class", classModel);
			context.put("classname", classModel.getName());
			context.put("classnameL1", StringUtil.getL1(classModel.getName()));
			context.put("package", classModel.packageName);
			context.put("attrs", classModel.getAttributes());
			context.put("methods", classModel.getMethods());
			
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

		template = Velocity.getTemplate(templateName);

		StringWriter sw = new StringWriter();

		template.merge(context, sw);

		res = sw.toString();
		return res;
	}
	
	public String generate(
			String templateAsString
			, Attribute model
			) throws Exception {
		attrModel = model;
		return generateBase(templateAsString);
	}
	

	public String generate(
			String templateAsString
			, Class classModel
			) throws Exception {
		this.classModel = classModel;
		
		return generateBase(templateAsString);

	}
}
