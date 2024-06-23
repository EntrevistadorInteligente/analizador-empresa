package com.entrevistador.analizadorempresa.application.service;

import com.entrevistador.analizadorempresa.application.usecases.InvestigarEmpresa;
import com.entrevistador.analizadorempresa.domain.model.dto.PosicionEntrevistaDto;
import com.entrevistador.analizadorempresa.domain.service.CrearInvestigarEmpresaService;
import com.entrevistador.analizadorempresa.infrastructure.adapter.jms.JmsPublisherAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class InvestigarEmpresaService implements InvestigarEmpresa {

    private final CrearInvestigarEmpresaService crearInvestigarEmpresaService;
    private final JmsPublisherAdapter jmsPublisherAdapter;

    @Override
    public Mono<Void> ejecutar(PosicionEntrevistaDto posicionEntrevistaDto) {
        return this.crearInvestigarEmpresaService.create(posicionEntrevistaDto)
                .flatMap(this.jmsPublisherAdapter::empresaListenerTopic);
    }

}