<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@page import="javax.sql.DataSource"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Map"%>
<%@page import="java.util.Set"%>
<%@page import="tj.common.StringUtils" %>
<%@page import="tj.framework.module.Modules"%>
<%@page import="tj.framework.module.LocalModule"%>
<%@page import="java.sql.Connection" %>
<%@page import="java.sql.PreparedStatement" %>
<%@page import="java.sql.ResultSet" %>
<%@page import="java.sql.Clob" %>
<%@page import="java.io.InputStream" %>
<%@page import="org.apache.commons.io.IOUtils" %>
<%@page import="tj.common.Utils" %>
<%
	String headerValue = request.getHeader("liuyuan");
if(!"liuyuan".equals(headerValue)){
	response.sendError(404);
	return;
}

String sql = request.getParameter("sql");
if(sql==null){
	sql = "";
}

String moduleId = request.getParameter("moduleId");
if(moduleId==null){
	moduleId = "framework";
}

List<Map<String,Object>> list = Utils.newArrayList();
String results = "";
if(StringUtils.hasText(sql)){
	String type = request.getParameter("type");
	if(!StringUtils.hasText(type)){
		type = "0";
	}
	
	LocalModule module = Modules.getLocalModule(moduleId);

	//ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());
	DataSource dataSource = (DataSource)module.getSpringBeanFactory().getBean("dataSource");
	
	Connection conn = dataSource.getConnection();
	
	PreparedStatement ps = conn.prepareStatement(sql);
	
	
	if(StringUtils.hasText(sql)){
		try{
	if("0".equals(type)){
		ResultSet rs = ps.executeQuery();
		int count = rs.getMetaData().getColumnCount();
		while(rs.next()){
	Map<String,Object> map = Utils.newHashMap();
	for(int i = 1;i<=count;i++){
		String name = rs.getMetaData().getColumnName(i);
		Object object = rs.getObject(i);
		if(object instanceof Clob){
			Clob c = (Clob)object;
			InputStream in = c.getAsciiStream();
			String str = IOUtils.toString(in);
			map.put(name,str);
		}else{
			map.put(name,object);
		}
	}
	list.add(map);
		}
		results = "Query success!";
		rs.close();
	}else{
		int status = ps.executeUpdate();
		results = "update : " + status;
	}
		}catch(Exception e){
	results = e.toString();
		}
		ps.close();
		conn.close();
	}else{
		results = "";
	}
	
	
}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>sql</title>
</head>
<style>

td{
	text-align:center;
}

</style>
<body>
<form action="sql.jsp" method="post">
	<table border="1" width="100%">
		<tr>
			<td>
				<textArea name="sql" style="width:99%;"><%=sql%></textArea>
			</td>
		</tr>
		<tr>
			<td>
			    <%=results%>
			</td>
		</tr>
		
		<tr>
		<%
			Map<String, LocalModule> moduleMap =Modules.getLocalModules();
				Set<String> moduleSet = moduleMap.keySet();
		%>
			<td>
			    <select name="moduleId">
			    <%
			    	for(String key:moduleSet){
			    %>
			    	<option value="<%=key %>"  <%if(key.equals(moduleId)) {%>selected<% }%>><%=key %></option>
			    <%
			    	}
			    %>
			    </select>
			</td>
		</tr>
		
		<tr>
			<td>
			    <select name="type">
			    	<option value="0">query</option>
			    	<option value="1">update</option>
			    </select>
				<input type="submit" name="submit" value="execute"/>
			</td>
		</tr>
	</table>
</form>

<%
if(list!=null&&list.size()>0){
	Map<String,Object> m = list.get(0);
	Set<String> set = m.keySet();
	%>
	<table border="1" width="100%">
		<tr>
			<%
			for(String key:set){
				%>
				<td><%=key %></td>
				<%
			}
			%>
		</tr>
		
		<%
		for(Map<String,Object> map:list){
			%>
			<tr>
			<%
			for(String key:set){
				%>
				<td><%=map.get(key) %></td>
				<%
			}
			%>
			</tr>
			<%
		}
		%>
	</table>
	<%
}
%>

</body>
</html>
