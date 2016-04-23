package tj.common.tools.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import tj.common.sql.query.QueryTemplate;
import tj.common.util.Utils;

public class UserTab {
	private final List<UserTabCol> cols = Utils.newArrayList();
	private final String tableName;
	
	public UserTab(String tableName) throws Exception { 
		this.tableName = tableName;
		this.load();
	}

	public String getTableName() {
		return tableName;
	}

	public List<UserTabCol> getCols() {
		return cols;
	}
	
	public String getClassName() {
		String[] tmps = StringUtils.split(tableName, "_");
		StringBuffer sb = new StringBuffer();
		for(String s: tmps) {
			if(sb.length() == 0 && s.length() <= 2) continue;
			sb.append(StringUtils.capitalize(s.toLowerCase()));
		}
		return sb.toString();
	}
	
	public UserTabCol getIdProperty() {
		for(UserTabCol col: cols) {
			if(col.getColumnName().toUpperCase().startsWith(this.getClassName().toUpperCase()) && col.getColumnName().toUpperCase().endsWith("_ID")) {
				return col;
			}
		}
		return cols.get(0);
	}
	
	public UserTabCol getLastProperty() {
		return cols.get(cols.size() - 1);
	}
	
	@SuppressWarnings("rawtypes")
	private void load() throws Exception {
		final String sql = "select t1.column_name, t1.data_type, t1.data_length,t2.comments, t1.nullable " + 
				"from user_tab_cols t1,user_col_comments t2 " + 
				"where t1.table_name=t2.table_name and t1.column_name=t2.column_name and t1.table_name='"+tableName+"'";
		new QueryTemplate(sql) {
			public Object handle(ResultSet rs) throws SQLException {
				while(rs.next()) {
					UserTabCol c = new UserTabCol();
					c.setColumnName(rs.getString(1));
					c.setDataType(rs.getString(2));
					c.setDataLength(rs.getInt(3));
					c.setComment(rs.getString(4));
					c.setNullable("Y".equalsIgnoreCase(rs.getString(5)));
					cols.add(c);
				}
				return cols;
			}
			
		}.query();
	}
	
}
