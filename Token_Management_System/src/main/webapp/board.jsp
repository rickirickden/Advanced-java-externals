<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Now Serving | Token Management System</title>
<%-- the waiting-room screen reloads itself every 10 seconds --%>
<meta http-equiv="refresh" content="10">
<link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body class="board">

<div class="board-head">
    <div class="t">NOW SERVING</div>
    <div class="s">please watch the board and go to the counter when your number appears</div>
</div>

<div class="board-grid">
    <c:forEach var="c" items="${counters}">
        <c:if test="${c.status == 'OPEN'}">
            <div class="board-cell">
                <div class="cname">${c.name}</div>
                <div class="cserve">${c.serviceName}</div>
                <c:choose>
                    <c:when test="${not empty c.currentTokenNo}">
                        <div class="num">${c.currentTokenNo}</div>
                        <div class="since">${c.waitingCount} token(s) waiting</div>
                    </c:when>
                    <c:otherwise>
                        <div class="num none">--</div>
                        <div class="since">${c.waitingCount} token(s) waiting</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </c:if>
    </c:forEach>

    <c:if test="${empty counters}">
        <div class="board-cell"><div class="num none">--</div></div>
    </c:if>
</div>

<div class="board-foot">
    screen refreshes automatically every 10 seconds
</div>

</body>
</html>
