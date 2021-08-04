package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;
import models.UserBean;

import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class VotarAction extends ActionSupport {
    private AdminBean adminBean;
    private HashMap<String, CopyOnWriteArrayList<String>> lista;
    private UserBean userBean;
    private String username;
    private String selectedEleicao;
    private String selectedLista;
    private String resultado;

    public String getSelectedEleicao() {
        return selectedEleicao;
    }

    public void setSelectedEleicao(String selectedEleicao) {
        this.selectedEleicao = selectedEleicao;
    }

    public String getSelectedLista() {
        return selectedLista;
    }

    public void setSelectedLista(String selectedLista) {
        this.selectedLista = selectedLista;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public VotarAction() {
        this.setAdminBean(new AdminBean());
        this.setLista(new HashMap<>());
        this.setUserBean(new UserBean());
        for (String eleicao : this.adminBean.getListaEleicoes()) {
            CopyOnWriteArrayList<String> listinhas = this.adminBean.getObterNomesListasCandidatas(eleicao);
            listinhas.add("Voto em Branco");
            listinhas.add("Voto Nulo");
            this.getLista().put(eleicao, listinhas);
        }
    }

    @Override
    public String execute() {
        setSelectedLista(getSelectedLista().equals("Voto em Branco") ? "" : getSelectedLista());
        setSelectedLista(getSelectedLista().equals("Voto Nulo") ? null : getSelectedLista());

        String mensagem = this.getAdminBean().votar(selectedEleicao, "Online", username, selectedLista);
        this.setResultado(mensagem);
        return SUCCESS;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public void setLista(HashMap<String,CopyOnWriteArrayList<String>> lista) {
        this.lista = lista;
    }

    public HashMap<String, CopyOnWriteArrayList<String>> getLista() {
        return this.lista;
    }

    public UserBean getUserBean() {
        return this.userBean;
    }

    public void setUserBean (UserBean userBean) {
        this.userBean = userBean;
    }

    public String displayData() {
        return NONE;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }
}
