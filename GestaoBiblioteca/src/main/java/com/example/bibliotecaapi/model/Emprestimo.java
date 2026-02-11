package com.example.bibliotecaapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@ToString

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Entity
@Table(name = "tb_emprestimo")
@EntityListeners(AuditingEntityListener.class)
public class Emprestimo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    @NotNull(message = "O usuário é obrigatório")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "livro_id", nullable = false)
    @NotNull(message = "O livro é obrigatório")
    private Livro livro;


    @Column(nullable = false)
    @Builder.Default
    private Integer qtdRenovacoes = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private StatusEmprestimo status = StatusEmprestimo.ATIVO;

    @Column(nullable = false)
    @Builder.Default
    private Double valorMulta = 0.0;


    @CreatedDate
    @Column(name = "data_emprestimo", updatable = false)
    private LocalDate dataEmprestimo;


    @Column(nullable = false)
    private LocalDate dataPrevista;   // Data máxima para entrega (ex: +7 dias)

    private LocalDate dataDevolucaoReal;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Emprestimo that = (Emprestimo) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}