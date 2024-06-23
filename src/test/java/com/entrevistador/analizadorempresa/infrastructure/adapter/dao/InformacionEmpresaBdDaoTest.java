package com.entrevistador.analizadorempresa.infrastructure.adapter.dao;

import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.domain.model.dto.QuestionDto;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.InformacionEmpresaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.repository.AnalizadorEmpresaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InformacionEmpresaBdDaoTest {
    @InjectMocks
    private InformacionEmpresaBdDao informacionEmpresaBdDao;
    @Mock
    private AnalizadorEmpresaRepository analizadorEmpresaRepository;

    @Test
    void testCreate() {
        InformacionEmpresaEntity informacionEmpresaEntity = InformacionEmpresaEntity.builder().empresa("any").build();

        InformacionEmpresaDto informacionEmpresaDto = InformacionEmpresaDto.builder()
                .empresa("any")
                .build();
        QuestionDto questionDto = QuestionDto.builder().build();
        List<InterviewDto> entrevistasDto = List.of(InterviewDto.builder().preguntas(List.of(questionDto)).build());

        when(this.analizadorEmpresaRepository.save(any())).thenReturn(Mono.just(informacionEmpresaEntity));

        Mono<InformacionEmpresaDto> publisher = this.informacionEmpresaBdDao.create(informacionEmpresaDto, entrevistasDto);

        StepVerifier
                .create(publisher)
                .assertNext(informacion -> assertEquals(informacionEmpresaDto.getEmpresa(), informacion.getEmpresa()))
                .verifyComplete();

        verify(this.analizadorEmpresaRepository, times(1)).save(any());
    }
}