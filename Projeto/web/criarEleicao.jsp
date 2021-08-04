<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Criar Eleição</title>
</head>
    <body>
        <h1>Criar Eleição</h1>
        <s:form action="criarEleicao2" method="post">
            <s:textfield label="Título" key="titulo"/><br>
            <s:textfield label="Data de Início" key="inicio"/><br>
            <s:textfield label="Data de Fim" key="fim"/><br>
            <s:textarea label="Descrição" name="descricao"/><br>
            <s:checkboxlist label="Eleitores" name="eleitor" list="eleitores"/>	<!-- Lista de checkboxes -->
            <s:submit type="button">
                <s:text name="Criar" />
            </s:submit>
        </s:form><br>
        <a href="<s:url action="admin" />">Voltar ao menu principal</a>
    </body>
</html>