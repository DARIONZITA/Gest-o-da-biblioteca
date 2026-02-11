package com.example.bibliotecaapi.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter // Apenas Getters
@Setter // Apenas Setters
@ToString // Cuidado aqui: se houver listas, deves usar @ToString.Exclude
@RequiredArgsConstructor
@NoArgsConstructor // Padrão é public
@Builder // <--- ISTO cria o metodo .builder()
@AllArgsConstructor // <--- OBRIGATÓRIO para o Builder funcionar
@Entity
@Table(name = "tb_autor")
@EntityListeners(AuditingEntityListener.class)
public class Autor {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NonNull
    @NotBlank(message = "O nome é obrigatorio")
    @Column(length = 150, nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @Column(length = 100)
    @Builder.Default
    private String nacionalidade = "Desconhecida";

    @CreationTimestamp
    @Column(name = "data_cadastro", updatable = false)
    private LocalDateTime dataCadastro;

    // Implementação manual e segura do equals e hashCode para JPA
    @Override
    public final boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null)
            return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass()
                : this.getClass();
        if (thisEffectiveClass != oEffectiveClass)
            return false;
        Autor autor = (Autor) o;
        return getId() != null && Objects.equals(getId(), autor.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy
                ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode()
                : getClass().hashCode();
    }
}