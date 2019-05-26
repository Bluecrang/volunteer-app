<%@ page contentType="text/html;charset=UTF-8" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@include file="/WEB-INF/parts/header.jsp"%>
<html>
<head>
    <title><fmt:message key="main.title"/></title>
</head>
<body>
    <div class="container mt-3 col-sm-6 offset-sm-3">
        <h1 class="text-center"><fmt:message key="main.header"/></h1><br/>
        <h3 class="text-center"><fmt:message key="main.after_header"/></h3><br/>
        <div id="carousel" class="carousel slide" data-ride="carousel">
            <ol class="carousel-indicators">
                <li data-target="#carousel" data-slide-to="0" class="active"></li>
                <li data-target="#carousel" data-slide-to="1"></li>
                <li data-target="#carousel" data-slide-to="2"></li>
            </ol>
            <div class="carousel-inner">
                <div class="carousel-item active">
                    <img class="d-block w-100" src="${pageContext.request.contextPath}/images/seal.jpg" alt="Seal">
                    <div class="carousel-caption d-none d-md-block">
                        <h5><fmt:message key="main.seal_text"/></h5>
                    </div>
                </div>
                <div class="carousel-item">
                    <img class="d-block w-100" src="${pageContext.request.contextPath}/images/doge.jpg" alt="Dog">
                    <div class="carousel-caption d-none d-md-block">
                        <h5><fmt:message key="main.doge_text"/></h5>
                    </div>
                </div>
                <div class="carousel-item">
                    <img class="d-block w-100" src="${pageContext.request.contextPath}/images/parrot.jpg" alt="Parrot">
                    <div class="carousel-caption d-none d-md-block">
                        <h5><fmt:message key="main.parrot_text"/></h5>
                    </div>
                </div>
            </div>
            <a class="carousel-control-prev" href="#carousel" role="button" data-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" href="#carousel" role="button" data-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
        <br/>
        <div class="text-center">
            <fmt:message key="main.end"/>
        </div>
    </div>
</body>
</html>
