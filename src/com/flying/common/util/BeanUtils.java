package com.flying.common.util;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.bind.PropertyException;

import org.apache.commons.beanutils.PropertyUtils;


/**
 * @author wanghaifeng
 *
 */
@SuppressWarnings("unchecked")
public abstract class BeanUtils {
	public final static  Object[] METHOD_EMPTY_PARAMS = new Object[]{};
	
	/**
	 * @modify wanghaifeng Aug 9, 2006 1:09:26 PM
	 * 为一对象中的一个属性设置值
	 * @param target
	 * @param propertyName
	 * @param value
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PropertyException
	 */
	public static void setProperty(Object target, String propertyName, Object value) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(target == null || StringUtils.isEmpty(propertyName)) return;
		if(!hasProperty(target.getClass(), propertyName)) return;
		if(target instanceof Map) {
			((Map)target).put(propertyName, value);
			return;
		}
		PropertyDescriptor descriptor = getPropertyDescriptor(target.getClass(), propertyName);
		if(descriptor.getWriteMethod() != null) {
			PropertyUtils.setProperty(target, propertyName, value);
		} else {
			Method[] methods = target.getClass().getMethods();
			String mName = "set"+propertyName.substring(0,1).toUpperCase()+propertyName.substring(1);
			for(Method m:methods){
				if(mName.equals(m.getName())){
					m.invoke(target, new Object[]{value});
					return;
				}
			}
		}
	}
	
	/**
	 * 为一个对象赋一个简单对象的值
	 * @param target
	 * @param propertyType
	 * @param propertyName
	 * @param value
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws NumberFormatException 
	 * @throws PropertyException 
	 */
	public static void setProperty(Object target,Class propertyType, String propertyName, String value) throws NumberFormatException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		if(propertyType == int.class || propertyType == Integer.class){
			setProperty(target, propertyName, new Integer(value ==null?0:Integer.parseInt(value)));
		} else if(propertyType == long.class || propertyType == Long.class){
			setProperty(target, propertyName, new Long(value == null?0:Long.parseLong(value)));
		} else if(propertyType == double.class || propertyType == Double.class){
			setProperty(target, propertyName, new Double(value == null?0:Double.parseDouble(value)));
		} else if(propertyType == float.class || propertyType == Float.class){
			setProperty(target, propertyName, new Float(value == null?0:Float.parseFloat(value)));
		} else if(propertyType == boolean.class || propertyType == Boolean.class){
			setProperty(target, propertyName, new Boolean(StringUtils.equalsIgnoreCase("true", value)));
		} else if(propertyType == java.util.Date.class){
			setProperty(target, propertyName, DateUtils.parseDate(value));
		} else if(propertyType == java.sql.Date.class){
			java.util.Date d = DateUtils.parseDate(value);
			if(d != null){
				setProperty(target, propertyName, new java.sql.Date(d.getTime()));
			} else {
				setProperty(target, propertyName, null);
			}
		} else if(propertyType == java.lang.String.class) {
			setProperty(target, propertyName, value);
		}
	}
	
	/**
	 * @modify wanghaifeng Aug 26, 2006 10:19:57 AM
	 * @param obj 对象
	 * @param property 属性名称
	 * @return 属性值
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws PropertyException
	 */
	public static Object getProperty(Object obj, String property) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		if(obj == null || StringUtils.isEmpty(property)) return null;

		if(obj instanceof Map) {
			return ((Map)obj).get(property);
		} else {
			return PropertyUtils.getProperty(obj, property);
		}
	}
	
	/**
	 * 一个Class是否有某个属性
	 * @param cls
	 * @param propertyName
	 * @return
	 */
	public static boolean hasProperty(Class cls, String propertyName){
		PropertyDescriptor[] descriptors = getPropertyDescriptors(cls);
		for(int i=0; i< descriptors.length; i++){
			if(StringUtils.equalsIgnoreCase(descriptors[i].getName(), propertyName)){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 获取Class的所有PropertyDesctiptor对象
	 * @param cls
	 * @return
	 */
	public static PropertyDescriptor[] getPropertyDescriptors(Class cls){
		return org.apache.commons.beanutils.PropertyUtils.getPropertyDescriptors(cls);
	}
	
	public static PropertyDescriptor getPropertyDescriptor(Class cls, String property){
		if(StringUtils.isEmpty(property)) return null;
		PropertyDescriptor[] descriptors = getPropertyDescriptors(cls);
		for(PropertyDescriptor descriptor: descriptors){
			if(StringUtils.equals(property, descriptor.getName())){
				return descriptor;
			}
		}
		return null;
	}
	
	public static Map<String, Serializable> convert(Object o) {
		if(o == null) return null;
		Map<String, Serializable> res = Utils.newHashMap();
		PropertyDescriptor[] ds = getPropertyDescriptors(o.getClass());
		for(PropertyDescriptor d: ds) {
			Object p = null;
			try {
				p = getProperty(o, d.getName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(p instanceof Class) continue;
			if(p != null && p instanceof Serializable) {
				res.put(d.getName(), (Serializable)p);
			}
		}
		return res;
	}
	
	public static Map<String, Object> convert2Map(Object o) {
		if(o == null) return null;
		Map<String, Object> res = Utils.newHashMap();
		PropertyDescriptor[] ds = getPropertyDescriptors(o.getClass());
		for(PropertyDescriptor d: ds) {
			try {
				Object p = getProperty(o, d.getName());
				if(p != null && !(p instanceof Class<?>))
					res.put(d.getName(), p);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return res;
	}
	
	/**
	 * 将两个bean中相同属性的值 由 baseForm 付值组 objForm
	 * @param baseForm
	 * @param objForm
	 */
	public static  void copyBeanToBean(Object baseForm, Object objForm){
		
		//如果有一个为空， 直接返回
		if(baseForm == null || objForm == null) return;
		
		 try {
			// 取得拷贝对象的所有域
			 Field[] fieldsBase = baseForm.getClass().getDeclaredFields();
			 Field.setAccessible(fieldsBase, true);
			 // 取得目标对象的所有域
			 Field[] fieldsObj = objForm.getClass().getDeclaredFields();
			 Field.setAccessible(fieldsObj, true);
			 for (int i = 0; i < fieldsObj.length; i++) {
			     // 取得域名称
			     Field fieldObj = fieldsObj[i];
			     for (int j = 0; j < fieldsBase.length; j++) {
			         Field fieldBase = fieldsBase[j];
			         // 比较两域名称是否一致
			         if (fieldObj.getName().equals(fieldBase.getName())) {
			        	 try {
			        		 // 取得域名称并将第一个字母大小
				             String fieldName = fieldObj.getName();
				             String firstChar = fieldName.substring(0, 1).toUpperCase();
				             fieldName = firstChar + fieldName.substring(1);
				             // 取得目标对象中的set方法
				             Method methodObj = objForm.getClass().getMethod("set" + fieldName,
				                     new Class[] { fieldObj.getType() });
				             // 取得参照对象中的get方法
				             Method methodGet = baseForm.getClass().getMethod("get" + fieldName, null);
				             // 执行参照对象的get方法并取得返回值
				             Object resultObj = methodGet.invoke(baseForm, null);
				             // 执行目标对象的set方法并进行设值
				             Object[] objects = null;
				             
				             if((resultObj instanceof Integer) &&fieldObj.getType().equals(Long.class)){
				            	 objects = new Object[] { Long.parseLong(resultObj.toString())};
				             }else{
				            	 objects = new Object[] { resultObj };
				             }
				             methodObj.invoke(objForm, objects);
						} catch (Exception e) {
							e.printStackTrace();
						}
			            
			             break;
			         }
			     }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static <T> T restore(Map<String, Serializable> map,Class<T> clasz) throws Exception {
		T o = clasz.newInstance();
		for(String key:map.keySet()){
			Object v = map.get(key);
			BeanUtils.setProperty(o, key, v);
		}
		return o;
	}
}
