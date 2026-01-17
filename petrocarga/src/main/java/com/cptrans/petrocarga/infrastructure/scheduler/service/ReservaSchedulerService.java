package com.cptrans.petrocarga.infrastructure.scheduler.service;

import java.util.Date;
import java.util.UUID;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.ReservaDTO;
import com.cptrans.petrocarga.infrastructure.configs.quartz.QuartzGroups;
import com.cptrans.petrocarga.infrastructure.scheduler.job.reserva.FinalizarReservaJob;

@Service
public class ReservaSchedulerService {
    private final Scheduler scheduler;

    public ReservaSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void agendarFinalizacaoReserva(ReservaDTO reservaDTO) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "finaliza-reserva-" + reservaDTO.getId(),
            QuartzGroups.RESERVAS
        );

        if (scheduler.checkExists(jobKey)) {
            return;
        }
        
        JobDetail job = JobBuilder.newJob(FinalizarReservaJob.class)
        .withIdentity(
            "finaliza-reserva-" + reservaDTO.getId(),
            QuartzGroups.RESERVAS)
        .usingJobData("reservaId", reservaDTO.getId().toString())
        .build();

        Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity(
            "trigger-finaliza-reserva-" + reservaDTO.getId(),
            QuartzGroups.RESERVAS)
        .startAt(Date.from(reservaDTO.getFim().toInstant()))
        .build();

        scheduler.scheduleJob(job, trigger);
    }
    public void cancelarScheduler(UUID reservaId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey("finaliza-reserva-" + reservaId, QuartzGroups.RESERVAS);
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        scheduler.deleteJob(
            JobKey.jobKey("finaliza-reserva-" + reservaId, QuartzGroups.RESERVAS)
        );
    }
}

