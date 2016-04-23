package tj.common.tools.db;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import tj.common.util.Utils;

public class UserTabCol {
	private String columnName;
	private String dataType;
	private int dataLength;
	private String comment;
	private boolean nullable;

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public int getDataLength() {
		return dataLength;
	}
	
	public String getPropertyName() {
		String[] tmps = StringUtils.split(columnName, "_");
		StringBuffer sb = new StringBuffer();
		for(String s: tmps) {
			if(s.length() <= 1) continue;
			if(sb.length() == 0) 
				sb.append(s.toLowerCase());
			else
				sb.append(StringUtils.capitalize(s.toLowerCase()));
		}
		return sb.toString();
	}
	
	public String getPropertyType() {
		if(StringUtils.equalsIgnoreCase("date", dataType)) {
			return "java.util.Date";
		} else if(StringUtils.equalsIgnoreCase("number", dataType)) {
			return "Long";
		} else {
			return "String";
		}
	}
	
	public String getSetterName() {
		String[] tmps = StringUtils.split(columnName, "_");
		StringBuffer sb = new StringBuffer();
		for(String s: tmps) {
			if(s.length() <= 1) continue;
			sb.append(StringUtils.capitalize(s.toLowerCase()));
		}
		return "set"+sb.toString();
	}
	
	public String getGetterName() {
		String[] tmps = StringUtils.split(columnName, "_");
		StringBuffer sb = new StringBuffer();
		for(String s: tmps) {
			if(s.length() <= 1) continue;
			sb.append(StringUtils.capitalize(s.toLowerCase()));
		}
		return "get"+sb.toString();
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getComment() {
		return comment == null?this.columnName: this.comment;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public boolean isNullable() {
		return nullable;
	}
	
	public List<String[]> getOptions() {
		if( this.comment == null) return null;
		if( this.comment.indexOf("(") >= 0 && this.comment.indexOf("（") < 0) return null;
		int start = Math.max(this.comment.indexOf("("), this.comment.indexOf("（"));
		int end = Math.max(this.comment.indexOf(")"), this.comment.indexOf("）"));
		String subs = this.comment.substring(start + 1, end);
		String[] tmps = StringUtils.split(subs, "；");
		if(tmps.length == 1) tmps = StringUtils.split(subs, "\n");
		List<String[]> list = Utils.newArrayList();
		for(String t: tmps) {
			t = t.trim();
			list.add(StringUtils.split(t, "："));
		}
		return list;
	}
	
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.SIMPLE_STYLE);
	}
}
