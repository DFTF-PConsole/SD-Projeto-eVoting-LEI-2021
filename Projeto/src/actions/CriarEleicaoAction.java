package actions;

import ServerRMI.AdminConsoleInterface;
import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class CriarEleicaoAction extends ActionSupport {
    private AdminBean adminBean;
    private String titulo;
    private String fim;
    private String inicio;
    private String descricao;
    private String eleitor;


    public CriarEleicaoAction() {
        setAdminBean(new AdminBean());
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFim() {
        return fim;
    }

    public void setFim(String fim) {
        this.fim = fim;
    }

    public String getInicio() {
        return inicio;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getEleitor() {
        return eleitor;
    }

    public void setEleitor(String eleitor) {
        this.eleitor = eleitor;
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

        if(getAdminBean().criarEleicao(getTitulo(), getDescricao(), diaInicio, mesInicio, anoInicio, horaInicio,
                minutosInicio, diaFim, mesFim, anoFim, horaFim, minutosFim, getEleitor().contains("Estudante"),
                getEleitor().contains("Docente"), getEleitor().contains("Funcionário"))) {
            return SUCCESS;
        }
        return ERROR;
    }

}
