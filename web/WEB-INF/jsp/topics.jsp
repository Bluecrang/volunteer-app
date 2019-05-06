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
    <c:forEach var="topic" items="${topic_list}">
        <c:if test="${(!topic.hidden && !topic.closed) || account.accessLevel == 'ADMIN'}">
            <div>
                <a href="${pageContext.request.contextPath}/topic?topic_id=${topic.topicId}&page=last&command=show_topic_page&page=last"><c:out value="${topic.title}"/></a><br/>
                <fmt:message key="topics.account_login"/> <a href="${pageContext.request.contextPath}/profile?account_id=${topic.account.accountId}&command=show_profile"><c:out value="${topic.account.login}"/></a><br/>
                <fmt:message key="topics.topic_date"/> ${topic.date}<br/>
                <fmt:message key="topics.topic_closed"/> ${topic.closed}<br/>
                <br/>
                <c:if test="${account.accessLevel == 'ADMIN'}">
                    <fmt:message key="topics.topic_hidden"/>: ${topic.hidden}<br/>
                    <c:choose>
                        <c:when test="${topic.hidden}">
                            <form method="post" action="${pageContext.request.contextPath}/servlet">
                                <input type="hidden" name="command" value="change_topic_hidden_state"/>
                                <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                                <input type="hidden" name="hide" value="true"/>
                                <input type="submit" value="<fmt:message key="topics.hide_topic"/>"/>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form method="post" action="${pageContext.request.contextPath}/servlet">
                                <input type="hidden" name="command" value="change_topic_hidden_state"/>
                                <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                                <input type="hidden" name="hide" value="false"/>
                                <input type="submit" value="<fmt:message key="topics.make_topic_visible"/>"/>
                            </form>
                        </c:otherwise>
                    </c:choose>
                </c:if>
            </div>
        </c:if>
    </c:forEach>
    <c:if test="${not empty account}">
        <form method="post" id="create_topic" onsubmit="return validateTopicForm();" name="topic" action="${pageContext.request.contextPath}/servlet">
            <input type="hidden" name="command" value="create_topic"/>
            <fmt:message key="topics.create_topic.topic_name"/><br/>
            <input type="text" name="title"/><br/>
            <textarea id="topic_text" name="text" form="create_topic"><fmt:message key="topics.create_topic.textarea"/></textarea><br/>
            <input type="submit" value="<fmt:message key="topics.create_topic.submit"/>"/>
        </form>
        <div id="topic_creation_message">
            <c:if test="${not empty topic_creation_message}">
                <fmt:message key="${topic_creation_message}"/>
            </c:if>
        </div>
    </c:if>
</body>
</html>
