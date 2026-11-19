<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Counters" scope="request"/>

<jsp:include page="../includes/header.jsp"/>

<h1>Counters</h1>
<div class="sub">Open a counter, call the next token, then mark it served or no-show.</div>

<jsp:include page="../includes/messages.jsp"/>

<div class="box">
    <table class="list">
        <tr>
            <th>Counter</th>
            <th>Operator</th>
            <th>Service</th>
            <th>Status</th>
            <th>Now Serving</th>
            <th>Waiting</th>
            <th class="actions">Actions</th>
        </tr>
        <c:forEach var="c" items="${counters}">
            <tr>
                <td><b>${c.name}</b></td>
                <td>${empty c.operatorName ? "-" : c.operatorName}</td>
                <td>${empty c.serviceName ? "-" : c.serviceName}</td>
                <td><span class="badge badge-${c.status}">${c.status}</span></td>
                <td><b>${empty c.currentTokenNo ? "--" : c.currentTokenNo}</b></td>
                <td>${c.waitingCount}</td>
                <td class="actions">
                    <c:choose>
                        <c:when test="${c.status == 'OPEN'}">
                            <form class="inline" method="post">
                                <input type="hidden" name="action" value="close">
                                <input type="hidden" name="counter_id" value="${c.counterId}">
                                <button class="btn btn-grey btn-sm" type="submit">Close</button>
                            </form>
                        </c:when>
                        <c:otherwise>
                            <form class="inline" method="post">
                                <input type="hidden" name="action" value="open">
                                <input type="hidden" name="counter_id" value="${c.counterId}">
                                <button class="btn btn-green btn-sm" type="submit">Open</button>
                            </form>
                        </c:otherwise>
                    </c:choose>

                    <form class="inline" method="post">
                        <input type="hidden" name="action" value="call">
                        <input type="hidden" name="counter_id" value="${c.counterId}">
                        <button class="btn btn-sm" type="submit">Call Next</button>
                    </form>

                    <c:if test="${not empty c.currentTokenNo}">
                        <form class="inline" method="post">
                            <input type="hidden" name="action" value="served">
                            <input type="hidden" name="counter_id" value="${c.counterId}">
                            <button class="btn btn-green btn-sm" type="submit">Served</button>
                        </form>
                        <form class="inline" method="post"
                              onsubmit="return confirm('Mark ${c.currentTokenNo} as no-show?');">
                            <input type="hidden" name="action" value="noshow">
                            <input type="hidden" name="counter_id" value="${c.counterId}">
                            <button class="btn btn-grey btn-sm" type="submit">No Show</button>
                        </form>
                    </c:if>

                    <form class="inline" method="post"
                          onsubmit="return confirm('Remove ${c.name}?');">
                        <input type="hidden" name="action" value="remove">
                        <input type="hidden" name="counter_id" value="${c.counterId}">
                        <button class="btn btn-red btn-sm" type="submit">Remove</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</div>

<h2>Add a Counter</h2>
<div class="box">
    <form class="plain" method="post">
        <input type="hidden" name="action" value="add">
        <div class="row">
            <div class="col">
                <label>Counter Name *</label>
                <input type="text" name="name" placeholder="e.g. Counter 5" maxlength="30">
            </div>
            <div class="col">
                <label>Operator Name</label>
                <input type="text" name="operator" placeholder="staff handling this desk" maxlength="50">
            </div>
            <div class="col">
                <label>Service Handled</label>
                <select name="service_id">
                    <option value="0">- none yet -</option>
                    <c:forEach var="s" items="${services}">
                        <option value="${s.serviceId}">${s.name}</option>
                    </c:forEach>
                </select>
            </div>
        </div>
        <div style="margin-top:14px;">
            <button type="submit" class="btn">Add Counter</button>
        </div>
    </form>
</div>

<jsp:include page="../includes/footer.jsp"/>
