/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Exceptions;

/**
 * Excepção que é lançada quando a Password introduzida não corresponde à password do Utilizador.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Exception
 */
public class PasswordErradaException extends Exception {

    /**
     * Cria e instancia uma excepção da classe PasswordErradaException com uma dada mensagem.
     *
     * @param message String: Mensagem associada à excepção
     * @author Cláudia Campos
     * @version 1.0
     */
    public PasswordErradaException(String message) {
        super(message);
    }

}