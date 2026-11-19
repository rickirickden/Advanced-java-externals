<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- green/red banner shown after a redirect - the servlets pass msg / err --%>
<c:if test="${not empty param.msg}">
    <div class="msg">${param.msg}</div>
</c:if>
<c:if test="${not empty param.err}">
    <div class="err">${param.err}</div>
</c:if>
