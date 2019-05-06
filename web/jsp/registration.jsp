<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<html>
<head>
    <title><fmt:message key="registration.title"/></title>
</head>
<body>
    <form method="post" name="registration" onsubmit="return validateRegistrationForm();" action="${pageContext.request.contextPath}/servlet">
        <fmt:message key="registration.email"/> <br/>
        <input type="email" name="email"> <br/>
        <fmt:message key="registration.login"/> <br/>
        <input type="text" name="login"> <br/>
        <fmt:message key="registration.password"/> <br/>
        <input type="password" name="password"> <br/>
        <input type="hidden" name="command" value="user-registration">
        <input type="submit" value="<fmt:message key="registration.submit"/>">
    </form>
    <div id="registration_message">
        <c:if test="${not empty registration_message}">
            <fmt:message key="${registration_message}"/><br/>
        </c:if>
    </div>
    <a href="${pageContext.request.contextPath}/jsp/login.jsp">To login page</a>
</body>
</html>
