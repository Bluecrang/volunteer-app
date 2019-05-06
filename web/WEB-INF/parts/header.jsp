<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<script src="${pageContext.request.contextPath}/js/jquery-3.4.0.min.js"></script>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/css/bootstrap.min.css"/>
<script src="${pageContext.request.contextPath}/js/bootstrap.bundle.min.js"></script>

<c:choose>
    <c:when test="${empty locale}">
        <fmt:setLocale value="us_EN"/>
    </c:when>
    <c:otherwise>
        <fmt:setLocale value="${locale}"/>
    </c:otherwise>
</c:choose>
<fmt:setBundle basename="resources.localization"/>
<html>
<body>
    <div>
        <a href="${pageContext.request.contextPath}/jsp/main.jsp"><fmt:message key="header.main"/></a>
        <a href="${pageContext.request.contextPath}/topics?command=show_topics"><fmt:message key="header.topics"/></a>
        <a href="${pageContext.request.contextPath}/ranking?command=show_ranking_page&page=1&load_accounts=true"><fmt:message key="header.ranking"/></a>

        <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-display="static" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                <fmt:message key="header.locale"/>
            </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton">
                <a class="dropdown-item" href="${pageContext.request.contextPath}/servlet?locale=us_EN&command=change_locale">English</a>
                <a class="dropdown-item" href="${pageContext.request.contextPath}/servlet?locale=ru_RU&command=change_locale">Русский</a>
            </div>
        </div>
        <c:choose>
            <c:when test="${empty account}">
                <a href="${pageContext.request.contextPath}/jsp/login.jsp"><fmt:message key="header.login"/></a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/profile?account_id=${account.accountId}&command=show_profile"><fmt:message key="header.profile"/></a>
                <form method="post" action="${pageContext.request.contextPath}/servlet">
                    <input type="hidden" name="command" value="logout"/>
                    <input type="submit" value="<fmt:message key="header.logout"/>"/>
                </form>
            </c:otherwise>
        </c:choose>
    </div>
</body>
</html>
