/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.GregorianCalendar;

/**
 * Classe Abstrata Pessoa que vai conter todos os dados comuns às classes concretas Docente, Estudante e Funcionario,
 * permite agregar todos os dados que caracterizam uma pessoa.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see GregorianCalendar
 * @see Serializable
 */
public abstract class Pessoa implements Serializable {

    /**
     * Nome da Pessoa
     */
    protected String nome;

    /**
     * Contacto telefónico da Pessoa
     */
    protected String contacto;

    /**
     * Morada da Pessoa
     */
    protected String morada;

    /**
     * Código-Postal da Pessoa
     */
    protected String codigoPostal;

    /**
     * Número do Cartão de Cidadão da Pessoa
     */
    protected String numCC;

    /**
     * Validade do Cartão de Cidadão da Pessoa
     */
    protected GregorianCalendar validadeCC;

    /**
     * Faculdade à qual a Pessoa pertence
     */
    protected String faculdade;

    /**
     * Departamento ao qual a Pessoa pertence
     */
    protected String departamento;

    /**
     * Password da Pessoa
     */
    protected String password;

    /**
     * Cria e instancia um objecto da classe Pessoa.
     *
     * @param nome         String: Nome
     * @param contacto     String: Número de Telefone
     * @param morada       String: Morada
     * @param codigoPostal String: Código postal
     * @param numCC        String: Número do Cartão de Cidadão
     * @param validadeCC   GregorianCalendar: Validade do Cartão de Cidadão
     * @param faculdade    String: Faculdade
     * @param departamento String: Departamento
     * @param password     String: Password
     * @author Cláudia Campos
     * @version 1.0
     */
    protected Pessoa(String nome, String contacto, String morada, String codigoPostal, String numCC, GregorianCalendar validadeCC, String faculdade, String departamento, String password) {
        this.nome = nome;
        this.contacto = contacto;
        this.morada = morada;
        this.codigoPostal = codigoPostal;
        this.numCC = numCC;
        this.validadeCC = validadeCC;
        this.faculdade = faculdade;
        this.departamento = departamento;
        this.password = password;
    }

    /**
     * Obtém o nome da Pessoa.
     *
     * @return String: Nome
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getNome() {
        return nome;
    }

    /**
     * Obtém o número de telefone da Pessoa.
     *
     * @return String: Número de Telefone
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getContacto() {
        return this.contacto;
    }

    /**
     * Obtém a morada da Pessoa.
     *
     * @return String: Morada
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getMorada() {
        return morada;
    }

    /**
     * Obtém o código-postal da Pessoa.
     *
     * @return String: Código-Postal
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getCodigoPostal() {
        return codigoPostal;
    }

    /**
     * Obtém o Número de Cartão de Cidadão da Pessoa.
     *
     * @return String: Número do Cartão de Cidadão
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getNumCC() {
        return numCC;
    }

    /**
     * Obtém a data de validade do Cartão de Cidadão da Pessoa.
     *
     * @return GregorianCalendar: Validade do Cartão de Cidadão
     * @author Cláudia Campos
     * @version 1.0
     */
    public GregorianCalendar getValidadeCC() {
        return validadeCC;
    }

    /**
     * Obtém a Faculdade à qual a Pessoa pertence.
     *
     * @return String: Faculdade
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getFaculdade() {
        return faculdade;
    }

    /**
     * Obtém o Departamento ao qual a Pessoa pertence.
     *
     * @return String: Departamento
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getDepartamento() {
        return departamento;
    }

    /**
     * Obtém a password/código de acesso associada à Pessoa.
     *
     * @return String: Password
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getPassword() {
        return password;
    }

    /**
     * Modifica o nome da Pessoa.
     *
     * @param nome String: Novo Nome
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Modifica o número de telefone da Pessoa.
     *
     * @param contacto String: Número de Telefone
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    /**
     * Modifica a morada da Pessoa.
     *
     * @param morada String: Nova Morada
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setMorada(String morada) {
        this.morada = morada;
    }

    /**
     * Modifica o código-postal da Pessoa.
     *
     * @param codigoPostal String: Novo Código-Postal
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }

    /**
     * Modifica o número do cartão de cidadão da Pessoa.
     *
     * @param numCC String: Novo Número do Cartão de Cidadão
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setNumCC(String numCC) {
        this.numCC = numCC;
    }

    /**
     * Modifica a validade do cartão de cidadão da pessoa.
     *
     * @param validadeCC GregorianCalendar: Nova Validade do Cartão de Cidadão
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setValidadeCC(GregorianCalendar validadeCC) {
        this.validadeCC = validadeCC;
    }

    /**
     * Modifica a Faculdade da Pessoa.
     *
     * @param faculdade String: Nova Faculdade
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }

    /**
     * Modifica o Departamento da Pessoa.
     *
     * @param departamento String: Novo Departamento
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    /**
     * Modifica a password da Pessoa.
     *
     * @param password String: Nova Password
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Método abstrato que retorna o número de estudante associado a um estudante. A ser implementado pelas subclasses
     * desta classe.
     *
     * @return String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    public abstract String getNumEstudante();

    /**
     * Método abstrato que retorna o número mecanográfico associado a um docente. A ser implementado pelas subclasses
     * desta classe.
     *
     * @return String: Número Mecanográfico
     * @author Cláudia Campos
     * @version 1.0
     */
    public abstract String getNumMec();

    /**
     * Formata a data de validade do cartão de cidadão da Pessoa no formato "dd/MM/uuuu", isto é, dia/mês/ano.
     *
     * @return String: Data de validade do cartão de cidadão formatada
     * @author Cláudia Campos
     * @version 1.0
     */
    public String validadeCCFormatddMMyyyy() {
        return this.validadeCC.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu"));
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
        return "\n\tNome: " + this.nome + "\n\tContacto: " + this.contacto + "\n\tMorada: " + this.morada +
                "\n\tCódigo-Postal: " + this.codigoPostal + "\n\tNúmero CC: " + this.numCC + "\n\tValidade CC: " +
                this.validadeCCFormatddMMyyyy() + "\n\tFaculdade: " + this.faculdade + "\n\tDepartamento: " +
                this.departamento + "\n\tPassword: " + this.password;
    }

}