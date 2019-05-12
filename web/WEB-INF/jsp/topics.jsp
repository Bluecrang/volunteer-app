<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<html>
<head>
    <title><fmt:message key="topics.title"/></title>
</head>
<body>
    <div class="container align-content-center">
        <ul class="list-unstyled">
            <c:if test="${not empty topics_message}">
                <h3 class="text-center"><fmt:message key="${topics_message}"/></h3>
            </c:if>
            <c:forEach var="topic" items="${topic_list}">
                <c:if test="${!topic.hidden || account.accessLevel == 'ADMIN'}">
                    <li class="mb-4">
                        <div>
                            <h6><a href="${pageContext.request.contextPath}/topic?topic_id=${topic.topicId}&page=last&command=show_topic_page&page=last"><c:out value="${topic.title}"/></a></h6>
                            <p class="float-right text-muted"><c:out value="${topic.date}"/></p>
                        </div>
                        <fmt:message key="topics.account_username"/> <a href="${pageContext.request.contextPath}/profile?account_id=${topic.account.accountId}&command=show_profile"><c:out value="${topic.account.username}"/></a><br/>
                        <c:if test="${topic.closed}">
                            <fmt:message key="topics.topic_closed"/><br/>
                        </c:if>
                        <c:if test="${account.accessLevel == 'ADMIN'}">
                            <c:if test="${topic.hidden}">
                                <fmt:message key="topics.topic_hidden"/><br/>
                            </c:if>
                            <c:choose>
                                <c:when test="${not topic.hidden}">
                                    <form method="post" action="${pageContext.request.contextPath}/servlet">
                                        <input type="hidden" name="command" value="change_topic_hidden_state"/>
                                        <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                                        <input type="hidden" name="hide" value="true"/>
                                        <input class="btn btn-primary" type="submit" value="<fmt:message key="topics.hide_topic"/>"/>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <form method="post" action="${pageContext.request.contextPath}/servlet">
                                        <input type="hidden" name="command" value="change_topic_hidden_state"/>
                                        <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                                        <input type="hidden" name="hide" value="false"/>
                                        <input class="btn btn-primary" type="submit" value="<fmt:message key="topics.make_topic_visible"/>"/>
                                    </form>
                                </c:otherwise>
                            </c:choose>
                        </c:if>
                        <div class="w-100"><hr/></div>
                    </li>
                </c:if>
            </c:forEach>
        </ul>
        <c:if test="${not empty account}">
            <h4 class="text-center"><fmt:message key="topics.create_topic_header"/><h4/>
            <form method="post" id="create_topic" onsubmit="return validateTopicForm();" name="topic" action="${pageContext.request.contextPath}/servlet">
                <input type="hidden" name="command" value="create_topic"/>
                <fmt:message key="topics.create_topic.topic_name"/><br/>
                <input class="form-control" type="text" name="title"/><br/>
                <textarea class="form-control" id="topic_text" name="text" form="create_topic"><fmt:message key="topics.create_topic.textarea"/></textarea><br/>
                <input class="btn btn-primary" type="submit" value="<fmt:message key="topics.create_topic.submit"/>"/>
            </form>
            <c:if test="${not empty topic_creation_message}">
                <div id="topic_creation_message">
                    <fmt:message key="${topic_creation_message}"/>
                </div>
            </c:if>
        </c:if>
    </div>
</body>
</html>
