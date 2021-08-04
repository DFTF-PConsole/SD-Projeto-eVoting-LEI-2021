/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Exceptions;

/**
 * Excepção que é lançada quando uma Pessoa não pertencente a uma Lista Candidata corresponde a uma situação que não
 * deveria ocorrer.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Exception
 */
public class MembroNaoPertenceListaException extends Exception{

    /**
     * Cria e instancia uma excepção da classe MembroNaoPertenceListaException com uma dada mensagem.
     *
     * @param message String: Mensagem associada à excepção
     * @author Cláudia Campos
     * @version 1.0
     */
    public MembroNaoPertenceListaException(String message) {
        super(message);
    }

}
