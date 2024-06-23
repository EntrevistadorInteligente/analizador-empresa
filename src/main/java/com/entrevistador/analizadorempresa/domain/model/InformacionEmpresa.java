package com.entrevistador.analizadorempresa.domain.model;

import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class InformacionEmpresa {

    private String empresa;
    private String perfil;
    private String seniority;
    private String pais;
    private String descripcionVacante;
    private List<EntrevistaEntity> informacionEmpresaVect;

}
