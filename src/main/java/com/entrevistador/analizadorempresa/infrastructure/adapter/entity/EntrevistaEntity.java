package com.entrevistador.analizadorempresa.infrastructure.adapter.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "interviews")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EntrevistaEntity {

    @Id
    private String id;
    @Field(name = "company_name")
    private String nombreEmpresa;
    @Field(name = "title")
    private String titulo;
    @Field(name = "application_details")
    private String detalleAdicional;
    @Field(name = "interview_text")
    private String descripcionEntrevista;
    @Field(name = "questions", type = FieldType.Nested)
    private List<PreguntaEntity> preguntas;
    @Field(name = "experience_details", type = FieldType.Nested)
    private List<String> detallesExperiencia;

}

