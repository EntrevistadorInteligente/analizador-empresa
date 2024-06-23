package com.entrevistador.analizadorempresa.infrastructure.adapter.repository;

import com.entrevistador.analizadorempresa.infrastructure.adapter.entity.EntrevistaEntity;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface InterviewRepository extends ReactiveElasticsearchRepository<EntrevistaEntity, String> {

    @Query("""
    {
        "bool": {
            "must": [
                { "term": { "company_name": "?0" } }
            ]
        }
    }
    """)
    Flux<EntrevistaEntity> obtenerEntrevistas(String companyName, String multiMatchQueries);

}
