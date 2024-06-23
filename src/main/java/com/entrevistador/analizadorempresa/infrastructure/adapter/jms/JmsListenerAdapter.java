package com.entrevistador.analizadorempresa.infrastructure.adapter.jms;

import com.entrevistador.analizadorempresa.application.service.InvestigarEmpresaService;
import com.entrevistador.analizadorempresa.domain.model.dto.PosicionEntrevistaDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JmsListenerAdapter {
    private final InvestigarEmpresaService investigarEmpresaService;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "empresaPublisherTopic", groupId = "my-group")
    public void empresaPublisherTopic(String jsonData) {
        try {
            PosicionEntrevistaDto posicionEntrevista = objectMapper.readValue(jsonData, PosicionEntrevistaDto.class);
            Mono.just(posicionEntrevista)
                    .flatMap(this.investigarEmpresaService::ejecutar)
                    .doOnSubscribe(subscription -> System.out.println("Starting processing: " + posicionEntrevista))
                    .doOnSuccess(success -> System.out.println("Processed successfully: " + posicionEntrevista))
                    .doOnError(error -> System.err.println("Error processing: " + error.getMessage()))
                    .subscribe();
        } catch (IOException e) {
            throw new RuntimeException("Error al deserializar el mensaje JSON", e);
        }
    }
}
