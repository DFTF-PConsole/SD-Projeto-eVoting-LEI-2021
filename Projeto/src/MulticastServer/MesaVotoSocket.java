/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package MulticastServer;


import Outros.ProjGeral;
import ServerRMI.RMIServer;
import VotingTerminal.TerminalVoto;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Thread responsavel pela constante escuta (receive()) no socket multicast e a execução dos comandos associados a isso.
 * Executa/comunica com o server-multicast e com o server RMI.
 *
 * @see MesaVoto
 * @see MesaVotoStdin
 * @see RMIServer
 * @see Thread
 * @see ProjGeral
 * @see Runnable
 * @see Protocolo
 * @see TerminalVoto
 * @author Dario Felix
 * @version 1.0
 */
public class MesaVotoSocket implements ProjGeral, Protocolo, Runnable {
    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    private final static boolean DEBUG_ATIVO = true;   // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    private final static boolean INFO_ERRO_ATIVO = true;   // Default: false

    /**
     * Flag que indica se o INFO_RELEVANTE_ATIVO esta ativo. Indica se deve imprimir no stdout mensagens relevantes do fluxo normal do programa.
     *
     * @see ProjGeral#printAviso(String)
     */
    private final static boolean INFO_RELEVANTE_ATIVO = false;  // Default: false

    /**
     * Referencia ao objeto que contem os metodos para a conexão com o server RMI
     */
    private final MesaVoto mesaVoto;

    /**
     * Referencia para o socket associado ao grupo Multicast.
     *
     * @see MulticastSocket
     */
    private final MulticastSocket multicastSocket;

    /**
     * Referencia para o grupo Multicast.
     *
     * @see InetAddress
     */
    private final InetAddress grupo;

    /**
     * Porto Multicast
     * <p> Exemplo: 9876
     */
    private final int porto;


    /**
     * Construtor
     *
     * @param mesaVoto Referencia ao objeto que contem os metodos para a conexão com o server RMI
     * @param multicastSocket Referencia para o socket associado ao grupo Multicast
     * @param grupo Referencia para o grupo Multicast
     * @param porto Porto Multicast
     * @author Dario Felix
     * @version 1.0
     */
    public MesaVotoSocket (MesaVoto mesaVoto, MulticastSocket multicastSocket, InetAddress grupo, int porto) {
        this.mesaVoto = mesaVoto;
        this.multicastSocket = multicastSocket;
        this.grupo = grupo;
        this.porto = porto;
    }

    /**
     * Execucao da thread MesaVotoSocket: constante escuta (receive()) no socket multicast e executa/comunica com o server-multicast e com o server RMI
     * <p> Comando 1: type | hello  ;  id | 'numero_random'
     * <p> Comando 2: type | set_id  ;  old_id | 'numero_random'  ;  new_id | 'n'
     * <p> Comando 3: type | login  ;  id | 'n'  ;  username | 'cc'  ;  password | 'password'
     * <p> Comando 4: type | status  ;  id | 'n'  ;  {logged, voted, } | 'true|false'  ;  msg | 'msg'
     * <p> Comando 5: type | get_list  [;  id | 'n']
     * <p> Comando 6: type | item_list  [;  id | 'n']  ;  item_count | 'n'  ;  item_0_name | 'candidate list'  {  ; item_i_name | 'another candidate list' }
     * <p> Comando 7: type | vote  ;  id | 'n'  ;  list | 'lista'
     *
     * @see ProjGeral
     * @see Protocolo
     * @see MesaVoto
     * @see Thread
     * @see Runnable
     * @see MesaVotoStdin
     * @author Dario Felix
     * @version 1.0
     */
    @Override
    public void run() {
        byte [] buffer;
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap;

        while (true) {
            try {
                this.multicastSocket.setSoTimeout(TIMEOUT_INFINITO);

                // espera por msg
                buffer = new byte[BUFFER_LENGTH];
                packet = new DatagramPacket(buffer, buffer.length);
                this.multicastSocket.receive(packet);

                // verifica e executa o cmd
                cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));

                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("MesaVotoSocket", "run", "Mensagem Recebida: " + new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));

                if (cmdHashMap != null && cmdHashMap.get("type").equals("hello") && cmdHashMap.containsKey("id")) {
                    if ( hello(cmdHashMap) == ERRO ) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("MesaVotoSocket", "run", "hello() == ERRO");
                        return;
                    }

                } else if (cmdHashMap != null && cmdHashMap.get("type").equals("login") && cmdHashMap.containsKey("id") && cmdHashMap.containsKey("username") && cmdHashMap.containsKey("password")) {
                    if ( login(cmdHashMap) == ERRO ) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("MesaVotoSocket", "run", "login() == ERRO");
                        return;
                    }

                } else if (cmdHashMap != null && cmdHashMap.get("type").equals("vote") && cmdHashMap.containsKey("id") && cmdHashMap.containsKey("list")) {
                    if ( vote(cmdHashMap) == ERRO ) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("MesaVotoSocket", "run", "vote() == ERRO");
                        return;
                    }

                } else if (cmdHashMap != null && cmdHashMap.get("type").equals("get_list")) {   // nao e preciso: && cmdHashMap.containsKey("id")
                    if ( getList() == ERRO ) {
                        if (INFO_ERRO_ATIVO)
                            ProjGeral.printErro("MesaVotoSocket", "run", "getList() == ERRO");
                        return;
                    }

                }
            } catch (IOException e) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "run", "IOException", e);
                return;
            }
        }
    }


    /**
     * Executa os procedimentos necessarios aquando há uma conexão de um novo Terminal de Voto.
     *
     * @param cmdHashMapInput Map com o comando vindo da rede multicast.
     * @return ERRO ou SUCESSO
     * @see Protocolo
     * @see TerminalVoto
     * @author Dario Felix
     * @version 1.0
     */
    private boolean hello(HashMap<String, String> cmdHashMapInput) {
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap;
        int id;

        synchronized (this.mesaVoto) {
            id = this.mesaVoto.getNextAndAddCountIdTerminal();
            if (this.mesaVoto.addIdTerminal("" + id) == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "hello", "Erro no addIdTerminal");
                return ERRO;
            }
        }

        cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "set_id");
        cmdHashMap.put("old_id", cmdHashMapInput.get("id"));
        cmdHashMap.put("new_id", "" + id);

        String cmdString = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdString == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "hello", "cmdString == null");
            return ERRO;
        }

        try {
            // send set_id
            packet = new DatagramPacket(cmdString.getBytes(StandardCharsets.UTF_8), cmdString.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
            this.multicastSocket.setLoopbackMode(true);
            this.multicastSocket.send(packet);
            this.multicastSocket.setLoopbackMode(false);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "hello", "IOException", e);
            return ERRO;
        }

        synchronized (this.mesaVoto) {
            if (this.mesaVoto.msgAdminConsole("Novo Voting Terminal (ID=" + id + ") foi adicionado na Mesa de Voto da eleição \"" + this.mesaVoto.getIdTituloEleicao() + "\" do departamento \"" + this.mesaVoto.getDepartamento() + "\".") == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "hello", "Erro no msgAdminConsole");
                return ERRO;
            }
        }

        if (DEBUG_ATIVO)
            ProjGeral.printDebug("MesaVotoSocket", "hello", "Mensagem Enviada: " + cmdString);

        return SUCESSO;
    }


    /**
     * Executa os comandos aquando há um pedido de login.
     *
     * @param cmdHashMapInput Map com o comando vindo da rede multicast.
     * @return ERRO ou SUCESSO
     * @see Protocolo
     * @see TerminalVoto
     * @author Dario Felix
     * @version 1.0
     */
    private boolean login(HashMap<String, String> cmdHashMapInput) {
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap = new HashMap<>();

        synchronized (this.mesaVoto) {
            if (this.mesaVoto.login(cmdHashMapInput.get("username"), cmdHashMapInput.get("password"), cmdHashMap) == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "login", "Erro no login");
                return ERRO;
            }
        }

        cmdHashMap.put("type", "status");
        cmdHashMap.put("id", cmdHashMapInput.get("id"));

        String cmdString = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdString == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "login", "cmdString == null");
            return ERRO;
        }

        try {
            // send status logged
            packet = new DatagramPacket(cmdString.getBytes(StandardCharsets.UTF_8), cmdString.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
            this.multicastSocket.setLoopbackMode(true);
            this.multicastSocket.send(packet);
            this.multicastSocket.setLoopbackMode(false);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "login", "IOException", e);
            return ERRO;
        }

        synchronized (this.mesaVoto) {
            if (this.mesaVoto.updateRegistoTerminalEleitor(cmdHashMapInput.get("id"), cmdHashMapInput.get("username")) == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "updateRegistoTerminalEleitor", "Erro no update id-terminal <-> cc_eleitor");
                return ERRO;
            }
        }

        if (DEBUG_ATIVO)
            ProjGeral.printDebug("MesaVotoSocket", "login", "Mensagem Enviada: " + cmdString);

        return SUCESSO;
    }


    /**
     * Executa os procedimentos aquando recebe um pedido para registar um voto por parte dos Terminais de Voto.
     *
     * @param cmdHashMapInput Map com o comando vindo da rede multicast.
     * @return ERRO ou SUCESSO
     * @see Protocolo
     * @see TerminalVoto
     * @author Dario Felix
     * @version 1.0
     */
    private boolean vote(HashMap<String,String> cmdHashMapInput) {
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap = new HashMap<>();
        String cc;

        synchronized (this.mesaVoto) {
            if ((cc = this.mesaVoto.getEleitorByRegistoTerminalEleitor(cmdHashMapInput.get("id"))) == null) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "vote", "Erro ao determinar CC associado ao ID do Terminal");
                return ERRO;
            }
            if (this.mesaVoto.vote(cc, (cmdHashMapInput.get("list").equals("-") ? "" : cmdHashMapInput.get("list")), cmdHashMap) == ERRO) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "vote", "Erro no login");
                return ERRO;
            }
        }

        cmdHashMap.put("type", "status");
        cmdHashMap.put("id", cmdHashMapInput.get("id"));

        String cmdString = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdString == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "vote", "cmdString == null");
            return ERRO;
        }

        try {
            // send status voted
            packet = new DatagramPacket(cmdString.getBytes(StandardCharsets.UTF_8), cmdString.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
            this.multicastSocket.setLoopbackMode(true);
            this.multicastSocket.send(packet);
            this.multicastSocket.setLoopbackMode(false);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "vote", "IOException", e);
            return ERRO;
        }

        if (DEBUG_ATIVO)
            ProjGeral.printDebug("MesaVotoSocket", "vote", "Mensagem Enviada: " + cmdString);

        return SUCESSO;
    }


    /**
     * Envia para a rede multicast, as listas candidatas, aquando há um pedido nesse sentido por parte dos Terminais de Voto.
     *
     * @return ERRO ou SUCESSO
     * @see Protocolo
     * @see TerminalVoto
     * @author Dario Felix
     * @version 1.0
     */
    private boolean getList() {
        DatagramPacket packet;
        HashMap<String, String> cmdHashMap;
        ArrayList<String> listaList;
        int count, i;

        synchronized (this.mesaVoto) {
            if ((listaList = this.mesaVoto.getList()) == null) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoSocket", "getList", "listaList == null");
                return ERRO;
            }
        }

        count = listaList.size();

        cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "item_list");
        cmdHashMap.put("item_count", "" + count);

        i=0;
        for (String item : listaList) {
            cmdHashMap.put("item_" + i + "_name", item);
            i++;
        }

        String cmdString = Protocolo.cmdHashMapToString(cmdHashMap);

        if (cmdString == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "getList", "cmdString == null");
            return ERRO;
        }

        try {
            // send item_list
            packet = new DatagramPacket(cmdString.getBytes(StandardCharsets.UTF_8), cmdString.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
            this.multicastSocket.setLoopbackMode(true);
            this.multicastSocket.send(packet);
            this.multicastSocket.setLoopbackMode(false);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoSocket", "getList", "IOException", e);
            return ERRO;
        }

        if (DEBUG_ATIVO)
            ProjGeral.printDebug("MesaVotoSocket", "getList", "Mensagem Enviada: " + cmdString);

        return SUCESSO;
    }
}