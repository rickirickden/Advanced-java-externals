<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Services" scope="request"/>

<jsp:include page="../includes/header.jsp"/>

<h1>Services</h1>
<div class="sub">
    Each service has its own token series (the letter in A-001) and its own queue.
    Disabled services disappear from the "get token" page.
</div>

<jsp:include page="../includes/messages.jsp"/>

<div class="box" style="padding-bottom:6px;">
    <table class="list">
        <tr>
            <th>Service</th>
            <th>Series</th>
            <th>Avg. Minutes</th>
            <th>Status</th>
            <th class="actions">Action</th>
        </tr>
        <c:forEach var="s" items="${services}">
            <tr>
                <td><b>${s.name}</b></td>
                <td>${s.code}-001 onwards</td>
                <td>${s.avgMinutes}</td>
                <td>
                    <c:choose>
                        <c:when test="${s.active}"><span class="badge badge-OPEN">ACTIVE</span></c:when>
                        <c:otherwise><span class="badge badge-CLOSED">DISABLED</span></c:otherwise>
                    </c:choose>
                </td>
                <td class="actions">
                    <form class="inline" method="post">
                        <input type="hidden" name="service_id" value="${s.serviceId}">
                        <c:choose>
                            <c:when test="${s.active}">
                                <input type="hidden" name="action" value="disable">
                                <button class="btn btn-grey btn-sm" type="submit">Disable</button>
                            </c:when>
                            <c:otherwise>
                                <input type="hidden" name="action" value="enable">
                                <button class="btn btn-green btn-sm" type="submit">Enable</button>
                            </c:otherwise>
                        </c:choose>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<h2>Add a Service</h2>
<div class="box">
    <form class="plain" method="post">
        <input type="hidden" name="action" value="add">
        <div class="row">
            <div class="col">
                <label>Service Name *</label>
                <input type="text" name="name" placeholder="e.g. Cheque Deposit" maxlength="50">
            </div>
            <div class="col">
                <label>Series Letter *</label>
                <input type="text" name="code" placeholder="single letter, e.g. E" maxlength="1">
            </div>
            <div class="col">
                <label>Avg. Minutes per Token</label>
                <input type="number" name="avg_minutes" value="5" min="1" max="120">
            </div>
        </div>
        <div style="margin-top:14px;">
            <button type="submit" class="btn">Add Service</button>
        </div>
    </form>
</div>

<jsp:include page="../includes/footer.jsp"/>
