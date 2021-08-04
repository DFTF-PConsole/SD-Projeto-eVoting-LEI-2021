<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>E-Voting - Registar Eleitor</title>
</head>
    <body>
        <h1>Registar Eleitor</h1>
        <s:form action="registar" namespace="/" method="post">
            <s:select
                    label="Pessoa"
                    name="pessoa"
                    list="eleitores"
                    required="true"
            /><br>
            <s:textfield label="Nome" key="name"/><br>
            <s:textfield label="Contacto" key="phone"/><br>
            <s:textfield label="Morada" key="address"/><br>
            <s:textfield label="Código-Postal" name="postalCode"/><br>
            <s:textfield label="Número de Cartão de Cidadão" key="id"/><br>
            <s:textfield label="Validade do Cartão de Cidadão" key="dateId"/><br>
            <s:textfield label="Faculdade" key="faculdade"/><br>
            <s:textfield label="Departamento" key="departamento"/><br>
            <s:textfield label="Número Mecanográfico" key="mec"/><br>
            <s:textfield label="Número de Estudante" key="student"/><br>
            <s:password label="Password" key="password"/><br>
            <s:submit type="button">
                <s:text name="Registar" />
            </s:submit>
        </s:form><br>
        <a href="<s:url action="admin" />">Voltar ao menu principal</a>
    </body>
</html>