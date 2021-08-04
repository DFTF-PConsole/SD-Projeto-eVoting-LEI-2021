package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

import java.rmi.RemoteException;
import java.util.List;

public class SearchAction extends ActionSupport {
    private List<String> eleicoes;
    private AdminBean adminBean;
    private String eleicao;
    private String titulo;
    private String descricao;
    private String inicio;
    private String fim;
    private String id;
    private String data;
    private String membros;
    private List<String> pessoas;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }

    public String getMembros() {
        return membros;
    }

    public void setMembros(String membros) {
        this.membros = membros;
    }

    public List<String> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<String> pessoas) {
        this.pessoas = pessoas;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEleicao() {
        return this.eleicao;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
    }

    public SearchAction() {
        System.out.println(this);
        setAdminBean(new AdminBean());
        setEleicoes(adminBean.getListaEleicoes());
    }

    public List<String> getEleicoes() {
        return this.eleicoes;
    }

    public void setEleicoes(List<String> eleicoes) {
        this.eleicoes = eleicoes;
    }

    public AdminBean getAdminBean() {
        return this.adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    @Override
    public String execute() {
        System.out.println("Eleição: " + eleicao);
        System.out.println("ID: " + id);

        switch (getId()) {
            case "lista":
                setPessoas(getAdminBean().obterPessoasEleicao(getEleicao()));
                break;
            case "editar":
                setTitulo(getAdminBean().getTituloEleicao(getEleicao()));
                setDescricao(getAdminBean().getDescricaoEleicao(getEleicao()));
                setInicio(getAdminBean().getDataInicioEleicao(getEleicao()));
                setFim(getAdminBean().getDataFimEleicao(getEleicao()));
                break;
            case "consultar":
                setData(getAdminBean().getEleicao(eleicao));
                break;
            default:
                break;
        }
        return this.getId();
    }

    public String displayEleicoes() {
        return NONE;
    }
}
