/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

// ARGUMENTOS
// <IP_MULTICAST>  <PORTO_MULTICAST>  <IP_RMI>  <PORTO_RMI>  <NOME_RMI>  <NOME_DEPARTAMENTO>   <ID_ELEICAO>
// 224.0.224.0 9876 10.20.30.0 6789 xpto dei 12


package MulticastServer;


import ServerRMI.Exceptions.*;
import VotingTerminal.*;
import Outros.*;
import ServerRMI.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * Gere localmente os terminais de voto que lhe estao associados. Permite aos membros da mesa realizar a
 * funcionalidade 6 e realiza automaticamente a funcionalidade 5.
 *
 * @see TerminalVoto
 * @see RMIServer
 * @see Thread
 * @see ProjGeral
 * @see Runnable
 * @see Protocolo
 * @see MesaVotoSocket
 * @see MesaVotoStdin
 * @author Dario Felix
 * @version 1.0
 */
public class MesaVoto implements ProjGeral, Protocolo {
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
    private final static int N_ARGS = 7;

    /**
     * IP Multicast (necessariamente igual ao dos TerminalVoto)
     * <p> Exemplo: 224.0.224.0
     */
    private final String ipMulticast;

    /**
     * Porto Multicast (necessariamente igual ao dos TerminalVoto)
     * <p> Exemplo: 9876
     */
    private final int portoMulticast;

    /**
     * IP RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: 30.20.10.0
     *
     * @see RMIServer
     * @see MulticastServerInterface
     */
    private final String ipRMI;

    /**
     * Porto RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: 6789
     *
     * @see RMIServer
     * @see MulticastServerInterface
     */
    private final int portoRMI;

    /**
     * Nome RMI (necessariamente igual ao RMIServer)
     * <p> Exemplo: xpto
     *
     * @see RMIServer
     * @see MulticastServerInterface
     */
    private final String nomeRMI;

    /**
     * Departamento onde esta localizado a MesaVoto (previamente registado no RMIServer)
     *
     * @see ServerRMI.Data.Eleicao
     */
    private final String departamento;

    /**
     * Identificacao da eleicao: titulo (previamente registada no RMI Server)
     *
     * @see ServerRMI.Data.Eleicao
     */
    private final String idTituloEleicao;

    /**
     * Objeto remoto do Server RMI
     *
     * @see RMIServer
     * @see MulticastServerInterface
     */
    private MulticastServerInterface clienteRemotoRMI;

    /**
     * Indica se esta mesa de voto foi validade pelo Server RMI
     *
     * @see RMIServer
     */
    private boolean isValido;

    /**
     * Registo temporario dos ultimos acessos em pares de (id-terminal-voto, username/cc)
     */
    private final HashMap<String, String> registoTerminalEleitor;

    /**
     * Auxiliar para atribuir IDs aos terminais de voto. De 1 a +infinito.
     */
    private int countIdTerminal;


    /**
     * Verifica os dados dos args e coloca operacional as threads MesaVotoStdin e MesavotoSocket
     * <p> Exemplo argumentos: 224.0.224.0 9876 10.20.30.0 6789 xpto dei 12
     *
     * @param args IP_MULTICAST  PORTO_MULTICAST  IP_RMI  PORTO_RMI  NOME_RMI  NOME_DEPARTAMENTO   ID_ELEICAO
     * @see Thread
     * @see Runnable
     * @see MesaVotoStdin
     * @see MesaVotoSocket
     * @author Dario Felix
     * @version 1.0
     */
    public static void main(String[] args){
        String ipMulticast;
        int portoMulticast;
        String ipRMI;
        int portoRMI;
        String nomeRMI;
        String departamento;
        String idTituloEleicao;
        InetAddress grupoThreadSocket;
        InetAddress grupoThreadStdin;
        MulticastSocket multicastSocketThreadSocket = null;
        MulticastSocket multicastSocketThreadStdin = null;
        boolean fatorJoinAll;

        if (args.length != N_ARGS) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "main", "numero de args diferente de " + N_ARGS);
            ProjGeral.printErro("MesaVoto", "main", "./MesaVoto <IP_MULTICAST> <PORTO_MULTICAST> <IP_RMI> <PORTO_RMI> <NOME_RMI> <NOME_DEPARTAMENTO> <ID_ELEICAO>");
            return;
        }

        ipMulticast = args[0];
        ipRMI = args[2];

        try {
            portoMulticast = Integer.parseInt(args[1]);
            portoRMI = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "main", "porto é um inteiro", e);
            ProjGeral.printErro("MesaVoto", "main", "./MesaVoto <IP_MULTICAST> <PORTO_MULTICAST> <IP_RMI> <PORTO_RMI> <NOME_RMI> <NOME_DEPARTAMENTO> <ID_ELEICAO>");
            return;
        }

        nomeRMI = args[4];
        departamento = args[5];
        idTituloEleicao = args[6];

        try {
            multicastSocketThreadSocket = new MulticastSocket(portoMulticast);
            grupoThreadSocket = InetAddress.getByName(ipMulticast);
            multicastSocketThreadSocket.joinGroup(grupoThreadSocket);
            multicastSocketThreadSocket.setTimeToLive(TIME_TO_LIVE);
            multicastSocketThreadSocket.setSoTimeout(TIMEOUT_INFINITO);

            multicastSocketThreadStdin = new MulticastSocket(portoMulticast);
            grupoThreadStdin = InetAddress.getByName(ipMulticast);
            multicastSocketThreadStdin.joinGroup(grupoThreadStdin);
            multicastSocketThreadStdin.setTimeToLive(TIME_TO_LIVE);
            multicastSocketThreadStdin.setSoTimeout(TIMEOUT_INFINITO);
        } catch (IOException e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "main", "bind do socket", e);
            if (multicastSocketThreadSocket != null)
                multicastSocketThreadSocket.close();
            if (multicastSocketThreadStdin != null)
                multicastSocketThreadStdin.close();
            return;
        }

        MesaVoto mesaVoto = new MesaVoto(ipMulticast, portoMulticast, ipRMI, portoRMI, nomeRMI, departamento, idTituloEleicao);

        if (mesaVoto.setClienteRemotoRMI() == ERRO) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "main", "setClienteRemotoRMI() == ERRO");
            return;
        }

        if (mesaVoto.validaMesaVoto() == ERRO) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "main", "validaMesaVoto() == ERRO");
            return;
        }

        MesaVotoStdin mesaVotoStdin = new MesaVotoStdin(mesaVoto, multicastSocketThreadStdin, grupoThreadStdin, portoMulticast);
        MesaVotoSocket mesaVotoSocket = new MesaVotoSocket(mesaVoto, multicastSocketThreadSocket, grupoThreadSocket, portoMulticast);

        Thread threadMesaVotoStdin = new Thread(mesaVotoStdin);
        Thread threadMesaVotoSocket = new Thread(mesaVotoSocket);

        threadMesaVotoSocket.start();
        threadMesaVotoStdin.start();

        fatorJoinAll = true;
        while (fatorJoinAll) {
            try {
                threadMesaVotoStdin.join();
                threadMesaVotoSocket.join();
                fatorJoinAll = false;
            } catch (InterruptedException e) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("MesaVoto", "main", "InterruptedException");
            }
        }
    }


    /**
     * Construtor
     *
     * @param ipMulticast IP Multicast (necessariamente igual ao dos TerminalVoto)
     * @param portoMulticast Porto Multicast (necessariamente igual ao dos TerminalVoto)
     * @param ipRMI IP RMI (necessariamente igual ao RMIServer)
     * @param portoRMI Porto RMI (necessariamente igual ao RMIServer)
     * @param nomeRMI Nome RMI (necessariamente igual ao RMIServer)
     * @param departamento Departamento onde esta localizado a MesaVoto (previamente registado no RMIServer)
     * @param idTituloEleicao Identificacao da eleicao: titulo (previamente registada no RMI Server)
     * @author Dario Felix
     * @version 1.0
     */
    public MesaVoto(String ipMulticast, int portoMulticast, String ipRMI, int portoRMI, String nomeRMI, String departamento, String idTituloEleicao) {
        this.ipMulticast = ipMulticast;
        this.portoMulticast = portoMulticast;
        this.ipRMI = ipRMI;
        this.portoRMI = portoRMI;
        this.nomeRMI = nomeRMI;
        this.departamento = departamento;
        this.idTituloEleicao = idTituloEleicao;
        this.clienteRemotoRMI = null;
        this.isValido = false;
        this.registoTerminalEleitor = new HashMap<>();
        this.countIdTerminal = 0;
    }


    /**
     * Antes de usar a MesaVoto, obter o objeto remoto.
     *
     * @return ERRO ou SUCESSO
     * @see RMIServer
     * @see MesaVoto#clienteRemotoRMI
     * @see LocateRegistry
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean setClienteRemotoRMI() {
        try {
            this.clienteRemotoRMI = (MulticastServerInterface) LocateRegistry.getRegistry(this.ipRMI, this.portoRMI).lookup(this.nomeRMI);
        } catch (Exception e) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "setClienteRemotoRMI", "Exception");
            return ERRO;
        }
        return SUCESSO;
    }


    /**
     * Antes de usar a Mesa de Voto, deve-se validar a Mesa de Voto junto do Server RMI atraves do objeto remoto.
     *
     * @return ERRO ou SUCESSO
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean validaMesaVoto() {
        if (this.clienteRemotoRMI == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "validaMesaVoto", "clienteRemotoRMI == null");
            return ERRO;

        } else if (this.isValido) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("MesaVoto", "validaMesaVoto", "já esta valido");

        } else {
            boolean fatorTentaNovamente = true;
            while (fatorTentaNovamente) {
                try {
                    this.clienteRemotoRMI.validarMesaVoto(this.idTituloEleicao, this.departamento);   // Se correr bem (== true)
                    this.isValido = true;
                    fatorTentaNovamente = false;
                } catch (EleicaoNaoComecouException | EleicaoNaoExistenteException | MesaVotoNaoRegistadaException | MesaVotoEmFuncionamentoException e1) {
                    ProjGeral.printErro("MesaVoto", "validaMesaVoto", e1.getMessage());
                    return ERRO;
                } catch (RemoteException e2) {
                    if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                        ProjGeral.printErro("MesaVoto", "validaMesaVoto", "Erro de conexão", e2);
                        return ERRO;
                    }
                }
            }
            if (INFO_RELEVANTE_ATIVO)
                ProjGeral.printAviso("MesaVoto Validado");

        }
        return SUCESSO;
    }


    /**
     * Get idTituloEleicao
     *
     * @return ID Titulo Eleição
     * @author Dario Felix
     * @version 1.0
     */
    public String getIdTituloEleicao() {
        return this.idTituloEleicao;
    }


    /**
     * Get departamento
     *
     * @return Local/Departamento
     * @author Dario Felix
     * @version 1.0
     */
    public String getDepartamento() {
        return this.departamento;
    }


    /**
     * Pesquisa por um eleitor na base de dados através do CC.
     *
     * @param cc Numero de cartao de cidadao (username)
     * @return ERRO caso nao exista ou SUCESSO caso exista o registo na base de dados
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean pesquisaEleitor(String cc) {
        if (!this.isValido) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "pesquisaEleitor", "isValido == false");
            return ERRO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.verificaEleitor(this.idTituloEleicao, cc);
                fatorTentaNovamente = false;
            } catch (PessoaNaoPodeVotarException e1) {
                System.out.printf("Eleitor não pode votar! (Msg Servidor: \"%s\")\n", e1.getMessage());
                return ERRO;
            } catch (PessoaNaoRegistadaException e2) {
                System.out.printf("Eleitor não registado! (Msg Servidor: \"%s\")\n", e2.getMessage());
                return ERRO;
            } catch (EleicaoNaoExistenteException e3) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVoto", "pesquisaEleitor", "EleicaoNaoExistenteException", e3);
                return ERRO;
            } catch (RemoteException e4) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("MesaVoto", "pesquisaEleitor", "Erro de conexão", e4);
                    return ERRO;
                }
            }
        }
        return SUCESSO;
    }


    /**
     * Tenta reconectar/recuperar o objeto remoto em caso de falha no Server RMI.
     * Se não conseguir reconectar em TEMPO_AVARIA_TEMPORARIA_MS ms emite um erro explicito.
     *
     * @return ERRO (erro grave) ou SUCESSO
     * @see ProjGeral#TEMPO_AVARIA_TEMPORARIA_MS
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    private boolean reconnectAndSetClienteRemotoRMI() {
        boolean fator;
        Instant fim;
        Duration duracao;

        this.isValido = false;
        Instant inicio = Instant.now();
        fator = true;
        while(fator) {
            try {
                this.clienteRemotoRMI = (MulticastServerInterface) LocateRegistry.getRegistry(this.ipRMI, this.portoRMI).lookup(this.nomeRMI);
                if (this.validaMesaVoto() == ERRO) {
                    if (INFO_ERRO_ATIVO)
                        ProjGeral.printErro("MesaVoto", "ReconnectAndSetClienteRemotoRMI", "validaMesaVoto() == ERRO");
                    return ERRO;
                }
                fator = false;
            } catch (Exception e) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("MesaVoto", "ReconnectAndSetClienteRemotoRMI", "Exception");
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
                            ProjGeral.printDebug("MesaVoto", "ReconnectAndSetClienteRemotoRMI", "InterruptedException");
                    }
                }
            }
        }
        return SUCESSO;
    }


    /**
     * Get proximo ID disponivel para atribuir a um novo terminal de voto.
     *
     * @return Novo ID
     * @author Dario Felix
     * @version 1.0
     */
    public int getNextAndAddCountIdTerminal () {
        return ++this.countIdTerminal;
    }


    /**
     * Registar um novo ID-Terminal-Voto na "base de dados" da Mesa de Voto.
     * @param newIdTerminal ID do novo Terminal de Voto
     * @return ERRO ou SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    public boolean addIdTerminal (String newIdTerminal) {
        if (newIdTerminal == null || newIdTerminal.isBlank()) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "addIdTerminal", "newIdTerminal is Blank");
            return ERRO;
        } else if (this.registoTerminalEleitor.containsKey(newIdTerminal)) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "addIdTerminal", "newIdTerminal ja existe");
            return ERRO;
        } else {
            this.registoTerminalEleitor.put(newIdTerminal, "");
        }
        return SUCESSO;
    }


    /**
     * Atualiza o par (ID-Terminal-Voto, Numero-CC-Username) na "base de dados" da Mesa de Voto.
     * Registo temporario dos ultimos acessos. Util para o vote().
     *
     * @param idTerminal ID Terminal de Voto
     * @param cc NumeroCC/Username do eleitor
     * @return ERRO ou SUCESSO
     * @author Dario Felix
     * @version 1.0
     */
    public boolean updateRegistoTerminalEleitor (String idTerminal, String cc) {
        if (idTerminal == null || idTerminal.isBlank()) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "updateRegistoTerminalEleitor", "newIdTerminal is Blank");
            return ERRO;
        } else if (!this.registoTerminalEleitor.containsKey(idTerminal)) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "updateRegistoTerminalEleitor", "newIdTerminal nao existe");
            return ERRO;
        } else if (cc == null || cc.isBlank()) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "updateRegistoTerminalEleitor", "cc is Blank");
            return ERRO;
        } else {
            this.registoTerminalEleitor.replace(idTerminal, cc);
        }
        return SUCESSO;
    }


    /**
     * Get NumeroCC/Username do eleitor através do ID-Terminal-Voto
     * @param idTerminal ID Terminal de Voto
     * @return NumeroCC/Username do eleitor em caso de SUCESSO, ou null em caso de ERRO
     * @author Dario Felix
     * @version 1.0
     */
    public String getEleitorByRegistoTerminalEleitor (String idTerminal) {
        if (idTerminal == null || idTerminal.isBlank()) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "getEleitorByRegistoTerminalEleitor", "newIdTerminal is Blank");
            return null;
        } else if (!this.registoTerminalEleitor.containsKey(idTerminal)) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "getEleitorByRegistoTerminalEleitor", "newIdTerminal nao existe");
            return null;
        }
        return this.registoTerminalEleitor.get(idTerminal);
    }


    /**
     * Get lista das listas candidatas á eleição.
     *
     * @return ArrayList das listas candidatas em caso de SUCESSO, ou null em caso de ERRO.
     * @author Dario Felix
     * @version 1.0
     */
    public ArrayList<String> getList() {
        if (!this.isValido) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "getList", "isValido == false");
            return null;
        }

        ArrayList<String> listaList = null;
        boolean fatorTentaNovamente = true;

        while (fatorTentaNovamente) {
            try {
                listaList = new ArrayList<>(this.clienteRemotoRMI.obterNomesListasCandidatas(this.idTituloEleicao));
                fatorTentaNovamente = false;
            } catch (EleicaoNaoExistenteException e1) {
                if (INFO_ERRO_ATIVO)
                    ProjGeral.printErro("MesaVoto", "getList", "EleicaoNaoExistenteException", e1);
                return null;
            } catch (RemoteException e2) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("MesaVoto", "getList", "Erro de conexão", e2);
                    return null;
                }
            }
        }
        return listaList;
    }


    /**
     * Imprime uma mensagem nas Admin Console através do RMI Server.
     *
     * @param msg mensagem a imprimir no Admin Console
     * @return ERRO ou SUCESSO
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean msgAdminConsole(String msg) {
        if (!this.isValido) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "msgAdminConsole", "isValido == false");
            return ERRO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                this.clienteRemotoRMI.sendMessageToAllConsoles(msg);
                fatorTentaNovamente = false;
            } catch (RemoteException e) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("MesaVoto", "msgAdminConsole", "Erro de conexão", e);
                    return ERRO;
                }
            }
        }
        return SUCESSO;
    }


    /**
     * Faz login de um eleitor através do RMI Server.
     *
     * @param cc NumeroCC/Username do eleitor
     * @param password Password do eleitor
     * @param cmdHashMap Map onde serão preenchidos os campos do comando a enviar posteriormente
     * @return ERRO ou SUCESSO
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean login(String cc, String password, HashMap<String, String> cmdHashMap) {
        if (!this.isValido) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "login", "isValido == false");
            return ERRO;
        }

        if (cmdHashMap == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "login", "cmdHashMap == null");
            return ERRO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                if (this.clienteRemotoRMI.autenticarEleitor(cc, password)){    // Se correr bem (== true)
                    cmdHashMap.put("logged", "true");
                } else {
                    cmdHashMap.put("logged", "false");
                }
                cmdHashMap.put("msg", "Sucesso!");
                fatorTentaNovamente = false;
            } catch (PessoaNaoRegistadaException | PasswordErradaException e1) {
                cmdHashMap.put("msg", e1.getMessage());
                cmdHashMap.put("logged", "false");
                return SUCESSO;
            } catch (RemoteException e2) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("MesaVoto", "login", "Erro de conexão", e2);
                    return ERRO;
                }
            }
        }
        return SUCESSO;
    }


    /**
     * Executa o metodo votar no server RMI.
     *
     * @param cc NumeroCC/Username do eleitor
     * @param lista lista (ou voto em branco) no qual o eleitor votou
     * @param cmdHashMap Map onde serão preenchidos os campos do comando a enviar posteriormente
     * @return ERRO ou SUCESSO
     * @see RMIServer
     * @see MulticastServerInterface
     * @author Dario Felix
     * @version 1.0
     */
    public boolean vote(String cc, String lista, HashMap<String, String> cmdHashMap) {
        if (!this.isValido) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "vote", "isValido == false");
            return ERRO;
        }

        if (cmdHashMap == null) {
            if (INFO_ERRO_ATIVO)
                ProjGeral.printErro("MesaVoto", "vote", "cmdHashMap == null");
            return ERRO;
        }

        boolean fatorTentaNovamente = true;
        while (fatorTentaNovamente) {
            try {
                if (this.clienteRemotoRMI.votar(this.idTituloEleicao, this.departamento, cc, lista)){    // Se correr bem (== true)
                    cmdHashMap.put("voted", "true");
                } else {
                    cmdHashMap.put("voted", "false");
                }
                cmdHashMap.put("msg", "Sucesso");
                fatorTentaNovamente = false;
            } catch (EleicaoNaoComecouException | EleicaoTerminouException | PessoaNaoPodeVotarException | PessoaVotouException | EleicaoNaoExistenteException | MesaVotoNaoRegistadaException | PessoaNaoRegistadaException e1) {
                cmdHashMap.put("msg", e1.getMessage());
                cmdHashMap.put("voted", "false");
                return SUCESSO;
            } catch (RemoteException e2) {
                if (this.reconnectAndSetClienteRemotoRMI() == ERRO) {
                    ProjGeral.printErro("MesaVoto", "vote", "Erro de conexão", e2);
                    return ERRO;
                }
            }
        }
        return SUCESSO;
    }
}