package com.entrevistador.analizadorempresa.domain.service;

import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.domain.model.dto.MensajeAnalizadorEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.PosicionEntrevistaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.ProcesoEntrevistaDto;
import com.entrevistador.analizadorempresa.domain.model.enums.EstadoEntrevistaEnum;
import com.entrevistador.analizadorempresa.domain.port.EntrevistaElasticsearch;
import com.entrevistador.analizadorempresa.domain.port.InformacionEmpresaDao;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class CrearInvestigarEmpresaService {
    private static final String ANALIZADOR_EMPRESA = "ANALIZADOR_EMPRESA";

    private final InformacionEmpresaDao informacionEmpresaDao;
    private final EntrevistaElasticsearch entrevistaElasticsearch;

    public Mono<MensajeAnalizadorEmpresaDto> create(PosicionEntrevistaDto posicionEntrevista) {
        return this.entrevistaElasticsearch.obtenerEntrevistasPorRepo(posicionEntrevista.getFormulario())
                .collectList()
                .flatMapMany(entrevistas -> {
                    if (entrevistas.isEmpty()) {
                        return Flux.empty();
                    }
                    return filtrarEntrevistasPorThreshold(entrevistas);
                })
                .collectList()
                .flatMap(entrevistasFiltradas ->
                        this.informacionEmpresaDao.create(posicionEntrevista.getFormulario(), entrevistasFiltradas)
                                .zipWith(Mono.just(posicionEntrevista), this::crearMensajeAnalizadorEmpresa)
                );
    }

    private Flux<InterviewDto> filtrarEntrevistasPorThreshold(List<InterviewDto> entrevistas) {
        double highestScore = entrevistas.stream().mapToDouble(InterviewDto::getPuntuacion).max().orElse(0);
        double threshold = highestScore * 0.5;

        return Flux.fromIterable(entrevistas)
                .filter(entrevista -> entrevista.getPuntuacion() >= threshold);
    }

    private MensajeAnalizadorEmpresaDto crearMensajeAnalizadorEmpresa(InformacionEmpresaDto infoEmpresaDto,
                                                                      PosicionEntrevistaDto posicionEntrevistaDto) {
        ProcesoEntrevistaDto procesoEntrevista = this.crearProcesoEntrevistaDto(posicionEntrevistaDto);
        return MensajeAnalizadorEmpresaDto.builder()
                .procesoEntrevista(procesoEntrevista)
                .idEntrevista(posicionEntrevistaDto.getIdEntrevista())
                .idInformacionEmpresaRag(infoEmpresaDto.getIdInformacionEmpresaRag())
                .build();
    }

    private ProcesoEntrevistaDto crearProcesoEntrevistaDto(PosicionEntrevistaDto posicionEntrevistaDto) {
        return ProcesoEntrevistaDto.builder()
                .uuid(posicionEntrevistaDto.getEventoEntrevistaId())
                .estado(EstadoEntrevistaEnum.FN)
                .fuente(ANALIZADOR_EMPRESA)
                .build();
    }
}
