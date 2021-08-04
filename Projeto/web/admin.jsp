<%@ taglib prefix="s" uri="/struts-tags"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>E-Voting - Administração</title>
    </head>
    <body>
        <h1>Consola de Administração</h1>
        <a href="<s:url action="registarPessoa" />">Registar um novo eleitor na Plataforma</a><br><br>
        <a href="<s:url action="criarEleicao" />">Criar uma nova Eleição</a><br><br>
        <a href="<s:url action="editarEleicao" includeParams="get"><s:param name="id" value="%{'editar'}" /></s:url>">Editar detalhes de uma Eleição</a><br><br>
        <a href="<s:url action="consultarEleicao" includeParams="get"><s:param name="id" value="%{'consultar'}" /></s:url>">Consultar os detalhes de uma Eleição</a><br><br>
        <a href="<s:url action="adicionarMesaVoto" includeParams="get"><s:param name="id" value="%{'mesa'}" /></s:url>">Adicionar mesa de voto a uma Eleição</a><br><br>
        <a href="<s:url action="adicionarListaCandidata" includeParams="get"><s:param name="id" value="%{'lista'}" /></s:url>">Adicionar lista candidata a uma Eleição</a><br><br>
        <a href="<s:url action="auditoria" includeParams="get"><s:param name="id" value="%{'audit'}" /></s:url>">Verificar em momento e em que mesa de voto votou cada eleitor</a><br><br><br><br>
        <a href="<s:url action="index" />">Loggout</a> <!-- fazer logout -->
    </body>
</html>