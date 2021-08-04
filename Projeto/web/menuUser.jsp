<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting</title>
</head>
    <body>
        <h1>Bem vindo, <s:property value="username" /></h1><br>
        <a href="<s:url action="selectVotar"><s:param name="username" value="%{username}" /></s:url>">Votar</a><br><br>
        <a href="<s:url action="index" />">Loggout</a>
    </body>
</html>