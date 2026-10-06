package br.org.edu.ifrn.LojaCarro.config;

import br.org.edu.ifrn.LojaCarro.model.Usuario;
import br.org.edu.ifrn.LojaCarro.services.LogAuditoriaService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditoriaAspect {

    private final LogAuditoriaService logAuditoriaService;

    @Around("@annotation(auditar)")
    public Object auditarOperacao(ProceedingJoinPoint joinPoint, Auditar auditar) throws Throwable {
        Object resultado = null;
        Long idAfetado = null;
        String status = "SUCESSO";
        String detalhes = "Operação realizada com sucesso.";

        try {
            resultado = joinPoint.proceed();

            // Tenta extrair o ID da entidade retornada (se for um objeto que herda de Usuario)
            if (resultado instanceof Usuario usuario) {
                idAfetado = usuario.getId();
            }

            return resultado;
        } catch (Exception e) {
            status = "FALHA";
            detalhes = "Erro: " + e.getMessage();
            throw e;
        } finally {
            logAuditoriaService.registrar(
                    auditar.acao(),
                    auditar.entidade(),
                    idAfetado,
                    detalhes,
                    "SISTEMA", // Substituir pelo usuário do Spring Security quando implementar login
                    status
            );
        }
    }
}