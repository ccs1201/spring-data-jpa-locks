package br.com.ccs.springdatajpalocks.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
public class ProdutoLockOtimista {

    @Id
    private UUID id;
    private String nome;
    private BigDecimal valorVenda;
    private BigDecimal valorCompra;
    @CreationTimestamp
    private LocalDateTime dataHoraCriacao;
    @UpdateTimestamp
    private LocalDateTime dataHoraAlteracao;
    @Version
    private Long versao;
}
