/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import ServerRMI.Exceptions.*;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

//TODO Adicionar protecções

/**
 * Classe que vai armazenar todos os dados relativos a uma Eleição: título, descrição, quem pode votar, datas de início
 * e fim, locais de voto, listas candidatas e todos os dados relativos aos votos.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see CopyOnWriteArrayList
 * @see Serializable
 */
public class Eleicao implements Serializable {

    /**
     * Título da Eleição
     */
    private String titulo;

    /**
     * Descrição da Eleição
     */
    private String descricao;

    /**
     * Data de início da Eleição
     */
    private GregorianCalendar inicio;

    /**
     * Data de fim da Eleição
     */
    private GregorianCalendar fim;

    /**
     * Eleição admite estudantes
     */
    private boolean estudantes;

    /**
     * Eleição admite docentes
     */
    private boolean docentes;

    /**
     * Eleição admite funcionários
     */
    private boolean funcionarios;

    /**
     * Lista de mesas de voto da Eleição
     */
    private final CopyOnWriteArrayList<String> mesasVoto = new CopyOnWriteArrayList<>();

    /**
     * Lista de listas candidatas à Eleição
     */
    private final CopyOnWriteArrayList<Candidatura> candidaturas = new CopyOnWriteArrayList<>();

    /**
     * Lista de comprovativos de voto da Eleição
     */
    private final CopyOnWriteArrayList<Voto> votos = new CopyOnWriteArrayList<>();

    /**
     * Número de votos nulos da Eleição
     */
    private int votosNulos;

    /**
     * Número de votos em branco da Eleição
     */
    private int votosEmBranco;

    /**
     * Cria e instancia um objecto da classe Eleicao.
     *
     * @param titulo       String: Título da Eleição
     * @param descricao    String: Descrição da Eleição
     * @param inicio       GregorianCalendar: Data de Início da Eleição
     * @param fim          GregorianCalendar: Data de Fim da Eleição
     * @param estudantes   boolean: Permite que estudantes votem nesta Eleição
     * @param docentes     boolean: Permite que docentes votem nesta Eleição
     * @param funcionarios boolean: Permite que funcionários votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public Eleicao(String titulo, String descricao, GregorianCalendar inicio, GregorianCalendar fim, boolean estudantes, boolean docentes, boolean funcionarios) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.inicio = inicio;
        this.fim = fim;
        this.estudantes = estudantes;
        this.docentes = docentes;
        this.funcionarios = funcionarios;
        this.votosNulos = 0;
        this.votosEmBranco = 0;
    }

    /**
     * Verifica se os estudantes podem votar nesta Eleição.
     *
     * @return boolean: Permite que estudantes votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean isEstudantes() {
        return this.estudantes;
    }

    /**
     * Modifica a possibilidade ou impossibilidade de estudantes votarem nesta Eleição.
     *
     * @param estudantes boolean: Permite que estudantes votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setEstudantes(boolean estudantes) {
        this.estudantes = estudantes;
    }

    /**
     * Verifica se os docentes podem votar nesta Eleição.
     *
     * @return boolean: Permite que docentes votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean isDocentes() {
        return this.docentes;
    }

    /**
     * Modifica a possibilidade ou impossibilidade de docentes votarem nesta Eleição.
     *
     * @param docentes boolean: Permite que docentes votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setDocentes(boolean docentes) {
        this.docentes = docentes;
    }

    /**
     * Verifica se os funcionários podem votar nesta Eleição.
     *
     * @return boolean: Permite que funcionários votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean isFuncionarios() {
        return this.funcionarios;
    }

    /**
     * Modifica a possibilidade ou impossibilidade de funcionários votarem nesta Eleição.
     *
     * @param funcionarios boolean: Permite que funcionários votem nesta Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setFuncionarios(boolean funcionarios) {
        this.funcionarios = funcionarios;
    }

    /**
     * Obtém o título actual da Eleição.
     *
     * @return String: Título da Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getTitulo() {
        return this.titulo;
    }

    /**
     * Obtém a descrição actual da Eleição.
     *
     * @return String: Descrição da Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getDescricao() {
        return this.descricao;
    }

    /**
     * Obtém a data de início actual da Eleição.
     *
     * @return GregorianCalendar: Data de início da Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public GregorianCalendar getInicio() {
        return this.inicio;
    }

    /**
     * Obtém a data de fim actual da Eleição.
     *
     * @return GregorianCalendar: Data de fim da Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public GregorianCalendar getFim() {
        return this.fim;
    }

    /**
     * Obtém o número actual de Votos Nulos nesta Eleição.
     *
     * @return int: Número de Votos Nulos
     * @author Cláudia Campos
     * @version 1.0
     */
    public int getVotosNulos() {
        return this.votosNulos;
    }

    /**
     * Obtém o número actual de Votos em Branco nesta Eleição.
     *
     * @return int: Número de Votos em Branco
     * @author Cláudia Campos
     * @version 1.0
     */
    public int getVotosEmBranco() {
        return this.votosEmBranco;
    }

    /**
     * Obtém o conjunto ordenado das Listas Candidatas a esta Eleição.
     *
     * @return CopyOnWriteArrayList: Lista de listas candidatas a esta Eleição.
     * @author Cláudia Campos
     * @version 1.0
     */
    public CopyOnWriteArrayList<Candidatura> getCandidaturas() {
        return this.candidaturas;
    }

    /**
     * Permite modificar o título da Eleição antes de esta iniciar.
     *
     * @param titulo String: Novo título da Eleição.
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException Se o novo título da Eleição é inválido.
     * @throws EleicaoComecouException Se a eleição já começou.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean setTitulo(String titulo) throws DadosInvalidosException, EleicaoComecouException {
        GregorianCalendar now = new GregorianCalendar();
        if (titulo == null || titulo.isEmpty() || titulo.isBlank()) {
            throw new DadosInvalidosException("O novo título da Eleição \"" + this.titulo + "\" é inválido.");
        } else if (now.after(this.inicio)) {
            throw new EleicaoComecouException("O Título da Eleição \"" + this.titulo + "\" não foi alterado porque a eleição já começou.");
        } else {    //if (now.before(this.inicio))
            this.titulo = titulo;
            System.out.println("Título alterado.");     //debug
            return true;
        }
    }

    /**
     * Permite modificar a descrição da Eleição antes de esta iniciar.
     *
     * @param descricao String: Nova Descrição da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException Se a nova descrição da Eleição é inválida.
     * @throws EleicaoComecouException Se a eleição já começou.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean setDescricao(String descricao) throws DadosInvalidosException, EleicaoComecouException {
        GregorianCalendar now = new GregorianCalendar();
        if (descricao == null || descricao.isEmpty() || descricao.isBlank()) {
            throw new DadosInvalidosException("A nova desrição da Eleição \"" + this.titulo + "\" é inválida.");
        } else if (now.after(this.inicio)) {
            throw new EleicaoComecouException("A Descrição da Eleição \"" + this.titulo + "\" não foi alterada porque a eleição já começou.");
        } else {    //if (now.before(this.inicio))
            this.descricao = descricao;
            System.out.println("Descrição alterada.");      //debug
            return true;
        }
    }

    /**
     * Permite modificar a data de início da Eleição antes de esta iniciar.
     *
     * @param inicio GregorianCalendar: Nova data de início da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException Se a data corresponde a um ponteiro NULL ou se a data de início fornecida é anterior à data actual.
     * @throws EleicaoComecouException Se a eleição já começou.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean setInicio(GregorianCalendar inicio) throws DadosInvalidosException, EleicaoComecouException {
        GregorianCalendar now = new GregorianCalendar();
        if (inicio == null) {
            throw new DadosInvalidosException("Data de início inválida.");
        } else if (now.after(this.inicio)) {
            throw new EleicaoComecouException("A data de início da Eleição \"" + this.titulo + "\" não foi alterada porque a eleição já começou.");
        } else if (inicio.before(now)) {
            throw new DadosInvalidosException("Data de início inválida: A data de início fornecida é anterior à data atual.");
        } else {
            this.inicio = inicio;
            System.out.println("Data de início alterada.");
            return true;
        }
    }

    /**
     * Permite modificar a data de fim da Eleição antes de esta iniciar.
     *
     * @param fim GregorianCalendar: Nova data de fim da Eleição
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException Se a data corresponde a um ponteiro NULL ou se a data de fim fornecida é anterior à data de início.
     * @throws EleicaoComecouException Se a eleição já começou.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean setFim(GregorianCalendar fim) throws DadosInvalidosException, EleicaoComecouException {
        GregorianCalendar now = new GregorianCalendar();
        if (fim == null) {
            throw new DadosInvalidosException("Data de fim inválida.");
        } else if (now.after(this.inicio)) {
            throw new EleicaoComecouException("A data de fim da Eleição \"" + this.titulo + "\" não foi alterada porque a eleição já começou.");
        } else if (fim.before(this.inicio)) {
            throw new DadosInvalidosException("Data de fim inválida: A data de fim fornecida é anterior à data de início actual.");
        } else {
            this.fim = fim;
            System.out.println("Data de fim alterada."); //DEBUG
            return true;
        }
    }

    /**
     * Incrementa o número de votos nulos desta Eleição em uma unidade.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    public void incrementaVotosNulos() {
        this.votosNulos++;
    }

    /**
     * Incrementa o número de votos em branco desta Eleição em uma unidade.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    public void incrementaVotosEmBranco() {
        this.votosEmBranco++;
    }

    /**
     * Obtém a mesa de voto cujo local corresponde ao nome da faculdade ou departamento fornecido.
     *
     * @param faculdadeOuDepartamento String: Nome de uma Faculdade ou de um Departamento
     * @return String: Nome de uma Faculdade ou de um Departamento
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getMesaVoto(String faculdadeOuDepartamento) {
        String mesaVoto = null;
        for (String mv : this.mesasVoto) {
            if (mv.equals(faculdadeOuDepartamento)) {
                mesaVoto = mv;
            }
        }
        return mesaVoto;
    }

    /**
     * Adiciona uma mesa de voto à Eleição.
     *
     * @param faculdadeOuDepartamento String: Nome de uma Faculdade ou de um Departamento
     * @return boolean: Operação efectuada com sucesso.
     * @throws DadosInvalidosException    Se o parâmetro faculdadeOuDepartamento corresponde a um ponteiro NULL ou se a String se encontra vazia.
     * @throws MesaVotoRegistadaException Se a mesa de voto a adicionar já se encontra registada.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean addMesaVoto(String faculdadeOuDepartamento) throws DadosInvalidosException, MesaVotoRegistadaException {
        if (faculdadeOuDepartamento == null || faculdadeOuDepartamento.isEmpty() || faculdadeOuDepartamento.isBlank()) {
            throw new DadosInvalidosException("A faculdade ou o departamento é inválida/o.");
        } else if (this.getMesaVoto(faculdadeOuDepartamento) != null) {
            throw new MesaVotoRegistadaException("A mesa de voto em \"" + faculdadeOuDepartamento + "\" já se encontra associada à eleição \"" + this.titulo + "\".");
        } else {
            this.mesasVoto.add(faculdadeOuDepartamento);        //Adiciona mesa de voto com sucesso
            return true;
        }
    }

    /**
     * Remove uma mesa de voto à Eleição.
     *
     * @param faculdadeOuDepartamento String: Nome de uma Faculdade ou de um Departamento
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException       Se o parâmetro faculdadeOuDepartamento corresponde a um ponteiro NULL ou se a String se encontra vazia.
     * @throws MesaVotoNaoRegistadaException Se a mesa de voto a remover não se encontra registada.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean removeMesaVoto(String faculdadeOuDepartamento) throws DadosInvalidosException, MesaVotoNaoRegistadaException {
        if (faculdadeOuDepartamento == null || faculdadeOuDepartamento.isEmpty() || faculdadeOuDepartamento.isBlank()) {
            throw new DadosInvalidosException("A faculdade ou o departamento é inválida/o.");
        } else if (this.getMesaVoto(faculdadeOuDepartamento) == null) {
            throw new MesaVotoNaoRegistadaException("A mesa de voto em \"" + faculdadeOuDepartamento + "\" não se encontra associada à eleição \"" + this.titulo + "\".");
        } else {
            this.mesasVoto.remove(faculdadeOuDepartamento);     //Remova mesa de voto com sucesso
            return true;
        }
    }

    /**
     * Adiciona uma lista candidata da Eleição.
     *
     * @param candidatura Candidatura: Lista Candidata à Eleição
     * @return boolean: Operação efectuada com sucesso
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean addCandidatura(Candidatura candidatura) {
        this.candidaturas.add(candidatura);
        return true;
    }

    /**
     * Remove uma lista candidata da Eleição.
     *
     * @param candidatura Candidatura: Lista Candidata à Eleição
     * @return boolean: Operação efectuada com sucesso
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean removeCandidatura(Candidatura candidatura) {
        this.candidaturas.remove(candidatura);
        return true;
    }

    /**
     * Obtém a lista candidata cujo nome corresponde ao fornecido.
     *
     * @param nomeCandidatura String: Nome da Lista Candidata à Eleição
     * @return Candidatura: Lista Candidata à Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public Candidatura getCandidaturaByNome(String nomeCandidatura) {
        Candidatura candidatura = null;
        for (Candidatura c : this.candidaturas) {
            if (nomeCandidatura.equals(c.getNome())) {
                candidatura = c;
            }
        }
        return candidatura;
    }

    /**
     * Adiciona um Comprovativo de Votação à Eleição.
     *
     * @param voto Voto: Comprovativo de Votação
     * @return boolean: Operação efectuada com sucesso
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean addVotos(Voto voto) {
        this.votos.add(voto);
        return true;
    }

    /**
     * Método que soma todos os votos de todas as candidaturas para esta Eleição.
     *
     * @return int: Número total de votos de todas as listas/candidaturas.
     * @author Cláudia Campos
     * @version 1.0
     */
    public int somaVotosListas() {
        int soma = 0;
        for (Candidatura candidatura : this.candidaturas) {
            soma += candidatura.getVotos();
        }
        return soma;
    }

    /**
     * Método que calcula o número total de votos para esta Eleição.
     *
     * @return int: Número Total de Votos
     * @author Cláudia Campos
     * @version 1.0
     */
    public int getTotalVotos() {
        return this.somaVotosListas() + this.votosNulos + this.votosEmBranco;
    }

    /**
     * Determina se a Eleição já terminou.
     *
     * @return boolean: true se a eleição terminou, false se a eleição não terminou
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean hasEnded() {
        GregorianCalendar now = new GregorianCalendar();
        if (now.after(this.fim)) {
            System.out.println("A Eleição " + this.titulo + " encontra-se terminada.");         //debug
            return true;
        } else {
            System.out.println("A Eleição " + this.titulo + " não se encontra terminada.");     //debug
            return false;
        }
    }

    /**
     * Determina se a Eleição já começou.
     *
     * @return boolean: true se a eleição começou, false se a eleição não começou
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean hasStarted() {
        GregorianCalendar now = new GregorianCalendar();
        if (now.after(this.inicio)) {
            System.out.println("Eleição " + this.titulo + " já começou.");
            return true;
        } else {
            System.out.println("Eleição " + this.titulo + " não começou.");
            return false;
        }
    }

    /**
     * Determina se a Eleição está a decorrer.
     *
     * @return boolean: Está a decorrer
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean isTakingPlace() {
        return this.hasStarted() && !this.hasEnded();
    }

    /**
     * Obtém os dados da votação (Voto) da pessoa cujo cartão de cidadão corresponde ao fornecido.
     *
     * @param numCC String: Número do Cartão de Cidadão
     * @return Voto: Dados da Votação
     * @author Cláudia Campos
     * @version 1.0
     */
    public Voto getVotoByPessoaNumCC(String numCC) {
        Voto voto = null;
        for (Voto v : this.votos) {
            if (v.getVotante().getNumCC().equals(numCC)) {
                voto = v;
            }
        }
        return voto;
    }

    /**
     * Obtém o local de votação de determinada pessoa.
     *
     * @param pessoa Pessoa: Eleitor
     * @return String: Local de Votação do Eleitor
     * @throws PessoaNaoRegistadaException Se a pessoa não votou na Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getLocalVotoEleitor(Pessoa pessoa) throws PessoaNaoRegistadaException {
        Voto voto;
        if ((voto = this.getVotoByPessoaNumCC(pessoa.getNumCC())) == null) {
            throw new PessoaNaoRegistadaException("A pessoa não votou na eleição \"" + this.titulo + "\".");        //PessoaNaoVotouException
        } else {
            return voto.getMesaVoto();
        }
    }

    /**
     * Obtém a data da votação de determinada pessoa.
     *
     * @param pessoa Pessoa: Eleitor
     * @return String: Data de Votação do Eleitor
     * @throws PessoaNaoRegistadaException Se a pessoa não votou na Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getDataVotoEleitor(Pessoa pessoa) throws PessoaNaoRegistadaException {
        Voto voto;
        if ((voto = this.getVotoByPessoaNumCC(pessoa.getNumCC())) == null) {
            throw new PessoaNaoRegistadaException("A pessoa não votou na eleição \"" + this.titulo + "\".");        //PessoaNaoVotouException
        } else {
            return voto.getDataVotacao().toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/yyy HH:mm"));
        }
    }

    /**
     * Obtém uma lista de Strings com o nome de todas as listas candidatas à Eleição.
     *
     * @return CopyOnWriteArrayList: Lista de nomes de todas as listas candidatas
     * @author Cláudia Campos
     * @version 1.0
     */
    public CopyOnWriteArrayList<String> getNomescandidaturas() {
        CopyOnWriteArrayList<String> nomesListasCandidatas = new CopyOnWriteArrayList<>();
        for (Candidatura candidatura : this.candidaturas) {
            nomesListasCandidatas.add(candidatura.getNome());
        }
        return nomesListasCandidatas;
    }

    /**
     * Conta o número de votantes que votaram numa determinada mesa de voto.
     *
     * @param mesaVoto String: Nome da faculdade ou departamento onde a mesa de voto se encontra
     * @return int: Número de votantes
     * @author Cláudia Campos
     * @version 1.0
     */
    public int countVotantesMesaVoto(String mesaVoto) {
        int soma = 0;
        for (String m : this.mesasVoto) {
            if (m.equals(mesaVoto)) {
                soma++;
            }
        }
        return soma;
    }

    /**
     * Formata a data de início da eleição no formato "dd/MM/uuuu H:m", isto é, dia/mês/ano hora:minuto.
     *
     * @return String: Data de início da eleição formatada
     * @author Cláudia Campos
     * @version 1.0
     */
    public String dataInicioFormat() {
        return this.inicio.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu H:m"));
    }

    /**
     * Formata a data de fim da eleição no formato "dd/MM/uuuu H:m", isto é, dia/mês/ano hora:minuto.
     *
     * @return String: Data de fim da eleição formatada
     * @author Cláudia Campos
     * @version 1.0
     */
    public String dataFimFormat() {
        return this.fim.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu H:m"));
    }

    /**
     * Obtém a percentagem de votos nulos da Eleição.
     *
     * @return double: Percentagem de votos nulos
     * @author Cláudia Campos
     * @version 1.0
     */
    public double getPercentagemVotosNulos() {
        if (this.getTotalVotos() == 0) {     //Evita DivisionByZeroException
            return 0.0;
        } else {
            return ((double) this.votosNulos / (double) this.getTotalVotos()) * 100.0;
        }
    }

    /**
     * Obtém a percentagem de votos em branco da Eleição.
     *
     * @return double: Percentagem de votos em branco
     * @author Cláudia Campos
     * @version 1.0
     */
    public double getPercentagemVotosEmBranco() {
        if (this.getTotalVotos() == 0) {     //Evita DivisionByZeroException
            return 0.0;
        } else {
            return ((double) this.votosEmBranco / (double) this.getTotalVotos()) * 100.0;
        }
    }

    /**
     * Converte a informação do objecto numa String.
     *
     * @return String
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String toString() {
        NumberFormat formatter = new DecimalFormat("#0.00");
        int numeroTotalVotos = this.getTotalVotos();        //Número total de votos
        String message = "Título da Eleição: " + this.titulo + "\n\n" +
                "Descrição da Eleição: " + this.descricao + "\n\n" +
                "Data de início da Eleição: " + this.dataInicioFormat() + "\n\n" +
                "Data de fim da Fim: " + this.dataFimFormat() + "\n\n" +
                "Mesas de Voto: ";
        for (String mesaVoto : this.mesasVoto) {
            message = message.concat(mesaVoto + ", ");
        }
        message = message.concat("\n\n");
        for (Candidatura candidatura : this.candidaturas) {
            message = message.concat("\t" + candidatura.toString() + "\n");
                for (Pessoa pessoa: candidatura.getMembros()) {
                    message = message.concat("\t\t" + pessoa.getNumCC() + "\n");
                }
                message = message.concat("\tNúmero de votos: " + candidatura.getVotos() + "\n" +
                    "\tPercentagem de votos:" + formatter.format(candidatura.getPercentagemVotos(numeroTotalVotos)) + "%\n\n");
        }
        message = message.concat("\tNúmero de votos nulos: " + this.getVotosNulos() + "\n" +
                "\tPercentagem de votos nulos: " + formatter.format(this.getPercentagemVotosNulos()) + "%\n\n" +
                "\tNúmero de votos em branco: " + this.getVotosEmBranco() + "\n" +
                "\tPercentagem de votos em branco: " + formatter.format(this.getPercentagemVotosEmBranco()) + "%\n\n");
        return message;
    }

}