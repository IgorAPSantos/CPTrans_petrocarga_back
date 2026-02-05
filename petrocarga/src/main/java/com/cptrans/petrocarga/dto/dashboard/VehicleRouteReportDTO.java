package com.cptrans.petrocarga.dto.dashboard;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Relatório de trajeto reconstruído a partir de Reserva/ReservaRapida.
 *
 * <p>A lista de paradas é ordenada temporalmente por {@code inicio}.</p>
 */
public class VehicleRouteReportDTO {
    private String placa;
    private OffsetDateTime periodStart;
    private OffsetDateTime periodEnd;
    private List<VehicleRouteStopDTO> stops;

    public VehicleRouteReportDTO() {
    }

    public VehicleRouteReportDTO(String placa, OffsetDateTime periodStart, OffsetDateTime periodEnd,
            List<VehicleRouteStopDTO> stops) {
        this.placa = placa;
        this.periodStart = periodStart;
        this.periodEnd = periodEnd;
        this.stops = stops;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public OffsetDateTime getPeriodStart() {
        return periodStart;
    }

    public void setPeriodStart(OffsetDateTime periodStart) {
        this.periodStart = periodStart;
    }

    public OffsetDateTime getPeriodEnd() {
        return periodEnd;
    }

    public void setPeriodEnd(OffsetDateTime periodEnd) {
        this.periodEnd = periodEnd;
    }

    public List<VehicleRouteStopDTO> getStops() {
        return stops;
    }

    public void setStops(List<VehicleRouteStopDTO> stops) {
        this.stops = stops;
    }
}
