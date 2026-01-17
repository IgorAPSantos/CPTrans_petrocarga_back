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

import com.cptrans.petrocarga.infrastructure.configs.quartz.QuartzGroups;
import com.cptrans.petrocarga.infrastructure.scheduler.job.notificacao.NotificarCheckInDisponivelJob;
import com.cptrans.petrocarga.infrastructure.scheduler.job.notificacao.NotificarFimProximoJob;

@Service
public class NotificacaoSchedulerService {
    private final Scheduler scheduler;
    private final String CHECKIN_DISPONIVEL = "CHECKIN_DISPONIVEL";
    private final String FIM_PROXIMO = "FIM_PROXIMO";

    public NotificacaoSchedulerService(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void agendarNotificacaoCheckInDisponivel(UUID usuarioId, UUID reservaId, OffsetDateTime inicioReserva) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "envia-notificacao-" + CHECKIN_DISPONIVEL + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        );

        if (scheduler.checkExists(jobKey)) {
            return;
        }

        JobDetail job = JobBuilder.newJob(NotificarCheckInDisponivelJob.class)
        .withIdentity("envia-notificacao-" + CHECKIN_DISPONIVEL + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        )
        .usingJobData("usuarioId", usuarioId.toString())
        .usingJobData("inicioReserva", inicioReserva.toString())
        .build();

        Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger-envia-notificacao-" + CHECKIN_DISPONIVEL + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        ).startAt(Date.from(inicioReserva.minusMinutes(5).toInstant()))
        .build();
        
        scheduler.scheduleJob(job, trigger);

    }

     public void agendarNotificacaoFimProximo(UUID usuarioId, UUID reservaId, OffsetDateTime fimReserva) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "envia-notificacao-" + FIM_PROXIMO + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        );

        if (scheduler.checkExists(jobKey)) {
            return;
        }

        JobDetail job = JobBuilder.newJob(NotificarFimProximoJob.class)
        .withIdentity("envia-notificacao-" + FIM_PROXIMO + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        )
        .usingJobData("usuarioId", usuarioId.toString())
        .usingJobData("fimReserva", fimReserva.toString())
        .usingJobData("reservaId", reservaId.toString())
        .build();

        Trigger trigger = TriggerBuilder.newTrigger()
        .withIdentity("trigger-envia-notificacao-" + FIM_PROXIMO + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        ).startAt(Date.from(fimReserva.minusMinutes(10).toInstant()))
        .build();
        
        scheduler.scheduleJob(job, trigger);

    }

    public void cancelarSchedulerCheckIn(UUID usuarioId, UUID reservaId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "envia-notificacao-" + CHECKIN_DISPONIVEL + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        );
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        scheduler.deleteJob(
            JobKey.jobKey(
                "envia-notificacao-" + CHECKIN_DISPONIVEL + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
                QuartzGroups.NOTIFICACAO
            )
        );
    }

    public void cancelarSchedulerFimProximo(UUID usuarioId, UUID reservaId) throws SchedulerException {
        JobKey jobKey = JobKey.jobKey(
            "envia-notificacao-" + FIM_PROXIMO + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
            QuartzGroups.NOTIFICACAO
        );
        if (!scheduler.checkExists(jobKey)) {
            return;
        }
        scheduler.deleteJob(
            JobKey.jobKey(
                "envia-notificacao-" + FIM_PROXIMO + "-usuario-" + usuarioId.toString() + "-reserva-" + reservaId.toString(),
                QuartzGroups.NOTIFICACAO
            )
        );
    }
}
