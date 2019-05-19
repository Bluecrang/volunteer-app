<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="administrators.title"/></title>
</head>
<body>
<div class="container align-content-center">
    <table class="table">
        <tr>
            <th><fmt:message key="administrators.username_header"/></th>
            <th><fmt:message key="administrators.rating_header"/></th>
            <th><fmt:message key="administrators.email_header"/></th>
        </tr>
        <c:forEach var="account_from_list" items="${account_list}">
            <tr>
                <td><a href="${pageContext.request.contextPath}/controller?account_id=${account_from_list.accountId}&command=show_profile"><c:out value="${account_from_list.username}"/></a></td>
                <td><c:out value="${account_from_list.rating}"/></td>
                <td><c:out value="${account_from_list.email}"/></td>
            </tr>
        </c:forEach>
    </table>
</div>
</body>
</html>
