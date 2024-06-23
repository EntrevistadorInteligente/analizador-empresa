package com.entrevistador.analizadorempresa.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class QuestionDto {

    private String titulo;
    private String descripcion;

}

