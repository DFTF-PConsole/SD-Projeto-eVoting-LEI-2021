package actions;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import models.User;
import models.UserBean;
import org.apache.struts2.interceptor.SessionAware;

import java.io.Serial;
import java.rmi.RemoteException;
import java.util.Map;

public class LoginAction extends ActionSupport implements SessionAware, ModelDriven<User> {
    @Serial
    private static final long serialVersionUID = 4L;
    private Map<String, Object> session;
    private final User user = new User();
    private static final String ADMIN = "admin";

    @Override
    public void validate() {
        if (this.user.getUsername() == null || this.user.getUsername().equals("")) {             //User id blank
            //Redireciona para a página onde os dados foram submetidos pelo utilizador)
            addFieldError("username", "Username cannot be blank");
        }
        if (this.user.getPassword() == null || this.user.getPassword().equals("")) {             //Password is blank
            //Redireciona para a página onde os dados foram submetidos pelo utilizador)
            addFieldError("password", "Password cannot be blank");
        }
    }

    @Override
    public String execute() {
        this.getUserBean().setUsername(this.user.getUsername());
        this.getUserBean().setPassword(this.user.getPassword());
        session.put("username", this.user.getUsername());
        try {
            if (this.getUserBean().verifyLogin()) {
                session.put("loggedin", true);          //this marks the user as logged in
            } else if (this.isAdmin()) {
                return ADMIN;
            } else {
                session.put("loggedin", false);         //this marks the user as not logged in
                return LOGIN;
            }
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return LOGIN;
        }
        return SUCCESS;
    }

    public boolean isAdmin() {
        return this.user.getUsername().equals("admin") && this.user.getPassword().equals("admin");
    }

    public Map<String, Object> getSession() {
        return this.session;
    }

    public void setUserBean(UserBean userBean) {

        this.session.put("userBean", userBean);
    }

    public UserBean getUserBean() {
        if(!session.containsKey("userBean"))
            this.setUserBean(new UserBean());
        return (UserBean) session.get("userBean");
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = session;
    }

    @Override
    public User getModel() {
        return this.user;
    }
}
