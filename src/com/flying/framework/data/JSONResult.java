package com.flying.framework.data;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonValueProcessor;
import net.sf.json.util.PropertyFilter;

import org.apache.commons.lang.StringUtils;

import com.flying.common.util.Constants;
import com.flying.framework.module.LocalModule;
import com.flying.framework.security.Principal;

public class JSONResult {
	private String result = "0";
	private String message;
	private Principal principal;
	private Data data;
	private Data request;

	public void setResult(String r) {
		this.result = r;
	}

	public void setPrincipal(Principal p) {
		this.principal = p;
	}

	public void setData(Data d) {
		this.data = d;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setRequest(Data request) {
		this.request = request;
	}

	public String toJSONString() throws IOException {
		JsonConfig config = new JsonConfig();
		config.setJsonPropertyFilter(new PropertyFilter() {
			public boolean apply(Object source, String name, Object value) {
				if (value != null
						&& (value instanceof byte[] || value instanceof LocalModule)) {
					return true;
				} else {
					return false;
				}
			}

		});
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		config.registerJsonValueProcessor(java.sql.Date.class, new DateJsonValueProcessor("yyyy-MM-dd"));
		config.registerJsonValueProcessor(java.sql.Timestamp.class, new DateJsonValueProcessor("yyyy-MM-dd HH:mm:ss"));
		//config.registerJsonValueProcessor(String.class, new DictJsonValueProcessor());

		JSONArray array = null;
		JSONObject json = null;
		if (this.data != null) {
			Object input = data.contains("$value")?data.get(data.getString("$value")):this.data.getValues();
			if(input instanceof Collection) {
				array = JSONArray.fromObject(input);
			} else {
				json = JSONObject.fromObject(input, config);
				json.put("result", result);
				if (this.message != null) {
					json.put("message", this.message);
				}
			}
		}else{
			json = new JSONObject();
		}
		if(json!=null)
			return json.toString(2);
		else
			return array.toString(2);
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

		public Object processObjectValue(String key, Object value,
				JsonConfig jsonConfig) {
			if(value==null){
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
	//

	class DictJsonValueProcessor implements JsonValueProcessor {
		private JSONObject model = null;
		public DictJsonValueProcessor() {
			String entity = request.getString(Constants.ACTION_ENTITY);
			//model = ModelUtils.getJSONModel(entity);
		}

		public Object processArrayValue(Object value, JsonConfig jsonConfig) {
			return value;
		}

		public Object processObjectValue(String key, Object value,
				JsonConfig jsonConfig) {
			JSONObject f = getField(key);
			if(f != null && f.containsKey("dataSource")) {
				try {
					JSONArray ds = f.getJSONArray("dataSource");
					for(int i=0; i< ds.size(); i++) {
						JSONObject row = ds.getJSONObject(i);
						String v = row.getString("value");
						if(StringUtils.equals((String)value, v)) {
							return row.getString("text");
						}
					}
				} catch (Exception e) {}
			}
			return value;
		}
		
		private JSONObject getField(String field) {
			JSONArray arr = model.getJSONArray("fields");
			for(int i=0; i< arr.size(); i++) {
				JSONObject f = arr.getJSONObject(i);
				if(field.equals(f.getString("name"))) {
					return f;
				}
			}
			return null;
		}
	}
}
