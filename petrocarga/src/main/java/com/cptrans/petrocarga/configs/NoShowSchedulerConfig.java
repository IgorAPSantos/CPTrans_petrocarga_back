package com.cptrans.petrocarga.configs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cptrans.petrocarga.services.ReservaService;

@Configuration
@EnableScheduling
@ConditionalOnProperty(name = "app.reservas.noshow.enabled", havingValue = "true", matchIfMissing = false)
public class NoShowSchedulerConfig {

    private static final Logger logger = LoggerFactory.getLogger(NoShowSchedulerConfig.class);

    @Autowired
    private ReservaService reservaService;

    @Value("${app.reservas.noshow.grace-minutes:10}")
    private int graceMinutes;

    /**
     * Job que roda periodicamente para processar reservas sem check-in (no-show).
     * Frequência controlada por: app.reservas.noshow.scan-ms (padrão: 600000ms = 10 minutos)
     * Ativação controlada por: app.reservas.noshow.enabled (padrão: false)
     */
    @Scheduled(fixedDelayString = "${app.reservas.noshow.scan-ms:600000}")
    public void processarNoShow() {
        try {
            reservaService.processarNoShow(graceMinutes);
        } catch (Exception e) {
            logger.error("Erro no job de no-show: {}", e.getMessage(), e);
        }
    }
}
