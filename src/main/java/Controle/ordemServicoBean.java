package controle;

import entidade.cliente;
import entidade.ordemServico;
import entidade.usuario;
import facade.clienteFacade;
import facade.ordemServicoFacade;
import facade.usuarioFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("ordemBean")
@ViewScoped
public class ordemServicoBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(ordemServicoBean.class.getName());

    private ordemServico ordem = new ordemServico();
    private List<ordemServico> listaOrdens = new ArrayList<>();
    private List<cliente> listaClientes = new ArrayList<>();
    private String termoBusca = "";
    private String filtroBusca = "numero";
    private boolean formularioVisivel = false;

    @EJB private ordemServicoFacade facade;
    @EJB private clienteFacade clienteFacade;
    @EJB private usuarioFacade usuarioFacade;

    @PostConstruct
    public void init() {
        try {
            listaOrdens = facade.findAll();
        } catch (Exception e) {
            LOG.severe("Erro ao carregar ordens: " + e.getMessage());
            listaOrdens = new ArrayList<>();
        }

        try {
            listaClientes = clienteFacade.findAll();
        } catch (Exception e) {
            LOG.severe("Erro ao carregar clientes: " + e.getMessage());
            listaClientes = new ArrayList<>();
        }
    }

    // ===================== CARREGAR LISTA =====================

    public void carregarLista() {
        try {
            listaOrdens = facade.findAll();
        } catch (Exception e) {
            LOG.severe("Erro ao carregar ordens: " + e.getMessage());
            listaOrdens = new ArrayList<>();
        }
    }

    // ===================== NOVO =====================

    public void novo() {
        ordem = new ordemServico();
        ordem.setStatus("AGUARDANDO_ANALISE");
        formularioVisivel = true;
    }

    // ===================== EDITAR =====================

    public void editar(ordemServico o) {
        ordemServico managed = new ordemServico();
        managed.setId(o.getId());
        managed.setNumeroOrdem(o.getNumeroOrdem());
        managed.setCliente(o.getCliente());
        managed.setFuncionario(o.getFuncionario());
        managed.setEquipamento(o.getEquipamento());
        managed.setMarca(o.getMarca());
        managed.setModelo(o.getModelo());
        managed.setNumeroSerie(o.getNumeroSerie());
        managed.setDefeito(o.getDefeito());
        managed.setObservacoes(o.getObservacoes());
        managed.setStatus(o.getStatus());
        managed.setDataAbertura(o.getDataAbertura());
        managed.setDataConclusao(o.getDataConclusao());
        this.ordem = managed;
        formularioVisivel = true;
    }

    // ===================== CANCELAR =====================

    public void cancelar() {
        ordem = new ordemServico();
        formularioVisivel = false;
    }

    // ===================== SALVAR =====================

    public void salvar() {

        if (ordem.getCliente() == null) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Selecione um cliente!");
            return;
        }

        if (ordem.getEquipamento() == null || ordem.getEquipamento().trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Informe o equipamento!");
            return;
        }

        if (ordem.getDefeito() == null || ordem.getDefeito().trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Informe o defeito!");
            return;
        }

        try {
            if (ordem.getId() == 0) {
                ordem.setNumeroOrdem(facade.gerarNumeroOrdem());

                // Vincula o funcionário logado se houver
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(false);
                if (session != null) {
                    usuario logado = (usuario) session.getAttribute("usuarioLogado");
                    if (logado != null) {
                        ordem.setFuncionario(logado);
                    }
                }

                facade.create(ordem);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso",
                        "Ordem " + ordem.getNumeroOrdem() + " aberta com sucesso!");
            } else {
                if ("CONCLUIDA".equals(ordem.getStatus()) && ordem.getDataConclusao() == null) {
                    ordem.setDataConclusao(new Date());
                }
                facade.edit(ordem);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Ordem atualizada com sucesso!");
            }

            cancelar();
            carregarLista();

        } catch (Exception e) {
            LOG.severe("Erro ao salvar ordem: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar ordem. Tente novamente.");
        }
    }

    // ===================== EXCLUIR =====================

    public void excluir(ordemServico o) {
        try {
            facade.remove(o);
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Ordem removida com sucesso!");
            carregarLista();
        } catch (Exception e) {
            LOG.severe("Erro ao excluir ordem: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível remover a ordem.");
        }
    }

    // ===================== BUSCAR =====================

    public void buscar() {
        try {
            if (termoBusca == null || termoBusca.trim().isEmpty()) {
                carregarLista();
                return;
            }
            switch (filtroBusca) {
                case "cliente": listaOrdens = facade.findByCliente(termoBusca.trim()); break;
                default:        listaOrdens = facade.findByNumero(termoBusca.trim());  break;
            }
        } catch (Exception e) {
            LOG.severe("Erro na busca: " + e.getMessage());
            listaOrdens = new ArrayList<>();
        }
    }

    // ===================== LIMPAR BUSCA =====================

    public void limparBusca() {
        termoBusca = "";
        filtroBusca = "numero";
        carregarLista();
    }

    // ===================== LABEL DO STATUS =====================

    public String getLabelStatus(String status) {
        if (status == null) return "";
        switch (status) {
            case "AGUARDANDO_ANALISE": return "Aguardando Análise";
            case "AGUARDANDO_PECA":   return "Aguardando Peça";
            case "PRE_PRONTO":        return "Pré-Pronto";
            case "CONCLUIDA":         return "Concluída";
            case "CANCELADA":         return "Cancelada";
            default: return status;
        }
    }

    // ===================== UTILITÁRIO =====================

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // ===================== GETTERS E SETTERS =====================

    public ordemServico getOrdem() { return ordem; }
    public void setOrdem(ordemServico ordem) { this.ordem = ordem; }

    public List<ordemServico> getListaOrdens() { return listaOrdens; }
    public List<cliente> getListaClientes() { return listaClientes; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    public String getFiltroBusca() { return filtroBusca; }
    public void setFiltroBusca(String filtroBusca) { this.filtroBusca = filtroBusca; }

    public boolean isFormularioVisivel() { return formularioVisivel; }
    public void setFormularioVisivel(boolean formularioVisivel) { this.formularioVisivel = formularioVisivel; }
}