package com.flying.common.serialize;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.IllegalClassException;

import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;

/**
 * @author liuyuan
 * @date 2014-6-10 下午1:27:00
 * 
 */
public class SerializeCodec {

	public static final Map<String, DataType> PRIMITIVES = new HashMap<String, DataType>();
	static {
		PRIMITIVES.put("string", DataType.STRING);
		PRIMITIVES.put("boolean", DataType.BOOLEAN);
		PRIMITIVES.put("date", DataType.DATE);
		PRIMITIVES.put("long", DataType.LONG);
		PRIMITIVES.put("float", DataType.FLOAT);
		PRIMITIVES.put("double", DataType.DOUBLE);
		PRIMITIVES.put("int", DataType.INT);
		PRIMITIVES.put("integer", DataType.INT);
		PRIMITIVES.put("byte[]", DataType.BYTE_ARRAY);
		PRIMITIVES.put("char[]", DataType.CHAR_ARRAY);
		PRIMITIVES.put("string[]", DataType.STRING_ARRAY);
	}

	@SuppressWarnings("rawtypes")
	public static byte[] serialize(Object object) throws Exception {
		Class clasz = object.getClass();
		DataType classType = PRIMITIVES.get(clasz.getSimpleName().toString());
		if(clasz.isPrimitive()){
			
		}
		
		Field columnsField = clasz.getDeclaredField("COLUMN_ARRAY");
		if (columnsField == null) {
			throw new IllegalClassException("Not foun COLUMN_ARRAY field in "
					+ clasz.getName());
		}
		String columnsFieldName = columnsField.getType().getSimpleName().toLowerCase();
		if (PRIMITIVES.get(columnsFieldName) != DataType.STRING_ARRAY) {
			throw new IllegalClassException(
					"COLUMN_ARRAY field must String array in "
							+ clasz.getName());
		}
		
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CodedOutputStream out = CodedOutputStream.newInstance(bout);

		columnsField.setAccessible(true);
		String[] columns = (String[]) columnsField.get(object);
		for (int i = 0; i < columns.length; i++) {
			String fieldName = columns[i];
			Field field = clasz.getDeclaredField(fieldName);
			if (field == null) {
				throw new IllegalClassException(
						"Error field " + fieldName + " in "
								+ clasz.getName());
			}
			field.setAccessible(true);
			String typeName = field.getType().getSimpleName().toLowerCase();
			DataType type = PRIMITIVES.get(typeName);
			if(type==null){
				throw new IllegalClassException(
						"Error type " + typeName + " in "
								+ clasz.getName());
			}
			Object value = field.get(object);
			serializePrimitive(type,value,out);
		}
		out.flush();
		return bout.toByteArray();
	}
	
	public static byte[] serializePrimitive(DataType type,Object value) throws Exception{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		CodedOutputStream out = CodedOutputStream.newInstance(bout);
		serializePrimitive(type,value,out);
		out.flush();
		return bout.toByteArray();
	}
	
	public static void serializePrimitive(DataType type,Object value,CodedOutputStream out) throws Exception{
		if(value==null){
			out.writeRawByte(DataType.NULL.ordinal());
			return;
		}
		switch (type) {
		case STRING:
			out.writeRawByte(type.ordinal());
			out.writeStringNoTag((String) value);
			break;
		case BOOLEAN:
			out.writeRawByte(type.ordinal());
			out.writeBoolNoTag((Boolean) value);
			break;
		case DATE:
			long time = ((Date) value).getTime();
			out.writeRawByte(type.ordinal());
			out.writeSInt64NoTag(time);
			break;
		case LONG:
			out.writeRawByte(type.ordinal());
			out.writeSInt64NoTag((Long) value);
			break;
		case FLOAT:
			out.writeRawByte(type.ordinal());
			out.writeFloatNoTag((Float)value);
			break;
		case DOUBLE:
			out.writeRawByte(type.ordinal());
			out.writeDoubleNoTag((Double)value);
			break;
		case INT:
			out.writeRawByte(type.ordinal());
			out.writeSInt32NoTag((Integer) value);
			break;
		case BYTE_ARRAY:
			byte[] bytes = (byte[]) value;
			out.writeRawByte(type.ordinal());
			out.writeRawVarint32(bytes.length);
			out.writeRawBytes(bytes);
			break;
		case CHAR_ARRAY:
			char[] chars = (char[])value;
			out.writeRawByte(type.ordinal());
			out.writeRawVarint32(chars.length*2);
			for (char c : chars) {
				byte b1 = (byte)(c>>8);
				byte b2 = (byte)c;
				out.writeRawByte(b1);
				out.writeRawByte(b2);
			}
			break;
		case STRING_ARRAY:
			String[] strings = (String[])value;
			out.writeRawByte(type.ordinal());
			out.writeRawVarint32(strings.length);
			for (String s : strings) {
				out.writeStringNoTag(s);
			}
			break;
		default:
			throw new IllegalClassException(
					"Not support field type " + type.ordinal());
		}
		return;
	}
	
	public static <T> T unserialize(Class<T> clasz,byte[] bytes) throws Exception{
		Field columnsField = clasz.getDeclaredField("COLUMN_ARRAY");
		if (columnsField == null) {
			throw new IllegalClassException("Not foun COLUMN_ARRAY field in "
					+ clasz.getName());
		}
		String columnsFieldName = columnsField.getType().getSimpleName().toLowerCase();
		if (PRIMITIVES.get(columnsFieldName) != DataType.STRING_ARRAY) {
			throw new IllegalClassException(
					"COLUMN_ARRAY field must String array in "
							+ clasz.getName());
		}
		
		CodedInputStream in = CodedInputStream.newInstance(bytes);
		T object = clasz.newInstance();

		columnsField.setAccessible(true);
		String[] columns = (String[]) columnsField.get(object);
		for (int i = 0; i < columns.length; i++) {
			String fieldName = columns[i];
			Field field = clasz.getDeclaredField(fieldName);
			if (field == null) {
				throw new IllegalClassException(
						"Error field " + fieldName + " in "
								+ clasz.getName());
			}
			field.setAccessible(true);			
			
			Object value = unserializePrimitive(in);
			
			field.set(object, value);
		}
		return object;
	}
	
	public static Object unserializePrimitive(byte[] bytes) throws Exception{
		CodedInputStream in = CodedInputStream.newInstance(bytes);
		return unserializePrimitive(in);
	}
	
	public static Object unserializePrimitive(CodedInputStream in) throws Exception{
		DataType type = DataType.values()[in.readRawByte()&0xff];
		int length = 0;
		Object t = null;
		switch (type) {
		case NULL:
			break;
		case STRING:
			t = in.readString();
			break;
		case BOOLEAN:
			t = in.readBool();
			break;
		case DATE:
			long time = in.readSInt64();
			t = new Date(time);
			break;
		case LONG:
			t = in.readSInt64();
			break;
		case FLOAT:
			t = in.readFloat();
			break;
		case DOUBLE:
			t = in.readDouble();
			break;
		case INT:
			t = in.readSInt32();
			break;
		case BYTE_ARRAY:
			length = in.readRawVarint32();
			t = in.readRawBytes(length);
			break;
		case CHAR_ARRAY:
			length = in.readRawVarint32();
			byte[] tmpBytes = in.readRawBytes(length);
			char[] chars = new char[length/2];
			for (int j = 0;j<chars.length;j++) {
				chars[j] = (char)((tmpBytes[j*2]<<8) ^ (tmpBytes[j*2+1]&0xff));
			}
			t = chars;
			break;
		case STRING_ARRAY:
			length = in.readRawVarint32();
			String[] strings = new String[length];
			for (int j = 0; j < strings.length; j++) {
				strings[j] = in.readString();
			}
			t = strings;
			break;
		default:
			throw new IllegalClassException(
					"Not support field type " + type.ordinal());
		}
		return t;
	}
}
