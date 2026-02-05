package com.cptrans.petrocarga.dto.dashboard;

/**
 * Métricas calculadas por interseção de intervalo (overlap).
 *
 * <p><b>Diferença importante</b>:</p>
 * <ul>
 *   <li><b>Iniciadas no período</b>: usam {@code inicio BETWEEN startDate AND endDate}.
 *       Ex.: KPIs atuais do dashboard.</li>
 *   <li><b>Ativas no período</b>: consideram reservas que estavam ativas em qualquer momento
 *       dentro do período, mesmo que tenham começado antes de {@code startDate}.
 *       Implementado via interseção: {@code inicio <= endDate AND (fim IS NULL OR fim >= startDate)}.</li>
 * </ul>
 */
public class ActiveDuringPeriodStatsDTO {
    private Long total;
    private Long reserva;
    private Long reservaRapida;

    public ActiveDuringPeriodStatsDTO() {
    }

    public ActiveDuringPeriodStatsDTO(Long total, Long reserva, Long reservaRapida) {
        this.total = total;
        this.reserva = reserva;
        this.reservaRapida = reservaRapida;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getReserva() {
        return reserva;
    }

    public void setReserva(Long reserva) {
        this.reserva = reserva;
    }

    public Long getReservaRapida() {
        return reservaRapida;
    }

    public void setReservaRapida(Long reservaRapida) {
        this.reservaRapida = reservaRapida;
    }
}
