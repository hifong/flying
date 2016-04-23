package com.flying.framework.data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.flying.framework.module.LocalModule;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.PropertyFilter;

public class JsonData {
	private String result = "0";
	private String message;
	private Map<String, Object> data;

	public void setResult(String r) {
		this.result = r;
	}

	public void setData(Map<String, Object> d) {
		this.data = d;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String toJSONString() throws IOException {
		JsonConfig config = new JsonConfig();
		config.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (value != null && (value instanceof byte[] || value instanceof LocalModule)) {
					return true;
				} else {
					return false;
				}
			}

		});
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));

		JSONObject json = null;
		if (this.data != null) {
			json = JSONObject.fromObject(this.data, config);
		} else {
			json = new JSONObject();
		}

		json.put("result", result);
		if (this.message != null) {
			json.put("message", this.message);
		}
		// if (this.principal != null)
		// json.put("$principal", JSONObject.fromObject(principal, config));
		//
		return json.toString();
	}

	class DateJsonValueProcessor implements JsonValueProcessor {
		private String format = "yyyy-MM-dd";

		public DateJsonValueProcessor() {
		}

		public DateJsonValueProcessor(String format) {
			this.format = format;
		}

		public Object processArrayValue(Object value, JsonConfig jsonConfig) {
			String[] obj = {};
			if (value instanceof Date[]) {
				SimpleDateFormat sf = new SimpleDateFormat(format);
				Date[] dates = (Date[]) value;
				obj = new String[dates.length];
				for (int i = 0; i < dates.length; i++) {
					obj[i] = sf.format(dates[i]);
				}
			}
			return obj;
		}

		public Object processObjectValue(String key, Object value, JsonConfig jsonConfig) {
			if (value == null) {
				return "";
			}
			if (value instanceof Date) {
				String str = new SimpleDateFormat(format).format((Date) value);
				return str;
			}
			return value.toString();
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}

	}
}
