# SD Projeto eVoting LEI 2021

PUBLIC

FCTUC DEI/LEI 2020/2021 - Licenciatura em Engenharia Informática

SD - Sistemas Distribuidos



## Projeto

* eVoting: Voto Eletrónico na UC



## Projeto criado com:

SDK: openjdk-16 version 16



## Compilação:

* javac ServerRMI/RMIServer.java
* javac AdminConsole/AdminConsole.java
* javac MulticastServer/MesaVoto.java
* javac VotingTerminal/TerminalVoto.java
* jar -cvf evoting.war *



## Execução (exemplo):

* java -jar ./RMIServer.jar 127.0.0.1 2000 server
* java -jar ./AdminConsole.jar 127.0.0.1 2000 server
* java -jar ./MesaVoto.jar 224.0.224.0 9876 127.0.0.1 2000 server DEI eleicao1est
* java -jar ./TerminalVoto.jar 224.0.224.0 9876

(Abrir .war file num containar, i. e., Tomcat)



## Argumentos transmitidos no arranque da execução:

* ./RMIServer  <ip_rmi>  <porto_rmi>  <nome_rmi>
* ./AdminConsole  <ip_rmi>  <porto_rmi>  <nome_rmi>
* ./MesaVoto  <ip_multicast>  <porto_multicast>  <ip_rmi>  <porto_rmi>  <nome_rmi>  <nome_local_dep>  <id_titulo_eleicao>
* ./TerminalVoto  <ip_multicast>  <porto_multicast>



## Sintaxe dos comandos para a inserção de dados através do AdminConsole:

* add ; estudante ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password ; numEstudante
* add ; docente ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password ; numMec
* add ; funcionario ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password
* add ; eleicao ; titulo ; descricao ; diaInicio ; mesInicio ; anoInicio ; horaInicio ; minutosInicio ; diaFim ; mesFim ; anoFim ; horaFim ; minutosFim ; estudantes ; docentes ; funcionarios
* add ; lista ; nomeEleicao ; nomeLista ; membrosNumCC ; ...
* add ; mesavoto ; nomeEleicao ; faculdadeOuDepartamento
* remove ; mesavoto ; nomeEleicao ; faculdadeOuDepartamento
* localeleitor ; nomeEleicao ; numCC
* update ; eleicao ; titulo ; nomeEleicao ; novoTitulo
* update ; eleicao ; datafim ; nomeEleicao ; diaFim ; mesFim ; anoFim ; horaFim ; minutoFim
* update ; eleicao ; datainicio ; nomeEleicao ; diaInicio ; mesInicio ; anoInicio ; horaInicio ; minutoInicio
* update ; eleicao ; descricao ; nomeEleicao ; novaDescricao
* show
* resultadosanteriores



## Notas:

Usar os comandos do ficheiro "EXEMPLO - CMDs Dados Admin Console.txt" na pasta "Testes" para a inserção de dados na aplicação através do AdminConsole.
Ou type 'help' no AdminConsole.



## Autores:
* Cláudia Campos, Nº 2018285941 (TaurusLegend)
* Dário Félix, Nº 2018275530 / DFTF@PConsole# (DFTF-PConsole)

