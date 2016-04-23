package com.flying.framework.util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

import com.flying.common.util.Utils;
import com.flying.framework.annotation.Param;
import com.flying.framework.data.Data;
import com.flying.framework.data.ValidationError;

public abstract class ValidationUtils {
	public static List<ValidationError> checkValidation(String field, @SuppressWarnings("rawtypes") Class type, Param param, Data data) {
		List<ValidationError> errors = Utils.newArrayList();
		if(param.required() && (!data.contains(field) || data.get(field) == null)) {
			errors.add(new ValidationError(field, type, ValidationError.OP.required));
			return errors;
		}
		if(type == int.class) {
			int val = data.getInt(field, 0);
			if(!StringUtils.isEmpty(param.max()) ) {
				int refValue = getIntRefValue(param.max(), data);
				if(val > refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.max, val, refValue));
			}
			if(!StringUtils.isEmpty(param.min()) ) {
				int refValue = getIntRefValue(param.min(), data);
				if(val < refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.min, val, refValue));
			}
			if(!StringUtils.isEmpty(param.lt()) ) {
				int refValue = getIntRefValue(param.lt(), data);
				if(val >= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.lt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.gt()) ) {
				int refValue = getIntRefValue(param.gt(), data);
				if(val <= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.gt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.eq()) ) {
				int refValue = getIntRefValue(param.eq(), data);
				if(val != refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		if(type == long.class) {
			long val = data.getLong(field, 0);
			if(!StringUtils.isEmpty(param.max()) ) {
				long refValue = getLongRefValue(param.max(), data);
				if(val > refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.max, val, refValue));
			}
			if(!StringUtils.isEmpty(param.min()) ) {
				long refValue = getLongRefValue(param.min(), data);
				if(val < refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.min, val, refValue));
			}
			if(!StringUtils.isEmpty(param.lt()) ) {
				long refValue = getLongRefValue(param.lt(), data);
				if(val >= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.lt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.gt()) ) {
				long refValue = getLongRefValue(param.gt(), data);
				if(val <= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.gt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.eq()) ) {
				long refValue = getLongRefValue(param.eq(), data);
				if(val != refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		if(type == float.class) {
			float val = data.getFloat(field, 0);
			if(!StringUtils.isEmpty(param.max()) ) {
				float refValue = getFloatRefValue(param.max(), data);
				if(val > refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.max, val, refValue));
			}
			if(!StringUtils.isEmpty(param.min()) ) {
				float refValue = getFloatRefValue(param.min(), data);
				if(val < refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.min, val, refValue));
			}
			if(!StringUtils.isEmpty(param.lt()) ) {
				float refValue = getFloatRefValue(param.lt(), data);
				if(val >= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.lt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.gt()) ) {
				float refValue = getFloatRefValue(param.gt(), data);
				if(val <= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.gt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.eq()) ) {
				float refValue = getFloatRefValue(param.eq(), data);
				if(val != refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		if(type == double.class) {
			double val = data.getDouble(field, 0);
			if(!StringUtils.isEmpty(param.max()) ) {
				double refValue = getDoubleRefValue(param.max(), data);
				if(val > refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.max, val, refValue));
			}
			if(!StringUtils.isEmpty(param.min()) ) {
				double refValue = getDoubleRefValue(param.min(), data);
				if(val < refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.min, val, refValue));
			}
			if(!StringUtils.isEmpty(param.lt()) ) {
				double refValue = getDoubleRefValue(param.lt(), data);
				if(val >= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.lt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.gt()) ) {
				double refValue = getDoubleRefValue(param.gt(), data);
				if(val <= refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.gt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.eq()) ) {
				double refValue = getDoubleRefValue(param.eq(), data);
				if(val != refValue) 
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		if(type == Date.class) {
			Date val = data.getDate(field);
			if(!StringUtils.isEmpty(param.max()) ) {
				Date refValue = getDateRefValue(param.max(), data);
				if(val.after(refValue))
					errors.add(new ValidationError(field, type, ValidationError.OP.max, val, refValue));
			}
			if(!StringUtils.isEmpty(param.min()) ) {
				Date refValue = getDateRefValue(param.min(), data);
				if(val.before(refValue))
					errors.add(new ValidationError(field, type, ValidationError.OP.min, val, refValue));
			}
			if(!StringUtils.isEmpty(param.lt()) ) {
				Date refValue = getDateRefValue(param.lt(), data);
				if(val.after(refValue))
					errors.add(new ValidationError(field, type, ValidationError.OP.lt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.gt()) ) {
				Date refValue = getDateRefValue(param.gt(), data);
				if(val.before(refValue))
					errors.add(new ValidationError(field, type, ValidationError.OP.gt, val, refValue));
			}
			if(!StringUtils.isEmpty(param.eq()) ) {
				Date refValue = getDateRefValue(param.eq(), data);
				if(!val.equals(refValue) )
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		if(type == String.class) {
			String val = data.getString(field);
			if(param.maxlength() > 0 ) {
				int refValue = param.maxlength();
				if(val != null && val.length() > refValue)
					errors.add(new ValidationError(field, type, ValidationError.OP.maxlength, val, refValue));
			}
			if(!StringUtils.isEmpty(param.format()) ) {
				String refValue = param.format();
				Pattern p = getPattern(refValue);
				if(!p.matcher(val).matches())
					errors.add(new ValidationError(field, type, ValidationError.OP.eq, val, refValue));
			}
		}
		return errors;
	}
	
	private static ConcurrentHashMap<String, Pattern> formatPatterns = new ConcurrentHashMap<String, Pattern>();
	
	private synchronized static Pattern getPattern(String format) {
		if(formatPatterns.containsKey(format)) return formatPatterns.get(format);
		Pattern p = Pattern.compile(format);
		formatPatterns.put(format, p);
		return p;
	}
	
	private static int getIntRefValue(String opValue, Data data) {
		if(StringUtils.isNumeric(opValue))
			return Integer.parseInt(opValue);
		String vKey = opValue.substring(1);
		return data.getInt(vKey, 0);
	}
	
	private static long getLongRefValue(String opValue, Data data) {
		if(StringUtils.isNumeric(opValue))
			return Long.parseLong(opValue);
		String vKey = opValue.substring(1);
		return data.getLong(vKey, 0);
	}
	
	private static float getFloatRefValue(String opValue, Data data) {
		if(StringUtils.isNumeric(opValue))
			return Float.parseFloat(opValue);
		String vKey = opValue.substring(1);
		return data.getFloat(vKey, 0);
	}
	
	private static double getDoubleRefValue(String opValue, Data data) {
		if(StringUtils.isNumeric(opValue))
			return Double.parseDouble(opValue);
		String vKey = opValue.substring(1);
		return data.getDouble(vKey, 0);
	}
	
	private static Date getDateRefValue(String opValue, Data data) {
		if("now".equalsIgnoreCase(opValue))
			return new Date();
		String vKey = opValue.substring(1);
		return data.getDate(vKey);
	}
}
