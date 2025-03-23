package br.com.curso.spring_keycloak.models;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "user", schema = "app_schema")
public class UserApp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_id", nullable = false)
    private String keycloakId;

    @Column(name = "create_date", nullable = false)
    private LocalDateTime createDate;

}