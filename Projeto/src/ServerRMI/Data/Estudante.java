/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Subclasse da classe abstrata Pessoa que vai conter todos os dados associados a um estudante incluíndo o seu número de
 * estudante.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Pessoa
 * @see GregorianCalendar
 * @see Serializable
 */
public class Estudante extends Pessoa implements Serializable {

    /**
     * Número de estudante do Estudante.
     */
    private String numEstudante;

    /**
     * Cria e instancia um objecto da classe Estudante.
     *
     * @param nome         String: Nome
     * @param contacto     String: Número de Telefone
     * @param morada       String: Morada
     * @param codigoPostal String: Código-Postal
     * @param numCC        String: Número do Cartão de Cidadão
     * @param validadeCC   GregorianCalendar: Validade do Cartão de Cidadão
     * @param faculdade    String: Faculdade
     * @param departamento String: Departamento
     * @param password     String: Password
     * @param numEstudante String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    public Estudante(String nome, String contacto, String morada, String codigoPostal, String numCC, GregorianCalendar validadeCC, String faculdade, String departamento, String password, String numEstudante) {
        super(nome, contacto, morada, codigoPostal, numCC, validadeCC, faculdade, departamento, password);
        this.numEstudante = numEstudante;
    }

    /**
     * Método que retorna o número de estudante associado a um Estudante.
     *
     * @return String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String getNumEstudante() {
        return numEstudante;
    }

    /**
     * Modifica o número de estudante de um Estudante.
     *
     * @param numEstudante String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setNumEstudante(String numEstudante) {
        this.numEstudante = numEstudante;
    }

    /**
     * Método que retorna o número mecanográfico associado a um Estudante. Ou seja, como o estudnate não possui um
     * número mecanográfico o método retorna null.
     *
     * @return String: Número Mecanográfico
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String getNumMec() {
        return null;
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
        return "Estudante:" + super.toString() + "\n\tNúmero de Estudante: " + this.numEstudante;
    }

}