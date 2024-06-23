package com.entrevistador.analizadorempresa.domain.port;

import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import reactor.core.publisher.Flux;

public interface EntrevistaElasticsearch {

    Flux<EntrevistaEntity> obtenerEntrevistas(InformacionEmpresaDto analizadorEmpresaDto);
    Flux<InterviewDto> obtenerEntrevistasPorRepo(InformacionEmpresaDto analizadorEmpresaDto);
}
