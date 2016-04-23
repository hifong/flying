<%@page contentType="text/html;charset=utf-8" pageEncoding="utf-8"%>

<%@page import="com.flyingwords.framework.application.*"%>
<%@page import="com.flyingwords.framework.module.*"%>
<%@page import="com.flyingwords.framework.config.*"%>
<%@page import="com.flyingwords.framework.data.*"%>
<%@page import="com.flyingwords.common.util.*"%>
<%@page import="java.util.*"%>
<%@page import="java.io.*"%>
<%@page import="java.lang.annotation.*"%>
<%!
String asString(Throwable t) {
	StringWriter sw = new StringWriter();
	t.printStackTrace(new PrintWriter(sw));
	return sw.toString();
}
%>
<%
String moduleId = request.getParameter("module");
String serviceId = request.getParameter("service");
String method = request.getParameter("method");

Modules modules = Application.getInstance().getModules();
LocalModule module = modules.getLocalModule(moduleId);

Data data = module.getDataConverter(HttpServletRequest.class.getName()).convert(request);

Data result = null;
Exception ex = null;

try {
	result = module.invoke(serviceId+":"+method, data);
} catch (Exception e) {
	ex = e;
}
%>
<html>
<head>
</head>
<body>

<strong>Request</strong>
<font size=-1><%= data %></font>
<hr>

<strong>Result</strong>
<font size=-1><%= result %></font>
<hr>

<%if(ex != null) { %>
<strong>Exception</strong>
<font size=-1><%= ex %></font><br>
<pre>
<%= asString(ex) %>
</pre>
<% } %>
<hr>

</body>
</html>