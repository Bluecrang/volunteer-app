<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<html>
<head>
    <title>${topic.title}</title>
</head>
<body>
    <c:if test="${not empty account && account.accessLevel == 'ADMIN'}">
        <form method="post" id="close_topic" action="${pageContext.request.contextPath}/servlet">
            <input type="hidden" name="topic_id" value="${topic.topicId}"/>
            <input type="hidden" name="command" value="close_topic"/>
            <input type="submit" value="<fmt:message key="topic.close_topic.submit"/>"/>
        </form>
        <br/>
    </c:if>
    <div>
        <a href="${pageContext.request.contextPath}/profile?account_id=${topic.account.accountId}&command=show_profile"><c:out value="${topic.account.login}"/></a>
        <c:out value="${topic.text}"/>
    </div>

    <c:forEach var="message" items="${message_list}">
    <div>
        <a href="${pageContext.request.contextPath}/profile?account_id=${message.account.accountId}&command=show_profile"><c:out value="${message.account.login}"/></a><br/>
        <c:out value="${message.message}"/><br/>
        <c:out value="${message.date}"/><br/>
        <c:if test="${not empty account && account.accessLevel == 'ADMIN'}">
            <form method="post" action="${pageContext.request.contextPath}/servlet">
                <input type="hidden" name="command" value="delete_message"/>
                <input type="hidden" name="message_id" value="${message.messageId}"/>
                <input type="submit" value="<fmt:message key="topic.delete_message"/>"/>
            </form>
        </c:if>
    </div>
    </c:forEach>

    <c:if test="${topic_current_page > 1}">
        <a href="${pageContext.request.contextPath}/topic?topic_id=${topic.topicId}&page=${topic_current_page - 1}&command=show_topic_page">
            <fmt:message key="message.previous"/>
        </a>
    </c:if>
    <c:forEach var="i" begin="${topic_current_page > messages_per_page ? topic_current_page - messages_per_page : 1}" end="${topic_number_of_pages - topic_current_page > messages_per_page ? topic_current_page + messages_per_page : topic_number_of_pages}">
        <c:choose>
            <c:when test="${topic_current_page == i}">
                ${i}
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/topic?topic_id=${topic.topicId}&page=${i}&command=show_topic_page">
                    ${i}
                </a>
            </c:otherwise>
        </c:choose>
    </c:forEach>
    <c:if test="${topic_current_page < topic_number_of_pages}">
        <a href="${pageContext.request.contextPath}/topic?topic_id=${topic.topicId}&page=${topic_current_page + 1}&command=show_topic_page">
            <fmt:message key="message.next"/>
        </a>
    </c:if>

    <c:if test="${not empty account}">
        <c:choose>
            <c:when test="${not topic.closed}">
                <form method="post" id="create_message" onsubmit="return validateMessageForm();" name="message" action="${pageContext.request.contextPath}/servlet">
                    <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                    <input type="hidden" name="command" value="create_message"/>
                    <textarea id="message_text" name="text" form="create_message"><fmt:message key="topic.create_message.textarea"/></textarea><br/>
                    <input type="submit" value="<fmt:message key="topic.create_message.submit"/>"/>
                </form>
                <div id="topic_action_notification">
                    <c:if test="${not empty topic_action_notification}">
                        <fmt:message key="${topic_action_notification}"/>
                    </c:if>
                </div>
            </c:when>
            <c:otherwise>
                <fmt:message key="topic.topic_closed"/>
            </c:otherwise>
        </c:choose>

    </c:if>
</body>
</html>
