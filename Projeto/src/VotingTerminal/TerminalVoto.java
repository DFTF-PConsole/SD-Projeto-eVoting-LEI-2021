/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// ARGUMENTOS
// <IP_MULTICAST> <PORTO_MULTICAST>
// 224.0.224.0 9876


package VotingTerminal;


import Outros.*;
import MulticastServer.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * TerminalVoto (ou Voting Terminal) sao os clientes Multicast que estao associados a cada mesa
 * de voto, e que permitem realizar as funcionalidades 7 e 8.
 *
 * @see MesaVoto
 * @see Thread
 * @see ProjGeral
 * @see Protocolo
 * @see Runnable
 * @see TerminalVotoAFK
 * @author Dario Felix
 * @version 1.0
 */
public class TerminalVoto implements ProjGeral, Runnable, Protocolo {
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
     * Constante que indica o numero de argumentos necessarios para o metodo main
     *
     * @see #main(String[])
     */
    private final static int N_ARGS = 2;

    /**
     * Tempo maximo que o TerminalVoto pode estar inativo
     * <p> ms = N s * 1000 ms
     */
    private final long INATIVIDADE_MS = 120 * 1000;     // Default: 120 * 1000   > (120s = 2 min)

    /**
     * IP Multicast (necessariamente igual ao da MesaVoto)
     * <p> Exemplo: 224.0.224.0
     */
    private final String ipMulticast;

    /**
     * Porto Multicast (necessariamente igual ao da MesaVoto)
     * <p> Exemplo: 9876
     */
    private final int porto;

    /**
     * Referencia para a thread TerminalVoto. Permite provocar um InterruptedException atraves do metodo interrupt()
     *
     * @see Thread#interrupt()
     * @see InterruptedException
     * @see TerminalVotoAFK#run()
     */
    private Thread threadApp;

    /**
     * Referencia para a thread TerminalVotoAFK. Permite provocar um InterruptedException atraves do metodo interrupt()
     *
     * @see Thread#interrupt()
     * @see InterruptedException
     * @see TerminalVotoAFK#run()
     */
    private Thread threadAFK;

    /**
     * Referencia para o socket associado ao grupo Multicast.
     *
     * @see MulticastSocket
     */
    private MulticastSocket multicastSocket;

    /**
     * Referencia para o grupo Multicast.
     *
     * @see InetAddress
     */
    private InetAddress grupo;

    /**
     * Flag utilizada para comunicacao entre threads (TerminalVoto e TerminalVotoAFK).
     * Indica se o TerminalVoto esta ativo/desbloqueado.
     *
     * @see AtomicBoolean
     * @see TerminalVotoAFK#run()
     */
    private final AtomicBoolean isDesbloqueado;

    /**
     * ID unico do TerminalVoto defenido pela MesaVoto aquando o hello()
     *
     * @see TerminalVoto#hello()
     */
    private int id;


    /**
     * Construtor
     *
     * @param ipMulticast Endereco IP multicast do grupo (Nx TerminalVoto + 1x MesaVoto)
     * @param porto Porto (socket/grupo) para descobrir e ser descoberto pelo servidor, e para comunicar a intencao de voto do eleitor
     * @author Dario Felix
     * @version 1.0
     */
    TerminalVoto(String ipMulticast, int porto) {
        this.ipMulticast = ipMulticast;
        this.porto = porto;
        this.threadApp = null;
        this.threadAFK = null;
        this.multicastSocket = null;
        this.isDesbloqueado = new AtomicBoolean(false);
        this.id = (new Random()).nextInt(9999999);
        this.grupo = null;
    }


    /**
     * Verifica os dados dos args e coloca operacional a thread TerminalVoto
     * <p> Exemplo argumentos: 224.0.224.0 9876
     *
     * @param args IP_MULTICAST  PORTO_MULTICAST
     * @see Runnable
     * @see Thread
     * @author Dario Felix
     * @version 1.0
     */
    public static void main(String[] args){
        int porto;

        if (args.length != N_ARGS) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "main", "numero de args diferente de " + N_ARGS);
            ProjGeral.printErro("TerminalVoto", "main", "./TerminalVoto <IP_MULTICAST> <PORTO_MULTICAST>");
            return;
        }

        try {
            porto = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "main", "porto é um inteiro", e);
            ProjGeral.printErro("TerminalVoto", "main", "./TerminalVoto <IP_MULTICAST> <PORTO_MULTICAST>");
            return;
        }

        TerminalVoto terminalVoto = new TerminalVoto(args[0], porto);
        Thread threadApp = new Thread(terminalVoto);
        terminalVoto.setThreadApp(threadApp);
        threadApp.start();

        try {
            threadApp.join();
        } catch (InterruptedException e) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("TerminalVoto", "main", "InterruptedException");
        }
    }

    /**
     * Set a threadApp.
     *
     * @param threadApp Thread App
     * @see Runnable
     * @see Thread
     * @author Dario Felix
     * @version 1.0
     */
    public void setThreadApp(Thread threadApp) {
        this.threadApp = threadApp;
    }


    /**
     * Execucao da thread TerminalVoto: le inputs do stdin e executa/comunica com o server-multicast
     * <p> Comando 1: type | who_available
     * <p> Comando 2: type | im_available  ;  id | 'n'
     * <p> Comando 3: type | unblock  ;  id | 'n'
     * <p> Comando 4: type | ack  ;  type_ack | {unblock, }
     *
     * @see ProjGeral
     * @see Protocolo
     * @see MesaVoto
     * @see Thread
     * @see Runnable
     * @author Dario Felix
     * @version 1.0
     */
    @Override
    public void run() {
        try {
            this.multicastSocket = new MulticastSocket(this.porto);
            this.grupo = InetAddress.getByName(this.ipMulticast);
            this.multicastSocket.joinGroup(grupo);
            this.multicastSocket.setTimeToLive(TIME_TO_LIVE);
            this.multicastSocket.setSoTimeout(TIMEOUT_INFINITO);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "run", "bind do socket", e);
            if (this.multicastSocket != null)
                this.multicastSocket.close();
            return;
        }

        if (this.hello() == ERRO) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "run", "hello() == ERRO");
            this.multicastSocket.close();
            return;
        }

        if (INFO_RELEVANTE_ATIVO)
            ProjGeral.printAviso("Voting Terminal Conectado, ID = " + this.id);

        byte [] buffer;
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "im_available");
        cmdHashMap.put("id", "" + this.id);

        String cmdStringImAvailable = Protocolo.cmdHashMapToString(cmdHashMap);

        cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "ack");
        cmdHashMap.put("type_ack", "unblock");

        String cmdStringAckUnblock = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdStringImAvailable == null || cmdStringAckUnblock == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "run", "cmdStringImAvailable == null");
            return;
        }

        boolean fatorRepeticaoBloqueado = true;

        while (true) {
            try {
                if (INFO_RELEVANTE_ATIVO && fatorRepeticaoBloqueado) {
                    ProjGeral.printAviso("Voting Terminal Bloqueado");
                    fatorRepeticaoBloqueado = false;
                }

                this.multicastSocket.setSoTimeout(TIMEOUT_INFINITO);

                // espera pelo who_available
                buffer = new byte[BUFFER_LENGTH];
                packet = new DatagramPacket(buffer, buffer.length);
                this.multicastSocket.receive(packet);

                // verifica e executa o cmd
                cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                if (cmdHashMap != null && cmdHashMap.get("type").equals("who_available")) {
                    // send im_available
                    packet = new DatagramPacket(cmdStringImAvailable.getBytes(StandardCharsets.UTF_8), cmdStringImAvailable.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                    this.multicastSocket.setLoopbackMode(true);
                    this.multicastSocket.send(packet);
                    this.multicastSocket.setLoopbackMode(false);

                    if (INFO_RELEVANTE_ATIVO)
                        ProjGeral.printAviso("Este Voting Terminal Está Disponivel");

                } else if (cmdHashMap != null && cmdHashMap.get("type").equals("unblock") && cmdHashMap.containsKey("id") && cmdHashMap.get("id").equals("" + this.id)) {
                    if (INFO_RELEVANTE_ATIVO)
                        ProjGeral.printAviso("A Desbloquear Voting Terminal...");

                    this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                    boolean factorAck = true;
                    Instant fim;
                    Duration duracao;
                    Instant inicio = Instant.now();

                    while (factorAck) {
                        // send Comando 4: type | ack  ;  type_ack | {unblock, }
                        packet = new DatagramPacket(cmdStringAckUnblock.getBytes(StandardCharsets.UTF_8), cmdStringAckUnblock.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                        this.multicastSocket.setLoopbackMode(true);
                        this.multicastSocket.send(packet);
                        this.multicastSocket.setLoopbackMode(false);

                        // espera e escuta para perceber se houve problemas
                        buffer = new byte[BUFFER_LENGTH];
                        packet = new DatagramPacket(buffer, buffer.length);
                        try {
                            this.multicastSocket.receive(packet);
                        } catch (SocketTimeoutException e) {
                            if (DEBUG_ATIVO)
                                ProjGeral.printDebug("TerminalVoto", "run", "SocketTimeoutException");
                            break;
                        }
                        cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));

                        fim = Instant.now();
                        duracao = Duration.between(inicio, fim);
                        if (!(cmdHashMap != null && cmdHashMap.get("type").equals("unblock") && cmdHashMap.containsKey("id") && cmdHashMap.get("id").equals("" + this.id)) && duracao.toMillis() > TEMPO_AVARIA_TEMPORARIA_MS)
                            factorAck = false;
                    }

                    this.multicastSocket.setSoTimeout(TIMEOUT_INFINITO);

                    if (INFO_RELEVANTE_ATIVO)
                        ProjGeral.printAviso("Voting Terminal Desbloqueado");

                    if (this.desbloqueia() == ERRO) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("TerminalVoto", "run", "desbloqueia() == ERRO");
                        this.multicastSocket.close();
                        return;
                    }
                    fatorRepeticaoBloqueado = true;
                }
            } catch (IOException e) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "run", "IOException", e);
                this.multicastSocket.close();
                return;
            }
        }
    }


    /**
     * Desbloqueia o Voting Terminal: inicia a Thread TerminalVotoAFK e chama o metodo app().
     *
     * @return ERRO ou SUCESSO
     * @see Thread
     * @see Thread#interrupt()
     * @see TerminalVotoAFK
     * @see ProjGeral#SUCESSO
     * @see ProjGeral#ERRO
     * @see InterruptedException
     * @author Dario Felix
     * @version 1.0
     */
    private boolean desbloqueia() {
        this.isDesbloqueado.set(true);
        TerminalVotoAFK terminalVotoAFK = new TerminalVotoAFK(this.INATIVIDADE_MS, this.threadApp, this.isDesbloqueado);
        this.threadAFK = new Thread(terminalVotoAFK);
        threadAFK.start();

        try {
            if (this.app() == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "desbloqueia", "app() == ERRO");
                return ERRO;
            }
            this.isDesbloqueado.set(false);
            this.threadAFK.interrupt();
            this.threadAFK.join();
            this.threadAFK = null;
        } catch (InterruptedException e) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("TerminalVoto", "desbloqueia", "InterruptedException");
            if (INFO_RELEVANTE_ATIVO) {
                System.out.println();
                ProjGeral.printAviso("Voting Terminal Inativo");
            }
            this.isDesbloqueado.set(false);
            this.threadAFK = null;
        }
        System.out.println("\n*** *** *** *** *** *** ***\n");
        return SUCESSO;
    }


    /**
     * O core da aplicacao do ponto de vista do utilizador. Chama os metodos login() e votar() ordeiramente.
     *
     * @return ERRO ou SUCESSO
     * @throws InterruptedException a thread TerminalVotoAFK envia InterruptedException se o metodo app() estiver inativo por SLEEP_MS ms
     * @see TerminalVoto#INATIVIDADE_MS
     * @see ProjGeral#ERRO
     * @see ProjGeral#SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    private boolean app() throws InterruptedException {
        if (this.login() == ERRO) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "app", "login() == ERRO");
            return ERRO;
        }

        if (this.votar() == ERRO) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "app", "votar() == ERRO");
            return ERRO;
        }

        return SUCESSO;
    }


    /**
     * TerminalVoto conecta-se pela primeira vez ao grupo multicast e avisa o MulticastServer (type: hello).
     * O MulticastServer responde ao atribuir um id unico ao TerminalVoto (type: set_id).
     * <p> Comando 1: type | hello  ;  id | 'numero_random'
     * <p> Comando 2: type | set_id  ;  old_id | 'numero_random'  ;  new_id | 'n'
     *
     * @see ProjGeral#ERRO
     * @see ProjGeral#SUCESSO
     * @see Protocolo
     * @return ERRO ou SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    private boolean hello() {
        boolean fatorTimeout;

        HashMap<String, String> cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "hello");
        cmdHashMap.put("id", "" + this.id);

        String cmdStringHello = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdStringHello == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "hello", "cmdStringHello == null");
            return ERRO;
        }

        byte[] buffer;
        DatagramPacket packet;

        fatorTimeout = true;
        while(fatorTimeout) {
            try {

                // send hello
                this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                packet = new DatagramPacket(cmdStringHello.getBytes(StandardCharsets.UTF_8), cmdStringHello.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                this.multicastSocket.setLoopbackMode(true);
                this.multicastSocket.send(packet);
                this.multicastSocket.setLoopbackMode(false);

                // espera pelo set_id
                buffer = new byte[BUFFER_LENGTH];
                packet = new DatagramPacket(buffer, buffer.length);
                this.multicastSocket.receive(packet);

                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVoto", "hello", "packet :" + new String(packet.getData(), 0, packet.getLength()));

                // verifica e executa o cmd
                cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                if (cmdHashMap != null && cmdHashMap.get("type").equals("set_id") && cmdHashMap.containsKey("old_id") && cmdHashMap.containsKey("new_id") && cmdHashMap.get("old_id").equals("" + this.id)) {
                    this.id = Integer.parseInt(cmdHashMap.get("new_id"),10);
                    fatorTimeout = false;
                }
            } catch (SocketTimeoutException e) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVoto", "hello", "SocketTimeoutException");
            } catch (IOException e) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "hello", "IOException", e);
                return ERRO;
            }
        }

        return SUCESSO;
    }


    /**
     * Faz login de eleitores com o Server Multicast utilizando os comandos do protocolo
     * <p> Comando 1: type | login  ;  id | 'n'  ;  username | 'cc'  ;  password | 'password'
     * <p> Comando 2: type | status  ;  id | 'n'  ;  {logged, voted, } | 'true|false'  ;  msg | 'msg'
     *
     * @throws InterruptedException Execeção transmitida quando a thread atual é interrompida
     * @see ProjGeral#ERRO
     * @see ProjGeral#SUCESSO
     * @see Thread#interrupt()
     * @see Protocolo
     * @return ERRO ou SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    private boolean login() throws InterruptedException {
        String username;
        String password;
        boolean fatorLogged;
        boolean fatorTimeout;
        HashMap<String, String> cmdHashMap;
        String cmdStringLogin;
        byte[] buffer;
        DatagramPacket packet;

        System.out.print("\n *** BEM-VINDO AO LOGIN *** \n\n");

        fatorLogged = false;
        do {
            username = "";
            password = "";
            while (username.isBlank()) {
                System.out.print("Digite o username: \n> ");
                username = getNextLineFromInputStdinEspecial();
                if (username.isBlank())
                    System.out.print("Username em branco.\n");
                this.threadAFK.interrupt();
            }

            while (password.isBlank()) {
                System.out.print("Digite a password: \n> ");
                password = getNextLineFromInputStdinEspecial();
                if (password.isBlank())
                    System.out.print("Password em branco.\n");
                this.threadAFK.interrupt();
            }

            cmdHashMap = new HashMap<>();
            cmdHashMap.put("type", "login");
            cmdHashMap.put("id", "" + this.id);
            cmdHashMap.put("username", username);
            cmdHashMap.put("password", password);
            cmdStringLogin = Protocolo.cmdHashMapToString(cmdHashMap);

            if (cmdStringLogin == null) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "login", "cmdStringLogin == null");
                return ERRO;
            }

            fatorTimeout = true;
            while(fatorTimeout) {
                try {
                    // send login
                    this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                    packet = new DatagramPacket(cmdStringLogin.getBytes(StandardCharsets.UTF_8), cmdStringLogin.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                    this.multicastSocket.setLoopbackMode(true);
                    this.multicastSocket.send(packet);
                    this.multicastSocket.setLoopbackMode(false);

                    // espera pelo status logged
                    buffer = new byte[BUFFER_LENGTH];
                    packet = new DatagramPacket(buffer, buffer.length);
                    this.multicastSocket.receive(packet);
                    this.threadAFK.interrupt();

                    // verifica e executa o cmd
                    cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                    if (cmdHashMap != null && cmdHashMap.get("type").equals("status") && cmdHashMap.containsKey("logged") && cmdHashMap.containsKey("id") && cmdHashMap.get("id").equals("" + this.id)) {
                        fatorLogged = Boolean.parseBoolean(cmdHashMap.get("logged"));
                        fatorTimeout = false;

                        if (!cmdHashMap.containsKey("msg"))
                            cmdHashMap.put("msg", "---");

                        if (fatorLogged) {
                            System.out.printf("Autenticado! (Msg:\"%s\")\n", cmdHashMap.get("msg"));
                        } else {
                            System.out.printf("Não foi possivel autenticar! (Msg:\"%s\")\n", cmdHashMap.get("msg"));
                            System.out.print("\nTente Novamente!\n");
                        }
                    }
                } catch (SocketTimeoutException e) {
                    this.threadAFK.interrupt();
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("TerminalVoto", "login", "SocketTimeoutException");
                } catch (IOException e) {
                    if (INFO_ERRO_ATIVO)
                        ProjGeral.printErro("TerminalVoto", "login", "IOException", e);
                    return ERRO;
                }
            }
        } while (!fatorLogged);

        return SUCESSO;
    }


    /**
     * Comunica ao Server Multicast votos de eleitores utilizando os comandos do protocolo
     * <p> Comando 1: type | get_list  [;  id | 'n']
     * <p> Comando 2: type | item_list  [;  id | 'n']  ;  item_count | 'n'  ;  item_0_name | 'candidate list'  {  ; item_i_name | 'another candidate list' }
     * <p> Comando 3: type | vote  ;  id | 'n'  ;  list | 'lista'
     * <p> Comando 4: type | status  ;  id | 'n'  ;  {logged, voted, } | 'true|false'  ;  msg | 'msg'
     *
     * @throws InterruptedException Execeção transmitida quando a thread atual é interrompida
     * @see ProjGeral#ERRO
     * @see ProjGeral#SUCESSO
     * @see Thread#interrupt()
     * @see Protocolo
     * @return ERRO ou SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    private boolean votar() throws InterruptedException {
        boolean fatorTimeout;
        boolean fatorVoted;
        HashMap<String, String> cmdHashMap, listas;
        String cmdStringGetList, cmdStringVote;
        byte[] buffer;
        DatagramPacket packet;
        int countListas = 0;
        int escolha;
        int i;

        System.out.print("\n *** VOTAÇÃO NA ELEIÇÃO *** \n\n");

        cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "get_list");
        cmdStringGetList = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdStringGetList == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "votar", "cmdStringGetList == null");
            return ERRO;
        }

        fatorTimeout = true;
        while(fatorTimeout) {
            try {
                // send get_list
                this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                packet = new DatagramPacket(cmdStringGetList.getBytes(StandardCharsets.UTF_8), cmdStringGetList.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                this.multicastSocket.setLoopbackMode(true);
                this.multicastSocket.send(packet);
                this.multicastSocket.setLoopbackMode(false);

                // espera pelo item_list
                buffer = new byte[BUFFER_LENGTH];
                packet = new DatagramPacket(buffer, buffer.length);
                this.multicastSocket.receive(packet);
                this.threadAFK.interrupt();

                // verifica e executa o cmd
                cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                if (cmdHashMap != null && cmdHashMap.get("type").equals("item_list") && cmdHashMap.containsKey("item_count")) {
                    countListas = Integer.parseInt(cmdHashMap.get("item_count"));
                    fatorTimeout = false;

                    if (countListas <= 0) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("TerminalVoto", "votar", "countListas <= 0");
                        return ERRO;
                    } else {
                        for (i = 0; i < countListas; i++) {
                            if (!cmdHashMap.containsKey("item_" + i + "_name")) {
                                if (INFO_ERRO_ATIVO)
                                    ProjGeral.printErro("TerminalVoto", "votar", "item_<num>_name no cmd: nao contem todas as listas");
                                return ERRO;
                            }
                        }
                    }
                }
            } catch (SocketTimeoutException e) {
                this.threadAFK.interrupt();
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVoto", "votar", "SocketTimeoutException");
            } catch (IOException e) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "votar", "IOException", e);
                return ERRO;
            }
        }

        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }

        listas = cmdHashMap;
        fatorVoted = false;
        do {
            System.out.print("Listas candidatas:\n");
            for (i = 0 ; i < countListas; i++) {
                System.out.printf(" %2d. \"%s\"\n", i+1, listas.get("item_" + i + "_name"));
            }
            System.out.printf(" %2d. \"%s\"\n", i+1, "VOTO EM BRANCO");

            escolha = 0;
            while (escolha <= 0 || escolha > countListas +1) {
                System.out.printf("Escolha uma lista (digite um numero entre 1 e %d): \n> ", countListas+1);
                try {
                    escolha = Integer.parseInt(getNextLineFromInputStdinEspecial());
                } catch (InputMismatchException e) {
                    escolha = 0;
                }
                if (escolha <= 0 || escolha > countListas +1)
                    System.out.print("Numero digitado errado.\n");
                this.threadAFK.interrupt();
            }

            cmdHashMap = new HashMap<>();
            cmdHashMap.put("type", "vote");
            cmdHashMap.put("id", "" + this.id);
            if (escolha == countListas +1)
                cmdHashMap.put("list", "-"); // voto em branco
            else
                cmdHashMap.put("list", listas.get("item_" + (escolha - 1) + "_name"));
            cmdStringVote = Protocolo.cmdHashMapToString(cmdHashMap);

            if (cmdStringVote == null) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("TerminalVoto", "votar", "cmdStringVote == null");
                return ERRO;
            }

            fatorTimeout = true;
            while(fatorTimeout) {
                try {
                    // send login
                    this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                    packet = new DatagramPacket(cmdStringVote.getBytes(StandardCharsets.UTF_8), cmdStringVote.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                    this.multicastSocket.setLoopbackMode(true);
                    this.multicastSocket.send(packet);
                    this.multicastSocket.setLoopbackMode(false);

                    // espera pelo status logged
                    buffer = new byte[BUFFER_LENGTH];
                    packet = new DatagramPacket(buffer, buffer.length);
                    this.multicastSocket.receive(packet);
                    this.threadAFK.interrupt();

                    // verifica e executa o cmd
                    cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                    if (cmdHashMap != null && cmdHashMap.get("type").equals("status") && cmdHashMap.containsKey("voted") && cmdHashMap.containsKey("id") && cmdHashMap.get("id").equals("" + this.id)) {
                        fatorVoted = Boolean.parseBoolean(cmdHashMap.get("voted"));
                        fatorTimeout = false;

                        if (!cmdHashMap.containsKey("msg"))
                            cmdHashMap.put("msg", "---");

                        if (fatorVoted) {
                            System.out.printf("Votação efetuada com sucesso! (Msg:\"%s\")\n", cmdHashMap.get("msg"));
                        } else {
                            System.out.printf("Não foi possivel votar! (Msg:\"%s\")\n", cmdHashMap.get("msg"));
                            System.out.print("\nPode tentar votar novamente dependendo do problema! Ou então sair.\n");

                            escolha = 0;
                            while (escolha <= 0 || escolha > 2) {
                                System.out.print("Deseja terminar a sessão? (para 'sim' digite '1', para 'não' digite '2'): \n> ");
                                try {
                                    escolha = Integer.parseInt(getNextLineFromInputStdinEspecial());
                                } catch (InputMismatchException e) {
                                    escolha = 0;
                                }
                                if (escolha <= 0 || escolha > 2)
                                    System.out.print("Numero digitado errado.\n");
                                this.threadAFK.interrupt();
                            }
                            if (escolha == 1) {
                                System.out.print("Sessão Terminada!\n");
                                fatorVoted = true;
                            } else {
                                System.out.print("\nRepetindo...\n");
                                fatorVoted = false;
                            }
                        }
                    }
                } catch (SocketTimeoutException e) {
                    this.threadAFK.interrupt();
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("TerminalVoto", "votar", "SocketTimeoutException");
                } catch (IOException e) {
                    if (INFO_ERRO_ATIVO)
                        ProjGeral.printErro("TerminalVoto", "votar", "IOException", e);
                    return ERRO;
                }
            }
        } while (!fatorVoted);

        return SUCESSO;
    }


    /**
     * Verifica se tem input do stdin para ler, caso afirmativo retorna-a, se não tiver nada para ler,
     * verifica se a currentThread foi interrompida (se sim faz throw new InterruptedException()), se não
     * faz wait(SHORT_SLEEP_WAIT_MS).
     * Este procedimento é repetido até br.ready() ser true.
     *
     * @return String vazia em caso de erro ou nextLine do stdin em caso de sucesso.
     * @throws InterruptedException Execeção transmitida quando a thread atual é interrompida
     * @see Thread#wait(long)
     * @see Thread#isInterrupted()
     * @see BufferedReader
     * @author Dario Felix
     * @version 1.0
     */
    private String getNextLineFromInputStdinEspecial() throws InterruptedException {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input;
        boolean fatorGet;

        try {
            fatorGet = true;
            while (fatorGet) {
                if (br.ready()) {
                    fatorGet = false;
                } else {
                    if (Thread.currentThread().isInterrupted()) {
                        throw new InterruptedException();
                    } else {
                        synchronized (this) {
                            this.wait(SHORT_SLEEP_WAIT_MS);
                        }
                    }
                }
            }
            input = br.readLine();
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("TerminalVoto", "getNextLineFromInputStdinEspecial", "IOException", e);
            return "";
        }
        return input;
    }
}