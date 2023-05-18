
package com.todocodeacademy.automovil.persistencia;

import com.todocodeacademy.automovil.logica.Automovil;
import com.todocodeacademy.automovil.persistencia.exceptions.NonexistentEntityException;
import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author Luisina
 */
public class AutomovilJpaController implements Serializable {

    public AutomovilJpaController(EntityManagerFactory emf) {
        this.emf = emf;
    }
    
   public AutomovilJpaController () {
       emf = Persistence.createEntityManagerFactory ("AutoPU");
   }
    
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Automovil automovil) {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            em.persist(automovil);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Automovil automovil) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            automovil = em.merge(automovil);
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                int id = automovil.getId();
                if (findAutomovil(id) == null) {
                    throw new NonexistentEntityException("The automovil with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(int id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Automovil automovil;
            try {
                automovil = em.getReference(Automovil.class, id);
                automovil.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The automovil with id " + id + " no longer exists.", enfe);
            }
            em.remove(automovil);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Automovil> findAutomovilEntities() {
        return findAutomovilEntities(true, -1, -1);
    }

    public List<Automovil> findAutomovilEntities(int maxResults, int firstResult) {
        return findAutomovilEntities(false, maxResults, firstResult);
    }

    private List<Automovil> findAutomovilEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Automovil.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Automovil findAutomovil(int id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Automovil.class, id);
        } finally {
            em.close();
        }
    }

    public int getAutomovilCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Automovil> rt = cq.from(Automovil.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
