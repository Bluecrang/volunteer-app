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
    <nav class="navbar navbar-expand-lg navbar-light bg-light">
        <a class="navbar-brand" href="${pageContext.request.contextPath}/jsp/main.jsp"><fmt:message key="header.brand"/></a>
        <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarContent" aria-controls="navbarContent" aria-expanded="false" aria-label="<fmt:message key="header.toggle_navigation"/>">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarContent">
            <ul class="navbar-nav mr-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/jsp/main.jsp"><fmt:message key="header.main"/></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/topics?command=show_topics"><fmt:message key="header.topics"/></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/ranking?command=show_ranking_page&page=1"><fmt:message key="header.ranking"/></a>
                </li>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="languageDropdown" role="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
                        <fmt:message key="header.locale"/>
                    </a>
                    <div class="dropdown-menu" aria-labelledby="languageDropdown">
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/servlet?locale=us_EN&command=change_locale"><fmt:message key="header.language_dropdown.english"/></a>
                        <a class="dropdown-item" href="${pageContext.request.contextPath}/servlet?locale=ru_RU&command=change_locale"><fmt:message key="header.language_dropdown.russian"/></a>
                    </div>
                </li>
                <c:choose>
                    <c:when test="${empty account}">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/jsp/login.jsp"><fmt:message key="header.login"/></a>
                        </li>
                    </c:when>
                    <c:otherwise>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/profile?account_id=${account.accountId}&command=show_profile"><fmt:message key="header.profile"/></a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
            <c:if test="${not empty account}">
                <form class="form-inline my-2 mr-2 my-lg-0" method="post" action="${pageContext.request.contextPath}/servlet">
                    <input type="hidden" name="command" value="logout"/>
                    <input class="btn btn-outline-danger my-2 m-sm-0" type="submit" value="<fmt:message key="header.logout"/>"/>
                </form>
            </c:if>
            <form class="form-inline my-2 my-lg-0" action="${pageContext.request.contextPath}/servlet">
                <input class="form-control mr-sm-2" name="text" type="search" placeholder="<fmt:message key="header.topic_search.placeholder"/>" aria-label="<fmt:message key="header.topic_search.aria_label"/>">
                <input type="hidden" name="command" value="search_for_topics"/>
                <input class="btn btn-outline-success my-2 my-sm-0" type="submit" value="<fmt:message key="header.topic_search.submit"/>"/>
            </form>
        </div>
    </nav>
</body>
</html>
