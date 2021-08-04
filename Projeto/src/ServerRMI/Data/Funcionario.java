/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Subclasse da classe abstrata Pessoa que vai conter todos os dados associados a um funcionário.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Pessoa
 * @see GregorianCalendar
 * @see Serializable
 */
public class Funcionario extends Pessoa implements Serializable {

    /**
     * Cria e instancia um objecto da classe Funcionario.
     *
     * @param nome         String: Nome
     * @param contacto     String: Número de Telefone
     * @param morada       String: Morada
     * @param codigoPostal String: Código
     * @param numCC        String: Número do Cartão de Cidadão
     * @param validadeCC   GregorianCalendar: Validade do Cartão de Cidadão
     * @param faculdade    String: Faculdade
     * @param departamento String: Departamento
     * @param password     String: Password
     * @author Cláudia Campos
     * @version 1.0
     */
    public Funcionario(String nome, String contacto, String morada, String codigoPostal, String numCC, GregorianCalendar validadeCC, String faculdade, String departamento, String password) {
        super(nome, contacto, morada, codigoPostal, numCC, validadeCC, faculdade, departamento, password);
    }

    /**
     * Método que retorna o número mecanográfico associado a um Funcionário. Ou seja, como o funcionário não possui um
     * número mecanográfico o método retorna null.
     *
     * @return String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String getNumMec() {
        return null;
    }

    /**
     * Método que retorna o número de estudante associado a um Funcionário. Ou seja, como o funcionário não possui um
     * número de estudante o método retorna null.
     *
     * @return String: Número de Estudante
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String getNumEstudante() {
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
        return "Funcionário:" + super.toString();
    }

}