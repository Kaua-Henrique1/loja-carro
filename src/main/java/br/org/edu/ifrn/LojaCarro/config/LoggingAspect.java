package br.org.edu.ifrn.LojaCarro.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.stream.Collectors;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    // Captura QUALQUER método em subpacotes de controller e service
    @Pointcut("execution(* br.org.edu.ifrn.LojaCarro.controllers..*.*(..)) || execution(* br.org.edu.ifrn.LojaCarro.services..*.*(..))")
    public void applicationPackagePointcut() {}

    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        // Formata os argumentos mascarando campos sensíveis como 'senha'
        String argsSanitizados = Arrays.stream(joinPoint.getArgs())
                .map(this::mascararDadosSensiveis)
                .collect(Collectors.joining(", "));

        log.info("--> [ENTRADA] {}.{}() | Args: [{}]", className, methodName, argsSanitizados);

        long startTime = System.currentTimeMillis();
        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("<-- [SAÍDA] {}.{}() | Tempo: {} ms", className, methodName, executionTime);
            return result;
        } catch (Exception e) {
            log.error("X-- [EXCEÇÃO] {}.{}() | Mensagem: {}", className, methodName, e.getMessage());
            throw e;
        }
    }

    private String mascararDadosSensiveis(Object arg) {
        if (arg == null) return "null";
        String argString = arg.toString();
        // Substitui senhas no log por [PROTEGIDO]
        return argString.replaceAll("(?i)senha=([^,\\)]+)", "senha=[PROTEGIDO]");
    }
}