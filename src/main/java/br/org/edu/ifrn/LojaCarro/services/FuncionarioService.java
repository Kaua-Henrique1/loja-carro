package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.config.Auditar;
import br.org.edu.ifrn.LojaCarro.model.Funcionario;
import lombok.extern.slf4j.Slf4j;
import br.org.edu.ifrn.LojaCarro.repository.FuncionarioRepository;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioBaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final UsuarioBaseRepository usuarioBaseRepository;

    @Auditar(acao = "CRIAR_FUNCIONARIO", entidade = "Funcionario")
    @Transactional
    public Funcionario salvar(Funcionario funcionario) {
        validarUnicidadeCampos(funcionario);
        funcionario.setAtivo(true);
        return funcionarioRepository.save(funcionario);
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarTodos() {
        return funcionarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Funcionario> listarAtivos() {
        return funcionarioRepository.findByAtivoTrue();
    }

    @Transactional(readOnly = true)
    public Funcionario buscarPorId(Long id) {
        return funcionarioRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Funcionário não encontrado com o ID: " + id));
    }

    @Auditar(acao = "ATUALIZAR_FUNCIONARIO", entidade = "Funcionario")
    @Transactional
    public Funcionario atualizar(Long id, Funcionario funcionarioAtualizado) {
        Funcionario funcionarioExistente = buscarPorId(id);

        if (!funcionarioExistente.getCpf().equals(funcionarioAtualizado.getCpf())
                && usuarioBaseRepository.existsByCpf(funcionarioAtualizado.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }

        if (!funcionarioExistente.getEmail().equalsIgnoreCase(funcionarioAtualizado.getEmail())
                && usuarioBaseRepository.existsByEmail(funcionarioAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        if (!funcionarioExistente.getMatricula().equalsIgnoreCase(funcionarioAtualizado.getMatricula())
                && funcionarioRepository.existsByMatricula(funcionarioAtualizado.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada no sistema.");
        }

        // Atualização dos campos herdados de Usuario
        funcionarioExistente.setNome(funcionarioAtualizado.getNome());
        funcionarioExistente.setCpf(funcionarioAtualizado.getCpf());
        funcionarioExistente.setEmail(funcionarioAtualizado.getEmail());
        funcionarioExistente.setTelefone(funcionarioAtualizado.getTelefone());
        funcionarioExistente.setDataNascimento(funcionarioAtualizado.getDataNascimento());

        // Atualização dos campos específicos de Funcionario
        funcionarioExistente.setMatricula(funcionarioAtualizado.getMatricula());
        funcionarioExistente.setSalarioBase(funcionarioAtualizado.getSalarioBase());
        funcionarioExistente.setPercentualComissao(funcionarioAtualizado.getPercentualComissao());
        funcionarioExistente.setDataAdmissao(funcionarioAtualizado.getDataAdmissao());
        funcionarioExistente.setCargo(funcionarioAtualizado.getCargo());

        return funcionarioRepository.save(funcionarioExistente);
    }

    @Auditar(acao = "INATIVAR_FUNCIONARIO", entidade = "Funcionario")
    @Transactional
    public void desativar(Long id) {
        Funcionario funcionario = buscarPorId(id);
        funcionario.setAtivo(false);
        funcionarioRepository.save(funcionario);
    }

    @Auditar(acao = "EXCLUIR_FUNCIONARIO", entidade = "Funcionario")
    @Transactional
    public void deletarDefinitivo(Long id) {
        Funcionario funcionario = buscarPorId(id);
        funcionarioRepository.delete(funcionario);
    }

    private void validarUnicidadeCampos(Funcionario funcionario) {
        if (usuarioBaseRepository.existsByCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }
        if (usuarioBaseRepository.existsByEmail(funcionario.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }
        if (funcionarioRepository.existsByMatricula(funcionario.getMatricula())) {
            throw new IllegalArgumentException("Matrícula já cadastrada no sistema.");
        }
    }
}