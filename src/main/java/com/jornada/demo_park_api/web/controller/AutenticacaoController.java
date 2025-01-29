package com.jornada.demo_park_api.web.controller;


import com.jornada.demo_park_api.jwt.JwtToken;
import com.jornada.demo_park_api.jwt.JwtUserDetailsService;
import com.jornada.demo_park_api.web.dto.UsuarioLoginDto;
import com.jornada.demo_park_api.web.dto.UsuarioResponseDto;
import com.jornada.demo_park_api.web.exception.ErrorMessage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(name = "Autenticacao", description = "recurso para proceder com a autenticacao da api ")
@RequestMapping("/api/v1")
@RestController
@Slf4j
@RequiredArgsConstructor
public class AutenticacaoController {

    private final JwtUserDetailsService jwtUserDetailsService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "autenticar na api", description = "recurso de autenticacao na api",
            responses = {
                    @ApiResponse(responseCode = "200", description = "autenticacao realizada com sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "credenciais invalidas", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class))),
                    @ApiResponse(responseCode = "409", description = "campos invalidos", content = @Content(mediaType = "application/json", schema = @Schema (implementation = ErrorMessage.class)))
            }
    )

    @PostMapping("/auth")
    public ResponseEntity<?> autenticar(@RequestBody @Valid UsuarioLoginDto dto, HttpServletRequest request) {
        log.info("Processo de autenticacao pelo login {", dto.getUsername());
        try{
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword());
            authenticationManager.authenticate(authenticationToken);
            JwtToken token = jwtUserDetailsService.getTokenAuthenticated(dto.getUsername());

            return ResponseEntity.ok(token);
        }catch (AuthenticationException ex){
            log.warn("Erro ao autenticar usuario {}", dto.getUsername(), ex);
        }
        return ResponseEntity.badRequest().body(new ErrorMessage(request, HttpStatus.BAD_REQUEST, "Credenciais invalidas"));
    }
}
