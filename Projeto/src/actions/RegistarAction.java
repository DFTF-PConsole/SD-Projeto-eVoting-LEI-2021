package actions;

import com.opensymphony.xwork2.ActionSupport;
import models.AdminBean;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RegistarAction extends ActionSupport {
    private AdminBean adminBean;
    private List<String> eleitores;
    private String pessoa;
    private String name;
    private String phone;
    private String address;
    private String postalCode;
    private String id;
    private String dateId;
    private String mec;
    private String student;
    private String password;
    private String faculdade;
    private String departamento;

    public RegistarAction() {
        setAdminBean(new AdminBean());
        eleitores = new ArrayList<>();
        eleitores.add("Docente");
        eleitores.add("Estudante");
        eleitores.add("Funcionário");
        setPessoa(eleitores.get(0));
    }

    public String getFaculdade() {
        return faculdade;
    }

    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public AdminBean getAdminBean() {
        return adminBean;
    }

    public void setAdminBean(AdminBean adminBean) {
        this.adminBean = adminBean;
    }

    public String getDefaultEleitor() {
        return eleitores.get(0);
    }

    public List<String> getEleitores() {
        return eleitores;
    }

    public void setEleitores(List<String> eleitores) {
        this.eleitores = eleitores;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDateId() {
        return dateId;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public String getMec() {
        return mec;
    }

    public void setMec(String mec) {
        this.mec = mec;
    }

    public String getStudent() {
        return student;
    }

    public void setStudent(String student) {
        this.student = student;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String execute() {

        LocalDate data;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.UK);

        try {
            data = LocalDate.parse(this.dateId, formatter);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return ERROR;
        }

        int validadeDia = data.getDayOfYear(),
                validadeMes = data.getMonthValue(),
                validadeAno = data.getYear();

        boolean registo = false;
        if (getPessoa().equals(getEleitores().get(0))) {        //Docente
            registo = getAdminBean().registarDocente(name, phone, address, postalCode, id, validadeDia, validadeMes, validadeAno,
                    faculdade, departamento, password, mec);
        } else if (getPessoa().equals(getEleitores().get(1))) { //Estudante
            registo = getAdminBean().registarEstudante(name, phone, address, postalCode, id, validadeDia, validadeMes, validadeAno,
                    faculdade, departamento, password, student);
        } else if (getPessoa().equals(getEleitores().get(2))) { //Funcionário
            registo = getAdminBean().registarFuncionario(name, phone, address, postalCode, id, validadeDia, validadeMes, validadeAno,
                    faculdade, departamento, password);
        }

        if (registo) {
            return SUCCESS;
        }
        return ERROR;
    }

    public String displayRegisto() {
        return NONE;
    }

    public String displayCriarEleicao() {
        return NONE;
    }

}