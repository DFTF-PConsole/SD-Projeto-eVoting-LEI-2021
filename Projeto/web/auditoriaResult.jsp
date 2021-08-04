<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Auditoria</title>
</head>
    <body>
        <h1>Auditoria - Eleição <s:property value="eleicao" /></h1><br>
        <p>Eleitor: <s:property value="id"/></p>
        <p>Local de Votação: <s:property value="mesaVoto"/></p>
        <p>Data de Votação: <s:property value="dataVoto"/></p><br>
        <a href="<s:url action="admin" />">Voltar ao menu principal</a>
    </body>
</html>