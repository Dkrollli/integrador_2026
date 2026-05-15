package controle;

import entidade.usuario;
import facade.usuarioFacade;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.HttpSession;

@Named("loginBean")
@RequestScoped
public class loginBean {

    private static final Logger LOG = Logger.getLogger(loginBean.class.getName());

    // Credenciais do admin fixas — troque depois de subir o sistema
    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_SENHA = "admin123";

    private String login;
    private String senha;

    @EJB
    private usuarioFacade facade;

    public String entrar() {

        if (login == null || login.trim().isEmpty()
                || senha == null || senha.trim().isEmpty()) {
            addMessage(FacesMessage.SEVERITY_WARN, "Atenção", "Preencha o login e a senha");
            return null;
        }

        // ===== LOGIN DO ADMIN (hardcoded) =====
        if (login.trim().equals(ADMIN_LOGIN) && senha.equals(ADMIN_SENHA)) {
            LOG.info("Login admin realizado com sucesso");
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                    .getExternalContext().getSession(true);
            session.setAttribute("perfil", "ADMIN");
            session.setAttribute("nomeLogado", "Administrador");
            return "index.xhtml?faces-redirect=true&pagina=inicio";
        }

        // ===== LOGIN DE FUNCIONÁRIOS (banco) =====
        usuario u = facade.findByLogin(login.trim());

        if (u != null) {
            // Funcionários cadastrados pelo admin usam senha em texto puro por enquanto
            if (senha.equals(u.getSenha())) {
                LOG.info("Login bem-sucedido: " + login + " | Perfil: " + u.getPerfil());
                HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                        .getExternalContext().getSession(true);
                session.setAttribute("usuarioLogado", u);
                session.setAttribute("perfil", u.getPerfil());
                session.setAttribute("nomeLogado", u.getNome());
                return "index.xhtml?faces-redirect=true&pagina=inicio";
            }
        }

        LOG.warning("Tentativa de login falhou para: " + login);
        addMessage(FacesMessage.SEVERITY_ERROR, "Erro", "Login ou senha inválidos");
        return null;
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
                .getExternalContext().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "login.xhtml?faces-redirect=true";
    }

    private void addMessage(FacesMessage.Severity severity, String summary, String detail) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(severity, summary, detail));
    }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
}