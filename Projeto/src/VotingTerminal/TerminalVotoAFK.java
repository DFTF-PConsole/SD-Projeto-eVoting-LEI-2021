/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package VotingTerminal;


import Outros.*;
import java.util.concurrent.atomic.AtomicBoolean;


/**
 * TerminalVotoAFK permite verificar se ha inatividade na thread TerminalVoto
 *
 * @see TerminalVoto
 * @see Runnable
 * @see ProjGeral
 * @see Thread#interrupt()
 * @see InterruptedException
 * @author Dario Felix
 * @version 1.0
 */
public class TerminalVotoAFK implements ProjGeral, Runnable {
    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    private final static boolean DEBUG_ATIVO = false;   // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    private final static boolean INFO_ERRO_ATIVO = false;   // Default: false

    /**
     * Flag que indica se o INFO_RELEVANTE_ATIVO esta ativo. Indica se deve imprimir no stdout mensagens relevantes do fluxo normal do programa.
     *
     * @see ProjGeral#printAviso(String)
     */
    private final static boolean INFO_RELEVANTE_ATIVO = true;   // Default: true

    /**
     * Tempo maximo que o TerminalVoto pode estar inativo
     * <p> ms = N s * 1000 ms
     */
    private final long INATIVIDADE_MS;

    /**
     * Referencia para a thread TerminalVoto. Permite provocar um InterruptedException atraves do metodo interrupt()
     *
     * @see Thread#interrupt()
     * @see InterruptedException
     * @see TerminalVotoAFK#run()
     */
    private final Thread threadApp;

    /**
     * Flag utilizada para comunicacao entre threads (TerminalVoto e TerminalVotoAFK).
     * Indica se o TerminalVoto esta ativo/desbloqueado.
     *
     * @see AtomicBoolean
     * @see TerminalVotoAFK#run()
     */
    private final AtomicBoolean isDesbloqueado;

    /**
     * Construtor
     *
     * @param sleepMs Tempo maximo que o TerminalVoto pode estar inativo
     * @param threadApp Referencia para a thread TerminalVoto
     * @param isDesbloqueado Referencia para o objeto-flag utilizada para comunicacao entre as threads
     * @author Dario Felix
     * @version 1.0
     */
    TerminalVotoAFK(long sleepMs, Thread threadApp, AtomicBoolean isDesbloqueado) {
        this.INATIVIDADE_MS = sleepMs;
        this.threadApp = threadApp;
        this.isDesbloqueado = isDesbloqueado;
    }


    /**
     * Execucao da thread TerminalVotoAFK: verifica se ha inatividade na thread TerminalVoto
     *
     * @see Thread#interrupt()
     * @see InterruptedException
     * @author Dario Felix
     * @version 1.0
     */
    @Override
    public void run() {
        while (true) {
            try {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVotoAFK", "run", "Entrei no Synchronized");
                synchronized (Thread.currentThread()) {
                    if (DEBUG_ATIVO)
                        ProjGeral.printDebug("TerminalVotoAFK", "run", "Entrei no Synchronized");
                    Thread.currentThread().wait(this.INATIVIDADE_MS);
                }
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVotoAFK", "run", "Sai no Synchronized");

            } catch (InterruptedException e) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("TerminalVotoAFK", "run", "InterruptedException");
                if (this.isDesbloqueado.get())  // true -> ainda esta a ser executado, e um sinal de vida -> reset sleep
                    continue;
                else
                    return; // false -> ja terminou, terminar esta thread tambem
            }
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("TerminalVotoAFK", "run", "INATIVIDADE_MS: BLOQUEIA!!!!");
            this.threadApp.interrupt();
            return;
        }
    }
}