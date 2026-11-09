package com.bootcamp.directorio.controller;

import com.bootcamp.directorio.model.Funcionario;
import com.bootcamp.directorio.service.FuncionarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funcionarios")
public class FuncionarioController {

    private final FuncionarioService service;

    public FuncionarioController(FuncionarioService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<Funcionario>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String departamento) {
        List<Funcionario> lista = service.listar(nombre, departamento);
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public Funcionario consultar(@PathVariable Long id) {
        return service.consultar(id);
    }

    @PostMapping
    public ResponseEntity<Funcionario> registrar(@Valid @RequestBody Funcionario funcionario) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(funcionario));
    }

    @PutMapping("/{id}")
    public Funcionario actualizar(@PathVariable Long id, @Valid @RequestBody Funcionario funcionario) {
        return service.actualizar(id, funcionario);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
