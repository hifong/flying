<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@page import="com.flyingwords.framework.application.*"%>
<%@page import="com.flyingwords.framework.module.*"%>
<%@page import="com.flyingwords.framework.config.*"%>
<%@page import="java.util.*"%>
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
Modules modules = Application.getInstance().getModules();

for(String mid: sort(modules.getLocalModules().keySet())) {
LocalModule m = modules.getLocalModule(mid);
ModuleConfig mc = m.getModuleConfig();
%>
<h2><%= m.getId() %></h2><h4><%= m.getPath() %></h4>

<table border=1 cellpadding=0 cellspacing=0 bgcolor="#FFFFFF" width="90%">
<tr>
	<td  width="20%">moduleHome</td>
	<td  width="80%"><%= mc.getModuleHome() %></td>
</tr>
<tr>
	<td  width="20%">imports</td>
	<td  width="80%"><% 
	for(String[] f: mc.getImports()) {
	%>
	<%= f[0] %> || <%= f[1] %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">filters</td>
	<td  width="80%"><% 
	for(String f: mc.getFilters()) {
	%>
	<%= f %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">events</td>
	<td  width="80%"><% 
	for(String[] f: mc.getEvents()) {
	%>
	<%= f[0] %> || <%= f[1] %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">requests</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getRequests().keySet())) {
	%>
	<%= f %> || <%= mc.getRequests().get(f) %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">converters</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getConverters().keySet())) {
	%>
	<%= f %> || <%= mc.getConverters().get(f) %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">annotations</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getAnnotations().keySet())) {
	%>
	<%= f %> || <%= mc.getAnnotations().get(f) %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">configs</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getConfigs().keySet())) {
	%>
	<%= f %> || <%= mc.getConfigs().get(f) %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">beans</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getBeans().keySet())) {
	%>
	<%= f %> || <%= mc.getBeans().get(f) %><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">metadatas <a target="_blank" href="metadata.jsp?module=<%= m.getId() %>">Reload</a></td>
	<td  width="80%"><table width='100%' border=1 cellspacing=0 cellpadding=0 bordercolor='#FEFEFE'>
	<% 
	for(String f: sort(m.getMetadataRepository().getRepository().keySet())) {
	%>
	<tr><td><%= f %></td><td><%= m.getMetadataRepository().getMetadata(f).getData() %></td>
	<% } %></table></td>
</tr>
<tr>
	<td  width="20%">serviceConfigs</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getServiceConfigs().keySet())) {
	%>
	<a href="service.jsp?module=<%= mid %>&service=<%= f %>"><%= f %></a><br>
	<% } %></td>
</tr>
<tr>
	<td  width="20%">pageConfigs</td>
	<td  width="80%"><% 
	for(String f: sort(mc.getPageConfigs().keySet())) {
	%>
	<%= f %> || <%= mc.getPageConfigs().get(f) %><br>
	<% } %></td>
</tr>
</table>

<hr>
<%}%>
</body>
</html>