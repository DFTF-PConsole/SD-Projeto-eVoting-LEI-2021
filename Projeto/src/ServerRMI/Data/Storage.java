/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * REALIZADO POR: Cláudia Campos N.2018285941 | Dário Félix N.2018275530 | Projeto "eVoting" Meta 1 - SD 2020/2021 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package ServerRMI.Data;

import java.io.*;

//TODO Fazer melhor handling das exceptions

/**
 * Classe que permite gerir ficheiros de objectos que são usados para armazenar os dados da aplicação.
 *
 * @author Cláudia Campos
 * @version 1.0
 */
public class Storage {

    /**
     * Método que permite guardar um determinado objecto num dado ficheiro de objectos.
     *
     * @param filename String: Nome do ficheiro de objectos
     * @param object   Object: Objecto a ser guardado no ficheiro de objectos
     * @author Cláudia Campos
     * @version 1.0
     */
    public void saveObjectFile(String filename, Object object) {
        File f = new File(filename);

        try {

            if (f.createNewFile()) {
                System.out.println("Ficheiro de objectos \"" + filename + "\"criado com sucesso.");
            } else {
                System.out.println("Ficheiro de objectos \"" + filename + "\"já existe.");
            }

        } catch (IOException ex) {
            System.out.println("Não foi possível criar o ficheiro \"" + filename + "\": " + ex.getMessage());
            //ex.printStackTrace();
        }

        if (f.exists() && f.isFile()) {
            try {
                FileOutputStream fos = new FileOutputStream(f);
                ObjectOutputStream oos = new ObjectOutputStream(fos);

                oos.writeObject(object);

                oos.flush();
                fos.flush();
                oos.close();
                fos.close();
            } catch (IOException ex) {
                System.out.println("Não foi possível abrir o ficheiro \"" + filename + "\": " + ex.getMessage());
                //ex.printStackTrace();
            }
        } else {
            System.out.println("O ficheiro \"" + filename + "\" não existe.");
        }

    }

    /**
     * Método estático que permite extrair um determinado objecto de um ficheiro de objectos.
     *
     * @param filename String: Nome do ficheiro de objectos
     * @return Object: Objecto que se encontra armazenado no ficheiro de objectos
     * @author Cláudia Campos
     * @version 1.0
     */
    public Object loadObject(String filename) {
        Object object = null;

        try {
            FileInputStream fis = new FileInputStream(filename);
            ObjectInputStream ois = new ObjectInputStream(fis);

            object = ois.readObject();

            ois.close();
            fis.close();
        } catch (ClassNotFoundException ex) {
            System.out.println("ClassNotFoundException: " + ex.getMessage());
            //ex.printStackTrace();
        } catch (FileNotFoundException ex) {
            System.out.println("FileNotFoundException: " + ex.getMessage());
            //ex.printStackTrace();

            File f = new File(filename);
            try {
                if (f.createNewFile()) {
                    System.out.println("O ficheiro \"" + filename + "\" foi criado com sucesso.");
                } else {
                    System.out.println("O ficheiro \"" + filename + "\" já existente.");
                }
            } catch (IOException e) {
                System.out.println("Não foi possível criar o ficheiro \"" + filename + "\": " + e.getMessage());
                //ex.printStackTrace();
            }

        } catch (EOFException ex) {
            System.out.println("O ficheiro \"" + filename + "\" está vazio: " + ex.getMessage());
            //ex.printStackTrace();
        } catch (IOException ex) {
            System.out.println("Não foi possível abrir o ficheiro \"" + filename + "\": " + ex.getMessage());
            //ex.printStackTrace();
        } catch (NullPointerException ex) {
            System.out.println("NullPointerException: " + ex.getMessage());
            //ex.printStackTrace();
        }

        return object;
    }

}