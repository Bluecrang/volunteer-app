<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="cm" uri="customtags" %>
<%@include file="/WEB-INF/parts/header.jsp"%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<html>
<head>
    <title>${topic.title}</title>
</head>
<body>
    <div class="container align-content-center">
        <h4><c:out value="${topic.title}"/></h4>
        <c:if test="${account.accessLevel == 'ADMIN' && not topic.closed}">
            <form class="my-2" method="post" id="close_topic" action="${pageContext.request.contextPath}/servlet">
                <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                <input type="hidden" name="command" value="close_topic"/>
                <input class="btn btn-primary" type="submit" value="<fmt:message key="topic.close_topic.submit"/>"/>
            </form>
        </c:if>
        <div class="media">
            <div class="mr-2">
                <cm:image test="${not empty topic.account.avatarBase64}"
                          imgClass="shadow mb-2"
                          src="data:image/jpeg;base64,${topic.account.avatarBase64}"
                          defaultSrc="${pageContext.request.contextPath}/images/profile_default.png"
                          alt="User avatar"/>
            </div>
            <div class="mb-4 clearfix media-body">
                <div>
                    <strong><a href="${pageContext.request.contextPath}/profile?account_id=${topic.account.accountId}&command=show_profile"><c:out value="${topic.account.username}"/></a></strong>
                    <p class="float-right text-muted"><c:out value="${topic.date}"/></p>
                </div>
                <p><c:out value="${topic.text}"/></p>
            </div>
        </div>
        <div class="w-100"><hr/></div>
        <ul class="list-unstyled">
            <c:forEach var="message" items="${message_list}">
                <li class="my-2 ml-6">
                    <div class="media">
                        <div class="mr-2">
                        <cm:image test="${not empty message.account.avatarBase64}"
                                  imgClass="shadow mb-2"
                                  src="data:image/jpeg;base64,${message.account.avatarBase64}"
                                  defaultSrc="${pageContext.request.contextPath}/images/profile_default.png"
                                  alt="User avatar"/>
                        </div>
                        <div class="clearfix media-body">
                            <div>
                                <strong><a href="${pageContext.request.contextPath}/profile?account_id=${message.account.accountId}&command=show_profile"><c:out value="${message.account.username}"/></a></strong>
                                <p class="float-right text-muted"><c:out value="${message.date}"/></p>
                            </div>
                            <p><c:out value="${message.message}"/></p>
                        </div>
                    </div>
                    <c:if test="${not empty account && account.accessLevel == 'ADMIN'}">
                        <form class="mt-2" method="post" action="${pageContext.request.contextPath}/servlet">
                            <input type="hidden" name="command" value="delete_message"/>
                            <input type="hidden" name="message_id" value="${message.messageId}"/>
                            <input class="btn btn-danger btn-sm" type="submit" value="<fmt:message key="topic.delete_message"/>"/>
                        </form>
                    </c:if>
                    <div class="w-100"><hr/></div>
                </li>
            </c:forEach>
        </ul>
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
        <div class="mt-2">
            <c:if test="${not empty account}">
                <c:choose>
                    <c:when test="${not topic.closed}">
                        <form method="post" id="create_message" onsubmit="return validateMessageForm();" name="message" action="${pageContext.request.contextPath}/servlet">
                            <input type="hidden" name="topic_id" value="${topic.topicId}"/>
                            <input type="hidden" name="command" value="create_message"/>
                            <textarea class="form-control" id="message_text" name="text" form="create_message"><fmt:message key="topic.create_message.textarea"/></textarea><br/>
                            <input class="btn btn-primary" type="submit" value="<fmt:message key="topic.create_message.submit"/>"/>
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
        </div>
    </div>
</body>
</html>
