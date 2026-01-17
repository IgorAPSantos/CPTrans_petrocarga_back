package com.cptrans.petrocarga.infrastructure.scheduler.job.reserva;


import java.util.UUID;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.cptrans.petrocarga.services.ReservaService;

@DisallowConcurrentExecution
@Component
public class FinalizarReservaJob implements Job {

    @Autowired
    private ReservaService reservaService;

    @Override
    public void execute(JobExecutionContext context) {
        UUID reservaId = UUID.fromString(
            context.getMergedJobDataMap().getString("reservaId")
        );

        reservaService.finalizarReserva(reservaId);
    }
}

