package facade;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import entidade.usuario;

@Stateless
public class usuarioFacade extends AbstractFacade<usuario> {

    @PersistenceContext(unitName = "EstoqueOlindaPU")
    private EntityManager em;

    public usuarioFacade() {
        super(usuario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    // 🔎 Buscar usuário no banco (login real)
    public usuario buscarLogin(String nome, String senha) {
        try {
            return em.createQuery(
                    "SELECT u FROM usuario u WHERE u.nome = :nome AND u.senha = :senha",
                    usuario.class)
                    .setParameter("nome", nome)
                    .setParameter("senha", senha)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
}