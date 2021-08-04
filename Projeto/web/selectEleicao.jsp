<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Selecionar Eleição</title>
</head>
    <body>
        <h1>Selecionar Eleição</h1>
        <s:form action="search" namespace="/" method="post">
            <s:div style="display: none;">
                <s:hidden style="display: none;" key="id">${param.id}</s:hidden>
            </s:div>
            <s:select
                    label="Eleição"
                    name="eleicao"
                    list="eleicoes"
                    required="true"
            /><br>
            <s:submit type="button">
                <s:text name="Selecionar" />
            </s:submit>
        </s:form><br>
        <a href="<s:url action="admin" />">Cancelar</a>
    </body>
</html>