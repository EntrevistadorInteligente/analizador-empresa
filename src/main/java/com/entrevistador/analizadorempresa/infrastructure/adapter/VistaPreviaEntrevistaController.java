package com.entrevistador.analizadorempresa.infrastructure.adapter;


import com.entrevistador.analizadorempresa.application.usecases.InvestigarEmpresa;
import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.domain.model.dto.PosicionEntrevistaDto;
import com.entrevistador.analizadorempresa.domain.port.EntrevistaElasticsearch;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.http.HttpResponse;

@RestController
@RequestMapping("/v1/empresa")
@RequiredArgsConstructor
public class VistaPreviaEntrevistaController {

    private final EntrevistaElasticsearch entrevistaPrueba;

    private final InterviewRepository entrevistaPrueba2;
    private final InvestigarEmpresa investigarEmpresa;

    @PostMapping()
    public Flux<InterviewDto> mostrarListaPerfiles(@RequestBody InformacionEmpresaDto analizadorEmpresaDto){
        var asd = entrevistaPrueba.obtenerEntrevistasPorRepo(analizadorEmpresaDto );

        return asd;
    }

    @PostMapping("/2")
    public Mono<EntrevistaEntity> mostrarListaPerfiles2(@RequestBody InformacionEmpresaDto analizadorEmpresaDto){
        var asd = entrevistaPrueba2.findById("49Tl8I8Bkeccm24tEmpw" );

        return asd;
    }

    @PostMapping("/3")
    public Mono<Integer> mostrarListaPerfiles24(@RequestBody InformacionEmpresaDto analizadorEmpresaDto){
        return investigarEmpresa.ejecutar(PosicionEntrevistaDto.builder()
                        .idEntrevista("123")
                        .eventoEntrevistaId("123")
                        .formulario(analizadorEmpresaDto)
                        .build())
                .then(Mono.just(HttpStatus.SC_ACCEPTED));
    }
}

