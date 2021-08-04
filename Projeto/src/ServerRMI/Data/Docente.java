/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.Serializable;
import java.util.*;

/**
 * Subclasse da classe abstrata Pessoa que vai conter todos os dados associados a um docente incluíndo o seu número
 * mecanográfico.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Pessoa
 * @see GregorianCalendar
 * @see Serializable
 */
public class Docente extends Pessoa implements Serializable {

    /**
     * Número mecanográfico do Docente
     */
    private String numMec;

    /**
     * Cria e instancia um objecto da classe Docente.
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
     * @param numMec       String: Número Mecanográfico
     * @author Cláudia Campos
     * @version 1.0
     */
    public Docente(String nome, String contacto, String morada, String codigoPostal, String numCC, GregorianCalendar validadeCC, String faculdade, String departamento, String password, String numMec) {
        super(nome, contacto, morada, codigoPostal, numCC, validadeCC, faculdade, departamento, password);
        this.numMec = numMec;
    }

    /**
     * Método que retorna o número mecanográfico associado a um Docente.
     *
     * @return String: Número Mecanográfico
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String getNumMec() {
        return numMec;
    }

    /**
     * Modifica o número mecanográfico de um Docente.
     *
     * @param numMec String: Número mecanográfico
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setNumMec(String numMec) {
        this.numMec = numMec;
    }

    /**
     * Método que retorna o número de estudante associado a um Docente. Ou seja, como o docente não possui um número de
     * estudante o método retorna null.
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
        return "Docente:" + super.toString() + "\n\tNúmero Mecanográfico: " + this.numMec;
    }

}