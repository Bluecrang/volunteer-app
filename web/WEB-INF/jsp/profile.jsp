<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="profile.title"/></title>
</head>
<body>
    <img alt="profile image" src="data:image/jpeg;base64,${profile.avatarBase64}"/><br/>
    <fmt:message key="profile.login"/> <c:out value="${profile.login}"/><br/>
    <fmt:message key="profile.rating"/> <c:out value="${profile.rating}"/><br/>
    <c:if test="${not empty account}">
        <c:if test="${account.accountId == profile.accountId or account.accessLevel == 'ADMIN'}">
            <fmt:message key="profile.email"/> <c:out value="${profile.email}"/><br/>
        </c:if>
        <c:if test="${account.accountId == profile.accountId}">
            <form method="post" id="upload_image" action="${pageContext.request.contextPath}/upload" enctype="multipart/form-data">
                <input type="file" id="profile_image" name="image"/>
                <input type="hidden" name="command" value="upload_avatar"/>
                <input type="submit" value="<fmt:message key="profile.image.submit"/>"/>
            </form>
        </c:if>
        <c:if test="${account.accessLevel == 'ADMIN'}">
            <form method="post" id="block_account" action="${pageContext.request.contextPath}/servlet">
                <input type="hidden" name="account_id" value="${profile.accountId}"/>
                <input type="hidden" name="command" value="change_account_block_state"/>
                <c:choose>
                    <c:when test="${!profile.blocked}">
                        <input type="hidden" name="block" value="true"/>
                        <input type="submit" value="<fmt:message key="profile.block_account.submit"/>"/>
                    </c:when>
                    <c:otherwise>
                        <input type="hidden" name="block" value="false"/>
                        <input type="submit" value="<fmt:message key="profile.unlock_account.submit"/>"/>
                    </c:otherwise>
                </c:choose>
            </form>
        </c:if>
    </c:if>
</body>
</html>
