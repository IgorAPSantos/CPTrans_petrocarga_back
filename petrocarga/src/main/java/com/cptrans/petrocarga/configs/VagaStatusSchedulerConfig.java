package com.cptrans.petrocarga.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cptrans.petrocarga.services.VagaService;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.vagas.status.enabled", havingValue = "true", matchIfMissing = false)
public class VagaStatusSchedulerConfig {

    private static final Logger logger = LoggerFactory.getLogger(VagaStatusSchedulerConfig.class);

    @Autowired
    private VagaService vagaService;

    @Value("${app.vagas.status.scan-ms:60000}")
    private long scanMs;

    @Scheduled(fixedDelayString = "${app.vagas.status.scan-ms:60000}")
    public void processarStatusVagas() {
        try {
            vagaService.sincronizarStatusVagas();
        } catch (Exception e) {
            logger.error("Erro no job de sincronização de status de vagas: {}", e.getMessage(), e);
        }
    }
}
