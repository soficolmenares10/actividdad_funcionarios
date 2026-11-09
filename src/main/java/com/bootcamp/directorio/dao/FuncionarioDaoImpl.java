package com.bootcamp.directorio.dao;

import com.bootcamp.directorio.model.Funcionario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class FuncionarioDaoImpl implements FuncionarioDao {

    @PersistenceContext
    private EntityManager em;

    @Override
    public List<Funcionario> findAll() {
        return em.createQuery("SELECT f FROM Funcionario f ORDER BY f.id", Funcionario.class)
                .getResultList();
    }

    @Override
    public Optional<Funcionario> findById(Long id) {
        return Optional.ofNullable(em.find(Funcionario.class, id));
    }

    /** Coincidencia parcial, sin distinguir mayúsculas. */
    @Override
    public List<Funcionario> findByNombre(String nombre) {
        return em.createQuery(
                        "SELECT f FROM Funcionario f WHERE LOWER(f.nombre) LIKE LOWER(:nombre) ORDER BY f.id",
                        Funcionario.class)
                .setParameter("nombre", "%" + nombre + "%")
                .getResultList();
    }

    /** Nombre completo del área, sin distinguir mayúsculas. */
    @Override
    public List<Funcionario> findByDepartamento(String departamento) {
        return em.createQuery(
                        "SELECT f FROM Funcionario f WHERE LOWER(f.departamento) = LOWER(:departamento) ORDER BY f.id",
                        Funcionario.class)
                .setParameter("departamento", departamento)
                .getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long total = em.createQuery("SELECT COUNT(f) FROM Funcionario f WHERE f.id = :id", Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return total > 0;
    }

    @Override
    public Funcionario save(Funcionario funcionario) {
        if (funcionario.getId() == null) {
            em.persist(funcionario);
            return funcionario;
        }
        return em.merge(funcionario);
    }

    @Override
    public void deleteById(Long id) {
        Funcionario f = em.find(Funcionario.class, id);
        if (f != null) {
            em.remove(f);
        }
    }
}
