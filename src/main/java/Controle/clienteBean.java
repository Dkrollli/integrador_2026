package controle;

import entidade.cliente;
import facade.clienteFacade;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("clienteBean")
@ViewScoped
public class clienteBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(clienteBean.class.getName());

    private cliente cliente = new cliente();
    private List<cliente> listaClientes;
    private String termoBusca = "";
    private String pagina = "inicio";

    // Controla se o formulário de cadastro/edição está visível
    private boolean formularioVisivel = false;

    @EJB
    private clienteFacade facade;

    @PostConstruct
    public void init() {
        String paginaParam = FacesContext.getCurrentInstance()
                .getExternalContext().getRequestParameterMap().get("pagina");
        if (paginaParam != null && !paginaParam.isEmpty()) {
            this.pagina = paginaParam;
        }
        carregarLista();
    }

    // ===================== CARREGAR LISTA =====================

    public void carregarLista() {
        listaClientes = facade.findAll();
    }

    // ===================== NOVO (abre formulário vazio) =====================

    public void novo() {
        cliente = new cliente();
        formularioVisivel = true;
    }

    // ===================== EDITAR (abre formulário preenchido) =====================

    public void editar(cliente c) {
        this.cliente = new cliente();
        this.cliente.setId(c.getId());
        this.cliente.setNome(c.getNome());
        this.cliente.setEndereco(c.getEndereco());
        this.cliente.setTelefone(c.getTelefone());
        this.cliente.setCpf(c.getCpf());
        formularioVisivel = true;
    }

    // ===================== CANCELAR (fecha formulário) =====================

    public void cancelar() {
        cliente = new cliente();
        formularioVisivel = false;
    }

    // ===================== SALVAR (CREATE / UPDATE) =====================

    public void salvar() {

        if (facade.cpfJaCadastrado(cliente.getCpf(), cliente.getId())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "CPF já cadastrado para outro cliente!");
            return;
        }

        try {
            if (cliente.getId() == 0) {
                facade.create(cliente);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente cadastrado com sucesso!");
            } else {
                facade.edit(cliente);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente atualizado com sucesso!");
            }

            cancelar();        // fecha o formulário após salvar
            carregarLista();

        } catch (Exception e) {
            LOG.severe("Erro ao salvar cliente: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar cliente. Tente novamente.");
        }
    }

    // ===================== EXCLUIR =====================

    public void excluir(cliente c) {
        try {
            facade.remove(c);
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Cliente removido com sucesso!");
            carregarLista();
        } catch (Exception e) {
            LOG.severe("Erro ao excluir cliente: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível remover o cliente.");
        }
    }

    // ===================== BUSCAR =====================

    public void buscar() {
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            carregarLista();
        } else {
            listaClientes = facade.findByNome(termoBusca.trim());
        }
    }

    // ===================== LIMPAR BUSCA =====================

    public void limparBusca() {
        termoBusca = "";
        carregarLista();
    }

    // ===================== UTILITÁRIO =====================

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    // ===================== GETTERS E SETTERS =====================

    public cliente getCliente() { return cliente; }
    public void setCliente(cliente cliente) { this.cliente = cliente; }

    public List<cliente> getListaClientes() { return listaClientes; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    public String getPagina() { return pagina; }
    public void setPagina(String pagina) { this.pagina = pagina; }

    public boolean isFormularioVisivel() { return formularioVisivel; }
    public void setFormularioVisivel(boolean formularioVisivel) { this.formularioVisivel = formularioVisivel; }
}