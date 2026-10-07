package br.org.edu.ifrn.LojaCarro.services;

import br.org.edu.ifrn.LojaCarro.config.Auditar;
import br.org.edu.ifrn.LojaCarro.model.Cliente;
import br.org.edu.ifrn.LojaCarro.repository.ClienteRepository;
import br.org.edu.ifrn.LojaCarro.repository.UsuarioBaseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final UsuarioBaseRepository usuarioBaseRepository;

    @Auditar(acao = "CRIAR_CLIENTE", entidade = "Cliente")
    @Transactional
    public Cliente salvar(Cliente cliente) {
        validarUnicidadeCampos(cliente);
        cliente.setAtivo(true);
        return clienteRepository.save(cliente);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarAtivos() {
        return clienteRepository.findByAtivoTrue();
    }

     @Transactional(readOnly = true)
    public Cliente buscarPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + id));
    }

    @Auditar(acao = "ATUALIZAR_CLIENTE", entidade = "Cliente")
    @Transactional
    public Cliente atualizar(Long id, Cliente clienteAtualizado) {
        Cliente clienteExistente = buscarPorId(id);

        // Valida CPF e Email apenas se foram alterados
        if (!clienteExistente.getCpf().equals(clienteAtualizado.getCpf())
                && usuarioBaseRepository.existsByCpf(clienteAtualizado.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }

        if (!clienteExistente.getEmail().equalsIgnoreCase(clienteAtualizado.getEmail())
                && usuarioBaseRepository.existsByEmail(clienteAtualizado.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }

        // Atualização dos campos herdados de Usuario
        clienteExistente.setNome(clienteAtualizado.getNome());
        clienteExistente.setCpf(clienteAtualizado.getCpf());
        clienteExistente.setEmail(clienteAtualizado.getEmail());
        clienteExistente.setTelefone(clienteAtualizado.getTelefone());
        clienteExistente.setDataNascimento(clienteAtualizado.getDataNascimento());

        // Atualização dos campos específicos de Cliente
        clienteExistente.setCnh(clienteAtualizado.getCnh());
        clienteExistente.setValidadeCnh(clienteAtualizado.getValidadeCnh());
        clienteExistente.setRendaMensal(clienteAtualizado.getRendaMensal());
        clienteExistente.setEndereco(clienteAtualizado.getEndereco());

        return clienteRepository.save(clienteExistente);
    }

    @Auditar(acao = "INATIVAR_CLIENTE", entidade = "Cliente")
    @Transactional
    public void desativar(Long id) {
        Cliente cliente = buscarPorId(id);
        cliente.setAtivo(false);
        clienteRepository.save(cliente);
    }

    @Auditar(acao = "EXCLUIR_CLIENTE", entidade = "Cliente")
    @Transactional
    public void deletarDefinitivo(Long id) {
        Cliente cliente = buscarPorId(id);
        clienteRepository.delete(cliente);
    }

    private void validarUnicidadeCampos(Cliente cliente) {
        if (usuarioBaseRepository.existsByCpf(cliente.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado no sistema.");
        }
        if (usuarioBaseRepository.existsByEmail(cliente.getEmail())) {
            throw new IllegalArgumentException("E-mail já cadastrado no sistema.");
        }
        if (cliente.getCnh() != null && clienteRepository.existsByCnh(cliente.getCnh())) {
            throw new IllegalArgumentException("CNH já cadastrada no sistema.");
        }
    }
}