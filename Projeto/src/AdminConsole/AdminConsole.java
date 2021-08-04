/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package AdminConsole;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.*;
import java.rmi.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;
import Outros.*;
import ServerRMI.*;


/**
 * Permitem realizar as funcionalidades 1–4 e 9–18
 *
 * @see ServerRMI
 * @see RMIInterfaceClient
 * @see ProjGeral
 * @see UnicastRemoteObject
 * @see AdminConsoleInterface
 * @author Dario Felix
 * @version 1.0
 */
public class AdminConsole extends UnicastRemoteObject implements ProjGeral, RMIInterfaceClient {
    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    private final static boolean DEBUG_ATIVO = false;   // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    private final static boolean INFO_ERRO_ATIVO = false;   // Default: false

    /**
     * Flag que indica se o INFO_RELEVANTE_ATIVO esta ativo. Indica se deve imprimir no stdout mensagens relevantes do fluxo normal do programa.
     *
     * @see ProjGeral#printAviso(String)
     */
    private final static boolean INFO_RELEVANTE_ATIVO = true;   // Default: true

    /**
     * Separador de campos na introdução de inputs/comandos.
     */
    String SEPARADOR_CAMPOS = ";";

    /**
     * Constante que indica o numero de argumentos necessarios para o metodo main
     *
     * @see #main(String[])
     */
    private final static int N_ARGS = 3;

    /**
     * IP RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: 30.20.10.0
     *
     * @see RMIServer
     * @see RMIInterfaceClient
     * @see AdminConsoleInterface
     */
    private final String ipRMI;

    /**
     * Porto RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: 6789
     *
     * @see RMIServer
     * @see RMIInterfaceClient
     * @see AdminConsoleInterface
     */
    private final int portoRMI;

    /**
     * Nome RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: xpto
     *
     * @see RMIServer
     * @see RMIInterfaceClient
     * @see AdminConsoleInterface
     */
    private final String nomeRMI;

    /**
     * Objeto remoto do Server RMI
     *
     * @see RMIServer
     * @see AdminConsoleInterface
     */
    private AdminConsoleInterface clienteRemotoRMI;


    /**
     * Construtor. Set o objeto remoto. Add esta consola ao Server RMI.
     *
     * @param ipRMI Ip do RMI Server
     * @param nomeRMI Nome do RMI Server
     * @param portoRMI Porto do RMI Server
     * @throws RemoteException Em caso de erro de conexão ao Server RMI
     * @throws NotBoundException Em caso de erro de conexão ao Server RMI
     * @author Dario Felix
     * @version 1.0
     */
    public AdminConsole(String ipRMI, int portoRMI, String nomeRMI) throws RemoteException, NotBoundException {
        super();
        this.ipRMI = ipRMI;
        this.portoRMI = portoRMI;
        this.nomeRMI = nomeRMI;
        this.clienteRemotoRMI = null;

        this.setClienteRemotoRMI();
        this.clienteRemotoRMI.addAdminConsole(this);
    }


    /**
     * Antes de usar a AdminConsole, obter o objeto remoto.
     *
     * @throws RemoteException Em caso de erro de conexão ao Server RMI
     * @throws NotBoundException Em caso de erro de conexão ao Server RMI
     * @see RMIServer
     * @see AdminConsole#clienteRemotoRMI
     * @see LocateRegistry
     * @see AdminConsoleInterface
     * @author Dario Felix
     * @version 1.0
     */
    private void setClienteRemotoRMI() throws RemoteException, NotBoundException {
        this.clienteRemotoRMI = (AdminConsoleInterface) LocateRegistry.getRegistry(this.ipRMI, this.portoRMI).lookup(this.nomeRMI);
    }


    /**
     * Tenta reconectar/recuperar o objeto remoto em caso de falha no Server RMI.
     * Se não conseguir reconectar em TEMPO_AVARIA_TEMPORARIA_MS ms emite um erro explicito.
     *
     * @return ERRO (erro grave) ou SUCESSO
     * @see ProjGeral#TEMPO_AVARIA_TEMPORARIA_MS
     * @see RMIServer
     * @see AdminConsoleInterface
     * @author Dario Felix
     * @version 1.0
     */
    private synchronized boolean reconnectAndSetClienteRemotoRMI() {
        boolean fator;
        Instant fim;
        Duration duracao;

        Instant inicio = Instant.now();
        fator = true;
        while(fator) {
            try {
                this.clienteRemotoRMI = (AdminConsoleInterface) LocateRegistry.getRegistry(this.ipRMI, this.portoRMI).lookup(this.nomeRMI);
                this.clienteRemotoRMI.addAdminConsole(this);
                fator = false;
            } catch (Exception e) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("AdminConsole", "ReconnectAndSetClienteRemotoRMI", "Exception");
                fim = Instant.now();
                duracao = Duration.between(inicio, fim);
                if(duracao.toMillis() > TEMPO_AVARIA_TEMPORARIA_MS) {
                    if (INFO_RELEVANTE_ATIVO)
                        ProjGeral.printAviso("duracao.toMillis() > TEMPO_AVARIA_TEMPORARIA_MS");
                    return ERRO;
                } else {
                    try {
                        this.wait(SLEEP_WAIT_MS);
                    } catch (InterruptedException e2) {
                        if (DEBUG_ATIVO)
                            ProjGeral.printDebug("AdminConsole", "ReconnectAndSetClienteRemotoRMI", "InterruptedException");
                    }
                }
            }
        }
        return SUCESSO;
    }


    /**
     * Verifica os dados dos args e coloca operacional o Admin Console
     * <p> Exemplo argumentos: 224.0.224.0 9876 xpto
     *
     * @param args IP_RMI  PORTO_RMI  NOME_RMI
     * @see RMIServer
     * @see AdminConsoleInterface
     * @see RMIInterfaceClient
     * @author Dario Felix
     * @version 1.0
     */
    public static void main(String[] args){
        String ipRMI;
        int portoRMI;
        String nomeRMI;
        AdminConsole adminConsole;

        if (args.length != N_ARGS) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "main", "numero de args diferente de " + N_ARGS);
            ProjGeral.printErro("AdminConsole", "main", "./AdminConsole <IP_RMI> <PORTO_RMI> <NOME_RMI>");
            return;
        }

        ipRMI = args[0];

        try {
            portoRMI = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "main", "porto é um inteiro", e);
            ProjGeral.printErro("AdminConsole", "main", "./AdminConsole <IP_RMI> <PORTO_RMI> <NOME_RMI>");
            return;
        }

        nomeRMI = args[2];

        try {
            adminConsole = new AdminConsole(ipRMI, portoRMI, nomeRMI);
        } catch (RemoteException | NotBoundException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "main", "RemoteException ao criar o objeto adminConsole", e);
            return;
        }

        adminConsole.executa();
    }


    /**
     * Método principal do objeto. É executado em loop. Chamar depois de instaciado.
     * Le os comandos do stdin e executa-os.
     *
     * @see AdminConsole#getNextLineFromInputStdinEspecial()
     * @author Dario Felix
     * @version 1.0
     */
    public void executa() {
        String input;
        ArrayList<String> lista;
        int size;
        String msg;

        System.out.print("\n *** ADMIN CONSOLE ATIVO *** \n\n");

        while (true) {
            input = "";

            while (input.isBlank()) {
                System.out.print("> ");
                input = getNextLineFromInputStdinEspecial();
            }

            lista = this.manipulaInput(input);
            if (lista == null || lista.size() < 1) {
                this.msgErroInput("[ADMIN CONSOLE]", "input nulo ou com poucos campos");
                continue;
            }
            size = lista.size();

            switch (lista.get(0)) {
                case "add":
                    if (size <= 2) {
                        this.msgErroInput("[ADMIN CONSOLE]", "cmd 'add' com poucos campos");
                        break;
                    } else {
                        lista.set(1, lista.get(1).toLowerCase().replace(" ", ""));
                    }

                    switch (lista.get(1)) {
                        case "estudante":
                            if (this.registarEstudante(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar registarEstudante()");
                            }

                            break;
                        case "docente":
                            if (this.registarDocente(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar registarDocente()");
                            }

                            break;
                        case "funcionario":
                            if (this.registarFuncionario(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar registarFuncionario()");
                            }

                            break;
                        case "eleicao":
                            if (this.criarEleicao(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar criarEleicao()");
                            }

                            break;
                        case "lista":
                            if (this.adicionarListaAEleicao(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar adicionarListaAEleicao()");
                            }

                            break;
                        case "mesavoto":
                            if (this.adicionarMesaVoto(lista, size, 2) == ERRO) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar adicionarMesaVoto()");
                            }

                            break;
                        default:
                            this.msgErroInput("[ADMIN CONSOLE]", "segundo campo desconhecido");
                            break;
                    }

                    break;
                case "update":
                    if (size <= 3) {
                        this.msgErroInput("[ADMIN CONSOLE]", "cmd 'update' com poucos campos");
                        break;
                    } else {
                        lista.set(1, lista.get(1).toLowerCase().replace(" ", ""));
                        lista.set(2, lista.get(2).toLowerCase().replace(" ", ""));
                    }

                    switch (lista.get(1)) {
                        case "eleicao":
                            switch (lista.get(2)) {
                                case "titulo":
                                    if (this.alterarTituloEleicao(lista, size, 3) == ERRO) {
                                        if (INFO_ERRO_ATIVO)
                                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar alterarTituloEleicao()");
                                    }

                                    break;
                                case "datafim":
                                    if (this.alterarDataFimEleicao(lista, size, 3) == ERRO) {
                                        if (INFO_ERRO_ATIVO)
                                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar alterarDataFimEleicao()");
                                    }

                                    break;
                                case "datainicio":
                                    if (this.alterarDataInicioEleicao(lista, size, 3) == ERRO) {
                                        if (INFO_ERRO_ATIVO)
                                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar alterarDataInicioEleicao()");
                                    }

                                    break;
                                case "descricao":
                                    if (this.alterarDescricaoEleicao(lista, size, 3) == ERRO) {
                                        if (INFO_ERRO_ATIVO)
                                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar alterarDescricaoEleicao()");
                                    }

                                    break;
                                default:
                                    this.msgErroInput("[ADMIN CONSOLE]", "terceiro campo desconhecido");
                                    break;
                            }

                            break;
                        default:
                            this.msgErroInput("[ADMIN CONSOLE]", "segundo campo desconhecido");
                            break;
                    }

                    break;
                case "remove":
                    if (size <= 2) {
                        this.msgErroInput("[ADMIN CONSOLE]", "cmd 'remove' com poucos campos");
                        break;
                    } else {
                        lista.set(1, lista.get(1).toLowerCase().replace(" ", ""));
                    }

                    if ("mesavoto".equals(lista.get(1))) {
                        if (this.removeMesaVoto(lista, size, 2) == ERRO) {
                            if (INFO_ERRO_ATIVO)
                                ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar removeMesaVoto()");
                        }
                    } else {
                        this.msgErroInput("[ADMIN CONSOLE]", "segundo campo desconhecido");
                    }

                    break;
                case "resultadosanteriores":
                case "show":
                    if (this.imprimeResultadosEleicoesAnteriores() == ERRO) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar imprimeResultadosEleicoesAnteriores()");
                    }

                    break;
                case "localeleitor":
                    if (this.obterLocalVotoEleitor(lista, size, 1) == ERRO) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("AdminConsole", "executa", "Erro grave ao executar obterLocalVotoEleitor()");
                    }

                    break;
                case "help":
                    msg = "COMANDOS (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t 'help' ... \n");
                    msg = msg.concat("\t 'add' " + SEPARADOR_CAMPOS + " ['estudante', 'docente', 'funcionario', 'eleicao', 'lista', 'mesavoto'] ... \n");
                    msg = msg.concat("\t 'remove' " + SEPARADOR_CAMPOS + " ['mesavoto'] ... \n");
                    msg = msg.concat("\t 'update' " + SEPARADOR_CAMPOS + " ['eleicao'] ... \n");
                    msg = msg.concat("\t 'resultadosanteriores' \n");
                    msg = msg.concat("\t 'localeleitor' ... \n");
                    System.out.println(msg);

                    break;
                case "helpupdateeleicao":
                    msg = "COMANDOS (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t update " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + "  ['titulo', 'datafim', 'datainicio', 'descricao'] ... \n");
                    System.out.println(msg);

                    break;
                case "helpaddestudante":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " estudante " + SEPARADOR_CAMPOS + " String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numEstudante \n");
                    System.out.println(msg);

                    break;
                case "helpadddocente":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " docente " + SEPARADOR_CAMPOS + " String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numMec \n");
                    System.out.println(msg);

                    break;
                case "helpaddfuncionario":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " funcionario " + SEPARADOR_CAMPOS + " String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password \n");
                    System.out.println(msg);

                    break;
                case "helpaddeleicao":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + " String titulo, String descricao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutosInicio, int diaFim, int mesFim, int anoFim, int horaFim, int minutosFim, boolean estudantes, boolean docentes, boolean funcionarios \n");
                    System.out.println(msg);

                    break;
                case "helpaddlista":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " lista " + SEPARADOR_CAMPOS + " String nomeEleicao, String nomeLista, CopyOnWriteArrayList<String> membrosNumCC \n");
                    System.out.println(msg);

                    break;
                case "helpaddmesavoto":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t add " + SEPARADOR_CAMPOS + " mesavoto " + SEPARADOR_CAMPOS + " String nomeEleicao, String faculdadeOuDepartamento \n");
                    System.out.println(msg);

                    break;
                case "helpremovemesavoto":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t remove " + SEPARADOR_CAMPOS + " mesavoto " + SEPARADOR_CAMPOS + " String nomeEleicao, String faculdadeOuDepartamento \n");
                    System.out.println(msg);

                    break;
                case "helplocaleleitor":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t localeleitor " + SEPARADOR_CAMPOS + " String nomeEleicao, String numCC \n");
                    System.out.println(msg);

                    break;
                case "helpupdateeleicaotitulo":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t update " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + " titulo " + SEPARADOR_CAMPOS + " String nomeEleicao, String novoTitulo \n");
                    System.out.println(msg);

                    break;
                case "helpupdateeleicaodatafim":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t update " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + " datafim " + SEPARADOR_CAMPOS + " String nomeEleicao, int diaFim, int mesFim, int anoFim, int horaFim, int minutoFim \n");
                    System.out.println(msg);

                    break;
                case "helpupdateeleicaodatainicio":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t update " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + " datainicio " + SEPARADOR_CAMPOS + " String nomeEleicao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutoInicio \n");
                    System.out.println(msg);

                    break;
                case "helpupdateeleicaodescricao":
                    msg = "COMANDO (campos separados por \"" + SEPARADOR_CAMPOS + "\"):\n";
                    msg = msg.concat("\t update " + SEPARADOR_CAMPOS + " eleicao " + SEPARADOR_CAMPOS + " descricao " + SEPARADOR_CAMPOS + " String nomeEleicao, String novaDescricao \n");
                    System.out.println(msg);

                    break;
                default:
                    this.msgErroInput("[ADMIN CONSOLE]", "primeiro campo desconhecido");
                    break;
            }
        }
    }


    /**
     * Metodo utilizado pelo RMI Server para imprimir uma mensagem em todas as AdminConsole através do mecanismo callback
     *
     * @param message Mensagem a imprimir pelo RMI Server no AdminConsole
     * @see RMIInterfaceClient
     * @author Dario Felix
     * @version 1.0
     */
    @Override
    public void printOnAdminConsole(String message) {
        System.out.println("[SERVER RMI CALLBACK] " + message);
    }


    /**
     * Manipula a linha: faz split com a ajuda do SEPARADOR_CAMPOS.
     * Faz strip() a todos os campos campos e faz toLowerCase() e replace(" ", "") ao primeiro campo.
     *
     * @param input Linha que veio do stdin.
     * @return Lista de campos que constavam nessa linha
     * @author Dario Felix
     * @version 1.0
     */
    private ArrayList<String> manipulaInput(String input) {
        int i;
        String campo;

        if (input == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "manipulaInput", "input == null");
            return null;
        }

        ArrayList<String> lista = new ArrayList<>(Arrays.asList(input.split(SEPARADOR_CAMPOS)));

        for (i = 0 ; i < lista.size(); i++) {
            campo = lista.get(i).strip();

            if (i == 0)
                campo = campo.toLowerCase().replace(" ", "");

            lista.set(i, campo);
        }
        return lista;
    }


    /**
     * Imprime uma mensagem de erro (em resposta aos comandos inseridos).
     * <p> Exemplo 1: [SERVER RMI] "msg"
     * <p> Exemplo 2: [ADMIN CONSOLE] "msg"
     *
     * @param quem Indica quem produziu a mensagem
     * @param msg Mensagem a imprimir
     * @author Dario Felix
     * @version 1.0
     */
    private void msgErroInput(String quem, String msg) {
        if (msg == null || msg.isBlank()) {
            msg = "---";
        }
        System.out.printf("%s Erro no CMD! Tente novamente! (Msg: \"%s\")\n", quem, msg);
    }


    /**
     * Cria uma eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; eleicao ; titulo ; descricao ; diaInicio ; mesInicio ; anoInicio ; horaInicio ; minutosInicio ; diaFim ; mesFim ; anoFim ; horaFim ; minutosFim ; estudantes ; docentes ; funcionarios
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean criarEleicao(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 15 + start;
        String titulo;
        String descricao;
        int diaInicio;
        int mesInicio;
        int anoInicio;
        int horaInicio;
        int minutosInicio;
        int diaFim;
        int mesFim;
        int anoFim;
        int horaFim;
        int minutosFim;
        boolean estudantes;
        boolean docentes;
        boolean funcionarios;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "criarEleicao: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        titulo = lista.get(start);
        descricao = lista.get(start + 1);

        try {
            diaInicio = Integer.parseInt(lista.get(start + 2));
            mesInicio = Integer.parseInt(lista.get(start + 3));
            anoInicio = Integer.parseInt(lista.get(start + 4));
            horaInicio = Integer.parseInt(lista.get(start + 5));
            minutosInicio = Integer.parseInt(lista.get(start + 6));
            diaFim = Integer.parseInt(lista.get(start + 7));
            mesFim = Integer.parseInt(lista.get(start + 8));
            anoFim = Integer.parseInt(lista.get(start + 9));
            horaFim = Integer.parseInt(lista.get(start + 10));
            minutosFim = Integer.parseInt(lista.get(start + 11));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "criarEleicao: parseInt");
            return SUCESSO;
        }

        try {
            estudantes = Boolean.parseBoolean(lista.get(start + 12));
            docentes = Boolean.parseBoolean(lista.get(start + 13));
            funcionarios = Boolean.parseBoolean(lista.get(start + 14));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "criarEleicao: parseBoolean");
            return SUCESSO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.criarEleicao(titulo, descricao, diaInicio, mesInicio, anoInicio, horaInicio, minutosInicio, diaFim, mesFim, anoFim, horaFim, minutosFim, estudantes, docentes, funcionarios);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "criarEleicao", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Regista um estudante em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; estudante ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password ; numEstudante
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean registarEstudante(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 12 + start;
        String nome;
        String contacto;
        String morada;
        String codigoPostal;
        String numCC;
        int validadeDia;
        int validadeMes;
        int validadeAno;
        String faculdade;
        String departamento;
        String password;
        String numEstudante;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarEstudante: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nome = lista.get(start);
        contacto = lista.get(start + 1);
        morada = lista.get(start + 2);
        codigoPostal = lista.get(start + 3);
        numCC = lista.get(start + 4);

        try {
            validadeDia = Integer.parseInt(lista.get(start + 5));
            validadeMes = Integer.parseInt(lista.get(start + 6));
            validadeAno = Integer.parseInt(lista.get(start + 7));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarEstudante: parseInt");
            return SUCESSO;
        }

        faculdade = lista.get(start + 8);
        departamento = lista.get(start + 9);
        password = lista.get(start + 10);
        numEstudante = lista.get(start + 11);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.registarEstudante(nome, contacto, morada, codigoPostal, numCC, validadeDia,validadeMes, validadeAno, faculdade, departamento, password, numEstudante);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "registarEstudante", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Regista um docente em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; docente ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password ; numMec
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean registarDocente(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 12 + start;
        String nome;
        String contacto;
        String morada;
        String codigoPostal;
        String numCC;
        int validadeDia;
        int validadeMes;
        int validadeAno;
        String faculdade;
        String departamento;
        String password;
        String numMec;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarDocente: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nome = lista.get(start);
        contacto = lista.get(start + 1);
        morada = lista.get(start + 2);
        codigoPostal = lista.get(start + 3);
        numCC = lista.get(start + 4);

        try {
            validadeDia = Integer.parseInt(lista.get(start + 5));
            validadeMes = Integer.parseInt(lista.get(start + 6));
            validadeAno = Integer.parseInt(lista.get(start + 7));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarDocente: parseInt");
            return SUCESSO;
        }

        faculdade = lista.get(start + 8);
        departamento = lista.get(start + 9);
        password = lista.get(start + 10);
        numMec = lista.get(start + 11);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.registarDocente(nome, contacto, morada, codigoPostal, numCC, validadeDia, validadeMes, validadeAno, faculdade, departamento, password, numMec);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "registarDocente", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Regista um funcionario em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; funcionario ; nome ; contacto ; morada ; codigoPostal ; numCC ; validadeDia ; validadeMes ; validadeAno ; faculdade ; departamento ; password
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean registarFuncionario(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 11 + start;
        String nome;
        String contacto;
        String morada;
        String codigoPostal;
        String numCC;
        int validadeDia;
        int validadeMes;
        int validadeAno;
        String faculdade;
        String departamento;
        String password;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarFuncionario: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nome = lista.get(start);
        contacto = lista.get(start + 1);
        morada = lista.get(start + 2);
        codigoPostal = lista.get(start + 3);
        numCC = lista.get(start + 4);

        try {
            validadeDia = Integer.parseInt(lista.get(start + 5));
            validadeMes = Integer.parseInt(lista.get(start + 6));
            validadeAno = Integer.parseInt(lista.get(start + 7));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "registarFuncionario: parseInt");
            return SUCESSO;
        }

        faculdade = lista.get(start + 8);
        departamento = lista.get(start + 9);
        password = lista.get(start + 10);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.registarFuncionario(nome, contacto, morada, codigoPostal, numCC, validadeDia, validadeMes, validadeAno, faculdade, departamento, password);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "registarFuncionario", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Imprime no terminal os resultados de eleições anteriores em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando 1: show
     * <p> Ou Sintaxe do Comando 2: resultadosanteriores
     *
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean imprimeResultadosEleicoesAnteriores() {
        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.imprimeResultadosEleicoesAnteriores(this);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "imprimeResultadosEleicoesAnteriores", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Regista uma mesa de voto em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; mesavoto ; nomeEleicao ; faculdadeOuDepartamento
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean adicionarMesaVoto(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 2 + start;
        String nomeEleicao;
        String faculdadeOuDepartamento;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "adicionarMesaVoto: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        faculdadeOuDepartamento = lista.get(start + 1);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.adicionarMesaVoto(nomeEleicao, faculdadeOuDepartamento);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "adicionarMesaVoto", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Remove uma mesa de voto em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: remove ; mesavoto ; nomeEleicao ; faculdadeOuDepartamento
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean removeMesaVoto(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 2 + start;
        String nomeEleicao;
        String faculdadeOuDepartamento;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "removeMesaVoto: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        faculdadeOuDepartamento = lista.get(start + 1);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.removeMesaVoto(nomeEleicao, faculdadeOuDepartamento);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "removeMesaVoto", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Atualiza o titulo da eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: update ; eleicao ; titulo ; nomeEleicao ; novoTitulo
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean alterarTituloEleicao(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 2 + start;
        String nomeEleicao;
        String novoTitulo;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarTituloEleicao: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        novoTitulo = lista.get(start + 1);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.alterarTituloEleicao(nomeEleicao, novoTitulo);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "alterarTituloEleicao", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Atualiza a descrição da eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: update ; eleicao ; descricao ; nomeEleicao ; novaDescricao
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean alterarDescricaoEleicao(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 2 + start;
        String nomeEleicao;
        String novaDescricao;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarDescricaoEleicao: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        novaDescricao = lista.get(start + 1);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.alterarDescricaoEleicao(nomeEleicao, novaDescricao);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "alterarDescricaoEleicao", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Atualiza a data de inicio da eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: update ; eleicao ; datainicio ; nomeEleicao ; diaInicio ; mesInicio ; anoInicio ; horaInicio ; minutoInicio
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean alterarDataInicioEleicao(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 6 + start;
        String nomeEleicao;
        int diaInicio;
        int mesInicio;
        int anoInicio;
        int horaInicio;
        int minutoInicio;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarDataInicioEleicao: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);

        try {
            diaInicio = Integer.parseInt(lista.get(start + 1));
            mesInicio = Integer.parseInt(lista.get(start + 2));
            anoInicio = Integer.parseInt(lista.get(start + 3));
            horaInicio = Integer.parseInt(lista.get(start + 4));
            minutoInicio = Integer.parseInt(lista.get(start + 5));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarDataInicioEleicao: parseInt");
            return SUCESSO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.alterarDataInicioEleicao(nomeEleicao, diaInicio, mesInicio, anoInicio, horaInicio, minutoInicio);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "alterarDataInicioEleicao", "Erro de conexão", e);
                    return ERRO;
                }
            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Atualiza a data de fim da eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: update ; eleicao ; datafim ; nomeEleicao ; diaFim ; mesFim ; anoFim ; horaFim ; minutoFim
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean alterarDataFimEleicao(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 6 + start;
        String nomeEleicao;
        int diaFim;
        int mesFim;
        int anoFim;
        int horaFim;
        int minutoFim;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarDataFimEleicao: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);

        try {
            diaFim = Integer.parseInt(lista.get(start + 1));
            mesFim = Integer.parseInt(lista.get(start + 2));
            anoFim = Integer.parseInt(lista.get(start + 3));
            horaFim = Integer.parseInt(lista.get(start + 4));
            minutoFim = Integer.parseInt(lista.get(start + 5));
        } catch (NumberFormatException e) {
            this.msgErroInput("[ADMIN CONSOLE]", "alterarDataFimEleicao: parseInt");
            return SUCESSO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.alterarDataFimEleicao(nomeEleicao, diaFim, mesFim, anoFim, horaFim, minutoFim);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "alterarDataFimEleicao", "Erro de conexão", e);
                    return ERRO;
                }

            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Imprime no ecrã o local de voto do eleitor em uma determinada eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: localeleitor ; nomeEleicao ; numCC
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean obterLocalVotoEleitor(ArrayList<String> lista, int size, int start) {
        int INPUT_N_ARGS = 2 + start;
        String nomeEleicao;
        String numCC;
        String output;

        if (size != INPUT_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "obterLocalVotoEleitor: size != INPUT_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        numCC = lista.get(start + 1);

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                output = this.clienteRemotoRMI.obterLocalVotoEleitor(nomeEleicao, numCC);
                System.out.printf("[SERVER RMI] %s\n", output);
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "obterLocalVotoEleitor", "Erro de conexão", e);
                    return ERRO;
                }

            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Adiciona uma lista a uma eleição em resposta ao comando inserido no stdin.
     * <p> Sintaxe do Comando: add ; lista ; nomeEleicao ; nomeLista ; membrosNumCC ; ...
     *
     * @param lista Lista dos campos inseridos
     * @param size Tamanho da lista
     * @param start Numero de campos já lidos anteriormente.
     * @return SUCESSO ou ERRO
     * @see AdminConsoleInterface
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see RemoteException
     * @see ServerRMI.Exceptions
     * @author Dario Felix
     * @version 1.0
     */
    private boolean adicionarListaAEleicao (ArrayList<String> lista, int size, int start) {
        int INPUT_MIN_N_ARGS = 3 + start;
        String nomeEleicao;
        String nomeLista;
        CopyOnWriteArrayList<String> membrosNumCC = new CopyOnWriteArrayList<>();
        int i;

        if (size < INPUT_MIN_N_ARGS) {
            this.msgErroInput("[ADMIN CONSOLE]", "adicionarListaAEleicao: size < INPUT_MIN_N_ARGS");
            return SUCESSO;
        }

        nomeEleicao = lista.get(start);
        nomeLista = lista.get(start + 1);

        for (i=start+2 ; i < size ; i++) {
            membrosNumCC.add(lista.get(i));
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.adicionarListaAEleicao(nomeEleicao, nomeLista, membrosNumCC);
                System.out.print("[SERVER RMI] Sucesso! \n");
                fatorTentaNovamente = false;

            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("AdminConsole", "adicionarListaAEleicao", "Erro de conexão", e);
                    return ERRO;
                }

            } catch (Exception e) {
                this.msgErroInput("[SERVER RMI]", e.getMessage());
                fatorTentaNovamente = false;
            }
        }
        return SUCESSO;
    }


    /**
     * Verifica se tem input do stdin para ler, caso afirmativo retorna-a, se não tiver nada para ler
     * faz wait(NOT_SO_SHORT_SLEEP_WAIT_MS) e tambem verifica se o servidor RMI ainda está "ativo" (isAlive()),
     * se não estiver tenta conectar-se ao secundário (reconnectAndSetClienteRemotoRMI()).
     * Este procedimento é repetido até br.ready() ser true.
     *
     * @return String vazia em caso de erro ou nextLine do stdin em caso de sucesso.
     * @see AdminConsoleInterface#isAlive()
     * @see AdminConsole#reconnectAndSetClienteRemotoRMI()
     * @see Thread#wait(long)
     * @see BufferedReader
     * @author Dario Felix
     * @version 1.0
     */
    private String getNextLineFromInputStdinEspecial() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        boolean fatorGet;

        try {
            fatorGet = true;
            while (fatorGet) {
                if (br.ready()) {
                    fatorGet = false;
                } else {
                    synchronized (this) {
                        this.wait(NOT_SO_SHORT_SLEEP_WAIT_MS);
                    }
                    try {
                        this.clienteRemotoRMI.isAlive();
                    } catch (RemoteException e) {
                        if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                            ProjGeral.printErro("AdminConsole", "getNextLineFromInputStdinEspecial", "Erro de conexão", e);
                        }

                    }
                }
            }
            input = br.readLine();
        } catch (IOException e1) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "getNextLineFromInputStdinEspecial", "IOException", e1);
            return "";
        } catch (InterruptedException e2) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("AdminConsole", "getNextLineFromInputStdinEspecial", "InterruptedException", e2);
            return "";
        }
        return input;
    }
}