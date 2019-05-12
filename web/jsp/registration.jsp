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
    <div class="container mt-3 col-sm-6 offset-sm-3">
        <h2 class="display-4 text-center"><fmt:message key="registration.form_header"/></h2>
        <form class="justify-content-center" method="post" name="registration" onsubmit="return validateRegistrationForm();" action="${pageContext.request.contextPath}/servlet">
            <div class="form-group">
                <label for="inputEmail"><fmt:message key="registration.email"/></label>
                <input class="form-control" type="email" id="inputEmail" name="email" placeholder="<fmt:message key="registration.email_placeholder"/>"/>
            </div>
            <div class="form-group">
                <label for="inputUsername"><fmt:message key="registration.username"/></label>
                <input class="form-control" type="text" id="inputUsername" name="username" aria-describedby="usernameDescription" placeholder="<fmt:message key="registration.username_placeholder"/>"/>
                <small id="usernameDescription" class="form-text text-muted"><fmt:message key="registration.username_description"/></small>
            </div>
            <div class="form-group">
                <label for="inputPassword"><fmt:message key="registration.password"/></label>
                <input class="form-control" type="password" id="inputPassword" name="password" aria-describedby="passwordDescription" placeholder="<fmt:message key="registration.password_placeholder"/>"/>
                <small id="passwordDescription" class="form-text text-muted"><fmt:message key="registration.password_description"/></small>
            </div>
            <input type="hidden" name="command" value="user_registration">
            <div class="form-group text-center">
                <input class="btn btn-primary" type="submit" value="<fmt:message key="registration.submit"/>"/>
            </div>
        </form>
        <div id="registration_message">
            <c:if test="${not empty registration_message}">
                <fmt:message key="${registration_message}"/><br/>
            </c:if>
        </div>
        <a href="${pageContext.request.contextPath}/jsp/login.jsp"><fmt:message key="registration.login_link"/></a>
    </div>
</body>
</html>
