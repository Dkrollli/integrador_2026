package controle;

import entidade.usuario;
import facade.usuarioFacade;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "loginBean")
@RequestScoped
public class loginBean {

    private String usuario;
    private String senha;

    @EJB
    private usuarioFacade facade;

    public String login() {

        if (usuario == null || usuario.isEmpty()
                || senha == null || senha.isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Atenção", "Preencha usuário e senha"));
            return null;
        }

        List<usuario> lista = facade.findAll();

        for (usuario u : lista) {
            if (u.getNome().equals(usuario) && u.getSenha().equals(senha)) {
                return "index.xhtml?faces-redirect=true";
            }
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Erro", "Usuário ou senha inválidos"));

        return null;
    }

    // getters e setters

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public usuarioFacade getFacade() {
        return facade;
    }

    public void setFacade(usuarioFacade facade) {
        this.facade = facade;
    }
}
