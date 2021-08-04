/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Exceptions;

/**
 * Excepção que é lançada quando uma determinada Eleição já iniciada corresponde a uma situação que não deveria ocorrer.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Exception
 */
public class EleicaoComecouException extends Exception {

    /**
     * Cria e instancia uma excepção da classe EleicaoComecouException com uma dada mensagem.
     *
     * @param message String: Mensagem associada à excepção
     * @author Cláudia Campos
     * @version 1.0
     */
    public EleicaoComecouException(String message) {
        super(message);
    }

}
