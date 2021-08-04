/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package Outros;


/**
 * Interface com constantes e metodos universais e padronizados para as restantes classes do projeto
 *
 * @author Dario Felix
 * @version 1.0
 */
public interface ProjGeral {
    /**
     * Flag que indica se o DEBUG esta ativo. Indica se imprime no stdout informacoes para debug.
     *
     * @see #printDebug(String, String, String)
     */
    boolean DEBUG_ATIVO = false;    // Default: false

    /**
     * Flag que indica se o INFO_ERRO esta ativo. Indica se imprime no stdout as mensagens de erro.
     *
     * @see #printErro(String, String, String)
     */
    boolean INFO_ERRO_ATIVO = false;    // Default: false

    /**
     * Flag que indica se o INFO_RELEVANTE_ATIVO esta ativo. Indica se deve imprimir no stdout mensagens relevantes do fluxo normal do programa.
     *
     * @see #printAviso(String)
     */
    boolean INFO_RELEVANTE_ATIVO = true;   // Default: false

    /**
     * Constante a ser usado pelos metodos pra indicar ERRO
     * <p>
     * Exemplo 1: return ERRO;
     * <p>
     * Exemplo 2: if(function() == ERRO)
     */
    boolean ERRO = true;

    /**
     * Constante a ser usado pelos metodos pra indicar SUCESSO
     * <p>
     * Exemplo 1: return SUCESSO;
     * <p>
     * Exemplo 2: if(function() == SUCESSO)
     */
    boolean SUCESSO = false;

    /**
     * Tempo que as threads podem estar inativas no wait()
     * <p> ms = N s * 1000 ms
     *
     * @see Thread#wait(long)
     */
    long SLEEP_WAIT_MS = 1000;

    /**
     * Tempo para refresh dentro whiles (para Thread.wait())
     *
     * @see Thread#wait(long)
     */
    long SHORT_SLEEP_WAIT_MS = 100;

    /**
     * Tempo para refresh dentro whiles (para Thread.wait())
     *
     * @see Thread#wait(long)
     */
    long NOT_SO_SHORT_SLEEP_WAIT_MS = 500;

    /**
     * Tempo de avarias temporárias
     * <p> ms = N s * 1000 ms
     */
    long TEMPO_AVARIA_TEMPORARIA_MS = 30 * 1000;


    /**
     * Imprime no stdout info padronizado sobre o que gerou o erro
     *
     * @param StrObj Nome do objeto
     * @param StrMetodo Nome do metodo
     * @param StrMsgErro Mensagem ou descricao do erro
     * @param e Exception para executar o e.printStackTrace()
     * @see Exception
     * @see Exception#printStackTrace()
     * @see #printErro(String, String, String)
     * @author Dario Felix
     * @version 1.0
     */
    static void printErro(String StrObj, String StrMetodo, String StrMsgErro, Exception e) {
        printErro(StrObj, StrMetodo, StrMsgErro);
        System.out.print("### EXCEPTION ###  ");
        e.printStackTrace();
        System.out.println();
    }


    /**
     * Imprime no stdout info padronizado sobre o que gerou o erro
     *
     * @param StrObj Nome do objeto
     * @param StrMetodo Nome do metodo
     * @param StrMsgErro Mensagem ou descricao do erro
     * @author Dario Felix
     * @version 1.0
     */
    static void printErro(String StrObj, String StrMetodo, String StrMsgErro) {
        System.out.printf("### ERRO ###  CLASSE: \"%s\" | METODO: \"%s()\" | MSG: \"%s\" \n", StrObj, StrMetodo, StrMsgErro);
    }


    /**
     * Imprime no stdout info padronizado sobre o que gerou o erro
     *
     * @param StrMsgErro Mensagem ou descricao do erro
     * @see #printErro(String, String, String)
     * @author Dario Felix
     * @version 1.0
     */
    static void printErro(String StrMsgErro) {
        printErro("?", "?", StrMsgErro);
    }


    /**
     * Imprime no stdout info padronizado sobre o que gerou o erro
     *
     * @param StrMsgErro Mensagem ou descricao do erro
     * @param StrMetodo Nome do metodo
     * @see #printErro(String, String, String)
     * @author Dario Felix
     * @version 1.0
     */
    static void printErro(String StrMetodo, String StrMsgErro) {
        printErro("?", StrMetodo, StrMsgErro);
    }


    /**
     * Imprime no stdout uma mensagem debug padronizado
     *
     * @param StrObj Nome do objeto
     * @param StrMetodo Nome do metodo
     * @param StrMsgDebug Mensagem ou descricao do debug
     * @author Dario Felix
     * @version 1.0
     */
    static void printDebug(String StrObj, String StrMetodo, String StrMsgDebug) {
        System.out.printf("### DEBUG ###  CLASSE: \"%s\" | METODO: \"%s()\" | MSG: \"%s\" \n", StrObj, StrMetodo, StrMsgDebug);
    }


    /**
     * Imprime no stdout uma mensagem debug padronizado
     *
     * @param StrMsgDebug Mensagem ou descricao do debug
     * @see #printDebug(String, String, String)
     * @author Dario Felix
     * @version 1.0
     */
    static void printDebug(String StrMsgDebug) {
        printDebug("?", "?", StrMsgDebug);
    }


    /**
     * Imprime no stdout uma mensagem debug padronizado
     *
     * @param StrMsgDebug Mensagem ou descricao do debug
     * @param StrMetodo Nome do metodo
     * @see #printDebug(String, String, String)
     * @author Dario Felix
     * @version 1.0
     */
    static void printDebug(String StrMetodo, String StrMsgDebug) {
        printDebug("?", StrMetodo, StrMsgDebug);
    }


    /**
     * Imprime no stdout uma mensagem/informacao relevante e padronizado durante o fluxo normal do programa.
     *
     * @param StrMsgAviso Mensagem ou descricao
     * @see ProjGeral#INFO_RELEVANTE_ATIVO
     * @author Dario Felix
     * @version 1.0
     */
    static void printAviso(String StrMsgAviso) {
        System.out.printf("### AVISO ###  \"%s\"\n", StrMsgAviso);
    }
}