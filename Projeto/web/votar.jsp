<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>E-Voting - Votar</title>
    </head>
    <body>
        <h1>Bem vindo, ${param.username}</h1> <!-- Bem vindo, <nome do utilizador> -->

        <s:form action="votarAction" namespace="/" method="post">

            <s:div style="display: none;">
                <s:hidden name="username">${param.username}</s:hidden>
            </s:div>

            <s:doubleselect label="Votar"
                            name="selectedEleicao" list="lista.keySet()"
                            doubleName="selectedLista" doubleList="lista.get(top)" />

            <s:submit type="button">
                <s:text name="Votar" />
            </s:submit>

        </s:form><br>

        <a href="<s:url action="menuUser" />">Voltar</a>

    </body>
</html>