package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cliente extends Usuario {

    @Column(unique = true, length = 11)
    private String cnh;

    @Column(name = "validade_cnh")
    private LocalDate validadeCnh;

    @Column(name = "renda_mensal", precision = 10, scale = 2)
    private BigDecimal rendaMensal;

    private String endereco;
}