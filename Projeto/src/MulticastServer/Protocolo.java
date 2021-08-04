/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */


package MulticastServer;


import Outros.ProjGeral;
import java.util.*;
import VotingTerminal.*;


/**
 * Interface com constantes e metodos universais e padronizados que utilizam o "Protocolo Multicast" para as restantes classes do projeto
 * <p> Sintaxe do comando: type | valor_type  { ; chave | valor }
 * <p> Nota: { ; chave | valor } > opcional e pode ser repetido recursivamente
 * <p> Comando 1: type | hello  ;  id | 'numero_random'
 * <p> Comando 2: type | set_id  ;  old_id | 'numero_random'  ;  new_id | 'n'
 * <p> Comando 3: type | who_available
 * <p> Comando 4: type | im_available  ;  id | 'n'
 * <p> Comando 5: type | unblock  ;  id | 'n'
 * <p> Comando 6: type | ack  ;  type_ack | {unblock, }
 * <p> Comando 7: type | login  ;  id | 'n'  ;  username | 'cc'  ;  password | 'password'
 * <p> Comando 8: type | status  ;  id | 'n'  ;  {logged, voted, } | 'true|false'  ;  msg | 'msg'
 * <p> Comando 9: type | get_list  [;  id | 'n']
 * <p> Comando 10: type | item_list  [;  id | 'n']  ;  item_count | 'n'  ;  item_0_name | 'candidate list'  {  ; item_i_name | 'another candidate list' }
 * <p> Comando 11: type | vote  ;  id | 'n'  ;  list | 'lista'
 *
 * @see ProjGeral
 * @see MesaVoto
 * @see TerminalVoto
 * @see Protocolo#LISTA_CHAVES
 * @see Protocolo#LISTA_VALORES_TYPES
 * @author Dario Felix
 * @version 1.0
 */
public interface Protocolo extends ProjGeral {
    /**
     * Tamanho padronizado para o buffer utilizado nos diferentes atores da rede.
     * <p> KB = N * 1024 bytes
     */
    int BUFFER_LENGTH = 2 * 1024;

    /**
     * TTL (max. de saltos na rede) padronizado utilizado nos diferentes atores da rede.
     * <p> Rede local: TTL = 1
     */
    int TIME_TO_LIVE = 1;

    /**
     * Timeout "finito" em ms utilizado nos diferentes atores da rede.
     * <p> ms = N s * 1000 ms
     */
    int TIMEOUT_FINITO = 2 * 1000;

    /**
     * Timeout "infinito" (= 0)
     */
    int TIMEOUT_INFINITO = 0;

    /**
     * Separador de pares 'chave-valor' escrita na String utilizado para comunicar na rede (comandos).
     */
    String SEPARADOR_PARES = ";";

    /**
     * Separador dos campos 'chave' e 'valor' escrita na String utilizado para comunicar na rede (comandos).
     * <p> "\\|" em regex equivalente ao char "|", ver: https://bytes.com/topic/java/answers/772186-cant-do-str-split
     *
     * @see String#split(String)
     */
    String SEPARADOR_CHAVE_VALOR = "|";

    /**
     * Lista das chaves possiveis (permite mais tarde verificar e validar o comando)
     *
     * @see Protocolo#cmdHashMapToString(HashMap)
     * @see Protocolo#cmdStringToHashMap(String)
     */
    HashSet<String> LISTA_CHAVES = new HashSet<>(Arrays.asList("type", "id", "old_id", "new_id", "type_ack", "username", "password", "logged", "voted", "msg", "item_count", "list", "item_0_name"));

    /**
     * Lista dos valores possiveis que a chave "type" pode tomar (permite mais tarde verificar e validar o comando)
     *
     * @see Protocolo#cmdHashMapToString(HashMap)
     * @see Protocolo#cmdStringToHashMap(String)
     */
    HashSet<String> LISTA_VALORES_TYPES = new HashSet<>(Arrays.asList("hello", "set_id", "who_available", "im_available", "unblock", "ack", "login", "status", "get_list", "item_list", "vote"));


    /**
     * Faz parsing do comando e mapeia para um Map [ chave: valor ]
     * <p> Sintaxe do comando: type | valor_type { ; chave | valor }
     *
     * @param buffer String que contem o comando
     * @return null em caso de erro, ou um Map [ chave: valor ] em caso de sucesso
     * @see HashMap
     * @author Dario Felix
     * @version 1.0
     */
    static HashMap<String, String> cmdStringToHashMap(String buffer) {
        if (buffer == null || buffer.isBlank()) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdStringToHashMap", "buffer is blank ou null");
            return null;
        }

        if (DEBUG_ATIVO) {
            ProjGeral.printDebug("cmdStringToHashMap", "Input-String cmd: " + buffer);
        }

        ArrayList<String> listaParesChavesValores = new ArrayList<> (Arrays.asList(buffer.split(SEPARADOR_PARES)));
        HashMap<String, String> cmd = new HashMap<>();

        for (String parChaveValor: listaParesChavesValores) {
            ArrayList<String> listaChaveValor = new ArrayList<> (Arrays.asList(parChaveValor.split("\\" + SEPARADOR_CHAVE_VALOR)));
            listaChaveValor.set(0, listaChaveValor.get(0).toLowerCase().replace(" ", ""));

            if (DEBUG_ATIVO) {
                ProjGeral.printDebug("cmdStringToHashMap", "listaChaveValor: " + listaChaveValor);
            }

            if (listaChaveValor.size() == 1 && listaChaveValor.get(0).equals("msg") ) {
                listaChaveValor.add("---");
            }

            if (listaChaveValor.size() != 2 || cmd.containsKey(listaChaveValor.get(0)) || ( !LISTA_CHAVES.contains(listaChaveValor.get(0)) && (cmdVerificarChaveItemList(listaChaveValor.get(0)) == ERRO) )) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("cmdStringToHashMap", "problema nas chaves");
                return null;
            } else {
                cmd.put(listaChaveValor.get(0), listaChaveValor.get(1));
            }
        }

        if (!cmd.containsKey("type")) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdStringToHashMap", "cmd nao contem chave 'type'");
            return null;
        } else {
            cmd.replace("type", cmd.get("type").toLowerCase().replace(" ", ""));
            if (!LISTA_VALORES_TYPES.contains(cmd.get("type"))){
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("cmdStringToHashMap", "valor de 'type' nao conhecido");
                return null;
            }
        }

        if (DEBUG_ATIVO) {
            ProjGeral.printDebug("cmdStringToHashMap", "Output-HashMap cmd: " + cmd);
        }

        return cmd;
    }


    /**
     * Mapeia o comando de um Map [ chave: valor ] para uma String
     * <p> Sintaxe do comando: type | valor_type { ; chave | valor }
     *
     * @param cmdChaveValor Map com os campos do comando
     * @return null em caso de erro, ou uma String em caso de sucesso
     * @see HashMap
     * @author Dario Felix
     * @version 1.0
     */
    static String cmdHashMapToString(HashMap<String, String> cmdChaveValor) {
        if (cmdChaveValor == null || cmdChaveValor.isEmpty() || !cmdChaveValor.containsKey("type")) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdHashMapToString", "problema na HashMap");
            return null;
        }

        if (DEBUG_ATIVO) {
            ProjGeral.printDebug("cmdHashMapToString", "Input-HashMap cmd: " + cmdChaveValor);
        }

        StringBuilder cmd = new StringBuilder();
        boolean fator = false;

        for (String chave: cmdChaveValor.keySet()) {
            String chaveEditada = chave.toLowerCase().replace(" ", "");
            if (( !LISTA_CHAVES.contains(chaveEditada) && (cmdVerificarChaveItemList(chaveEditada) == ERRO) )) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("cmdHashMapToString", "chave nao conhecida");
                return null;
            } else if (chaveEditada.equals("type") && !LISTA_VALORES_TYPES.contains(cmdChaveValor.get(chaveEditada).toLowerCase().replace(" ", ""))) {
                if (DEBUG_ATIVO)
                    ProjGeral.printDebug("cmdHashMapToString", "valor de 'type' nao conhecido");
                return null;
            } else {
                if (fator)
                    cmd.append(SEPARADOR_PARES);
                else
                    fator = true;
                if (chaveEditada.equals("type"))
                    cmdChaveValor.replace(chaveEditada, cmdChaveValor.get(chaveEditada).toLowerCase().replace(" ", ""));
                cmd.append(chaveEditada).append(SEPARADOR_CHAVE_VALOR).append(cmdChaveValor.get(chave));
            }
        }

        if (DEBUG_ATIVO) {
            ProjGeral.printDebug("cmdHashMapToString", "Output-String cmd: " + cmd.toString());
        }

        return cmd.toString();
    }


    /**
     * Verifica se a sintaxe da chave esta correta num contexto de um comando type = item_list
     * <p> Sintaxe da chave: item_'num'_name
     * <p> Exemplo 1: item_0_name
     * <p> Exemplo 2: item_1_name
     *
     * @param chave Chave a verificar
     * @return ERRO ou SUCESSO
     * @see ProjGeral#SUCESSO
     * @see ProjGeral#ERRO
     * @author Dario Felix
     * @version 1.0
     */
    static boolean cmdVerificarChaveItemList(String chave) {
        String itemNameSemNumP1 = "item_";
        String itemNameSemNumP2 = "_name";
        int num;

        if (chave == null) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdVerificarChaveItemList", "chave nula");
            return ERRO;
        }

        chave = chave.toLowerCase().replace(" ", "");
        chave = chave.replace(itemNameSemNumP1, "");
        chave = chave.replace(itemNameSemNumP2, "");

        try {
            num = Integer.parseInt(chave,10);
        } catch (NumberFormatException e) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdVerificarChaveItemList", "chave '" + chave + "' nao contem um num");
            return ERRO;
        }

        if (!"".equals(chave.replace("" + num, ""))) {
            if (DEBUG_ATIVO)
                ProjGeral.printDebug("cmdVerificarChaveItemList", "chave desconhecida");
            return ERRO;
        }

        return SUCESSO;
    }
}