<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="login.title"/></title>
</head>
<body>
    <div class="container mt-3 col-sm-6 offset-sm-3">
        <h2 class="display-4 text-center"><fmt:message key="login.form_header"/></h2>
        <form class="justify-content-center" method="post" action="${pageContext.request.contextPath}/servlet">
            <div class="form-group">
                <label for="inputEmail"><fmt:message key="login.email"/></label>
                <input class="form-control" type="email" id="inputEmail" name="email" placeholder="<fmt:message key="login.email_placeholder"/>"/>
            </div>
            <div class="form-group">
                <label for="inputPassword"><fmt:message key="login.password"/></label>
                <input class="form-control" type="password" id="inputPassword" name="password" placeholder="<fmt:message key="login.password_placeholder"/>"/>
            </div>
            <input type="hidden" name="command" value="authentication"/>
            <div class="form-group text-center">
                <input class="btn btn-primary" type="submit" value="<fmt:message key="login.submit"/>"/>
            </div>
        </form>
        <c:if test="${not empty authorization_message}">
            <fmt:message key="${authorization_message}"/><br/>
        </c:if>
        <a href="${pageContext.request.contextPath}/jsp/registration.jsp"><fmt:message key="login.registration_link"/></a>
    </div>
</body>
</html>
