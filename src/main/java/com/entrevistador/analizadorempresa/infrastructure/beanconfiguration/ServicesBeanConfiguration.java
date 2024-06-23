package com.entrevistador.analizadorempresa.infrastructure.beanconfiguration;

import com.entrevistador.analizadorempresa.domain.port.EntrevistaElasticsearch;
import com.entrevistador.analizadorempresa.domain.service.CrearInvestigarEmpresaService;
import com.entrevistador.analizadorempresa.infrastructure.adapter.dao.InformacionEmpresaBdDao;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServicesBeanConfiguration {
    @Bean
    public CrearInvestigarEmpresaService feedbackConstruccionService(InformacionEmpresaBdDao informacionEmpresaBdDao,
                                                                     EntrevistaElasticsearch entrevistaElasticsearch) {
        return new CrearInvestigarEmpresaService(informacionEmpresaBdDao, entrevistaElasticsearch);
    }
}
