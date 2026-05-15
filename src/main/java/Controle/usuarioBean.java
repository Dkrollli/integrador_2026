package controle;

import entidade.usuario;
import facade.usuarioFacade;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;

@Named("usuarioBean")
@ViewScoped
public class usuarioBean implements Serializable {

    private static final Logger LOG = Logger.getLogger(usuarioBean.class.getName());

    private usuario usuario = new usuario();
    private List<usuario> listaUsuarios;
    private String senhaTexto = "";
    private String confirmarSenha = "";
    private String termoBusca = "";
    private boolean formularioVisivel = false;

    @EJB
    private usuarioFacade facade;

    @PostConstruct
    public void init() {
        carregarLista();
    }

    // ===================== CARREGAR LISTA =====================

    public void carregarLista() {
        listaUsuarios = facade.findAll();
    }

    // ===================== NOVO =====================

    public void novo() {
        usuario = new usuario();
        senhaTexto = "";
        confirmarSenha = "";
        formularioVisivel = true;
    }

    // ===================== EDITAR =====================

    public void editar(usuario u) {
        this.usuario = new usuario();
        this.usuario.setId(u.getId());
        this.usuario.setNome(u.getNome());
        this.usuario.setLogin(u.getLogin());
        this.usuario.setSenha(u.getSenha());
        this.usuario.setPerfil(u.getPerfil());
        senhaTexto = "";
        confirmarSenha = "";
        formularioVisivel = true;
    }

    // ===================== CANCELAR =====================

    public void cancelar() {
        usuario = new usuario();
        senhaTexto = "";
        confirmarSenha = "";
        formularioVisivel = false;
    }

    // ===================== SALVAR =====================

    public void salvar() {

        // Validação de login duplicado
        if (facade.loginJaCadastrado(usuario.getLogin(), usuario.getId())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Este login já está em uso!");
            return;
        }

        // Novo usuário: senha obrigatória
        if (usuario.getId() == 0 && (senhaTexto == null || senhaTexto.trim().isEmpty())) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Informe uma senha!");
            return;
        }

        // Se digitou senha, valida e salva em texto puro
        if (senhaTexto != null && !senhaTexto.trim().isEmpty()) {

            if (senhaTexto.length() < 6) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "A senha deve ter pelo menos 6 caracteres!");
                return;
            }

            if (!senhaTexto.equals(confirmarSenha)) {
                addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "As senhas não conferem!");
                return;
            }

            // Salva direto sem hash por enquanto
            usuario.setSenha(senhaTexto);
        }

        try {
            if (usuario.getId() == 0) {
                facade.create(usuario);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Funcionário cadastrado com sucesso!");
            } else {
                facade.edit(usuario);
                addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Funcionário atualizado com sucesso!");
            }
            cancelar();
            carregarLista();

        } catch (Exception e) {
            LOG.severe("Erro ao salvar usuário: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Falha ao salvar. Tente novamente.");
        }
    }

    // ===================== EXCLUIR =====================

    public void excluir(usuario u) {
        try {
            facade.remove(u);
            addMessage(FacesMessage.SEVERITY_INFO, "Sucesso", "Usuário removido com sucesso!");
            carregarLista();
        } catch (Exception e) {
            LOG.severe("Erro ao excluir usuário: " + e.getMessage());
            addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Não foi possível remover o usuário.");
        }
    }

    // ===================== BUSCAR =====================

    public void buscar() {
        if (termoBusca == null || termoBusca.trim().isEmpty()) {
            carregarLista();
        } else {
            listaUsuarios = facade.findByNome(termoBusca.trim());
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

    public usuario getUsuario() { return usuario; }
    public void setUsuario(usuario usuario) { this.usuario = usuario; }

    public List<usuario> getListaUsuarios() { return listaUsuarios; }

    public String getSenhaTexto() { return senhaTexto; }
    public void setSenhaTexto(String senhaTexto) { this.senhaTexto = senhaTexto; }
                                                                            
    public String getConfirmarSenha() { return confirmarSenha; }
    public void setConfirmarSenha(String confirmarSenha) { this.confirmarSenha = confirmarSenha; }

    public String getTermoBusca() { return termoBusca; }
    public void setTermoBusca(String termoBusca) { this.termoBusca = termoBusca; }

    public boolean isFormularioVisivel() { return formularioVisivel; }
    public void setFormularioVisivel(boolean formularioVisivel) { this.formularioVisivel = formularioVisivel; }
}