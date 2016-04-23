package com.flying.framework.util;

import java.io.Writer;

import org.lilystudio.smarty4j.Context;
import org.lilystudio.smarty4j.Engine;
import org.lilystudio.smarty4j.Template;

import com.flying.framework.data.Data;
import com.flying.framework.exception.AppException;
import com.flying.framework.module.LocalModule;

/**
 * @author wanghaifeng
 *
 */
public class SmartyOutputUtils {
	public static void output(LocalModule module, Data data, String templatefile, Writer writer) {
		Engine engine = SmartyUtils.getEngine(module);
		
		Context context = new Context();
		if(data != null) context.putAll(data.getValues());
		Template template;
		try {
			template = engine.getTemplate(templatefile);
			template.merge(context, writer);
		} catch (Exception e) {
			throw new AppException(e, module.getId());
		}
	}
}
