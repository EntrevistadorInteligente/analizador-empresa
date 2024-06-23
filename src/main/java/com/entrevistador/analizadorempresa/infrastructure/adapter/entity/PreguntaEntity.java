package com.entrevistador.analizadorempresa.infrastructure.adapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PreguntaEntity {

    @Field(name = "question_text", type = FieldType.Text)
    private String descripcion;

    @Field(name = "question_title", type = FieldType.Text)
    private String titulo;

}

