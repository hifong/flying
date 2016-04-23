package com.flying.framework.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.flying.common.util.DateUtils;
import com.flying.common.util.Utils;

/**
 * @author wanghaifeng
 * 
 */
public class Data implements Serializable {
	private static final long serialVersionUID = -8662885273436542677L;

	private final Map<String, Object> values;

	public Data(Map<String, Object> values) {
		this.values = values == null ? new HashMap<String, Object>() : values;
	}

	public Data(Object ...params  ) {
		this.values = Utils.newHashMap();
		if(params != null) {
			for(int i=0; i < params.length / 2; i++) {
				if(2 * i  + 1 >= params.length) break;
				String k = (String)params[2 * i];
				Object v = params[ 2* i + 1];
				this.values.put(k, v);
			}
		}
	}
	
	public Map<String, Object> getValues() {
		return values;
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key) {
		return (T) values.get(key);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(String key, T def) {
		if(this.contains(key))
			return (T)values.get(key);
		else
			return def;
	}

	@SuppressWarnings("unchecked")
	public <T> T remove(String key) {
		return (T)values.remove(key);
	}

	public <T> Data put(String key, T value) {
		values.put(key, value);
		return this;
	}

	public <T> Data putAll(Map<String, T> values) {
		if(values != null)
			this.values.putAll(values);
		return this;
	}

	public Data putAll(Data val) {
		this.values.putAll(val.values);
		return this;
	}

	public Set<String> keys() {
		return values.keySet();
	}

	public Collection<Object> values() {
		return values.values();
	}

	public boolean contains(String key) {
		return values.containsKey(key);
	}

	public boolean isEmpty() {
		return values.isEmpty();
	}

	public String getString(String key, String def) {
		String val = null;
		if(this.contains(key)) {
			val = getString(key);
		}
		val = StringUtils.isEmpty(val) ? def:val;
		return val;
	}
	
	public String getString(String key) {
		Object v = get(key);
		if (v == null)
			return null;
		if (v instanceof String) {
			return (String) v;
		} else if (v instanceof String[]) {
			return ((String[]) v)[0];
		} else {
			return v == null ? null : v.toString();
		}
	}

	@SuppressWarnings("rawtypes")
	public String[] getStrings(String key) {
		Object v = get(key);
		if(v == null) return new String[]{};;
		if (v instanceof String) 
			return new String[]{(String)v};
		if (v instanceof String[])
			return (String[]) v;
		if (v instanceof Collection) {
			Collection c = (Collection) v;
			String[] vs = new String[c.size()];
			int i = 0;
			for (Iterator it = c.iterator(); it.hasNext();) {
				Object iv = it.next();
				vs[i] = iv == null ? null : iv.toString();
				i++;
			}
			return vs;
		}
		return new String[]{v.toString()};
	}

	public boolean getBoolean(String key, Boolean def) {
		Object v = get(key);
		if (v instanceof Boolean) {
			return (Boolean) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : "true".equalsIgnoreCase(s);
		}
	}

	public boolean[] getBooleans(String key) {
		Object v = get(key);
		if (v instanceof boolean[]) {
			return (boolean[]) v;
		} else {
			String[] ss = getStrings(key);
			boolean[] res = new boolean[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = "true".equalsIgnoreCase(ss[i]);
			}
			return res;
		}
	}
	
	public int getInt(String key, int def) {
		Object v = get(key);
		if (v instanceof Integer) {
			return (Integer) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Integer.parseInt(s.toString());
		}
	}

	public int[] getInts(String key, int def) {
		Object v = get(key);
		if (v instanceof int[]) {
			return (int[]) v;
		} else {
			String[] ss = getStrings(key);
			int[] res = new int[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? def : Integer.parseInt(ss[i].toString());
			}
			return res;
		}
	}

	public long getLong(String key, long def) {
		Object v = get(key);
		if (v instanceof Long) {
			return (Long) v;
		} else {
			String s = getString(key);
			if(StringUtils.isNumeric(s) && !StringUtils.isEmpty(s))
				return StringUtils.isEmpty(s) ? def : Long.parseLong(s.toString());
			else
				return def;
		}
	}

	public long[] getLongs(String key, long def) {
		Object v = get(key);
		if (v instanceof long[]) {
			return (long[]) v;
		} else {
			String[] ss = getStrings(key);
			long[] res = new long[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? def : Long.parseLong(ss[i].toString());
			}
			return res;
		}
	}

	public double getDouble(String key, double def) {
		Object v = get(key);
		if (v instanceof Double) {
			return (Double) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Double.parseDouble(s.toString());
		}
	}

	public double[] getDoubles(String key, double def) {
		Object v = get(key);
		if (v instanceof double[]) {
			return (double[]) v;
		} else {
			String[] ss = getStrings(key);
			double[] res = new double[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? def : Double.parseDouble(ss[i].toString());
			}
			return res;
		}
	}

	public float getFloat(String key, float def) {
		Object v = get(key);
		if (v instanceof Float) {
			return (Float) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? def : Float.parseFloat(s.toString());
		}
	}

	public float[] getFloats(String key, float def) {
		Object v = get(key);
		if (v instanceof float[]) {
			return (float[]) v;
		} else {
			String[] ss = getStrings(key);
			float[] res = new float[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? def : Float.parseFloat(ss[i].toString());
			}
			return res;
		}
	}

	public Date getDate(String key) {
		Object v = get(key);
		if (v instanceof Date) {
			return (Date) v;
		} else {
			String s = getString(key);
			return StringUtils.isEmpty(s) ? null : DateUtils.parseDate(s);
		}
	}

	public Date[] getDates(String key) {
		Object v = get(key);
		if (v instanceof Date[]) {
			return (Date[]) v;
		} else {
			String[] ss = getStrings(key);
			Date[] res = new Date[ss.length];
			for(int i=0; i< ss.length; i++) {
				res[i] = StringUtils.isEmpty(ss[i]) ? null : DateUtils.parseDate(ss[i]);
			}
			return res;
		}
	}

	@SuppressWarnings("unchecked")
	public Data getValue(String key) {
		Object v = get(key);
		if (v == null)
			return null;
		if (v instanceof Data)
			return (Data) v;
		if (v instanceof Map)
			return (new Data((Map<String, Object>) v));
		return null;
	}

	public Map<String, Object> filterValues(DataFilter filter) {
		if (filter == null)
			return null;
		Map<String, Object> res = Utils.newHashMap();
		for (String key : values.keySet()) {
			if (!filter.isValid(key, values.get(key)))
				res.put(key, values.remove(key));
		}
		return res;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Data merge(Data that, boolean overrides) {
		for (String key : that.keys()) {
			Object thisValue = this.get(key);
			Object thatValue = that.get(key);
			if (this.contains(key)) {
				if ((thisValue instanceof Map || thisValue instanceof Data) && (thatValue instanceof Map || thatValue instanceof Data)) {
					Data thisData = thisValue instanceof Data ? (Data) thisValue : new Data((Map<String, Object>) thisValue);
					Data thatData = thatValue instanceof Data ? (Data) thatValue : new Data((Map<String, Object>) thatValue);
					thisData.merge(thatData, overrides);
					if(thisValue instanceof Map) {
						this.put(key, thisData.getValues());
					} 
				} else if(thisValue instanceof Collection && thatValue instanceof Collection){
					((Collection)thisValue).addAll((Collection)thatValue);
				}else {
					if (overrides) {
						this.put(key, thatValue);
					}
				}
			} else {
				this.put(key, thatValue);
			}
		}
		return this;
	}
	
	public <T> T as(Class<T> type) throws Exception {
		return DataUtils.convert(this, type);
	}

	public String toString() {
		return values.toString();
	}
}
