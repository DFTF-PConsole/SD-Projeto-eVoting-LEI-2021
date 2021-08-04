/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

/**
 * Classe auxiliar que armazena pares {Eleição | Mesa de Voto} para possibilitar a validação das Mesas de Voto activas.
 * Permite que apenas exista uma Mesa de Voto num dado Local e numa dada Eleição, ou seja, só deve existir um par
 * {Eleição | Mesa de Voto} igual, isto é, com os mesmos valores.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Eleicao
 * @see MulticastServer.MesaVoto
 */
public class MesaEleicao {

    /**
     * Eleição da Mesa de Voto
     */
    private Eleicao eleicao;

    /**
     * Faculdade ou Departamento da Mesa de Voto
     */
    private String faculdadeOuDepartamento;

    /**
     * Cria e instancia um objecto da classe MesaEleicao.
     *
     * @param eleicao                 Eleicao: Eleição
     * @param faculdadeOuDepartamento String: Faculdade ou Departamento da Mesa de Voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public MesaEleicao(Eleicao eleicao, String faculdadeOuDepartamento) {
        this.eleicao = eleicao;
        this.faculdadeOuDepartamento = faculdadeOuDepartamento;
    }

    /**
     * Obtém a eleição do par {Eleição | Mesa de Voto}.
     *
     * @return Eleicao: Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public Eleicao getEleicao() {
        return eleicao;
    }

    /**
     * Modifica a eleição do par {Eleição | Mesa de Voto}.
     *
     * @param eleicao Eleicao: Eleição
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setEleicao(Eleicao eleicao) {
        this.eleicao = eleicao;
    }

    /**
     * Obtém a faculdade ou departamento do par {Eleição | Mesa de Voto}.
     *
     * @return String: Faculdade ou Departamento da Mesa de Voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getFaculdadeOuDepartamento() {
        return faculdadeOuDepartamento;
    }

    /**
     * Modifica a faculdade ou departamento do par {Eleição | Mesa de Voto}.
     *
     * @param faculdadeOuDepartamento String: Faculdade ou Departamento da Mesa de Voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public void setFaculdadeOuDepartamento(String faculdadeOuDepartamento) {
        this.faculdadeOuDepartamento = faculdadeOuDepartamento;
    }

}