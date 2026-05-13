package controle;

import entidade.usuario;
import facade.usuarioFacade;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;

@ManagedBean(name = "usuarioBean")
@RequestScoped
public class usuarioBean {

    private usuario usuario = new usuario();
    private String confirmarSenha;

    @EJB
    private usuarioFacade facade;

    public String salvar() {

        if (usuario.getNome() == null || usuario.getNome().isEmpty() ||
            usuario.getSenha() == null || usuario.getSenha().isEmpty() ||
            confirmarSenha == null || confirmarSenha.isEmpty()) {

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                "Atenção", "Preencha todos os campos!"));
            return null;
        }

        if (!usuario.getSenha().equals(confirmarSenha)) {

            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                "Erro", "As senhas não conferem!"));
            return null;
        }

        facade.create(usuario);

        FacesContext.getCurrentInstance().addMessage(null,
            new FacesMessage(FacesMessage.SEVERITY_INFO,
            "Sucesso", "Usuário cadastrado com sucesso!"));

        usuario = new usuario();
        confirmarSenha = null;

        return null;
    }

    // getters e setters

    public usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(usuario usuario) {
        this.usuario = usuario;
    }

    public String getConfirmarSenha() {
        return confirmarSenha;
    }

    public void setConfirmarSenha(String confirmarSenha) {
        this.confirmarSenha = confirmarSenha;
    }

    public usuarioFacade getFacade() {
        return facade;
    }

    public void setFacade(usuarioFacade facade) {
        this.facade = facade;
    }
}