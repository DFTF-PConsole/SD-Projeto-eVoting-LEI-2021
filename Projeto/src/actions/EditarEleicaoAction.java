package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class EditarEleicaoAction extends ActionSupport {
    private AdminBean adminBean;
    private String eleicao;
    private String titulo;
    private String inicio;
    private String fim;
    private String descricao;

    public EditarEleicaoAction() {
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String execute() {
        LocalDateTime dataInicio, dataFim;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", Locale.UK);
        try {
            dataInicio = LocalDateTime.parse(inicio, formatter);
            dataFim = LocalDateTime.parse(fim, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ERROR;
        }

        int diaInicio = dataInicio.getDayOfYear(),
                mesInicio = dataInicio.getMonthValue(),
                anoInicio = dataInicio.getYear(),
                horaInicio = dataInicio.getHour(),
                minutosInicio = dataInicio.getMinute(),
                diaFim = dataFim.getDayOfYear(),
                mesFim = dataFim.getMonthValue(),
                anoFim = dataFim.getYear(),
                horaFim = dataFim.getHour(),
                minutosFim = dataFim.getMinute();

        boolean res = false;
        if (!getAdminBean().getDataInicioEleicao(eleicao).equals(inicio) && getAdminBean().alterarDataInicioEleicao(eleicao, diaInicio, mesInicio, anoInicio, horaInicio, minutosInicio)) {
            res = true;
        }
        if (!getAdminBean().getDataFimEleicao(eleicao).equals(fim) && getAdminBean().alterarDataFimEleicao(eleicao, diaFim, mesFim, anoFim, horaFim, minutosFim)) {
            res = true;
        }
        if (!getAdminBean().getDescricaoEleicao(eleicao).equals(descricao) && getAdminBean().alterarDescricaoEleicao(eleicao, descricao)) {
            res = true;
        }
        if (!getAdminBean().getTituloEleicao(eleicao).equals(titulo) && getAdminBean().alterarTituloEleicao(eleicao, titulo)) {
            setEleicao(getTitulo());
            res = true;
        }
        if (res) {
            return SUCCESS;
        } else {
            return ERROR;
        }
    }

}
