package facade;

import entidade.usuario;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@PermitAll
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

    public usuario findByLogin(String login) {
        try {
            TypedQuery<usuario> query = em.createQuery(
                "SELECT u FROM usuario u WHERE u.login = :login", usuario.class);
            query.setParameter("login", login);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<usuario> findByNome(String nome) {
        TypedQuery<usuario> query = em.createQuery(
            "SELECT u FROM usuario u WHERE LOWER(u.nome) LIKE LOWER(:nome) ORDER BY u.nome",
            usuario.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    public List<usuario> findFuncionarios() {
        TypedQuery<usuario> query = em.createQuery(
            "SELECT u FROM usuario u WHERE u.perfil = 'FUNCIONARIO' ORDER BY u.nome",
            usuario.class);
        return query.getResultList();
    }

    public boolean loginJaCadastrado(String login, int idAtual) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(u) FROM usuario u WHERE u.login = :login AND u.id <> :id",
            Long.class);
        query.setParameter("login", login);
        query.setParameter("id", idAtual);
        return query.getSingleResult() > 0;
    }
}