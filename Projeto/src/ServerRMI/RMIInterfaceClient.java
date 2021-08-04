/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI;

import java.rmi.*;


/**
 * Interface implementada pela Consola de Administração (AdminConsole) para efeitos de Callback.
 *
 * @author Cláudia Campos
 * @version 1.0
 */
public interface RMIInterfaceClient extends Remote {

    /**
     * Metodo utilizado pelo RMI Server para imprimir uma mensagem em todas as AdminConsole através do mecanismo callback
     *
     * @param message Mensagem a imprimir pelo RMI Server no AdminConsole
     * @throws RemoteException RemoteException.
     * @author Dário Félix
     * @version 1.0
     * @see RMIInterfaceClient
     */
    void printOnAdminConsole(String message) throws RemoteException;

}