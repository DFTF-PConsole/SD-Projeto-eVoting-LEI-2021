package models;

import ServerRMI.Exceptions.PasswordErradaException;
import ServerRMI.Exceptions.PessoaNaoRegistadaException;
import ServerRMI.MulticastServerInterface;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class UserBean {
    private MulticastServerInterface rmiServer;
    private final User user = new User();

    public UserBean() {
        try {
            //this.rmiServer = (MulticastServerInterface) Naming.lookup("server");
            this.rmiServer = (MulticastServerInterface) LocateRegistry.getRegistry("127.0.0.1", 2000).lookup("server");
        }
        catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyLogin() throws RemoteException {
        try {
            return rmiServer.autenticarEleitor(this.getUsername(), this.getPassword());
        } catch (PessoaNaoRegistadaException | PasswordErradaException e) {
            e.printStackTrace();
            return false;
        }
    }

    public String getUsername() {
        return this.user.getUsername();
    }

    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    public String getPassword() {
        return this.user.getPassword();
    }

    public void setPassword(String password) {
        this.user.setPassword(password);
    }

}
