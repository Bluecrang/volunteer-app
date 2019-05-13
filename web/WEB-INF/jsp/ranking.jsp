<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="ranking.title"/></title>
</head>
<body>
    <div class="container align-content-center">
        <table class="table">
            <tr>
                <th><fmt:message key="ranking.username_header"/></th>
                <th><fmt:message key="ranking.rating_header"/></th>
                <th><fmt:message key="ranking.account_type_header"/></th>
            </tr>
            <c:forEach var="account_from_list" items="${account_list}">
                <tr>
                    <td><a href="${pageContext.request.contextPath}/profile?account_id=${account_from_list.accountId}&command=show_profile"><c:out value="${account_from_list.username}"/></a></td>
                    <td><c:out value="${account_from_list.rating}"/></td>
                </tr>
            </c:forEach>
        </table>
        <c:if test="${ranking_current_page > 1}">
            <a href="${pageContext.request.contextPath}/ranking?page=${ranking_current_page - 1}&command=show_ranking_page">
                <fmt:message key="ranking.previous"/>
            </a>
        </c:if>
        <c:forEach var="i" begin="${ranking_current_page > accounts_per_page ? ranking_current_page - accounts_per_page : 1}" end="${ranking_number_of_pages - ranking_current_page > accounts_per_page ? ranking_current_page + accounts_per_page : ranking_number_of_pages}">
            <c:choose>
                <c:when test="${ranking_current_page == i}">
                    ${i}
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/ranking?page=${i}&command=show_ranking_page">${i}</a>
                </c:otherwise>
            </c:choose>
        </c:forEach>
        <c:if test="${ranking_current_page < ranking_number_of_pages}">
            <a href="${pageContext.request.contextPath}/ranking?page=${ranking_current_page + 1}&command=show_ranking_page"><fmt:message key="ranking.next"/></a>
        </c:if>
    </div>
</html>
