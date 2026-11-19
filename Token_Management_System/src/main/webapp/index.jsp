<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Welcome" scope="request"/>

<%-- the landing page has its own simple layout, no topbar --%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Token Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>

<div class="landing">
    <div class="logo">Token <span>Management</span> System</div>
    <p>
        Take a token from your phone or the front desk, watch the live display
        board, and walk up to the counter when your number is called.
    </p>

    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            <c:choose>
                <c:when test="${sessionScope.user.role == 'ADMIN'}">
                    <a class="btn" href="${pageContext.request.contextPath}/admin/home">Open Dashboard</a>
                </c:when>
                <c:otherwise>
                    <a class="btn" href="${pageContext.request.contextPath}/user/home">Go to My Tokens</a>
                </c:otherwise>
            </c:choose>
        </c:when>
        <c:otherwise>
            <a class="btn" href="${pageContext.request.contextPath}/login">Login</a>
            <a class="btn btn-grey" href="${pageContext.request.contextPath}/register">Create Account</a>
        </c:otherwise>
    </c:choose>

    <div style="margin-top:26px;">
        <a class="muted" href="${pageContext.request.contextPath}/board">View the waiting-area display board</a>
    </div>
</div>

<footer class="page">
    Token Management System &middot; Advanced Java project &middot; Apache Tomcat 9 + MySQL
</footer>

</body>
</html>
