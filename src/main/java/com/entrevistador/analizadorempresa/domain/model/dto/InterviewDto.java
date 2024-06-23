package com.entrevistador.analizadorempresa.domain.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class InterviewDto {

    private String nombreEmpresa;
    private String tituloOferta;
    private String detalleAdicional;
    private double puntuacion;
    private List<QuestionDto> preguntas;
    private List<String> detallesExperiencia;

}

