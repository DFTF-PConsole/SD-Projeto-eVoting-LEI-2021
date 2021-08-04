<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Adicionar Mesa de Voto</title>
</head>
<body>
<h1>Adicionar Mesa de Voto - Eleição <s:property value="eleicao" /></h1>
<s:form action="mesaVotoSubmit" method="post">
    <s:hidden name="eleicao" />
    <s:textfield label="Mesa de Voto" key="mesaVoto"/><br>
    <s:submit type="button">
        <s:text name="Submeter" />
    </s:submit>
</s:form>
</body>
</html>