package com.cptrans.petrocarga.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cptrans.petrocarga.services.ReservaService;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.reservas.autoconclusao.enabled", havingValue = "true", matchIfMissing = false)
public class AutoConclusaoSchedulerConfig {

    private static final Logger logger = LoggerFactory.getLogger(AutoConclusaoSchedulerConfig.class);

    @Autowired
    private ReservaService reservaService;

    /**
     * Roda periodicamente para processar auto-conclusão de reservas ATIVAS cujo fim já passou.
     * Frequência controlada por: app.reservas.autoconclusao.scan-ms (padrão: 600000ms = 10 minutos)
     * Ativação controlada por: app.reservas.autoconclusao.enabled (padrão: false)
     */
    @Scheduled(fixedDelayString = "${app.reservas.autoconclusao.scan-ms:600000}")
    public void processarAutoConclusao() {
        try {
            reservaService.processarAutoConclusao();
        } catch (Exception e) {
            logger.error("Erro no job de auto-conclusão: {}", e.getMessage(), e);
        }
    }
}
