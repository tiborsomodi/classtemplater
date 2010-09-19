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

public class TemplateGen {

	public static String generate(
			String templateAsString
			, Class classModel
			) throws Exception {
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

		context.put("class", classModel);
		context.put("classname", classModel.getName());
		context.put("classnameL1", StringUtil.getL1(classModel.getName()));
		context.put("package", classModel.packageName);
		context.put("attrs", classModel.getAttributes());
		context.put("methods", classModel.getMethods());
		context.put("delimiter", '\n');
		context.put("nl", '\n');

		//imports
		Set<Importable> imports = new HashSet<Importable>();
		for (Attribute attr : classModel.getAttributes()){
			if (!attr.isStatic){
				if (!attr.getType().equals("Long")
						&& !attr.getType().equals("Boolean")
						&& !attr.getType().equals("String")
						&& !attr.getType().equals("Double")){
					
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
		context.put("utils", new StringUtil());
		
		Template template = null;

		template = Velocity.getTemplate(templateName);

		StringWriter sw = new StringWriter();

		template.merge(context, sw);

		res = sw.toString();
		return res;

	}
}
