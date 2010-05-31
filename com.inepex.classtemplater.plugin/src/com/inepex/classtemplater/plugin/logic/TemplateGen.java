package com.inepex.classtemplater.plugin.logic;

import java.io.StringWriter;
import java.util.ArrayList;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

public class TemplateGen {

	public static String generate(String templateAsString, ArrayList<String[]> attrs
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

		Velocity.init();
		StringResourceRepository repo = StringResourceLoader.getRepository("repo");

		String templateName = "/templates/thetemplate.vm";
		repo.putStringResource(templateName, templateAsString);


		VelocityContext context = new VelocityContext();

		context.put("attrs", attrs);
		context.put("delimiter", '\n');

		Template template = null;

		try {

			template = Velocity.getTemplate(templateName);
		} catch (ResourceNotFoundException rnfe) {
			// couldn't find the template
		} catch (ParseErrorException pee) {
			// syntax error: problem parsing the template
		} catch (MethodInvocationException mie) {
			// something invoked in the template
			// threw an exception
		} catch (Exception e) {
		}

		StringWriter sw = new StringWriter();

		template.merge(context, sw);

		res = sw.toString();
		return res;

	}
}
