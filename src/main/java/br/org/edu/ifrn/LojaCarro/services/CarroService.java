package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.CarroException;
import br.org.edu.ifrn.LojaCarro.config.Auditar;
import br.org.edu.ifrn.LojaCarro.model.Carro;
import br.org.edu.ifrn.LojaCarro.repository.CarroRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CarroService {

    private final CarroRepository carroRepository;

    @Auditar(acao = "CRIAR_CARRO", entidade = "Carro")
    @Transactional
    public Carro save(Carro c) {
        log.info("Iniciando cadastro do carro modelo: {}", c.getModelo());
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());

        Carro salvo = carroRepository.save(c);
        log.info("Carro cadastrado com sucesso. ID: {}", salvo.getId());
        return salvo;
    }

    @Auditar(acao = "ATUALIZAR_CARRO", entidade = "Carro")
    @Transactional
    public Carro update(Carro c) {
        log.info("Iniciando atualização do carro ID: {}", c.getId());
        if (c.getId() == null) {
            log.warn("Tentativa de atualização sem ID fornecido.");
            throw new CarroException("O ID do carro para atualização não pode ser nulo.");
        }
        if (!carroRepository.existsById(c.getId())) {
            log.warn("Carro com ID {} não encontrado para atualização.", c.getId());
            throw new CarroException("Carro com ID " + c.getId() + " não encontrado para atualização.");
        }
        validarModelo(c.getModelo());
        validarPreco(c.getPreco());

        Carro atualizado = carroRepository.save(c);
        log.info("Carro ID: {} atualizado com sucesso.", atualizado.getId());
        return atualizado;
    }

    @Auditar(acao = "EXCLUIR_CARRO_POR_ID", entidade = "Carro")
    @Transactional
    public void deleteById(Long id) {
        log.warn("Solicitada exclusão do carro por ID: {}", id);
        if (id <= 0) {
            log.warn("ID inválido fornecido para exclusão: {}", id);
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        carroRepository.deleteById(id);
        log.info("Carro ID: {} removido com sucesso.", id);
    }

    @Auditar(acao = "ATUALIZAR_PRECO_POR_MODELO", entidade = "Carro")
    @Transactional
    public Carro updateByModelo(String modelo, double preco) {
        log.info("Atualizando preço do modelo: {}", modelo);
        Carro carro = localizarCarroPorModelo(modelo);
        validarPreco(preco);
        carro.setPreco(preco);

        Carro atualizado = carroRepository.save(carro);
        log.info("Preço do modelo {} atualizado com sucesso.", modelo);
        return atualizado;
    }

    @Auditar(acao = "EXCLUIR_CARRO_POR_MODELO", entidade = "Carro")
    @Transactional
    public Carro deleteByModelo(String modelo) {
        log.warn("Solicitada exclusão do carro por modelo: {}", modelo);
        Carro carro = localizarCarroPorModelo(modelo);
        carroRepository.delete(carro);
        log.info("Carro modelo {} removido com sucesso.", modelo);
        return carro;
    }

    @Transactional
    public Carro saveFromLegacy(String modelo, double preco) {
        log.info("Cadastrando carro via sistema legado - Modelo: {}", modelo);
        Carro carro = new Carro(modelo, LocalDate.now().getYear(), preco);
        return save(carro);
    }

    // --- MÉTODOS DE LEITURA (SEM @Auditar) ---

    @Transactional(readOnly = true)
    public Optional<Carro> findById(Long id) {
        log.debug("Buscando carro por ID: {}", id);
        if (id <= 0) {
            log.warn("ID inválido fornecido para busca: {}", id);
            throw new CarroException("O ID do carro não pode ser negativo. ID fornecido: " + id);
        }
        return carroRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Carro> findAll() {
        log.debug("Buscando todos os carros");
        return carroRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Carro> findByModelo(String modelo) {
        log.debug("Buscando primeiro carro por modelo: {}", modelo);
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo);
    }

    // --- MÉTODOS AUXILIARES DE VALIDAÇÃO ---

    private void validarModelo(String modelo) {
        if (modelo == null || modelo.trim().isEmpty()) {
            log.warn("Validação falhou: Modelo vazio ou nulo.");
            throw new CarroException("O modelo do carro não pode estar vazio.");
        }
        if (modelo.length() >= 5) {
            log.warn("Validação falhou: Modelo excedeu o tamanho limite de 5 caracteres.");
            throw new CarroException("O modelo do carro deve ter menos de 5 caracteres. Tamanho atual: " + modelo.length());
        }
    }

    private void validarPreco(double preco) {
        if (preco < 0) {
            log.warn("Validação falhou: Preço negativo ({})", preco);
            throw new CarroException("O preço do carro não pode ser negativo. Valor fornecido: " + preco);
        }
    }

    private Carro localizarCarroPorModelo(String modelo) {
        validarModelo(modelo);
        return carroRepository.findFirstByModelo(modelo)
                .orElseThrow(() -> {
                    log.warn("Carro modelo {} não encontrado.", modelo);
                    return new CarroException("Carro com modelo " + modelo + " não encontrado.");
                });
    }
}