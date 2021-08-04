/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI;

import ServerRMI.Exceptions.*;

import java.rmi.*;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Adicionar protecções

/**
 * Interface implementada pelo Servidor RMI, com os métodos que a Consola de Administração (AdminConsole) pode invocar.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see RMIServer
 */
public interface AdminConsoleInterface extends Remote {

    /**
     * Adiciona uma Eleição à plataforma.
     *
     * @param titulo        String: Título da Eleição
     * @param descricao     String: Descrição da Eleição
     * @param diaInicio     int: Dia de início da Eleição
     * @param mesInicio     int: Mês de início da Eleição
     * @param anoInicio     int: Ano de início da Eleição
     * @param horaInicio    int: Hora de início da Eleição
     * @param minutosInicio int: Minuto de início da Eleição
     * @param diaFim        int: Dia de fim da Eleição
     * @param mesFim        int: Mês de fim da Eleição
     * @param anoFim        int: Ano de fim da Eleição
     * @param horaFim       int: Hora de fim da Eleição
     * @param minutosFim    int: Minuto de fim da Eleição
     * @param estudantes    boolean: Permite que estudantes votem na Eleição
     * @param docentes      boolean: Permite que docentes votem na Eleição
     * @param funcionarios  boolean: Permite que funcionarios votem na Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws EleicaoExistenteException Se a eleição já se encontra registada na plataforma.
     * @throws RemoteException           RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean criarEleicao(String titulo, String descricao, int diaInicio, int mesInicio, int anoInicio,
                         int horaInicio, int minutosInicio, int diaFim, int mesFim, int anoFim, int horaFim,
                         int minutosFim, boolean estudantes, boolean docentes, boolean funcionarios)
            throws EleicaoExistenteException, RemoteException;

    /**
     * Adiciona uma Consola de Administração (objecto remoto) à plataforma.
     *
     * @param adminConsole RMIInterfaceClient: Consola de Administração
     * @throws RemoteException RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    void addAdminConsole(RMIInterfaceClient adminConsole) throws RemoteException;

    /**
     * Regista um Estudante na plataforma.
     *
     * @param nome         String: Nome do Estudante
     * @param contacto     String: Número de Telefone do Estudante
     * @param morada       String: Morada do Estudante
     * @param codigoPostal String: Código-Postal do Estudante
     * @param numCC        String: Número do Cartão de Cidadão do Estudante
     * @param validadeDia  int: Dia da Validade do Cartão de Cidadão do Estudante
     * @param validadeMes  int: Mês da Validade do Cartão de Cidadão do Estudante
     * @param validadeAno  int: Ano da Validade do Cartão de Cidadão do Estudante
     * @param faculdade    String: Faculdade do Estudante
     * @param departamento String: Departamento do Estudante
     * @param password     String: Password do Estudante
     * @param numEstudante String: Número de Estudante
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaRegistadaException Se o Estudante a registar na plataforma já se encontra registada na plataforma.
     * @throws RemoteException          RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean registarEstudante(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numEstudante) throws PessoaRegistadaException, RemoteException;

    /**
     * Regista um Docente na plataforma.
     *
     * @param nome         String: Nome do Docente
     * @param contacto     String: Número de Telefone do Docente
     * @param morada       String: Morada do Docente
     * @param codigoPostal String: Código-Postal do Docente
     * @param numCC        String: Número do Cartão de Cidadão do Docente
     * @param validadeDia  int: Dia da Validade do Cartão de Cidadão do Docente
     * @param validadeMes  int: Mês da Validade do Cartão de Cidadão do Docente
     * @param validadeAno  int: Ano da Validade do Cartão de Cidadão do Docente
     * @param faculdade    String: Faculdade do Docente
     * @param departamento String: Departamento do Docente
     * @param password     String: Password do Docente
     * @param numMec       String: Número Mecanográfico do Docente
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaRegistadaException Se o Docente a registar na plataforma já se encontra registada na plataforma.
     * @throws RemoteException          RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean registarDocente(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numMec) throws PessoaRegistadaException, RemoteException;

    /**
     * Regista um Funcionário na plataforma.
     *
     * @param nome         String: Nome do Funcionário
     * @param contacto     String: Número de Telefone do Funcionário
     * @param morada       String: Morada do Funcionário
     * @param codigoPostal String: Código-Postal do Funcionário
     * @param numCC        String: Número do Cartão de Cidadão do Funcionário
     * @param validadeDia  int: Dia da Validade do Cartão de Cidadão do Funcionário
     * @param validadeMes  int: Mês da Validade do Cartão de Cidadão do Funcionário
     * @param validadeAno  int: Ano da Validade do Cartão de Cidadão do Funcionário
     * @param faculdade    String: Faculdade do Funcionário
     * @param departamento String: Departamento do Funcionário
     * @param password     String: Password do Funcionário
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaRegistadaException Se o Funcionário a registar na plataforma já se encontra registada na plataforma.
     * @throws RemoteException          RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean registarFuncionario(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password) throws PessoaRegistadaException, RemoteException;

    /**
     * Envia uma os resultados das eleições anteriores à Consola de Administração que efectuou o pedido.
     *
     * @param console RMIInterfaceClient: Consola de Administração
     * @throws RemoteException RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    void imprimeResultadosEleicoesAnteriores(RMIInterfaceClient console) throws RemoteException;

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
     * Adiciona uma Mesa de Voto a uma determinada Eleição.
     * Questão? Podemos adicionar mesas de voto enquanto a eleição está a decorrer? - Esta verificação não está a ser feita.
     *
     * @param nomeEleicao             String: Nome da Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou Departamento
     * @return boolean: Operação efectuada com sucesso.
     * @throws DadosInvalidosException      Se o nome da Mesa de Voto corresponde a uma string vazia ou a um ponteiro NULL.
     * @throws EleicaoNaoExistenteException Se a Eleição é inexistente.
     * @throws MesaVotoRegistadaException   Se a Mesa de Voto já se encontra associada à eleição.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean adicionarMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws DadosInvalidosException, EleicaoNaoExistenteException, MesaVotoRegistadaException, RemoteException;

    /**
     * Remove uma Mesa de Voto de determinada Eleição.
     *
     * @param nomeEleicao             String: Nome da Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou Departamento
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException       Se o nome da Mesa de Voto corresponde a uma string vazia ou a um ponteiro NULL.
     * @throws EleicaoNaoExistenteException  Se a Eleição é inexistente.
     * @throws MesaVotoNaoRegistadaException Se a Mesa de Voto não se encontra associada à eleição.
     * @throws RemoteException               RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean removeMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws DadosInvalidosException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, RemoteException;

    /**
     * Permite alterar o título de determinada Eleição.
     *
     * @param nomeEleicao String: Título da Eleição
     * @param novoTitulo  String: Novo Título da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException      Se o título é inválido.
     * @throws EleicaoNaoExistenteException Se a eleição não existe.
     * @throws EleicaoExistenteException    Se o novo título da eleição corresponde ao título de uma eleição existente.
     * @throws EleicaoComecouException      Se a eleição já começou.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean alterarTituloEleicao(String nomeEleicao, String novoTitulo) throws DadosInvalidosException, EleicaoNaoExistenteException, EleicaoExistenteException, EleicaoComecouException, RemoteException;

    /**
     * Permite alterar a descrição de determinada Eleição.
     *
     * @param nomeEleicao   String: Título da Eleição
     * @param novaDescricao String: Nova Descrição da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException      Se a nova descrição é inválida.
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws EleicaoComecouException      Se a eleição já começou.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean alterarDescricaoEleicao(String nomeEleicao, String novaDescricao) throws DadosInvalidosException, EleicaoNaoExistenteException, EleicaoComecouException, RemoteException;

    /**
     * Permite alterar a data de início de determinada Eleição.
     *
     * @param nomeEleicao  String: Título da Eleição
     * @param diaInicio    int: Dia de Início da Eleição
     * @param mesInicio    int: Mês de Início da Eleição
     * @param anoInicio    int: Ano de Início da Eleição
     * @param horaInicio   int: Hora de Início da Eleição
     * @param minutoInicio int: Minuto de Início da Eleição
     * @return boolean: Operação Efectuada com sucesso
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws DadosInvalidosException      Se a nova data de início é inválida.
     * @throws EleicaoComecouException      Se a eleição já começou.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean alterarDataInicioEleicao(String nomeEleicao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutoInicio) throws EleicaoNaoExistenteException, DadosInvalidosException, EleicaoComecouException, RemoteException;

    /**
     * Permite alterar a data de fim de determinada Eleição.
     *
     * @param nomeEleicao String: Título da Eleição
     * @param diaFim      int: Dia de Fim da Eleição
     * @param mesFim      int: Mês de Fim da Eleição
     * @param anoFim      int: Ano de Fim da Eleição
     * @param horaFim     int: Hora de Fim da Eleição
     * @param minutoFim   int: Minuto de Fim da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws DadosInvalidosException      Se a nova data de fim é inválida.
     * @throws EleicaoComecouException      Se a eleição já começou.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean alterarDataFimEleicao(String nomeEleicao, int diaFim, int mesFim, int anoFim, int horaFim, int minutoFim) throws EleicaoNaoExistenteException, DadosInvalidosException, EleicaoComecouException, RemoteException;

    /**
     * Obtém o local do Voto de um Eleitor em determinada Eleição.
     *
     * @param nomeEleicao String: Nome da Eleição
     * @param numCC       String: Número do Cartão de Cidadão
     * @return String: Local de voto do Eleitor
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws PessoaNaoRegistadaException  Se a pessoa não se encontra registada na plataforma.
     * @throws RemoteException              RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    String obterLocalVotoEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException, RemoteException;

    /**
     * Permite adicionar uma Lista Candidata a uma determinada Eleição.
     *
     * @param nomeEleicao  String: Título da Eleição
     * @param nomeLista    String: Nome da Lista Candidata
     * @param membrosNumCC CopyOnWriteArrayList: Lista de números de Cartões de Cidadão dos Membros da Lista Candidata
     * @return boolean: Operação efectuada com sucesso
     * @throws MembroNaoPertenceListaException Se um dos membros não se encontra elegível para participar na Eleição.
     * @throws DadosInvalidosException         Se o nome da Lista Candidata é inválido ou se a Lista de membros se encontra vazia.
     * @throws EleicaoNaoExistenteException    Se a eleição é inexistente.
     * @throws EleicaoComecouException         Se a eleição já começou.
     * @throws CandidaturaExistenteException   Se já existe uma Lista Candidata registada com o mesmo nome.
     * @throws MembroPertenceListaException    Se o membro a inserir na Lista Candidata já nela se encontra.
     * @throws PessoaNaoRegistadaException     Se o membro a inserir na Lista Candidata não se encontra registado na plataforma.
     * @throws RemoteException                 RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean adicionarListaAEleicao(String nomeEleicao, String nomeLista, CopyOnWriteArrayList<String> membrosNumCC) throws MembroNaoPertenceListaException, DadosInvalidosException, EleicaoNaoExistenteException, EleicaoComecouException, CandidaturaExistenteException, MembroPertenceListaException, PessoaNaoRegistadaException, RemoteException;

    CopyOnWriteArrayList<String> getEleicoes() throws RemoteException;

    /**
     * Permite que os RMI Clients verifiquem a conectividade com o RMI Server.
     *
     * @return boolean
     * @throws RemoteException RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean isAlive() throws RemoteException;

    /**
     *
     * @param nomeEleicao
     * @param numCC
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws PessoaNaoRegistadaException
     * @throws RemoteException
     */
    String obterDataVotoEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException, RemoteException;

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
     * @param nomeEleicao
     * @return
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    CopyOnWriteArrayList<String> obterPessoasEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @param faculdadeOuDepartamento
     * @param numCC
     * @param nomeLista
     * @return
     * @throws PessoaNaoPodeVotarException
     * @throws PessoaVotouException
     * @throws EleicaoNaoExistenteException
     * @throws MesaVotoNaoRegistadaException
     * @throws PessoaNaoRegistadaException
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    boolean votar(String nomeEleicao, String faculdadeOuDepartamento, String numCC, String nomeLista) throws EleicaoTerminouException, PessoaNaoPodeVotarException, PessoaVotouException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, PessoaNaoRegistadaException, EleicaoNaoComecouException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    String getDescricaoEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    String getTituloEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    String getDataInicioEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    String getDataFimEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

    /**
     *
     * @param nomeEleicao
     * @return
     * @throws EleicaoNaoExistenteException
     * @throws RemoteException RemoteException.
     * @author Cláudia Campos
     * @version 1.0
     */
    String getEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException;

}