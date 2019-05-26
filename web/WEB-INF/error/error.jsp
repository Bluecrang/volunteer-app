<%@ page contentType="text/html;charset=UTF-8" isErrorPage="true" %>
<%--<%@taglib prefix="log" uri="http://logging.apache.org/log4j/tld/log" %>--%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title>Error</title>
</head>
<body>
    <h2>Error</h2><br/>
    <%--<log:error message="Exception thrown: requestURI - ${pageContext.errorData.requestURI}" exception="${pageContext.exception}"/>--%>
    <a href="${pageContext.request.contextPath}/controller?command=move_to_index_page">To the main page</a>
</body>
</html>
