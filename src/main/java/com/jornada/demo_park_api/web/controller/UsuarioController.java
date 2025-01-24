package com.jornada.demo_park_api.web.controller;

import com.jornada.demo_park_api.entity.Usuario;
import com.jornada.demo_park_api.service.UsuarioService;
import com.jornada.demo_park_api.web.dto.UsuarioCreateDto;
import com.jornada.demo_park_api.web.dto.UsuarioResponseDto;
import com.jornada.demo_park_api.web.dto.UsuarioSenhaDto;
import com.jornada.demo_park_api.web.dto.mapper.UsuarioMapper;
import com.jornada.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Contém todas as operações referente aos recursos CRUD de um usuário.")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Operation(summary = "Criar um novo usuário", description = "recurso para criar um novo usuario",
            responses = {
                    @ApiResponse(responseCode = "201", description = "recurso criado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "422", description = "dados de entrada invalidos", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "usuario ja cadastrado", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class)))
            }
    )
    @PostMapping
    public ResponseEntity<UsuarioResponseDto> create(@Valid @RequestBody UsuarioCreateDto createDto) {
        Usuario user = usuarioService.salvar(UsuarioMapper.toUsuario(createDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioMapper.toDto(user));
    }

    @Operation(summary = "Recuperar um usuario pelo id", description = "Recuperar um usuario pelo id",
            responses = {
                    @ApiResponse(responseCode = "200", description = "recurso recuperado com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "404", description = "recurso nao encontrado", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class))),
            }
    )
    @GetMapping({"/{id}"})
    public ResponseEntity<UsuarioResponseDto> getById(@PathVariable Long id) {
        Usuario user = usuarioService.buscarPorId(id);
        return ResponseEntity.ok(UsuarioMapper.toDto(user));
    }


    @Operation(summary = "Atualizar senha", description = "Atualizar senha",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Senha atualiza com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = void.class))),
                    @ApiResponse(responseCode = "400", description = "senha nao confere", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "404", description = "recurso nao encontrado", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class))),

            }
    )
    @PatchMapping({"/{id}"})
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @Valid @RequestBody UsuarioSenhaDto dto) {
        Usuario user = usuarioService.editarSenha(id, dto.getSenhaAtual(), dto.getNovaSenha(), dto.getConfirmaSenha());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDto>> getAll() {
        List<Usuario> users = usuarioService.buscarTodos();
        return ResponseEntity.ok(UsuarioMapper.toListDto(users));
    }
}
