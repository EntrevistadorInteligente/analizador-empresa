package com.entrevistador.analizadorempresa.infrastructure.adapter.dao;

import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.domain.model.dto.QuestionDto;
import com.entrevistador.analizadorempresa.domain.port.InformacionEmpresaDao;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.InformacionEmpresaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.PreguntaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.repository.AnalizadorEmpresaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InformacionEmpresaBdDao implements InformacionEmpresaDao {
    private final AnalizadorEmpresaRepository analizadorEmpresaRepository;

    @Override
    public Mono<InformacionEmpresaDto> create(InformacionEmpresaDto informacionEmpresaDto,
                                              List<InterviewDto> entrevistasDto) {
        return Mono.just(informacionEmpresaDto)
             .map(informacionEmpresa -> InformacionEmpresaEntity.builder()
                        .empresa(informacionEmpresa.getEmpresa())
                        .perfil(informacionEmpresa.getPerfil())
                        .seniority(informacionEmpresa.getSeniority())
                        .pais(informacionEmpresa.getPais())
                        .descripcionVacante(informacionEmpresa.getDescripcionVacante())
                        .informacionEmpresaVect(entrevistasDto.stream()
                                .map(this::convertToEntrevistaEntity)
                                .collect(Collectors.toList()))
                        .build())
                .flatMap(this.analizadorEmpresaRepository::save)
                .map(informacionEmpresaEntity -> InformacionEmpresaDto.builder()
                        .idInformacionEmpresaRag(informacionEmpresaEntity.getUuid())
                        .empresa(informacionEmpresaDto.getEmpresa())
                        .perfil(informacionEmpresaDto.getPerfil())
                        .seniority(informacionEmpresaDto.getSeniority())
                        .pais(informacionEmpresaDto.getPais())
                        .build());
    }

    private EntrevistaEntity convertToEntrevistaEntity(InterviewDto dto) {
        return EntrevistaEntity.builder()
                .nombreEmpresa(dto.getNombreEmpresa())
                .titulo(dto.getTituloOferta())
                .detalleAdicional(dto.getDetalleAdicional())
                .preguntas(dto.getPreguntas().stream()
                        .map(this::convertToPreguntaEntity)
                        .collect(Collectors.toList()))
                .detallesExperiencia(dto.getDetallesExperiencia())
                .build();
    }

    private PreguntaEntity convertToPreguntaEntity(QuestionDto dto) {
        return PreguntaEntity.builder()
                .titulo(dto.getTitulo())
                .descripcion(dto.getDescripcion())
                .build();
    }
}
