package com.entrevistador.analizadorempresa.infrastructure.adapter.dao;

import com.entrevistador.analizadorempresa.domain.model.dto.InformacionEmpresaDto;
import com.entrevistador.analizadorempresa.domain.model.dto.InterviewDto;
import com.entrevistador.analizadorempresa.domain.model.dto.QuestionDto;
import com.entrevistador.analizadorempresa.domain.port.EntrevistaElasticsearch;
import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import com.entrevistador.analizadorempresa.infrastructure.adapter.repository.InterviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.StringQuery;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EntrevistaElasticsearchDao implements EntrevistaElasticsearch {

    private final InterviewRepository entrevistaPrueba;
    private final ReactiveElasticsearchOperations operations;

    private static StringQuery getStringQuery(InformacionEmpresaDto analizadorEmpresaDto, Pageable pageable) {
        String queryString = """
            {
                "bool": {
                    "must": [
                        {
                            "bool": {
                                "should": [
                                    {
                                        "match": {
                                            "company_name": {
                                                "query": "%s",
                                                "fuzziness": "AUTO",
                                                "operator": "and"
                                            }
                                        }
                                    },
                                    {
                                        "wildcard": {
                                            "company_name": {
                                                "value": "*%s*",
                                                "case_insensitive": true
                                            }
                                        }
                                    }
                                ],
                                "minimum_should_match": 1
                            }
                        },
                        {
                            "bool": {
                                "should": [
                                    {
                                        "multi_match": {
                                            "query": "%s",
                                            "fields": [
                                                "title^3",
                                                "interview_text^2"
                                            ]
                                        }
                                    }
                                ],
                                "minimum_should_match": 1
                            }
                        }
                    ]
                }
            }
            """.formatted(analizadorEmpresaDto.getEmpresa(), analizadorEmpresaDto.getEmpresa().toLowerCase(),
                analizadorEmpresaDto.getPerfil() + " " + analizadorEmpresaDto.getSeniority());

        StringQuery stringQuery = new StringQuery(queryString, pageable);
        return stringQuery;
    }


    @Override
    public Flux<EntrevistaEntity> obtenerEntrevistas(InformacionEmpresaDto analizadorEmpresaDto) {
        List<Map<String, String>> queries = List.of(
                Map.of("query", analizadorEmpresaDto.getPerfil())
        );
        String multiMatchQueries = queries.stream()
                .map(query -> String.format("""
                {
                    "multi_match": {
                        "query": "%s",
                        "fields": [
                            "title^3",
                            "interview_text^2"
                        ]
                    }
                }
                """, query.get("query")))
                .collect(Collectors.joining(","));

        return entrevistaPrueba.obtenerEntrevistas(analizadorEmpresaDto.getEmpresa(), multiMatchQueries);
    }

    @Override
    public Flux<InterviewDto> obtenerEntrevistasPorRepo(InformacionEmpresaDto analizadorEmpresaDto) {
        Pageable pageable = PageRequest.of(0, 10);

        StringQuery stringQuery = getStringQuery(analizadorEmpresaDto, pageable);

        Flux<SearchHit<EntrevistaEntity>> searchHits = operations.search(stringQuery, EntrevistaEntity.class);

        return searchHits
                .map(this::mapToInterviewDto);
    }

    private InterviewDto mapToInterviewDto(SearchHit<EntrevistaEntity> hit) {
        EntrevistaEntity entity = hit.getContent();
        List<QuestionDto> preguntas = new ArrayList<>();
        if(entity != null && entity.getPreguntas() != null){
            preguntas = entity.getPreguntas().stream()
                    .map(pregunta -> QuestionDto.builder()
                            .titulo(pregunta.getTitulo())
                            .descripcion(pregunta.getDescripcion())
                            .build())
                    .toList();
        }
        return InterviewDto.builder()
                .nombreEmpresa(entity.getNombreEmpresa())
                .tituloOferta(entity.getTitulo())
                .detalleAdicional(entity.getDetalleAdicional())
                .puntuacion(hit.getScore())
                .preguntas(preguntas)
                .detallesExperiencia(entity.getDetallesExperiencia())
                .build();
    }
}
