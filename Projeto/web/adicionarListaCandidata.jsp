<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Adicionar Lista Candidata</title>
</head>
    <body>
        <h1>Adicionar Lista Candidata na Eleição <s:property value="eleicao" /></h1>
        <s:form action="adicionarListaCandidataSubmit" method="post">
            <s:hidden name="eleicao" />
            <s:textfield label="Nome da Lista Candidata" key="nomeLista" />
            <s:select
                    label="Membros"
                    name="membros"
                    list="pessoas"
                    required="true"
                    multiple="true"
            /><br>
            <s:submit type="button">
                <s:text name="Adicionar" />
            </s:submit>
        </s:form>
    </body>
</html>