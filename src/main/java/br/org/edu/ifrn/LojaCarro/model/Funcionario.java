package br.org.edu.ifrn.LojaCarro.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_funcionario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario extends Usuario {

    @Column(nullable = false, unique = true, length = 20)
    private String matricula;

    @Column(name = "salario_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal salarioBase;

    @Column(name = "percentual_comissao", precision = 5, scale = 2)
    private BigDecimal percentualComissao;

    @Column(name = "data_admissao", nullable = false)
    private LocalDate dataAdmissao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Cargo cargo;
}