package com.cptrans.petrocarga.infrastructure.scheduler.job.reserva;

import java.util.UUID;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cptrans.petrocarga.services.ReservaService;

public class NoShowJob implements Job {

    @Autowired
    private ReservaService reservaService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
       UUID reservaId = UUID.fromString(context.getMergedJobDataMap().getString("reservaId"));
       reservaService.processarNoShow(reservaId);
    }
    
}
