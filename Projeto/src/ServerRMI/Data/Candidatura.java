/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import ServerRMI.Exceptions.DadosInvalidosException;
import ServerRMI.Exceptions.MembroNaoPertenceListaException;
import ServerRMI.Exceptions.MembroPertenceListaException;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Classe que vai armazenar os dados relativos a uma lista candidata, isto é, o seu nome, os membros que a constituem e
 * o número de votos por ela obtidos.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Pessoa
 * @see CopyOnWriteArrayList
 * @see Serializable
 */
public class Candidatura implements Serializable {

    /**
     * Nome da Lista Candidata
     */
    private String nome;

    /**
     * Membros da Lista Candidata
     */
    private CopyOnWriteArrayList<Pessoa> membros = new CopyOnWriteArrayList<>();

    public CopyOnWriteArrayList<Pessoa> getMembros() {
        return membros;
    }

    public void setMembros(CopyOnWriteArrayList<Pessoa> membros) {
        this.membros = membros;
    }

    public void setVotos(int votos) {
        this.votos = votos;
    }

    /**
     * Número de votos obtidos pela Lista Candidata
     */
    private int votos;

    /**
     * Cria e instancia um objecto da classe Candidatura.
     *
     * @param nome String: Nome da Lista Candidata
     * @author Cláudia Campos
     * @version 1.0
     */
    public Candidatura(String nome) {
        this.nome = nome;
        this.votos = 0;
    }

    /**
     * Cria e instancia um objecto da classe Candidatura.
     *
     * @param nome    String: Nome da Lista Candidata
     * @param membros CopyOnWriteArrayList: Membros da Lista Candidata
     * @author Cláudia Campos
     * @version 1.0
     */
    public Candidatura(String nome, CopyOnWriteArrayList<Pessoa> membros) {
        this.nome = nome;
        this.membros = membros;
        this.votos = 0;
    }

    /**
     * Obtém o membro da Lista Candidata cujo número de cartão de cidadão corresponde ao número de cartão de cidadão
     * fornecido.
     *
     * @param numCC String: Número do Cartão de Cidadão
     * @return Pessoa: Membro da Candidatura/Lista Candidata
     * @author Cláudia Campos
     * @version 1.0
     */
    public Pessoa getPessoaByNumCC(String numCC) {
        for (Pessoa membro : this.membros) {
            if (membro.getNumCC().equals(numCC)) {
                return membro;
            }
        }
        return null;
    }

    /**
     * Adiciona uma pessoa à lista de membros da Candidatura.
     *
     * @param pessoa Pessoa: Pessoa a adicionar à lista de membros
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException      Se o objecto Pessoa corresponde a um ponteiro para NULL.
     * @throws MembroPertenceListaException Se o membro a adicionar já pertence à Lista Candidata.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean addPessoa(Pessoa pessoa) throws DadosInvalidosException, MembroPertenceListaException {
        if (pessoa == null) {
            throw new DadosInvalidosException("A pessoa não existe.");
        } else if (this.getPessoaByNumCC(pessoa.getNumCC()) != null) {
            throw new MembroPertenceListaException("A pessoa \"" + pessoa.getNumCC() + "\" já se encontra na lista \"" + this.nome + "\"");
        } else {
            this.membros.add(pessoa);
            return true;                    //Pessoa inserida com sucesso
        }
    }

    /**
     * Remove um membro da Lista Candidata.
     *
     * @param pessoa Pessoa: Pessoa a remover da lista de membros
     * @return boolean: Operação efectuada com sucesso
     * @throws DadosInvalidosException         Se o objecto Pessoa corresponde a um ponteiro para NULL.
     * @throws MembroNaoPertenceListaException Se o membro a remover não pertence à Lista Candidata.
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean removeMembro(Pessoa pessoa) throws DadosInvalidosException, MembroNaoPertenceListaException {
        if (pessoa == null) {
            throw new DadosInvalidosException("A pessoa não existe.");
        } else if (getPessoaByNumCC(pessoa.getNumCC()) == null) {
            throw new MembroNaoPertenceListaException("A pessoa \"" + pessoa.getNumCC() + "\" já não se encontra na lista \"" + this.nome + "\"");
        } else {
            this.membros.remove(pessoa);
            return true;                    //Pessoa removida com sucesso
        }
    }

    /**
     * Obtém o nome da Lista Candidata.
     *
     * @return String: Nome da Candidatura
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getNome() {
        return this.nome;
    }

    /**
     * Obtém o número de votos que a Lista Candidata possui.
     *
     * @return int: Número de votos da Candidatura
     * @author Cláudia Campos
     * @version 1.0
     */
    public int getVotos() {
        return votos;
    }

    /**
     * Modifica o nome da Lista Candidata.
     *
     * @param nome String: Novo Nome da Candidatura
     * @author Cláudia Campos
     * @version 1.0
     */
    private void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Incrementa o número de votos da Lista Candidata em uma unidade.
     *
     * @author Cláudia Campos
     * @version 1.0
     */
    public void incrementaVotos() {
        this.votos++;
    }

    /**
     * Calcula a percentagem de votos obtidos pela Lista Candidata.
     *
     * @param numeroTotalVotos int: Número total de votos da Eleição
     * @return double: Percentagem de votos
     * @author Cláudia Campos
     * @version 1.0
     */
    public double getPercentagemVotos(int numeroTotalVotos) {
        if (numeroTotalVotos == 0) {
            return 0.0;
        } else {
            return ((double) this.votos / (double) numeroTotalVotos) * 100.0;
        }
    }

    /**
     * Verifica se a lista de membros da Candidatura se encontra vazia.
     *
     * @return boolean
     * @author Cláudia Campos
     * @version 1.0
     */
    public boolean membrosIsEmpty() {
        return this.membros.isEmpty();
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
        return "Nome da lista: " + this.getNome();
    }

}