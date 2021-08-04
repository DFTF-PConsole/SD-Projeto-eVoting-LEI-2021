/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI;

import ServerRMI.Exceptions.*;

import java.rmi.*;
import java.util.concurrent.CopyOnWriteArrayList;


/**
 * Interface implementada pelo Servidor RMI, com os métodos que o Multicast Server (MesaVoto) pode invocar.
 *
 * @author Cláudia Campos
 * @version 1.0
 */
public interface MulticastServerInterface extends Remote {

    /**
     * Permite que uma pessoa se autentique na plataforma fornecendo para esse efeito o seu número de cartão de cidadão
     * e a sua password.
     *
     * @param numCC    String: Número de Cartão de Cidadão da Pessoa
     * @param password String: Password da Pessoa
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaNaoRegistadaException Se a pessoa não se encontra registada na plataforma.
     * @throws PasswordErradaException     Se a password fornecida não corresponde à password da pessoa.
     * @throws RemoteException             RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean autenticarEleitor(String numCC, String password) throws PessoaNaoRegistadaException, PasswordErradaException, RemoteException;

    /**
     * Envia uma mensagem (String) a todas as Consolas de Administração.
     *
     * @param message String: Mensagem
     * @throws RemoteException RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    void sendMessageToAllConsoles(String message) throws RemoteException;

    /**
     * Verifica se uma determinada pessoa pode votar em determinada eleição.
     *
     * @param nomeEleicao String: Nome da Eleição
     * @param numCC       String: Número de Cartão de Cidadão da Pessoa
     * @return boolean: Se o eleitor está elegivel para votar na eleição
     * @throws EleicaoNaoExistenteException Se a eleição não existe.
     * @throws PessoaNaoRegistadaException  Se a pessoa não se encontra registada na plataforma.
     * @throws PessoaNaoPodeVotarException  Se a pessoa não está elegível para votar na eleição.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean verificaEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException, PessoaNaoPodeVotarException, RemoteException;

    /**
     * Permite que um eleitor vote numa determinada eleição.
     *
     * @param nomeEleicao             String: Nome da Eleição
     * @param faculdadeOuDepartamento String: Nome do Faculdade
     * @param numCC                   String: Número do Cartão de Cidadão do votante
     * @param nomeLista               String: Nome da lista escolhida
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaNaoPodeVotarException   Se a Pessoa não se encontra elegível para votar na Eleição.
     * @throws PessoaVotouException          Se a Pessoa já votou na Eleição.
     * @throws EleicaoNaoExistenteException  Se a eleição é inexistente.
     * @throws MesaVotoNaoRegistadaException Se a Mesa de Voto não se encontra registada.
     * @throws PessoaNaoRegistadaException   Se a Pessoa não se encontra registada na plataforma.
     * @throws RemoteException               RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean votar(String nomeEleicao, String faculdadeOuDepartamento, String numCC, String nomeLista) throws EleicaoTerminouException, PessoaNaoPodeVotarException, PessoaVotouException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, PessoaNaoRegistadaException, EleicaoNaoComecouException, RemoteException;

    /**
     * Valida a Mesa de Voto (MesaVoto) e regista-a na plataforma
     *
     * @param nomeEleicao             String: Título da Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou do Departamento
     * @return boolean: Operação efectuada com sucesso
     * @throws EleicaoNaoComecouException       Se a eleição ainda não começou.
     * @throws EleicaoNaoExistenteException     Se a eleição é inexistente.
     * @throws MesaVotoNaoRegistadaException    Se a Mesa de Voto não se encontra registada na Eleição.
     * @throws MesaVotoEmFuncionamentoException Se a Mesa de Voto já se encontra em funcionamento
     * @throws RemoteException                  RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     * @see MulticastServer.MesaVoto
     */
    boolean validarMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws EleicaoNaoComecouException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, MesaVotoEmFuncionamentoException, RemoteException;

    /**
     * Obtém uma lista de Strings com o nome de todas as listas candidatas à eleição fornecida.
     *
     * @param nomeEleicao Strring: Título da Eleição
     * @return CopyOnWriteArrayList: Lista de nomes de todas as listas candidatas
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    CopyOnWriteArrayList<String> obterNomesListasCandidatas(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @return
     * @throws RemoteException  RemoteException.
     * @author
     */
    CopyOnWriteArrayList<String> getEleicoes() throws RemoteException;

}