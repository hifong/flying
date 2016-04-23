package com.flying.common.util;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.util.Arrays;

import com.flying.common.exception.RestrictionException;
import com.flying.framework.annotation.Restriction;
import com.flying.common.util.Constants;
/**
 * 验证用Restriction注解的字段的合法性
 * @author liuyuan
 *
 */
public class RestrictionUtils {
	
	
	
	public static void verify(Object object) throws Exception{
		if(object==null
				||object instanceof String
				||object instanceof Number){
			return;
		}
		Class clasz = object.getClass();
		Field[] fields = clasz.getDeclaredFields();
		AccessibleObject.setAccessible(fields, true);
		for (Field field : fields) {
			if(field.isAnnotationPresent(Restriction.class)){
				String typeName = field.getType().getName();
				if(typeName.equals(Constants.STRING_CLASS_NAME)
						||typeName.equals(Constants.INTEGER_CLASS_NAME)
						||typeName.equals(Constants.INTEGER_TYPE_NAME)){
					Object value = field.get(object);
					Restriction r = field.getAnnotation(Restriction.class);
					boolean nillable = r.nillable();
					if(!nillable&&value==null){
						throw new RestrictionException(clasz.getName(),field.getName(),"Field value is null");
					}

					int minLength = r.minLength();
					int maxLength = r.maxLength();
					
					int minIntValue = r.minIntValue();
					int maxIntValue = r.maxIntValue();
					
					String[] enumStrValues = r.enumStrValues();
					int[] enumIntValues = r.enumIntValues();
					int length = 0;
					
					if(value!=null){
						length = String.valueOf(value).getBytes().length;
					}
					
					if(length<minLength||length>maxLength){
						throw new RestrictionException(clasz.getName(),field.getName(),"Field value length is not allowed. Value length:" + length + ", minLength:" + minLength + ", maxLength:" + maxLength);
					}
					if(typeName.equals(Constants.STRING_CLASS_NAME)){
						if(enumStrValues!=null&&enumStrValues.length>0){
							int i = Arrays.binarySearch(enumStrValues, value);
							if(i<0){
								throw new RestrictionException(clasz.getName(),field.getName(),"Field value is not allowed. enumValues :" + Arrays.toString(enumStrValues) + ", value:" + value);
							}
						}
					}
					
					if(typeName.equals(Constants.INTEGER_CLASS_NAME)||typeName.equals(Constants.INTEGER_TYPE_NAME)){
						if(enumIntValues!=null&&enumIntValues.length>0){
							int i = Arrays.binarySearch(enumIntValues, (Integer)value);
							if(i<0){
								throw new RestrictionException(clasz.getName(),field.getName(),"Field value is not allowed. enumValues :" + Arrays.toString(enumIntValues) + ", value:" + value);
							}
						}
						int v = (Integer)value;
						if(v<minIntValue||v>maxIntValue){
							throw new RestrictionException(clasz.getName(),field.getName(),"Field value is not allowed. Value :" + v + ", minIntValue:" + minIntValue + ", maxIntValue:" + maxIntValue);
						}
					}

				}else if(typeName.startsWith("[L")){
					field.setAccessible(true);
					Object value = field.get(object);
					Restriction r = field.getAnnotation(Restriction.class);
					boolean nillable = r.nillable();
					if(nillable&&value==null){
						throw new RestrictionException(clasz.getName(),field.getName(),"Field value is null");
					}
					
					int minLength = r.minLength();
					int maxLength = r.maxLength();
					int length = 0;
					
					if(value!=null){
						Object[] objects = (Object[])value;
						length = objects.length;
						
						if(length<minLength||length>maxLength){
							throw new RestrictionException(clasz.getName(),field.getName(),"Field value length is not allowed. Value length:" + length + ", minLength:" + minLength + ", maxLength:" + maxLength);
						}
						
						for (Object o : objects) {
							verify(o);
						}
					}
				}
			}
		}
	}
}
