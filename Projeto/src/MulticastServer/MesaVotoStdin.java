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
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Scanner;


/**
 * Thread responsavel pela leitura de stdin e a execução dos comandos associados a isso.
 * Executa/comunica com o server-multicast e com o server RMI.
 *
 * @see MesaVoto
 * @see MesaVotoSocket
 * @see RMIServer
 * @see Thread
 * @see ProjGeral
 * @see Runnable
 * @see Protocolo
 * @see TerminalVoto
 * @author Dario Felix
 * @version 1.0
 */
public class MesaVotoStdin implements ProjGeral, Protocolo, Runnable {
    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    private final boolean DEBUG_ATIVO = true;  // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    private final boolean INFO_ERRO_ATIVO = true;  // Default: false

    /**
     * Flag que indica se o INFO_RELEVANTE_ATIVO esta ativo. Indica se deve imprimir no stdout mensagens relevantes do fluxo normal do programa.
     *
     * @see ProjGeral#printAviso(String)
     */
    private final boolean INFO_RELEVANTE_ATIVO = true;  // Default: true

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
    public MesaVotoStdin (MesaVoto mesaVoto, MulticastSocket multicastSocket, InetAddress grupo, int porto) {
        this.mesaVoto = mesaVoto;
        this.multicastSocket = multicastSocket;
        this.grupo = grupo;
        this.porto = porto;
    }


    /**
     * Execucao da thread MesaVotoStdin: le inputs do stdin e executa/comunica com o server-multicast e com o server RMI
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
     * @see MesaVotoStdin
     * @author Dario Felix
     * @version 1.0
     */
    @Override
    public void run() {
        String input;
        Scanner sc = new Scanner(System.in);
        byte [] buffer;
        DatagramPacket packet;
        boolean fatorTimeout;
        int id;

        HashMap<String, String> cmdHashMap = new HashMap<>();
        cmdHashMap.put("type", "who_available");

        String cmdStringWhoAvailable = Protocolo.cmdHashMapToString(cmdHashMap);
        String cmdStringUnblock;

        if (cmdStringWhoAvailable == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVotoStdin", "run", "cmdStringWhoAvailable == null");
            return;
        }

        synchronized (this.mesaVoto) {
            System.out.printf("\n *** MESA DE VOTO ABERTA | LOCAL: \"%s\" | ELEIÇÃO: \"%s\" *** \n\n", this.mesaVoto.getDepartamento(), this.mesaVoto.getIdTituloEleicao());
        }

        while (true) {
            input = "";

            while (input.isBlank()) {
                System.out.print("\nDigite o CC do eleitor: \n> ");
                input = sc.nextLine();
                if (input.isBlank())
                    System.out.print("Input introduzido esta em branco. Tente Novamente!\n");
            }

            synchronized (this.mesaVoto) {
                if (this.mesaVoto.pesquisaEleitor(input) == ERRO) {
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("MesaVotoStdin", "run", "pesquisaEleitor == ERRO");
                    continue;
                }
            }

            if (INFO_RELEVANTE_ATIVO)
                ProjGeral.printAviso("Eleitor Validado");

            id = 0;
            fatorTimeout = true;
            while(fatorTimeout) {
                try {
                    // send who_available
                    this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                    packet = new DatagramPacket(cmdStringWhoAvailable.getBytes(StandardCharsets.UTF_8), cmdStringWhoAvailable.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                    this.multicastSocket.setLoopbackMode(true);
                    this.multicastSocket.send(packet);
                    this.multicastSocket.setLoopbackMode(false);

                    // espera pelo im_available
                    buffer = new byte[BUFFER_LENGTH];
                    packet = new DatagramPacket(buffer, buffer.length);
                    this.multicastSocket.receive(packet);

                    // verifica e executa o cmd
                    cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                    if (cmdHashMap != null && cmdHashMap.get("type").equals("im_available") && cmdHashMap.containsKey("id")) {
                        id = Integer.parseInt(cmdHashMap.get("id"),10);
                        fatorTimeout = false;
                    }
                } catch (SocketTimeoutException e) {
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("MesaVotoStdin", "run", "SocketTimeoutException");
                } catch (IOException e) {
                    if (INFO_ERRO_ATIVO)
                        ProjGeral.printErro("MesaVotoStdin", "run", "IOException", e);
                    return;
                }
            }

            cmdHashMap = new HashMap<>();
            cmdHashMap.put("type", "unblock");
            cmdHashMap.put("id", "" + id);

            cmdStringUnblock = Protocolo.cmdHashMapToString(cmdHashMap);

            if (cmdStringUnblock == null) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVotoStdin", "run", "cmdStringUnblock == null");
                return;
            }

            fatorTimeout = true;
            while(fatorTimeout) {
                try {
                    // send unblock
                    this.multicastSocket.setSoTimeout(TIMEOUT_FINITO);
                    packet = new DatagramPacket(cmdStringUnblock.getBytes(StandardCharsets.UTF_8), cmdStringUnblock.getBytes(StandardCharsets.UTF_8).length, this.grupo, this.porto);
                    this.multicastSocket.setLoopbackMode(true);
                    this.multicastSocket.send(packet);
                    this.multicastSocket.setLoopbackMode(false);

                    // espera pelo ack
                    buffer = new byte[BUFFER_LENGTH];
                    packet = new DatagramPacket(buffer, buffer.length);
                    this.multicastSocket.receive(packet);

                    // verifica e executa o cmd
                    cmdHashMap = Protocolo.cmdStringToHashMap(new String(packet.getData(), 0, packet.getLength(), StandardCharsets.UTF_8));
                    if (cmdHashMap != null && cmdHashMap.get("type").equals("ack") && cmdHashMap.containsKey("type_ack") && cmdHashMap.get("type_ack").equals("unblock")) {
                        fatorTimeout = false;
                        System.out.print("Terminal de voto desbloqueado\n");
                    }
                } catch (SocketTimeoutException e) {
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("MesaVotoStdin", "run", "SocketTimeoutException");
                } catch (IOException e) {
                    if (INFO_ERRO_ATIVO)
                        ProjGeral.printErro("MesaVotoStdin", "run", "IOException", e);
                    return;
                }
            }

            System.out.println("\n*** *** *** *** *** *** ***\n");
        }
    }
}