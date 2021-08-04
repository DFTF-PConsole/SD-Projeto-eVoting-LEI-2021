package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdicionarListaAction extends ActionSupport {
    private AdminBean adminBean;
    private String eleicao;
    private String nomeLista;
    private String membros;

    public String getNomeLista() {
        return nomeLista;
    }

    public void setNomeLista(String nomeLista) {
        this.nomeLista = nomeLista;
    }

    public AdicionarListaAction() {
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

    public String getMembros() {
        return membros;
    }

    public void setMembros(String membros) {
        this.membros = membros;
    }

    @Override
    public String execute() {
        CopyOnWriteArrayList<String> membrosNumCC = new CopyOnWriteArrayList<>();
        StringTokenizer stringTokenizer = new StringTokenizer(getMembros(), " ,");
        while (true) {
            try {
                membrosNumCC.add(stringTokenizer.nextToken());
            } catch (NoSuchElementException e) {
                break;
            }
        }
        System.out.println(membrosNumCC);
        if (getAdminBean().adicionarListaAEleicao(getEleicao(), getNomeLista(), membrosNumCC)) {
            return SUCCESS;
        }
        return ERROR;
    }
}
