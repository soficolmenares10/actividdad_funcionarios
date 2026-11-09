package com.bootcamp.directorio.dao;

import com.bootcamp.directorio.model.Funcionario;

import java.util.List;
import java.util.Optional;

public interface FuncionarioDao {

    List<Funcionario> findAll();

    Optional<Funcionario> findById(Long id);

    List<Funcionario> findByNombre(String nombre);

    List<Funcionario> findByDepartamento(String departamento);

    boolean existsById(Long id);

    Funcionario save(Funcionario funcionario);

    void deleteById(Long id);
}
