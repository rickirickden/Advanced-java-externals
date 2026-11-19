<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${pageTitle} | Token Management System</title>
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="stylesheet" href="${pageContext.request.contextPath}/style.css">
</head>
<body>

<div class="topbar">
    <div class="brand">Token <span>Management</span> System</div>
    <nav>
        <c:choose>
            <c:when test="${sessionScope.user.role == 'ADMIN'}">
                <a href="${pageContext.request.contextPath}/admin/home">Dashboard</a>
                <a href="${pageContext.request.contextPath}/admin/counters">Counters</a>
                <a href="${pageContext.request.contextPath}/admin/services">Services</a>
                <a href="${pageContext.request.contextPath}/board" target="_blank">Display Board</a>
            </c:when>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/user/home">My Tokens</a>
                <a href="${pageContext.request.contextPath}/user/new">Get Token</a>
                <a href="${pageContext.request.contextPath}/user/history">History</a>
                <a href="${pageContext.request.contextPath}/board" target="_blank">Display Board</a>
            </c:when>
        </c:choose>
        <c:choose>
            <c:when test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/logout">Logout (${sessionScope.user.username})</a>
            </c:when>
            <c:otherwise>
                <a href="${pageContext.request.contextPath}/login">Login</a>
                <a href="${pageContext.request.contextPath}/register">Register</a>
            </c:otherwise>
        </c:choose>
    </nav>
</div>

<div class="wrap">
