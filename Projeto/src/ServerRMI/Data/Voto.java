/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Classe que representa um voto numa dada eleição. Armazena a informação do eleitor que votou, onde votou e quando
 * votou, sem guardar em quem este votou, semelhante a um comprovativo de votação.
 *
 * @author Cláudia Campos
 * @version 1.0
 * @see Pessoa
 * @see GregorianCalendar
 */
public class Voto implements Serializable {
    /**
     * Eleitor que votou
     */
    private final Pessoa votante;

    /**
     * Local do voto
     */
    private final String mesaVoto;

    /**
     * Data da votação
     */
    private final GregorianCalendar dataVotacao;

    /**
     * Cria e instancia um objecto da classe Voto.
     *
     * @param votante     Pessoa: Eleitor associado ao voto
     * @param mesaVoto    String: Local do voto
     * @param dataVotacao GregorianCalendar: Data do voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public Voto(Pessoa votante, String mesaVoto, GregorianCalendar dataVotacao) {
        this.votante = votante;
        this.mesaVoto = mesaVoto;
        this.dataVotacao = dataVotacao;
    }

    /**
     * Obtém o eleitor associado ao Voto.
     *
     * @return Pessoa: Eleitor associado ao voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public Pessoa getVotante() {
        return votante;
    }

    /**
     * Obtém o local do Voto.
     *
     * @return String: Local do Voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public String getMesaVoto() {
        return mesaVoto;
    }

    /**
     * Obtém a data do Voto.
     *
     * @return GregorianCalendar: Data do voto
     * @author Cláudia Campos
     * @version 1.0
     */
    public GregorianCalendar getDataVotacao() {
        return dataVotacao;
    }

    /**
     * Formata a data da votação no formato "dd/MM/uuuu H:m", isto é, dia/mês/ano hora:minuto.
     *
     * @return String: Data da votação formatada
     * @author Cláudia Campos
     * @version 1.0
     */
    public String dataVotoFormat() {
        return this.dataVotacao.toZonedDateTime().format(DateTimeFormatter.ofPattern("dd/MM/uuuu H:m"));
    }

    /**
     * Converte a informação do objecto numa String.
     *
     * @return String
     * @author Cláudia Campos
     * @version 1.0
     */
    @Override
    public String toString() {
        return "Eleitor: " + this.getVotante().getNumCC() + "\nLocal de voto: " + this.getMesaVoto() + "\nData da votação: " + this.dataVotoFormat();
    }

}