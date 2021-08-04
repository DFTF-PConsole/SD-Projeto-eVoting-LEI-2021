package models;

import ServerRMI.AdminConsoleInterface;
import ServerRMI.Exceptions.*;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.concurrent.CopyOnWriteArrayList;

public class AdminBean {
    private AdminConsoleInterface rmiServer;

    public AdminBean() {
        try {
            //this.rmiServer = (AdminConsoleInterface) Naming.lookup("server");
            this.rmiServer = (AdminConsoleInterface) LocateRegistry.getRegistry("127.0.0.1", 2000).lookup("server");
        } catch(NotBoundException | RemoteException e) {
            e.printStackTrace();
        }
    }

    public CopyOnWriteArrayList<String> getListaEleicoes() {
        try {
            return this.rmiServer.getEleicoes();
        } catch (RemoteException e) {
            return new CopyOnWriteArrayList<>();
        }

    }


    public String votar(String nomeEleicao, String faculdadeOuDepartamento, String numCC, String nomeLista) {
        try {
            this.rmiServer.votar(nomeEleicao, faculdadeOuDepartamento, numCC, nomeLista);
        } catch (Exception e) {
            return "Não foi possivel votar! Temos pena: " + e.getMessage();
        }

        return "Voto efetuado com sucesso. Já pode ir p'ra casa.";
    }


    public CopyOnWriteArrayList<String> getObterNomesListasCandidatas(String eleicao) {
        try {
            return this.rmiServer.obterNomesListasCandidatas(eleicao);
        } catch (Exception e) {
            return new CopyOnWriteArrayList<>();
        }
    }


    public boolean criarEleicao(String titulo, String descricao, int diaInicio, int mesInicio, int anoInicio, int horaInicio,
                                int minutosInicio, int diaFim, int mesFim, int anoFim, int horaFim, int minutosFim,
                                boolean estudantes, boolean docentes, boolean funcionarios) {
        try {
            return this.rmiServer.criarEleicao(titulo, descricao, diaInicio, mesInicio, anoInicio, horaInicio, minutosInicio, diaFim,
                    mesFim, anoFim, horaFim, minutosFim, estudantes, docentes, funcionarios);
        } catch (EleicaoExistenteException | RemoteException e) {
            return false;
        }
    }

    public boolean registarDocente(String nome, String contacto, String morada, String codigoPostal, String numCC,
                                   int validadeDia, int validadeMes, int validadeAno, String faculdade,
                                   String departamento, String password, String numMec) {
        try {
            return this.rmiServer.registarDocente(nome, contacto, morada, codigoPostal, numCC, validadeDia, validadeMes,
                    validadeAno, faculdade, departamento, password, numMec);
        } catch (RemoteException | PessoaRegistadaException e) {
            return false;
        }
    }

    public boolean registarEstudante(String nome, String contacto, String morada, String codigoPostal, String numCC,
                                   int validadeDia, int validadeMes, int validadeAno, String faculdade,
                                   String departamento, String password, String numEstudante) {
        try {
            return this.rmiServer.registarEstudante(nome, contacto, morada, codigoPostal, numCC, validadeDia, validadeMes,
                    validadeAno, faculdade, departamento, password, numEstudante);
        } catch (RemoteException | PessoaRegistadaException e) {
            return false;
        }
    }

    public boolean registarFuncionario(String nome, String contacto, String morada, String codigoPostal, String numCC,
                                     int validadeDia, int validadeMes, int validadeAno, String faculdade,
                                     String departamento, String password) {
        try {
            return this.rmiServer.registarFuncionario(nome, contacto, morada, codigoPostal, numCC, validadeDia, validadeMes,
                    validadeAno, faculdade, departamento, password);
        } catch (RemoteException | PessoaRegistadaException e) {
            return false;
        }
    }

    public String getMesaVotoEleitor(String nomeEleicao, String numCC) {
        try {
            return this.rmiServer.obterLocalVotoEleitor(nomeEleicao, numCC);
        } catch (RemoteException | EleicaoNaoExistenteException | PessoaNaoRegistadaException e) {
            return e.getMessage();
        }
    }

    public String getDataVotoEleitor(String nomeEleicao, String numCC) {
        try {
            return this.rmiServer.obterDataVotoEleitor(nomeEleicao, numCC);
        } catch (PessoaNaoRegistadaException | EleicaoNaoExistenteException | RemoteException e) {
            return e.getMessage();
        }
    }

    public boolean adicionarMesaVoto(String nomeEleicao, String faculdadeOuDepartamento) {
        try {
            return this.rmiServer.adicionarMesaVoto(nomeEleicao, faculdadeOuDepartamento);
        } catch (RemoteException | DadosInvalidosException | EleicaoNaoExistenteException | MesaVotoRegistadaException e) {
            return false;
        }
    }

    public CopyOnWriteArrayList<String> obterPessoasEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.obterPessoasEleicao(nomeEleicao);
        } catch (RemoteException | EleicaoNaoExistenteException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return new CopyOnWriteArrayList<>();
        }
    }

    public boolean adicionarListaAEleicao(String nomeEleicao, String nomeLista, CopyOnWriteArrayList<String> membrosNumCC) {
        try {
            return this.rmiServer.adicionarListaAEleicao(nomeEleicao, nomeLista, membrosNumCC);
        } catch (RemoteException | MembroNaoPertenceListaException | DadosInvalidosException | EleicaoNaoExistenteException | EleicaoComecouException | CandidaturaExistenteException | MembroPertenceListaException | PessoaNaoRegistadaException e) {
            return false;
        }
    }

    public String getDescricaoEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.getDescricaoEleicao(nomeEleicao);
        } catch (EleicaoNaoExistenteException | RemoteException e) {
            return "";
        }
    }

    public String getDataInicioEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.getDataInicioEleicao(nomeEleicao);
        } catch (EleicaoNaoExistenteException | RemoteException e) {
            return "";
        }
    }

    public String getDataFimEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.getDataFimEleicao(nomeEleicao);
        } catch (EleicaoNaoExistenteException | RemoteException e) {
            return "";
        }
    }

    public String getTituloEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.getTituloEleicao(nomeEleicao);
        } catch (EleicaoNaoExistenteException | RemoteException e) {
            return "";
        }
    }

    public boolean alterarTituloEleicao(String nomeEleicao, String novoTitulo) {
        try {
            return this.rmiServer.alterarTituloEleicao(nomeEleicao, novoTitulo);
        } catch (RemoteException | DadosInvalidosException | EleicaoNaoExistenteException | EleicaoExistenteException | EleicaoComecouException e) {
            return false;
        }
    }

    public boolean alterarDescricaoEleicao(String nomeEleicao, String novaDescricao) {
        try {
            return this.rmiServer.alterarDescricaoEleicao(nomeEleicao, novaDescricao);
        } catch (RemoteException | DadosInvalidosException | EleicaoNaoExistenteException | EleicaoComecouException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    public boolean alterarDataInicioEleicao(String nomeEleicao, int diaInicio, int mesInicio, int anoInicio, int horaInicio, int minutoInicio) {
        try {
            return this.rmiServer.alterarDataInicioEleicao(nomeEleicao, diaInicio, mesInicio, anoInicio, horaInicio, minutoInicio);
        } catch (RemoteException | DadosInvalidosException | EleicaoNaoExistenteException | EleicaoComecouException e) {
            return false;
        }
    }

    public boolean alterarDataFimEleicao(String nomeEleicao, int diaFim, int mesFim, int anoFim, int horaFim, int minutoFim) {
        try {
            return this.rmiServer.alterarDataInicioEleicao(nomeEleicao, diaFim, mesFim, anoFim, horaFim, minutoFim);
        } catch (RemoteException | DadosInvalidosException | EleicaoNaoExistenteException | EleicaoComecouException e) {
            return false;
        }
    }

    public String getEleicao(String nomeEleicao) {
        try {
            return this.rmiServer.getEleicao(nomeEleicao);
        } catch (EleicaoNaoExistenteException | RemoteException e) {
            return "";
        }
    }

}
