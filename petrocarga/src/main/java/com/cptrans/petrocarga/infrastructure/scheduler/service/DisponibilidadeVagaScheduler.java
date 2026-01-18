package com.cptrans.petrocarga.infrastructure.scheduler.service;

import java.time.OffsetDateTime;
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

import com.cptrans.petrocarga.enums.StatusVagaEnum;
import com.cptrans.petrocarga.infrastructure.configs.quartz.QuartzGroups;
import com.cptrans.petrocarga.infrastructure.scheduler.job.disponibilidadeVaga.AlterarDisponibilidadeJob;
import com.cptrans.petrocarga.models.DisponibilidadeVaga;

@Service
public class DisponibilidadeVagaScheduler {

    private final Scheduler scheduler;

    public DisponibilidadeVagaScheduler(Scheduler scheduler) {
        this.scheduler = scheduler;
    }
    
    public void AgendarAlteracaoDisponibilidadeVaga(DisponibilidadeVaga disponibilidade, StatusVagaEnum status, OffsetDateTime dataAlteracao) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "alterar-disponibilidade-vaga-" + disponibilidade.getId().toString() + "-status-" + status.name(),
            QuartzGroups.DISPONIBILIDADE_VAGA
        );

        if (scheduler.checkExists(jobKey)) return;

        JobDetail jobDetail = JobBuilder.newJob(AlterarDisponibilidadeJob.class)
            .withIdentity(jobKey)
            .usingJobData("disponibilidadeId", disponibilidade.getId().toString())
            .usingJobData("status", status.name())
            .build();
        
        Trigger trigger = TriggerBuilder.newTrigger()
            .withIdentity(
                "trigger-alterar-disponibilidade-vaga-" + disponibilidade.getId().toString() + "-status-" + status.name(),
                QuartzGroups.DISPONIBILIDADE_VAGA
            )
            .startAt(Date.from(dataAlteracao.toInstant()))
            .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public void cancelarScheduler(UUID disponibilidadeId , StatusVagaEnum status) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "alterar-disponibilidade-vaga-" + disponibilidadeId.toString() + "-status-" + status.name(),
            QuartzGroups.DISPONIBILIDADE_VAGA
        );

        if (scheduler.checkExists(jobKey)) {
            scheduler.deleteJob(jobKey);
        }
    }
}
