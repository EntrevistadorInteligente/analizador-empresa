package com.entrevistador.analizadorempresa.infrastructure.adapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@Builder
@Document(collection = "informacion_empresa")
@AllArgsConstructor
public class InformacionEmpresaEntity {
    @Id
    private String uuid;
    private String empresa;
    private String perfil;
    private String seniority;
    private String pais;
    private String descripcionVacante;
    private List<EntrevistaEntity> informacionEmpresaVect;
}
