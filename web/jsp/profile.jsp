<%@ page contentType="text/html;charset=UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="cm" uri="customtags" %>
<%@include file="/WEB-INF/parts/header.jsp"%>
<script src="${pageContext.request.contextPath}/js/validation.js"></script>
<html>
<head>
    <title><fmt:message key="profile.title"/></title>
</head>
<body>
    <div class="container float-left ml-2 mt-2">
        <div class="row">
            <div class="col-auto">
                <cm:image test="${not empty profile.avatarBase64}"
                          imgClass="shadow mb-2"
                          src="data:image/jpeg;base64,${profile.avatarBase64}"
                          defaultSrc="${pageContext.request.contextPath}/images/profile_default.png"
                          alt="User avatar"
                          width="256"
                          height="256"/>
            </div>
            <div class="col-auto mt-2">
                <div class="row">
                    <h4><c:out value="${profile.username}"/></h4>
                </div>
                <div class="row">
                    <fmt:message key="profile.rating"/> <c:out value="${profile.rating}"/>
                </div>
                <c:if test="${account.accountType == 'ADMIN' || account.accountId == profile.accountId}">
                    <div class="row">
                        <c:if test="${profile.accountType == 'ADMIN'}"><fmt:message key="profile.account_type_admin"/></c:if>
                        <c:if test="${profile.accountType == 'VOLUNTEER'}"><fmt:message key="profile.account_type_volunteer"/></c:if>
                        <c:if test="${profile.accountType == 'USER'}"><fmt:message key="profile.account_type_user"/></c:if>
                    </div>
                </c:if>
                <c:if test="${not empty account}">
                    <c:if test="${account.accountId == profile.accountId or account.accountType == 'ADMIN'}">
                        <div class="row">
                            <fmt:message key="profile.email"/> <c:out value="${profile.email}"/>
                        </div>
                    </c:if>
                </c:if>
                <div class="row mt-2">
                    <c:if test="${account.accountType == 'ADMIN'}">
                        <c:if test="${profile.accountType != 'ADMIN'}">
                        <form method="post" id="block_account" action="${pageContext.request.contextPath}/controller">
                            <input type="hidden" name="account_id" value="${profile.accountId}"/>
                            <input type="hidden" name="command" value="change_account_type"/>
                            <input type="hidden" name="account_type" value="admin"/>
                                <input class="btn btn-primary" type="submit" value="<fmt:message key="profile.change_account_type_administrator.submit"/>"/>
                        </form>
                            <c:if test="${profile.accountType != 'VOLUNTEER'}">
                                <br/>
                                <form method="post" id="block_account" action="${pageContext.request.contextPath}/controller">
                                    <input type="hidden" name="account_id" value="${profile.accountId}"/>
                                    <input type="hidden" name="command" value="change_account_type"/>
                                    <input type="hidden" name="account_type" value="volunteer"/>
                                    <input class="btn btn-primary" type="submit" value="<fmt:message key="profile.change_account_type_volunteer.submit"/>"/>
                                </form>
                            </c:if>
                            <c:if test="${profile.accountType == 'VOLUNTEER'}">
                                <br/>
                                <form method="post" id="block_account" action="${pageContext.request.contextPath}/controller">
                                    <input type="hidden" name="account_id" value="${profile.accountId}"/>
                                    <input type="hidden" name="command" value="change_account_type"/>
                                    <input type="hidden" name="account_type" value="user"/>
                                    <input class="btn btn-primary" type="submit" value="<fmt:message key="profile.change_account_type_user.submit"/>"/>
                                </form>
                            </c:if>
                        </c:if>
                        <br/>
                        <form method="post" id="block_account" action="${pageContext.request.contextPath}/controller">
                            <input type="hidden" name="account_id" value="${profile.accountId}"/>
                            <input type="hidden" name="command" value="change_account_block_state"/>
                            <c:choose>
                                <c:when test="${!profile.blocked}">
                                    <input type="hidden" name="block" value="true"/>
                                    <input class="btn btn-warning" type="submit" value="<fmt:message key="profile.block_account.submit"/>"/>
                                </c:when>
                                <c:otherwise>
                                    <input type="hidden" name="block" value="false"/>
                                    <input class="btn btn-warning" type="submit" value="<fmt:message key="profile.unlock_account.submit"/>"/>
                                </c:otherwise>
                            </c:choose>
                        </form>
                    </c:if>
                </div>
            </div>
        </div>
        <c:if test="${account.accountId == profile.accountId}">
            <div class="row">
                <form class="form-inline" method="post" id="upload_image" action="${pageContext.request.contextPath}/upload" enctype="multipart/form-data">
                    <div class="form-group custom-file">
                        <input class="custom-file-input form-control-file" type="file" id="profile_image" name="image"/>
                        <label class="custom-file-label" for="profile_image"><fmt:message key="profile.file_label"/></label>
                    </div>
                    <input type="hidden" name="command" value="upload_avatar"/>
                    <input class="btn btn-primary mt-2" type="submit" value="<fmt:message key="profile.image.submit"/>"/>
                </form>
            </div>
        </c:if>
        <c:if test="${account.accountType == 'ADMIN'}">
            <h6><fmt:message key="profile.rating_form.header"/></h6>
            <div class="row">
                <form class="form-inline"  id="rating_addition_form" method="post" onsubmit="return validateRatingAdditionForm();" name="rating_form" action="${pageContext.request.contextPath}/controller">
                    <div class="form-group">
                        <input class="form-control" id="rating_addition_text" name="rating" type="text"/>
                    </div>
                    <input type="hidden" name="command" value="change_rating"/>
                    <input type="hidden" name="account_id" value="${profile.accountId}"/>
                    <input class="btn btn-primary mt-2 ml-2" type="submit" value="<fmt:message key="profile.rating_form.submit"/>"/>
                </form>
            </div>
            <div id="rating_change_message"></div>
        </c:if>
        <c:if test="${not empty profile_action_notiifaction}">
            <fmt:message key="${profile_action_notiification}"/>
            <c:remove var="profile_action_notification" scope="session"/>
        </c:if>
    </div>
</body>
</html>
