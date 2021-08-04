package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

public class AdicionarMesaVotoAction extends ActionSupport {
    private AdminBean adminBean;
    private String eleicao;
    private String mesaVoto;

    public AdicionarMesaVotoAction() {
        setAdminBean(new AdminBean());
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String getEleicao() {
        return eleicao;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
    }

    public String getMesaVoto() {
        return mesaVoto;
    }

    public void setMesaVoto(String mesaVoto) {
        this.mesaVoto = mesaVoto;
    }

    @Override
    public String execute() {
        if (getAdminBean().adicionarMesaVoto(getEleicao(), getMesaVoto())) {
            return SUCCESS;
        }
        return ERROR;
    }
}
