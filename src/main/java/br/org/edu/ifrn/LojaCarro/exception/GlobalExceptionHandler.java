package br.org.edu.ifrn.LojaCarro.exception;

import br.org.edu.ifrn.LojaCarro.CarroException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Trata regras de negócio específicas de Carro
    @ExceptionHandler(CarroException.class)
    public ResponseEntity<Map<String, Object>> handleCarroException(CarroException ex) {
        log.warn("[REGRA CARRO] Exceção capturada: {}", ex.getMessage());
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Trata buscas por ID não encontrados (Cliente, Funcionario, etc.)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(EntityNotFoundException ex) {
        log.warn("[NÃO ENCONTRADO] Recurso não localizado: {}", ex.getMessage());
        return construirResposta(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    // Trata validações de unicidade (CPF, Email, CNH já cadastrados)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleBadRequest(IllegalArgumentException ex) {
        log.warn("[REQUISIÇÃO INVÁLIDA] {}", ex.getMessage());
        return construirResposta(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // Trata qualquer erro inesperado do servidor (Evita vazar stacktrace para o cliente)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        log.error("[ERRO INTERNO] Exceção não tratada: {}", ex.getMessage(), ex);
        return construirResposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro interno no servidor.");
    }

    // Método auxiliar para montar o JSON de resposta padronizado
    private ResponseEntity<Map<String, Object>> construirResposta(HttpStatus status, String mensagem) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", mensagem);
        return ResponseEntity.status(status).body(body);
    }
}