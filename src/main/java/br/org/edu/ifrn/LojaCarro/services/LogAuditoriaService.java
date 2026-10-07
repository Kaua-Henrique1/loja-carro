package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.model.LogAuditoria;
import br.org.edu.ifrn.LojaCarro.repository.LogAuditoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LogAuditoriaService {

    private final LogAuditoriaRepository logAuditoriaRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrar(String acao, String entidade, Long entidadeId, String detalhes, String usuario, String status) {
        LogAuditoria log = new LogAuditoria();
        log.setAcao(acao);
        log.setEntidade(entidade);
        log.setEntidadeId(entidadeId);
        log.setDetalhes(detalhes);
        log.setUsuarioResponsavel(usuario != null ? usuario : "SISTEMA");
        log.setStatus(status);

        logAuditoriaRepository.save(log);
    }
}