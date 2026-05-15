package facade;

import entidade.ordemServico;
import java.util.List;
import javax.annotation.security.PermitAll;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Stateless
@PermitAll
public class ordemServicoFacade extends AbstractFacade<ordemServico> {

    @PersistenceContext(unitName = "EstoqueOlindaPU")
    private EntityManager em;

    public ordemServicoFacade() {
        super(ordemServico.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Busca ordens pelo nome do cliente (parcial, case-insensitive)
     */
    public List<ordemServico> findByCliente(String nome) {
        TypedQuery<ordemServico> query = em.createQuery(
            "SELECT o FROM ordemServico o WHERE LOWER(o.cliente.nome) LIKE LOWER(:nome) ORDER BY o.dataAbertura DESC",
            ordemServico.class);
        query.setParameter("nome", "%" + nome + "%");
        return query.getResultList();
    }

    /**
     * Busca ordens por status
     */
    public List<ordemServico> findByStatus(String status) {
        TypedQuery<ordemServico> query = em.createQuery(
            "SELECT o FROM ordemServico o WHERE o.status = :status ORDER BY o.dataAbertura DESC",
            ordemServico.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Busca pelo número da ordem
     */
    public List<ordemServico> findByNumero(String numero) {
        TypedQuery<ordemServico> query = em.createQuery(
            "SELECT o FROM ordemServico o WHERE LOWER(o.numeroOrdem) LIKE LOWER(:numero) ORDER BY o.dataAbertura DESC",
            ordemServico.class);
        query.setParameter("numero", "%" + numero + "%");
        return query.getResultList();
    }

    /**
     * Retorna todas as ordens ordenadas pela mais recente
     */
    @Override
    public List<ordemServico> findAll() {
        TypedQuery<ordemServico> query = em.createQuery(
            "SELECT o FROM ordemServico o ORDER BY o.dataAbertura DESC",
            ordemServico.class);
        return query.getResultList();
    }

    /**
     * Gera o próximo número de ordem no formato OS-00001
     */
    public String gerarNumeroOrdem() {
        TypedQuery<Long> query = em.createQuery(
            "SELECT COUNT(o) FROM ordemServico o", Long.class);
        long total = query.getSingleResult();
        return String.format("OS-%05d", total + 1);
    }
}