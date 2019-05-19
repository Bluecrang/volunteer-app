<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <h2>Error</h2><br/>
    <a href="${pageContext.request.contextPath}/controller?command=move_to_index_page">To the main page</a>
</body>
</html>
