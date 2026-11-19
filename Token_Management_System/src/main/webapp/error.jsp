<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page isErrorPage="true" %>
<c:set var="pageTitle" value="Error" scope="request"/>

<jsp:include page="includes/header.jsp"/>

<h1>Something went wrong</h1>

<div class="box" style="margin-top:14px;">
    <p>The page could not be opened. The usual reasons in this project are:</p>
    <ul style="margin:10px 0 10px 22px; line-height:1.7;">
        <li>MySQL is not running, or the database <b>token_db</b> has not been created yet
            (run <b>database.sql</b> first).</li>
        <li>The login details in
            <b>src/main/java/com/tms/util/DBConnection.java</b>
            do not match your MySQL setup.</li>
        <li>You opened a page that does not exist.</li>
    </ul>
    <p style="margin-top:12px;">
        <a class="btn" href="${pageContext.request.contextPath}/">Back to Home</a>
    </p>
</div>

<jsp:include page="includes/footer.jsp"/>
