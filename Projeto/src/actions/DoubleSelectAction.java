package actions;

import com.opensymphony.xwork2.ActionSupport;

import java.util.List;

public class DoubleSelectAction extends ActionSupport {
    private String eleicao;
    private String lista;
    private List<String> eleicoes;
    private List<String> listas;

    public String getEleicao() {
        return eleicao;
    }

    public void setEleicao(String eleicao) {
        this.eleicao = eleicao;
    }

    public String getLista() {
        return lista;
    }

    public void setLista(String lista) {
        this.lista = lista;
    }

    public List<String> getEleicoes() {
        return eleicoes;
    }

    public void setEleicoes(List<String> eleicoes) {
        this.eleicoes = eleicoes;
    }

    public List<String> getListas() {
        return listas;
    }

    public void setListas(List<String> listas) {
        this.listas = listas;
    }

    @Override
    public String execute() {
        return SUCCESS;
    }

}
