<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="login.title"/></title>
</head>
<body>
    <form method="post" action="${pageContext.request.contextPath}/servlet">
        <fmt:message key="login.login"/> <br/>
        <input type="text" name="login"/> <br/>
        <fmt:message key="login.password"/> <br/>
        <input type="password" name="password"/> <br/>
        <input type="hidden" name="command" value="authentication"/>
        <input type="submit" value="<fmt:message key="login.submit"/>"/>
    </form>
    <c:if test="${not empty authorization_message}">
        <fmt:message key="${authorization_message}"/><br/>
    </c:if>
    <a href="${pageContext.request.contextPath}/jsp/registration.jsp"><fmt:message key="login.registration_link"/></a>
</body>
</html>
