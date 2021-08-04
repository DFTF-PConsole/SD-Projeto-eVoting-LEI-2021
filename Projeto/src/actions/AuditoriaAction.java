package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

public class AuditoriaAction extends ActionSupport {
    private AdminBean adminBean;
    private String id;
    private String dataVoto;
    private String mesaVoto;
    private String eleicao;
    
    public AuditoriaAction() {
        setAdminBean(new AdminBean());
    }

    public String getEleicao() {
        return eleicao;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        System.out.println(this.eleicao);
        this.id = id;
        System.out.println(this.id);
        setMesaVoto(getAdminBean().getMesaVotoEleitor(getEleicao(), getId()));
        setDataVoto(getAdminBean().getDataVotoEleitor(getEleicao(), getId()));
        System.out.println(this.dataVoto);
        System.out.println(this.mesaVoto);
    }

    public String getDataVoto() {
        return dataVoto;
    }

    public void setDataVoto(String dataVoto) {
        this.dataVoto = dataVoto;
    }

    public String getMesaVoto() {
        return mesaVoto;
    }

    public void setMesaVoto(String mesaVoto) {
        this.mesaVoto = mesaVoto;
    }

    @Override
    public String execute() {
        return SUCCESS;
    }

}
