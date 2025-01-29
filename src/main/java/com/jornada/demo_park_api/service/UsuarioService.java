package com.jornada.demo_park_api.service;

import com.jornada.demo_park_api.entity.Usuario;
import com.jornada.demo_park_api.exception.EntityNotFoundException;
import com.jornada.demo_park_api.exception.UsernameUniqueViolationException;
import com.jornada.demo_park_api.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;


    @Transactional
    public Usuario salvar (Usuario usuario) {
        try {
            usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
            return usuarioRepository.save(usuario);
        } catch(org.springframework.dao.DataIntegrityViolationException ex) {
            throw new UsernameUniqueViolationException(String.format("Username {%s} já cadastrado", usuario.getUsername()));
        }
    }

    @Transactional
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario %s nao encontrado", id))
        );
    }

    @Transactional
    public Usuario editarSenha(Long id, String senhaAtual, String novaSenha, String confirmaSenha) {
        if(!novaSenha.equals(confirmaSenha)) {
            throw new RuntimeException("as senhas nao coincidem");
        }

        Usuario user = buscarPorId(id);
        if(!passwordEncoder.matches(senhaAtual, user.getPassword())) {
            throw new RuntimeException("sua senha nao confore");
        }
        user.setPassword(passwordEncoder.encode(novaSenha));
        return user;
    }

    @Transactional
    public List<Usuario> buscarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional
    public Usuario buscarPorUsername(String username) {
        return usuarioRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(String.format("Usuario %s nao encontrado", username))
        );
    }

    @Transactional
    public Usuario.Role buscarRolePorUsername(String username) {
        return usuarioRepository.findRoleByUsername(username);
    }
}
