<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Admin Dashboard" scope="request"/>

<jsp:include page="../includes/header.jsp"/>

<h1>Dashboard</h1>
<div class="sub">Today, <fmt:formatDate value="<%= new java.util.Date() %>" pattern="EEEE, dd MMMM yyyy"/></div>

<jsp:include page="../includes/messages.jsp"/>

<div class="stats">
    <div class="stat"><div class="num">${issuedToday}</div><div class="lbl">tokens issued today</div></div>
    <div class="stat"><div class="num">${waitingNow}</div><div class="lbl">waiting right now</div></div>
    <div class="stat"><div class="num">${servedToday}</div><div class="lbl">served</div></div>
    <div class="stat"><div class="num">${goneToday}</div><div class="lbl">cancelled / no-show</div></div>
</div>

<h2>Counter Status</h2>
<div class="box" style="padding-bottom:6px;">
    <table class="list">
        <tr>
            <th>Counter</th><th>Operator</th><th>Service</th><th>Status</th>
            <th>Serving</th><th>Waiting</th>
        </tr>
        <c:forEach var="c" items="${counters}">
            <tr>
                <td><b>${c.name}</b></td>
                <td>${empty c.operatorName ? "-" : c.operatorName}</td>
                <td>${empty c.serviceName ? "-" : c.serviceName}</td>
                <td><span class="badge badge-${c.status}">${c.status}</span></td>
                <td><b>${empty c.currentTokenNo ? "--" : c.currentTokenNo}</b></td>
                <td>${c.waitingCount}</td>
            </tr>
        </c:forEach>
    </table>
    <p class="muted" style="margin:8px 2px 10px;">
        open / close counters and call tokens from the
        <a href="${pageContext.request.contextPath}/admin/counters">Counters</a> page.
    </p>
</div>

<h2>Today's Tokens <span class="muted">(latest ${todayTokens.size()})</span></h2>
<div class="box" style="padding-bottom:6px;">
    <c:choose>
        <c:when test="${empty todayTokens}">
            <p class="empty">No token has been issued yet today.</p>
        </c:when>
        <c:otherwise>
            <table class="list">
                <tr>
                    <th>Token</th><th>Customer</th><th>Service</th><th>Counter</th>
                    <th>Status</th><th>Issued</th>
                </tr>
                <c:forEach var="t" items="${todayTokens}">
                    <tr>
                        <td><b>${t.tokenNo}</b></td>
                        <td>${t.userName}</td>
                        <td>${t.serviceName}</td>
                        <td>${empty t.counterName ? "-" : t.counterName}</td>
                        <td><span class="badge badge-${t.status}">${t.status}</span></td>
                        <td class="nowrap"><fmt:formatDate value="${t.issuedAt}" pattern="hh:mm a"/></td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="../includes/footer.jsp"/>
