package com.bootcamp.directorio.service;

import com.bootcamp.directorio.dao.FuncionarioDao;
import com.bootcamp.directorio.model.Funcionario;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class FuncionarioService {

    private final FuncionarioDao dao;

    public FuncionarioService(FuncionarioDao dao) {
        this.dao = dao;
    }

    /** Si llegan ambos filtros con valor, se aplica el de nombre. Vacíos o solo espacios = ausentes. */
    @Transactional(readOnly = true)
    public List<Funcionario> listar(String nombre, String departamento) {
        if (tieneValor(nombre)) {
            return dao.findByNombre(nombre.trim());
        }
        if (tieneValor(departamento)) {
            return dao.findByDepartamento(departamento.trim());
        }
        return dao.findAll();
    }

    @Transactional(readOnly = true)
    public Funcionario consultar(Long id) {
        return dao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario no encontrado"));
    }

    public Funcionario registrar(Funcionario f) {
        if (f.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se debe enviar un ID al registrar");
        }
        normalizar(f);
        return dao.save(f);
    }

    public Funcionario actualizar(Long id, Funcionario datos) {
        Funcionario existente = consultar(id);
        normalizar(datos);
        existente.setNombre(datos.getNombre());
        existente.setCargo(datos.getCargo());
        existente.setDepartamento(datos.getDepartamento());
        existente.setTelefono(datos.getTelefono());
        return dao.save(existente);
    }

    public void eliminar(Long id) {
        if (!dao.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Funcionario no encontrado");
        }
        dao.deleteById(id);
    }

    private boolean tieneValor(String s) {
        return s != null && !s.isBlank();
    }

    private void normalizar(Funcionario f) {
        f.setNombre(f.getNombre().trim());
        f.setCargo(f.getCargo().trim());
        f.setDepartamento(f.getDepartamento().trim());
        f.setTelefono(tieneValor(f.getTelefono()) ? f.getTelefono().trim() : null);
    }
}
