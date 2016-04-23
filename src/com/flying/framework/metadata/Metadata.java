package com.flying.framework.metadata;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.flying.common.log.Logger;
import com.flying.common.util.ServiceHelper;
import com.flying.common.util.Utils;
import com.flying.framework.data.Data;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Metadata {
	private static Logger log = Logger.getLogger(Metadata.class);
	
	private final Map<String, Object> data;
	private List<Field> fields;
	private List<Field> serviceFields;
	private final MetadataRepository repository;
	
	public Metadata(JSONObject jo, MetadataRepository repository) {
		this.repository = repository;
		this.data = toMap(jo);
		this.getFields();
		if(!StringUtils.isEmpty(getEntity())) {
			Metadata refmd = this.repository.getMetadata(this.getEntity());
			if(refmd == null) return;
			for(String k: refmd.data.keySet()) {
				if(!this.data.containsKey(k)) {
					this.data.put(k, refmd.data.get(k));
				}
			}
		}
	}
	
	public String getString(String key) {
		return (String)data.get(key);
	}
	
	public String getTable() {
		return getString("table-name");
	}
	
	public String getEntity() {
		return getString("entity");
	}
	
	public String[] getPrimaryKey() {
		String pks = getString("primary-key");
		if(pks == null) return new String []{};
		return StringUtils.split(pks, ",");
	}
	
	public Map<String, Object> getData() {
		return data;
	}
	
	public List<Field> getServiceFields() {
		if(this.serviceFields != null) return this.serviceFields;
		
		synchronized(this) {
			if(this.serviceFields != null) return this.serviceFields;
			
			this.serviceFields = Utils.newArrayList();
			for(Field f: this.getFields()) {
				if(!StringUtils.isEmpty(f.getServiceId())) {
					serviceFields.add(f);
				}
			}
			return serviceFields;
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<Field> getFields() {
		if(fields != null) return fields;
		
		synchronized(this) {
			if(fields != null) return fields;
			//
			fields = Utils.newArrayList();
			List<Object>fs = (List<Object>)this.data.get("fields");
			for(Object f: fs) {
				if(f instanceof String) {
					Metadata md = this.repository.getMetadata(this.getEntity());
					fields.add(md.getField((String)f));
				} else {
					Map<String, Object> fmap = (Map<String, Object>)f;
					Field fd = new Field(fmap);
					if(fd.containsKey("inherited-entity")) {
						String entity = fd.getInheritedEntity();
						String name = fd.getInheritedField();
						Metadata md = this.repository.getMetadata(entity);
						Field refField = md.getField(name);
						for(String k: refField.keySet()) {
							if(!fd.containsKey(k)) 
								fd.put(k, refField.get(k));
						}
					}
					fields.add(fd);
				}
			}
			this.data.put("fields", fields);
			return fields;
		}
	}
	
	public Field getField(String fieldName) {
		List<Field> fields = this.getFields();
		for(Field f: fields) {
			if(StringUtils.equalsIgnoreCase(fieldName, (String)f.get("name"))) {
				return f;
			}
		}
		return null;
	}

	private List<Object> toList(JSONArray array) {
		List<Object> list = Utils.newArrayList();
		for(int i=0; i< array.size(); i++) {
			Object o = array.get(i);
			if(o instanceof JSONObject) {
				list.add(toMap(array.getJSONObject(i)));
			} else {
				list.add(o);
			}
		}
		return list;
	}
	
	private Map<String, Object> toMap(JSONObject jo) {
		Map<String, Object> m = Utils.newHashMap();
		for(@SuppressWarnings("rawtypes") Iterator it = jo.keys(); it.hasNext();) {
			String key = it.next().toString();
			Object obj = jo.get(key);
			if(obj instanceof String) {
				m.put(key, jo.getString(key));
			} else if(obj instanceof JSONObject){
				m.put(key, toMap(jo.getJSONObject(key)));
			} else if(obj instanceof JSONArray) {
				m.put(key, toList(jo.getJSONArray(key)));
			}
		}
		return m;
	}
	
	public class Field extends HashMap<String, Object> {
		private static final long serialVersionUID = 1151724231670651224L;
		
		public Field(Map<String, Object> data) {
			this.putAll(data);
		}
		
		public String getName() {
			return (String)super.get("name");
		}
		
		public String getType() {
			return (String)super.get("type");
		}
		
		public String getValueField() {
			return (String)super.get("valueField");
		}
		
		public String getTextField() {
			return (String)super.get("textField");
		}
		//inherited
		public String getInheritedEntity() {
			return (String)super.get("inheritedEntity");
		}
		
		public String getInheritedField() {
			return this.containsKey("inheritedField")? (String)super.get("inheritedField"): this.getName();
		}
		//reference field,such as : select reference-value from reference-entity where ref-rel-field=rel-field
		public String getRefEntity() {
			return (String)super.get("refEntity");
		}
		
		public String getRefRelField() {
			return (String)super.get("refRelField");
		}
		
		public String getRelField() {
			return (String)super.get("relField");
		}
		//data source, combobox,combotree
		public String getUrl() {
			return (String)super.get("url");
		}
		//checkbox
		public String getModuleId() {
			return super.containsKey("moduleId")?(String)super.get("moduleId"): repository.getModule().getId();
		}

		public String getServiceId() {
			return (String)super.get("serviceId");
		}

		public String getOptionServiceId() {
			return (String)super.get("optionServiceId");
		}
		
		@SuppressWarnings("unchecked")
		public List<Map<String, Object>> getOptions() {
			if(this.containsKey("options") && this.getOptionServiceId() == null)
				return (List<Map<String, Object>>)super.get("options");
			if(this.getOptionServiceId() == null) return null;
			
			synchronized(this) {
				if(this.containsKey("options") && this.getOptionServiceId() == null)
					return (List<Map<String, Object>>)super.get("options");
				
				List<Map<String, Object>> options = Utils.newArrayList();
				try {
					Map<String, Object> serviceParams = (Map<String, Object>)super.get("optionParams");
					Data params = new Data(serviceParams);
					
					List<Map<String, Object>> rows = ServiceHelper.invoke(getModuleId(), getOptionServiceId(), params).get("rows");
					final String valueField = (String)this.get("optionValueField");
					final String textField  = (String)this.get("optionTextField");
					for(Map<String, Object> r: rows) {
						Map<String, Object> row = Utils.newHashMap();
						row.put("valueField", r.get(valueField));
						row.put("textField" , r.get(textField));
						options.add(row);
					}
					put("options", options);
				} catch (Exception e) {
					log.warn("Metadata field[" + this.getName() +"] getOptions fail for reason:" + e, e);
				}
				return options;
			}
		}
	}
}
