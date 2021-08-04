<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>E-Voting - Login</title>
    </head>
    <body>
        <h1>E-Voting - Login</h1>
        <s:form action="login" method="post">
            <s:textfield label="Número Cartão de Cidadão" key="username"/><br>
            <s:password label="Password" key="password"/><br>
            <s:submit type="button">
                <s:text name="Login" />
            </s:submit>
        </s:form>
    </body>
</html>