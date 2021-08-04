/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI;

import Outros.ProjGeral;
import ServerRMI.Data.*;
import ServerRMI.Exceptions.*;

import java.net.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.rmi.registry.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe que representa o Servidor RMI.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see UnicastRemoteObject
 * @see AdminConsoleInterface
 * @see MulticastServerInterface
 * @see Serializable
 */
public class RMIServer extends UnicastRemoteObject implements AdminConsoleInterface, MulticastServerInterface, Serializable, ProjGeral {

    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    boolean DEBUG_ATIVO = false;    // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    boolean INFO_ERRO_ATIVO = false;    // Default: false

    /**
     * Delay dos Heartbeats
     */
    private final long DELAY = 2000;

    /**
     * IP
     */
    private final String ip;

    /**
     * Porto
     */
    private final String port;

    /**
     * Nome
     */
    private final String name;

    /**
     * Nome do ficheiro de objectos que guarda a lista de eleições registadas na plataforma
     */
    private final String ficheiroEleicoes = "eleicoes";

    /**
     * Nome do ficheiro de objectos que guarda a lista de pessoas registadas na plataforma
     */
    private final String ficheiroPessoas = "pessoas";

    /**
     * Lista de eleições registadas na plataforma
     */
    private CopyOnWriteArrayList<Eleicao> eleicoes;

    /**
     * Lista de pessoas registadas na plataforma
     */
    private CopyOnWriteArrayList<Pessoa> pessoasRegistadas;

    /**
     * Lista de Consolas de Administração (AdminConsole) que se conectaram ao servidor RMI
     */
    private final CopyOnWriteArrayList<RMIInterfaceClient> consolas = new CopyOnWriteArrayList<>();

    /**
     * Mesas de Voto (MesaVoto) validadas e ligadas
     */
    private final CopyOnWriteArrayList<MesaEleicao> mesasVoto = new CopyOnWriteArrayList<>();

    /**
     * Gestor de ficheiros de objectos.
     */
    private final Storage storage = new Storage();

    /**
     * Cria e instancia um objecto da classe RMIServer.
     *
     * @param ip   IP
     * @param port Porto
     * @param name Nome da referência ao objecto remoto
     * @throws RemoteException RemoteException
     * @author Cláudia Campos
     * @version 1.0
     */
    protected RMIServer(String ip, String port, String name) throws RemoteException {
        super();
        this.ip = ip;
        this.port = port;
        this.name = name;
    }

    /**
     * Guarda os dados da aplicação para o ficheiro.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    private void savePessoas() {
        synchronized (this.storage) {
            this.storage.saveObjectFile(this.ficheiroPessoas, this.pessoasRegistadas);
        }
    }

    /**
     * Guarda os dados da aplicação para o ficheiro.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    private void saveEleicoes() {
        synchronized (this.storage) {
            this.storage.saveObjectFile(this.ficheiroEleicoes, this.eleicoes);
        }
    }

    /**
     * Carrega os dados do ficheiro para a aplicação.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    private void loadPessoas() {
        synchronized (this.storage) {
            CopyOnWriteArrayList<Pessoa> pessoas = (CopyOnWriteArrayList<Pessoa>) this.storage.loadObject(this.ficheiroPessoas);       //Corrigir Unchecked Cast
            this.pessoasRegistadas = Objects.requireNonNullElseGet(pessoas, CopyOnWriteArrayList::new);
        }
    }

    /**
     * Carrega os dados do ficheiro para a aplicação.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    private void loadEleicoes() {
        synchronized (this.storage) {
            CopyOnWriteArrayList<Eleicao> eleicoes = (CopyOnWriteArrayList<Eleicao>) this.storage.loadObject(this.ficheiroEleicoes);   //Corrigir Unchecked Cast
            this.eleicoes = Objects.requireNonNullElseGet(eleicoes, CopyOnWriteArrayList::new);
        }
    }

    /**
     * Obtém uma lista de Strings com o nome de todas as listas candidatas à eleição fornecida.
     *
     * @param nomeEleicao Strring: Título da Eleição
     * @return CopyOnWriteArrayList: Lista de nomes de todas as listas candidatas
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public CopyOnWriteArrayList<String> obterNomesListasCandidatas(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.getNomescandidaturas();
        }
    }

    /**
     * Obtém a pessoa que cujo número de cartão de cidadão corresponde ao número de cartão de cidadão fornecido, que
     * está registada na plataforma.
     *
     * @param numCC String: Número do Cartão de Cidadão
     * @return Pessoa: Pessoa Registada
     * @author Cláudia Campos
     * @version 1.0
     */
    private Pessoa getPessoaByNumCC(String numCC) {
        for (Pessoa pessoasRegistada : this.pessoasRegistadas) {
            if (pessoasRegistada.getNumCC().equals(numCC)) {
                return pessoasRegistada;
            }
        }
        return null;
    }

    /**
     * Adiciona uma Pessoa à plataforma.
     *
     * @param pessoa Pessoa: Pessoa a registar na plataforma
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaRegistadaException Se a pessoa a registar na plataforma já se encontra registada na plataforma.
     * @author Cláudia Campos
     * @version 1.0
     */
    private boolean addPessoa(Pessoa pessoa) throws PessoaRegistadaException {
        if (pessoa != null && this.getPessoaByNumCC(pessoa.getNumCC()) == null) {
            this.pessoasRegistadas.add(pessoa);
            this.savePessoas();
            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(pessoa.toString(), "addPessoa", "Pessoa adicionada com sucesso.");
            }
            return true;
        } else {
            throw new PessoaRegistadaException("A pessoa já se encontra registada.");
        }
    }

    /**
     * Remove uma Pessoa da plataforma
     *
     * @param pessoa Pessoa: Pessoa a eliminar da plataforma
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaNaoRegistadaException Se a pessoa a eliminar na plataforma não se encontra registada na plataforma.
     * @author Cláudia Campos
     * @version 1.0
     */
    private boolean removePessoa(Pessoa pessoa) throws PessoaNaoRegistadaException {
        if (pessoa != null && this.getPessoaByNumCC(pessoa.getNumCC()) != null) {
            this.pessoasRegistadas.remove(pessoa);
            this.savePessoas();
            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(pessoa.toString(), "removePessoa", "Pessoa removida com sucesso.");
            }
            return true;        //Pessoa removida com sucesso
        } else {
            throw new PessoaNaoRegistadaException("A pessoa não se encontra registada.");
        }
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean registarEstudante(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numEstudante) throws PessoaRegistadaException {
        return this.addPessoa(new Estudante(nome, contacto, morada, codigoPostal, numCC, new GregorianCalendar(validadeAno, (validadeMes - 1), validadeDia), faculdade, departamento, password, numEstudante));
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean registarDocente(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password, String numMec) throws PessoaRegistadaException {
        return this.addPessoa(new Docente(nome, contacto, morada, codigoPostal, numCC, new GregorianCalendar(validadeAno, (validadeMes - 1), validadeDia), faculdade, departamento, password, numMec));
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean registarFuncionario(String nome, String contacto, String morada, String codigoPostal, String numCC, int validadeDia, int validadeMes, int validadeAno, String faculdade, String departamento, String password) throws PessoaRegistadaException {
        return this.addPessoa(new Funcionario(nome, contacto, morada, codigoPostal, numCC, new GregorianCalendar(validadeAno, (validadeMes - 1), validadeDia), faculdade, departamento, password));
    }

    /**
     * Verifica se uma determinada pessoa pode votar em determinada eleição.
     *
     * @param nomeEleicao String: Nome da Eleição
     * @param numCC       String: Número de Cartão de Cidadão da Pessoa
     * @return boolean: Se o eleitor está elegivel para votar na eleição
     * @throws EleicaoNaoExistenteException Se a eleição não existe.
     * @throws PessoaNaoRegistadaException  Se a pessoa não se encontra registada na plataforma.
     * @throws PessoaNaoPodeVotarException  Se a pessoa não está elegível para votar na eleição.
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean verificaEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException, PessoaNaoPodeVotarException {
        Eleicao eleicao;
        Pessoa pessoa;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if ((pessoa = getPessoaByNumCC(numCC)) == null) {
            throw new PessoaNaoRegistadaException("A pessoa \"" + numCC + "\" não existe.");
        } else if (pessoa.getNumEstudante() != null && pessoa.getNumMec() == null && eleicao.isEstudantes() ||
                pessoa.getNumEstudante() == null && pessoa.getNumMec() != null && eleicao.isDocentes() ||
                pessoa.getNumEstudante() == null && pessoa.getNumMec() == null && eleicao.isFuncionarios()) {
            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(pessoa.toString(), "verificaEleitor", "A Pessoa pode votar na eleição \"" + eleicao.getTitulo() + "\".");
            }
            return true;
        } else {
            throw new PessoaNaoPodeVotarException("A pessoa \"" + pessoa.getNumCC() + "\" não pode votar na eleição \"" + nomeEleicao + "\".");
        }
    }

    /**
     * Permite que uma pessoa se autentique na plataforma fornecendo para esse efeito o seu número de cartão de cidadão
     * e a sua password.
     *
     * @param numCC    String: Número de Cartão de Cidadão da Pessoa
     * @param password String: Password da Pessoa
     * @return boolean: Operação efectuada com sucesso
     * @throws PessoaNaoRegistadaException Se a pessoa não se encontra registada na plataforma.
     * @throws PasswordErradaException     Se a password fornecida não corresponde à password da pessoa.
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean autenticarEleitor(String numCC, String password) throws PessoaNaoRegistadaException, PasswordErradaException {
        Pessoa pessoa = getPessoaByNumCC(numCC);
        if (pessoa != null && pessoa.getPassword().equals(password)) {

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(pessoa.toString(), "autenticarEleitor", "Utilizador \"" + pessoa.getNumCC() + "\" autenticado, password correcta.");
            }

            return true;
        } else if (pessoa != null) {
            throw new PasswordErradaException("Utilizador " + numCC + " não autenticado, password errada.");
        } else {
            throw new PessoaNaoRegistadaException("O Utilizador " + numCC + "não existe.");
        }
    }

    //TODO Questão? Podemos adicionar mesas de voto enquanto a eleição está a decorrer? - Esta verificação não está a ser feita.

    /**
     * Adiciona uma Mesa de Voto a uma determinada Eleição.
     *
     * @param nomeEleicao             String: Nome da Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou Departamento
     * @return boolean: Operação efectuada com sucesso.
     * @throws DadosInvalidosException      Se o nome da Mesa de Voto corresponde a uma string vazia ou a um ponteiro NULL.
     * @throws EleicaoNaoExistenteException Se a Eleição é inexistente.
     * @throws MesaVotoRegistadaException   Se a Mesa de Voto já se encontra associada à eleição.
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean adicionarMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws DadosInvalidosException, EleicaoNaoExistenteException, MesaVotoRegistadaException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            boolean res = eleicao.addMesaVoto(faculdadeOuDepartamento);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug("adicionarMesaVoto", "A Mesa de voto localizada em \"" + faculdadeOuDepartamento + "\" foi adicionada à eleição \"" + eleicao.getTitulo() + "\" com sucesso.");
            }

            return res;
        }
    }

    /**
     * Remove uma Mesa de Voto de determinada Eleição.
     *
     * @param nomeEleicao             String: Nome da Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou Departamento
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException       Se o nome da Mesa de Voto corresponde a uma string vazia ou a um ponteiro NULL.
     * @throws EleicaoNaoExistenteException  Se a Eleição é inexistente.
     * @throws MesaVotoNaoRegistadaException Se a Mesa de Voto não se encontra associada à eleição.
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean removeMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws DadosInvalidosException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            boolean res = eleicao.removeMesaVoto(faculdadeOuDepartamento);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug("removeMesaVoto", "A Mesa de voto localizada em \"" + faculdadeOuDepartamento + "\" foi removida da eleição \"" + eleicao.getTitulo() + "\" com sucesso.");
            }

            return res;
        }
    }

    /**
     * Obtém a Eleição que cujo nome corresponde ao nome fornecido.
     *
     * @param titulo String: Título da Eleição
     * @return Eleicao: Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    private Eleicao getEleicaoByTitulo(String titulo) {
        for (Eleicao eleicao : this.eleicoes) {
            if (eleicao.getTitulo().equals(titulo)) {
                return eleicao;
            }
        }
        return null;
    }

    /**
     * Regista uma Eleição na plataforma.
     *
     * @param eleicao Eleicao: Eleicao
     * @return boolean: Operação efectuada com sucesso
     * @throws EleicaoExistenteException Se a eleição já se encontra registada na plataforma.
     * @author Cláudia Campos
     * @version 1.0
     */
    private boolean addEleicao(Eleicao eleicao) throws EleicaoExistenteException {
        if (eleicao != null && getEleicaoByTitulo(eleicao.getTitulo()) == null) {
            this.eleicoes.add(eleicao);     //TODO
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(eleicao.toString(), "addEleicao", "A Eleição \"" + eleicao.getTitulo() + "\" foi criada com sucesso.");
            }

            return true;        //Eleição inserida com sucesso
        } else {
            throw new EleicaoExistenteException("A Eleição já se encontra registada.");
        }
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public synchronized boolean criarEleicao(String titulo, String descricao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutosInicio, int diaFim, int mesFim, int anoFim, int horaFim, int minutosFim, boolean estudantes, boolean docentes, boolean funcionarios) throws EleicaoExistenteException {
        boolean res = this.addEleicao(new Eleicao(titulo, descricao, new GregorianCalendar(anoInicio, mesInicio - 1, diaInicio, horaInicio, minutosInicio), new GregorianCalendar(anoFim, (mesFim - 1), diaFim, horaFim, minutosFim), estudantes, docentes, funcionarios));
        try {
            Objects.requireNonNull(getEleicaoByTitulo(titulo)).addMesaVoto("Online");
        } catch (DadosInvalidosException | MesaVotoRegistadaException e) {
            return false;
        }
        this.notifyAll();
        return res;
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean alterarTituloEleicao(String nomeEleicao, String novoTitulo) throws DadosInvalidosException, EleicaoNaoExistenteException, EleicaoExistenteException, EleicaoComecouException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if (this.getEleicaoByTitulo(novoTitulo) != null) {
            throw new EleicaoExistenteException("O novo nome da eleição corresponde ao nome de uma eleição já existente.");
        } else {
            boolean res = eleicao.setTitulo(novoTitulo);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(eleicao.toString(), "alterarTituloEleicao", "O título da eleição \"" + eleicao.getTitulo() + "\" foi alterado com sucesso.");
            }

            return res;
        }
    }

    /**
     * Permite alterar a descrição de determinada Eleição.
     *
     * @param nomeEleicao   String: Título da Eleição
     * @param novaDescricao String: Nova Descrição da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException      Se a nova descrição é inválida.
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws EleicaoComecouException      Se a eleição já começou.
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean alterarDescricaoEleicao(String nomeEleicao, String novaDescricao) throws DadosInvalidosException, EleicaoNaoExistenteException, EleicaoComecouException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            boolean res = eleicao.setDescricao(novaDescricao);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(eleicao.toString(), "alterarDescricaoEleicao", "A descrição da eleição \"" + eleicao.getTitulo() + "\" foi alterada com sucesso.");
            }

            return res;
        }
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean alterarDataInicioEleicao(String nomeEleicao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutoInicio) throws EleicaoNaoExistenteException, DadosInvalidosException, EleicaoComecouException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            boolean res = eleicao.setInicio(new GregorianCalendar(anoInicio, (mesInicio - 1), diaInicio, horaInicio, minutoInicio));
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(eleicao.toString(), "alterarDataInicioEleicao", "A data de início da eleição \"" + eleicao.getTitulo() + "\" foi alterada com sucesso.");
            }

            return res;
        }
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public synchronized boolean alterarDataFimEleicao(String nomeEleicao, int diaFim, int mesFim, int anoFim, int horaFim, int minutoFim) throws EleicaoNaoExistenteException, DadosInvalidosException, EleicaoComecouException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            boolean res = eleicao.setFim(new GregorianCalendar(anoFim, (mesFim - 1), diaFim, horaFim, minutoFim));
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(eleicao.toString(), "alterarDataInicioEleicao", "A data de início da eleição \"" + eleicao.getTitulo() + "\" foi alterada com sucesso.");
            }

            this.notifyAll();

            return res;
        }
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean adicionarListaAEleicao(String nomeEleicao, String nomeLista, CopyOnWriteArrayList<String> membrosNumCC) throws MembroNaoPertenceListaException, DadosInvalidosException, EleicaoNaoExistenteException, EleicaoComecouException, CandidaturaExistenteException, MembroPertenceListaException, PessoaNaoRegistadaException {
        Eleicao eleicao;

        if (nomeEleicao == null || nomeEleicao.isBlank() || nomeEleicao.isEmpty() || (eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            if (eleicao.hasStarted() || eleicao.hasEnded() || eleicao.isTakingPlace()) {
                throw new EleicaoComecouException("Não é possível adicionar a lista uma vez que a eleição já iniciou.");
            } else if (nomeLista == null || nomeLista.isBlank() || nomeLista.isEmpty()) {
                throw new DadosInvalidosException("O nome da lista é inválido.");
            } else if (eleicao.getCandidaturaByNome(nomeLista) != null) {
                throw new CandidaturaExistenteException("Não foi possível inserir a lista candidata \"" + nomeLista +
                        "\" porque esta já está regista na eleição \"" + nomeEleicao + "\"");
            } else if (membrosNumCC.isEmpty()) {
                throw new DadosInvalidosException("Não foi possível criar a lista \"" + nomeLista + "\" uma vez que " +
                        "esta não possui membros.");
            } else {
                Candidatura candidatura = criaCandidatura(eleicao, nomeLista, membrosNumCC);
                boolean res = eleicao.addCandidatura(candidatura);
                this.saveEleicoes();

                if (DEBUG_ATIVO) {
                    ProjGeral.printDebug(eleicao.toString(), "adicionarListaAEleicao", "A lista \"" + candidatura.getNome() + "\" foi adicionada à eleição \"" + eleicao.getTitulo() + "\" com sucesso.");
                }

                return res;
            }
        }
    }

    /**
     * Método auxiliar que cria e instancia um objecto da classe Candidatura, fazendo as verificações necessárias.
     *
     * @param eleicao      Eleicao: Eleição
     * @param nomeLista    String: Nome da Lista Candidata
     * @param membrosNumCC CopyOnWriteArrayList: Lista de números de Cartões de Cidadão dos Membros da nova Lista Candidata
     * @return Candidatura: Nova Candidatura
     * @throws MembroNaoPertenceListaException Se um dos membros não se encontra elegível para participar na Eleição.
     * @throws DadosInvalidosException         Se a Lista de membros se encontra vazia.
     * @throws PessoaNaoRegistadaException     Se o membro a inserir na Lista Candidata não se encontra registado na plataforma.
     * @throws MembroPertenceListaException    Se o membro a inserir na Lista Candidata já nela se encontra.
     * @author Cláudia Campos
     * @version 1.0
     */
    private Candidatura criaCandidatura(Eleicao eleicao, String nomeLista, CopyOnWriteArrayList<String> membrosNumCC) throws MembroNaoPertenceListaException, DadosInvalidosException, PessoaNaoRegistadaException, MembroPertenceListaException {
        Candidatura candidatura = new Candidatura(nomeLista);
        Pessoa membro;

        for (String numCC : membrosNumCC) {
            if ((membro = this.getPessoaByNumCC(numCC)) != null) {
                if (membro.getNumEstudante() != null && membro.getNumMec() == null && !eleicao.isEstudantes() ||
                        membro.getNumEstudante() == null && membro.getNumMec() != null && !eleicao.isDocentes() ||
                        membro.getNumEstudante() == null && membro.getNumMec() == null && !eleicao.isFuncionarios()) {
                    throw new MembroNaoPertenceListaException("A pessoa \"" + membro.getNumCC() + "\" não pode fazer " +
                            "parte da lista candidata \"" + nomeLista + "\" da eleição \"" + eleicao.getTitulo() + "\".");
                }
                candidatura.addPessoa(membro);
            } else {
                throw new PessoaNaoRegistadaException("Não foi possível adicionar a pessoa cujo número de cartão de " +
                        "cidadão é \"" + numCC + "\" à lista \"" + nomeLista + "\", uma vez que esta não se encontra " +
                        "registada na plataforma.");
            }
        }

        return candidatura;
    }

    /**
     * Adiciona uma Consola de Administração (objecto remoto) à plataforma.
     *
     * @param adminConsole RMIInterfaceClient: Consola de Administração
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public void addAdminConsole(RMIInterfaceClient adminConsole) {
        this.consolas.add(adminConsole);

        if (DEBUG_ATIVO) {
            ProjGeral.printDebug("addAdminConsole", "> Admin console added.");
        }

    }

    /**
     * Envia uma mensagem (String) a todas as Consolas de Administração.
     *
     * @param message String: Mensagem
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public void sendMessageToAllConsoles(String message) {
        for (RMIInterfaceClient console : this.consolas) {
            this.sendMessageToConsole(console, message);
        }
    }

    /**
     * Envia uma mensagem (String) a uma determinada Consola de Administração.
     *
     * @param console RMIInterfaceClient: Consola de Administração
     * @param message String: Mensagem
     * @author Cláudia Campos
     * @version 1.0
     */
    private void sendMessageToConsole(RMIInterfaceClient console, String message) {
        try {
            console.printOnAdminConsole(message);   //Envia mensagem à consola (RMI Client)
        } catch (RemoteException ex) {
            if (DEBUG_ATIVO) {
                ProjGeral.printDebug("sendMessageToConsole", "> Não foi possível enviar dados à consola.");
            }

            this.consolas.remove(console);          //Remove objecto da lista uma vez que está incontactável (porque obteve uma RemoteException)
        }
    }

    /**
     * Obtém o local do Voto de um Eleitor em determinada Eleição.
     *
     * @param nomeEleicao String: Nome da Eleição
     * @param numCC       String: Número do Cartão de Cidadão
     * @return String: Local de voto do Eleitor
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws PessoaNaoRegistadaException  Se a pessoa não se encontra registada na plataforma.
     * @author Cláudia Campos
     * @version 1.0
     */
    public String obterLocalVotoEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException {
        Eleicao eleicao;
        Pessoa pessoa;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if ((pessoa = getPessoaByNumCC(numCC)) == null) {
            throw new PessoaNaoRegistadaException("A pessoa \"" + numCC + "\" não existe.");
        } else {
            return eleicao.getLocalVotoEleitor(pessoa);
        }
    }

    /**
     * Envia uma os resultados das eleições anteriores à Consola de Administração que efectuou o pedido.
     *
     * @param console RMIInterfaceClient: Consola de Administração
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public void imprimeResultadosEleicoesAnteriores(RMIInterfaceClient console) {
        String message = "";
        for (Eleicao eleicao : this.eleicoes) {
            if (eleicao.hasEnded()) {
                message = message.concat(eleicao.toString());
            }
        }
        if (message.isEmpty()) {
            message = "Nenhuma informação a apresentar.";
        }
        this.sendMessageToConsole(console, message);
    }

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
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean votar(String nomeEleicao, String faculdadeOuDepartamento, String numCC, String nomeLista) throws PessoaNaoPodeVotarException, PessoaVotouException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, PessoaNaoRegistadaException, EleicaoTerminouException, EleicaoNaoComecouException {
        Eleicao eleicao;
        Pessoa pessoa;
        Candidatura candidatura;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if (!eleicao.hasStarted()) {
            throw new EleicaoNaoComecouException("A eleição \"" + nomeEleicao + "\" ainda não começou.");
        } else if (eleicao.hasEnded()) {
            throw new EleicaoTerminouException("A eleição \"" + nomeEleicao + "\" já terminou.");
        } else if (eleicao.getMesaVoto(faculdadeOuDepartamento) == null) {
            throw new MesaVotoNaoRegistadaException("A mesa de voto \"" + faculdadeOuDepartamento + "\" não existe.");
        } else if ((pessoa = getPessoaByNumCC(numCC)) == null) {
            throw new PessoaNaoRegistadaException("A pessoa \"" + numCC + "\" não existe.");
        } else if (pessoa.getNumEstudante() != null && pessoa.getNumMec() == null && !eleicao.isEstudantes() ||
                pessoa.getNumEstudante() == null && pessoa.getNumMec() != null && !eleicao.isDocentes() ||
                pessoa.getNumEstudante() == null && pessoa.getNumMec() == null && !eleicao.isFuncionarios()) {
            throw new PessoaNaoPodeVotarException("A pessoa \"" + pessoa.getNumCC() + "\" não pode votar na eleição \"" + nomeEleicao + "\".");
        } else if (eleicao.getVotoByPessoaNumCC(pessoa.getNumCC()) != null) {
            throw new PessoaVotouException("A pessoa \"" + pessoa.getNumCC() + "\" já votou na eleição \"" + eleicao.getTitulo() + "\".");
        } else if (nomeLista.isEmpty()) {    //Voto em branco
            Voto voto = new Voto(pessoa, faculdadeOuDepartamento, new GregorianCalendar());
            eleicao.incrementaVotosEmBranco();
            eleicao.addVotos(voto);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(voto.toString(), "votar", "Voto em branco.");
            }

            //this.sendMessageToAllConsoles("A pessoa \"" + pessoa.getNumCC() + "\" votou para a eleição \"" + eleicao.getTitulo() + "\" na mesa de voto \"" + faculdadeOuDepartamento + "\".\n" + eleicao.countVotantesMesaVoto(faculdadeOuDepartamento) + " pessoas votaram na mesa de voto \"" + faculdadeOuDepartamento + "\" até ao momento nesta eleição.");
            this.sendMessageToAllConsoles(eleicao.countVotantesMesaVoto(faculdadeOuDepartamento) + " pessoas votaram na mesa de voto \"" + faculdadeOuDepartamento + "\" na eleição \"" + eleicao.getTitulo() + "\".");

            return true;
        } else if ((candidatura = eleicao.getCandidaturaByNome(nomeLista)) == null) {        //Voto nulo????
            //throw new CandidaturaNaoExistenteException("A lista candidata \"" + nomeLista + "\" não exite.");
            Voto voto = new Voto(pessoa, faculdadeOuDepartamento, new GregorianCalendar());
            eleicao.incrementaVotosNulos();
            eleicao.addVotos(voto);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(voto.toString(), "votar", "Voto nulo.");
            }

            this.sendMessageToAllConsoles(eleicao.countVotantesMesaVoto(faculdadeOuDepartamento) + " pessoas votaram na mesa de voto \"" + faculdadeOuDepartamento + "\" na eleição \"" + eleicao.getTitulo() + "\".");

            return true;
        } else {
            Voto voto = new Voto(pessoa, faculdadeOuDepartamento, new GregorianCalendar());
            candidatura.incrementaVotos();
            eleicao.addVotos(voto);
            this.saveEleicoes();

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(voto.toString(), "votar", "Voto numa lista candidata");
            }

            this.sendMessageToAllConsoles(eleicao.countVotantesMesaVoto(faculdadeOuDepartamento) + " pessoas votaram na mesa de voto \"" + faculdadeOuDepartamento + "\" na eleição \"" + eleicao.getTitulo() + "\".");

            return true;
        }
    }

    /**
     * Ao invocar o método estático Naming.lookup() o programa vai obter uma referência para um objecto remoto presente
     * numa dada máquina. Que irá corresponder a um objecto da classe RMIServer.
     * Porém se este método fizer throw de uma RemoteException significa que o registry não pode ser contactado, ou
     * seja, não será possível obter o objecto remoto uma vez que este não se encontra no endereço especificado.
     * Se esta excepção ocorrer, significa que a primeira instância deste programa ainda não criou um registry
     * que contenha a referência para o objecto remoto. Quando a instância do programa fizer catch desta excepção vai
     * criar o registry no porto configurado e assumir o papel de servidor RMI primário.
     *
     * @return Retorna true quando já existe um servidor primário, retorna false caso contrário.
     * @author Cláudia Campos
     * @version 1.0
     * @see Naming
     * @see RemoteException
     * @see NotBoundException
     * @see MalformedURLException
     */
    public boolean existsPrimary() {
        try {
            Naming.lookup(String.format("rmi://%s:%s/%s", this.ip, this.port, this.name));
            return true;
        } catch (RemoteException | NotBoundException | MalformedURLException ex) {          //TODO Fazer melhor handle das exceptions
            return false;
        }
    }

    /**
     * A instância da classe que chamar este método assume o papel de servidor RMI Primário:
     * - Faz load dos dados dos ficheiros de objectos, que estão devidamente atualizados;
     * - Cria um registry no porto configurado ao qual irá fazer rebind do seu próprio objecto (instância da classe
     * RMIServer);
     * - A sua thread principal obtém a próxima eleição a terminar, calcula o número de milisegundos até à data de fim
     * desta eleição, e espera esse número de milisegundos. Após esta espera, notifica todas as consolas de
     * administração ligadas de que a eleição terminou e envia o seu resultado.
     *
     * @author Cláudia Campos
     * @version 1.0
     * @see Naming
     * @see RemoteException
     * @see NotBoundException
     * @see MalformedURLException
     */
    public void launchPrimary() {
        try {
            this.loadPessoas();
            this.loadEleicoes();
            Registry reg = LocateRegistry.createRegistry(Integer.parseInt(this.port));
            reg.rebind(this.name, this);
            //reg.rebind("admin", this);
            System.out.println("> Primary RMI Server ready...");
        } catch (Exception e) {
            System.out.println("Exception in RMIServer.main: " + e.getMessage());
            e.printStackTrace();
        }

        while (true) {
            synchronized (this) {
                try {
                    Eleicao eleicao = this.getElectionClosestToEnd();
                    if (eleicao == null) {
                        this.wait();
                    } else {
                        this.wait(eleicao.getFim().getTimeInMillis() - (new GregorianCalendar()).getTimeInMillis());
                        this.sendMessageToAllConsoles("Eleição Terminda:\n" + eleicao.toString());
                    }
                } catch (InterruptedException ex) {
                    System.out.println("Interrupted...");
                }
            }
        }

    }

    /**
     * Método que é chamado pelo servidor RMI secundário para testar periodicamente (DELAY milisegundos) o Servidor RMI
     * primário.
     *
     * @author Cláudia Campos
     * @version 1.0
     * @see Naming
     * @see RemoteException
     * @see NotBoundException
     * @see MalformedURLException
     * @see Thread
     */
    public void checkPrimary() {
        while (this.existsPrimary()) {
            try {
                synchronized (this) {
                    wait(this.DELAY);
                }
            } catch (InterruptedException ex) {
                System.out.println("InterruptedException: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    /**
     * A função main recebe três argumentos: o ip da máquina que vai criar o objecto remoto, o porto onde o RMI Registry
     * vai correr e o nome ao qual o objecto remoto ficará associado.
     * Primeiramente o programa verifica se existe um servidor RMI primário a executar. Se existir, a instância deste
     * programa vai assumir o papel de servidor RMI secundário e vai testar periódicamente o servidor RMI primário.
     * Se não existir, a instância vai assumir o papel de servidor RMI primário.
     * Em caso de falha do servidor RMI primário o servidor RMI secundário assume o seu papel.
     * Caso o servidor RMI primário recupere da falha, assume o papel se servidor RMI secundário.
     *
     * @param args Argumentos da função main: args[0] = ip; args[1] = porto; args[2] = nome;
     * @author Cláudia Campos
     * @version 1.0
     * @see Naming
     * @see RemoteException
     * @see NotBoundException
     * @see MalformedURLException
     * @see Thread
     */
    public static void main(String[] args) {
        if (args.length == 3) {
            try {
                RMIServer server = new RMIServer(args[0], args[1], args[2]);

                if (server.existsPrimary()) {
                    System.out.println("> Secondary RMI Server ready...");
                    server.checkPrimary();
                }

                server.launchPrimary();

            } catch (RemoteException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Usage: ./RMIServer <ip> <port> <name>");
        }
    }

    /**
     * Procura por um objecto MesaEleicao, presente na lista de objectos MesaEleicao, cuja eleição e local correspondem
     * aos fornecidos.
     *
     * @param eleicao                 Eleicao: Eleição
     * @param faculdadeOuDepartamento String: Nome da Faculdade ou Departamento
     * @return MesaEleicao: Par Mesa de Voto - Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    private MesaEleicao getMesaEleicaoByEleicaoAndLocal(Eleicao eleicao, String faculdadeOuDepartamento) {
        MesaEleicao mesaEleicao = null;
        for (MesaEleicao m : this.mesasVoto) {
            if (eleicao.equals(m.getEleicao()) && faculdadeOuDepartamento.equals(m.getFaculdadeOuDepartamento())) {
                mesaEleicao = m;
            }
        }
        return mesaEleicao;
    }

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
     * @author Cláudia Campos
     * @version 1.0
     * @see MulticastServer.MesaVoto
     */
    @Override
    public boolean validarMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) throws EleicaoNaoComecouException, EleicaoNaoExistenteException, MesaVotoNaoRegistadaException, MesaVotoEmFuncionamentoException {
        Eleicao eleicao;
        String mesaVoto;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if ((mesaVoto = eleicao.getMesaVoto(faculdadeOuDepartamento)) == null) {
            throw new MesaVotoNaoRegistadaException("A mesa de voto da eleição \"" + eleicao.getTitulo() + "\" em \"" +
                    faculdadeOuDepartamento + "\" não se encontra registada.");
        } else if (getMesaEleicaoByEleicaoAndLocal(eleicao, faculdadeOuDepartamento) != null) {
            throw new MesaVotoEmFuncionamentoException("A mesa de voto da eleição \"" + eleicao.getTitulo() + "\" em \""
                    + faculdadeOuDepartamento + "\" já se encontra em funcionamento.");
        } else if (!eleicao.isTakingPlace()) {
            throw new EleicaoNaoComecouException("A eleição \"" + eleicao.getTitulo() + "\" ainda não começou.\n");
        } else {
            this.mesasVoto.add(new MesaEleicao(eleicao, mesaVoto));
            this.saveEleicoes();
            this.sendMessageToAllConsoles("Nova Mesa de voto da eleição \"" + eleicao.getTitulo() + "\" foi adicionada "
                    + "em \"" + mesaVoto + "\".");

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug(mesaVoto, "validarMesaVoto", "Mesa de Voto validada.");
            }

            return true;
        }
    }

    /**
     * Permite que os RMI Clients verifiquem a conectividade com o RMI Server.
     *
     * @return boolean
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public boolean isAlive() {
        return true;
    }

    /**
     * Obtém a Eleição cuja data de fim é a mais próxima da data actual.
     *
     * @return Eleicao
     * @author Cláudia Campos
     * @version 1.0
     */
    public synchronized Eleicao getElectionClosestToEnd() {
        long milliseconds = Long.MAX_VALUE;
        Eleicao eleicao = null;
        GregorianCalendar now = new GregorianCalendar();
        for (Eleicao e : this.eleicoes) {
            if (!e.hasEnded()) {
                if (e.getFim().getTimeInMillis() - now.getTimeInMillis() < milliseconds) {
                    milliseconds = e.getFim().getTimeInMillis() - now.getTimeInMillis();
                    eleicao = e;
                }
            }
        }
        return eleicao;
    }
    
    @Override
    public CopyOnWriteArrayList<String> getEleicoes() {
        CopyOnWriteArrayList<String> eleicoes = new CopyOnWriteArrayList<>();
        for (Eleicao eleicao : this.eleicoes) {
            eleicoes.add(eleicao.getTitulo());
        }
        return eleicoes;
    }

    /**
     * Obtém a Data do Voto de um Eleitor em determinada Eleição.
     *
     * @param nomeEleicao String: Nome da Eleição
     * @param numCC       String: Número do Cartão de Cidadão
     * @return String: Data de voto do Eleitor
     * @throws EleicaoNaoExistenteException Se a eleição é inexistente.
     * @throws PessoaNaoRegistadaException  Se a pessoa não se encontra registada na plataforma.
     * @author Cláudia Campos
     * @version 1.0
     */
    public String obterDataVotoEleitor(String nomeEleicao, String numCC) throws EleicaoNaoExistenteException, PessoaNaoRegistadaException {
        Eleicao eleicao;
        Pessoa pessoa;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else if ((pessoa = getPessoaByNumCC(numCC)) == null) {
            throw new PessoaNaoRegistadaException("A pessoa \"" + numCC + "\" não existe.");
        } else {
            return eleicao.getDataVotoEleitor(pessoa);
        }
    }

    public CopyOnWriteArrayList<String> obterPessoasEleicao(String nomeEleicao) throws EleicaoNaoExistenteException, RemoteException {
        Eleicao eleicao;
        CopyOnWriteArrayList<String> pessoas = new CopyOnWriteArrayList<>();
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            for (Pessoa pessoa : this.pessoasRegistadas) {
                System.out.println(pessoa);
                if (pessoa.getNumEstudante() != null && pessoa.getNumMec() == null && eleicao.isEstudantes() ||
                        pessoa.getNumEstudante() == null && pessoa.getNumMec() != null && eleicao.isDocentes() ||
                        pessoa.getNumEstudante() == null && pessoa.getNumMec() == null && eleicao.isFuncionarios()) {
                    pessoas.add(pessoa.getNumCC());
                    System.out.println("Pode pertencer à lista.");
                }
            }
            return pessoas;
        }
    }

    /**
     *
     * @param nomeEleicao
     * @return
     */
    public String getDescricaoEleicao(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.getDescricao();
        }
    }

    public String getTituloEleicao(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.getTitulo();
        }
    }

    public String getDataInicioEleicao(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.getInicio().toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm"));
        }
    }

    public String getDataFimEleicao(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.getFim().toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm"));
        }
    }

    public String getEleicao(String nomeEleicao) throws EleicaoNaoExistenteException {
        Eleicao eleicao;
        if ((eleicao = this.getEleicaoByTitulo(nomeEleicao)) == null) {
            throw new EleicaoNaoExistenteException("A eleição \"" + nomeEleicao + "\" não existe.");
        } else {
            return eleicao.toString();
        }
    }

}