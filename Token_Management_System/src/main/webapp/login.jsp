<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Login" scope="request"/>

<jsp:include page="includes/header.jsp"/>

<h1>Login</h1>
<div class="sub">Use your account to take a token or manage the counters.</div>

<jsp:include page="includes/messages.jsp"/>

<div class="box auth-box">
    <form class="plain" method="post" action="${pageContext.request.contextPath}/login">
        <label for="username">Username *</label>
        <input type="text" id="username" name="username" autofocus>

        <label for="password">Password *</label>
        <input type="password" id="password" name="password">

        <div style="margin-top:16px;">
            <button type="submit" class="btn">Login</button>
            <a class="muted" style="margin-left:12px;"
               href="${pageContext.request.contextPath}/register">need an account?</a>
        </div>
    </form>
</div>

<div class="box auth-box muted">
    Demo accounts &nbsp;&middot;&nbsp; admin / admin123 &nbsp;&middot;&nbsp; user / user123
</div>

<jsp:include page="includes/footer.jsp"/>
