package facade;

import entidade.cliente;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
public class clienteFacade extends AbstractFacade<cliente> {

    @PersistenceContext(unitName = "EstoqueOlindaPU")
    private EntityManager em;

    public clienteFacade() {
        super(cliente.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca clientes pelo nome (pesquisa parcial, case-insensitive)
     */
    public List<cliente> findByNome(String nome) {
        TypedQuery<cliente> query = em.createQuery(
            "SELECT c FROM cliente c WHERE LOWER(c.nome) LIKE LOWER(:nome) ORDER BY c.nome",
            cliente.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    /**
     * Verifica se já existe um cliente com esse CPF (para validação de duplicidade)
     */
    public boolean cpfJaCadastrado(String cpf, int idAtual) {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(c) FROM cliente c WHERE c.cpf = :cpf AND c.id <> :id",
            Long.class);
        query.setParameter("cpf", cpf);
        query.setParameter("id", idAtual);
        return query.getSingleResult() > 0;
    }
}