<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@page import="com.flyingwords.framework.application.*"%>
<%@page import="com.flyingwords.framework.module.*"%>
<%@page import="com.flyingwords.framework.config.*"%>
<%@page import="com.flyingwords.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.lang.annotation.*"%>
<html>
<head>
</head>
<body>
<%!
List<String> sort(Set<String> sets) {
	List<String> moduleIds = new ArrayList<String>();
	moduleIds.addAll(sets);
	Collections.sort(moduleIds);
	return moduleIds;
}
%>
<%
String moduleId = request.getParameter("module");
String serviceId = request.getParameter("service");

Modules modules = Application.getInstance().getModules();
LocalModule module = modules.getLocalModule(moduleId);
ServiceConfig serviceConfig = module.getModuleConfig().getServiceConfig(serviceId);

%>
<strong><a href="modules.jsp">modules</a> / <%= moduleId %>:<%= serviceId %></strong>

<table border=1 cellpadding=0 cellspacing=0 bgcolor="#FFFFFF" width="90%" bordercolor='#FEFEFE'>
<tr>
	<td  width="20%">target</td>
	<td  width="80%"><%= serviceConfig.getTarget() %></td>
</tr>
<tr>
	<td  width="20%">type</td>
	<td  width="80%"><%= serviceConfig.getType() %></td>
</tr>
<tr>
	<td  width="20%">desc</td>
	<td  width="80%"><%= serviceConfig.getDesc() %></td>
</tr>
<tr>
	<td  width="20%">configs</td>
	<td  width="80%"><table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolor='#FEFEFE'>
	<% 
	for(String f: sort(serviceConfig.getConfigs().getValues().keySet())) {
	%>
	<tr><td><%= f %></td><td><%= serviceConfig.getConfigs().getValues().get(f) %></td>
	<% } %></table></td>
</tr>
</table>
<hr>
<%
Map<String,Map<String,Object>> methods = ServiceHelper.getServiceMethods(moduleId, serviceId);
for(String methodName: sort(methods.keySet())) {
	Map<String, Object> methodInfo = (Map<String, Object>)methods.get(methodName);
%>
<strong><%= methodName %></strong>
<form action="invoke.jsp" method="post" target="result_<%= methodName %>">
	<input type="hidden" name="module" value="<%= moduleId %>">
	<input type="hidden" name="service" value="<%= serviceId %>">
	<input type="hidden" name="method" value="<%= methodName %>">
<table width="99%" border=0>
<%
	List<Map<String, Object>> params = (List<Map<String, Object>>)methodInfo.get("params");
	int c = 0;
	if(params != null)
	for(Map<String, Object> param : params) {
	c ++;
%>
	<tr valign="middle">
		<td width="10%"><strong><%= param.get("name") %></strong><br>
			<font size=-1>(<%= param.get("type") %>)</font>
		</td>
		<td width="25%"><input type="text" name="<%= param.get("name") %>" style="width:80%" /></td>
		<td width="30%"><% if(param.get("param") != null){%><font size=-1><%= param.get("param").toString().substring(43) %></font><%}%></td>
		<% if (c == 1) {%>
		<td width="*" rowspan="<%= params.size() + 1 %>"><iframe width="100%" height="100%" name="result_<%= methodName %>"></iframe></td>
		<%}%>
	</tr>
<%
	}
%>
	<tr><td colspan=3 align="center"><input type=submit value="invoke" style="width:100px"></td></tr>
</table>
</form>
<hr>
<%}%>
</body>
</html>