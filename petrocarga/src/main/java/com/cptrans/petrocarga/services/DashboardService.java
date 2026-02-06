package com.cptrans.petrocarga.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import com.cptrans.petrocarga.dto.dashboard.LengthOccupancyStatsDTO;
import com.cptrans.petrocarga.dto.dashboard.VehicleRouteReportDTO;
import com.cptrans.petrocarga.dto.dashboard.VehicleRouteStopDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.dashboard.DashboardKpiDTO;
import com.cptrans.petrocarga.dto.dashboard.DashboardSummaryDTO;
import com.cptrans.petrocarga.dto.dashboard.ActiveDuringPeriodStatsDTO;
import com.cptrans.petrocarga.dto.dashboard.LocationStatDTO;
import com.cptrans.petrocarga.dto.dashboard.StayDurationStatsDTO;
import com.cptrans.petrocarga.dto.dashboard.VehicleTypeStatDTO;
import com.cptrans.petrocarga.repositories.ReservaRapidaRepository;
import com.cptrans.petrocarga.repositories.ReservaRepository;
import com.cptrans.petrocarga.repositories.VagaRepository;
import com.cptrans.petrocarga.repositories.projections.StayDurationAggProjection;
import com.cptrans.petrocarga.repositories.projections.VehicleRouteEventProjection;

@Service
public class DashboardService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaRapidaRepository reservaRapidaRepository;

    @Autowired
    private VagaRepository vagaRepository;

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

    private StayDurationStatsDTO getStayDurationStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        StayDurationAggProjection reservaAgg = reservaRepository.getStayDurationAggCompletedReservations(resolvedStart, resolvedEnd);
        StayDurationAggProjection rapidaAgg = reservaRapidaRepository.getStayDurationAggCompletedReservations(resolvedStart, resolvedEnd);

        long countReserva = reservaAgg != null && reservaAgg.getTotalCount() != null ? reservaAgg.getTotalCount() : 0L;
        long countRapida = rapidaAgg != null && rapidaAgg.getTotalCount() != null ? rapidaAgg.getTotalCount() : 0L;
        long totalCount = countReserva + countRapida;

        if (totalCount <= 0) {
            return null;
        }

        BigDecimal sumReserva = reservaAgg != null ? reservaAgg.getSumMinutes() : null;
        BigDecimal sumRapida = rapidaAgg != null ? rapidaAgg.getSumMinutes() : null;
        BigDecimal totalSum = (sumReserva != null ? sumReserva : BigDecimal.ZERO)
            .add(sumRapida != null ? sumRapida : BigDecimal.ZERO);

        BigDecimal avgMinutes = totalSum.divide(BigDecimal.valueOf(totalCount), 4, RoundingMode.HALF_UP);

        BigDecimal minReserva = reservaAgg != null ? reservaAgg.getMinMinutes() : null;
        BigDecimal minRapida = rapidaAgg != null ? rapidaAgg.getMinMinutes() : null;
        BigDecimal maxReserva = reservaAgg != null ? reservaAgg.getMaxMinutes() : null;
        BigDecimal maxRapida = rapidaAgg != null ? rapidaAgg.getMaxMinutes() : null;

        BigDecimal minMinutes;
        if (minReserva != null && minRapida != null) {
            minMinutes = minReserva.min(minRapida);
        } else {
            minMinutes = minReserva != null ? minReserva : minRapida;
        }

        BigDecimal maxMinutes;
        if (maxReserva != null && maxRapida != null) {
            maxMinutes = maxReserva.max(maxRapida);
        } else {
            maxMinutes = maxReserva != null ? maxReserva : maxRapida;
        }

        return new StayDurationStatsDTO(
            avgMinutes.doubleValue(),
            minMinutes != null ? minMinutes.doubleValue() : null,
            maxMinutes != null ? maxMinutes.doubleValue() : null
        );
    }

    private ActiveDuringPeriodStatsDTO getActiveDuringPeriodStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        Long reserva = reservaRepository.countActiveReservationsDuringPeriod(resolvedStart, resolvedEnd);
        Long rapida = reservaRapidaRepository.countActiveReservationsDuringPeriod(resolvedStart, resolvedEnd);

        int reservaCount = toInt(reserva);
        int rapidaCount = toInt(rapida);
        int total = reservaCount + rapidaCount;

        return new ActiveDuringPeriodStatsDTO(total, reservaCount, rapidaCount);
    }

    private LengthOccupancyStatsDTO getLengthOccupancyStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        long periodMinutes = ChronoUnit.MINUTES.between(resolvedStart.toInstant(), resolvedEnd.toInstant());
        if (periodMinutes <= 0) {
            return null;
        }

        Long availableMetersLong = vagaRepository.sumTotalAvailableLengthMeters();
        double availableMeters = availableMetersLong != null ? availableMetersLong.doubleValue() : 0.0;
        if (availableMeters <= 0.0) {
            return new LengthOccupancyStatsDTO(0.0, 0.0, 0.0);
        }

        BigDecimal occReserva = reservaRepository.sumOccupiedLengthMinutesActiveDuringPeriod(resolvedStart, resolvedEnd);
        BigDecimal occRapida = reservaRapidaRepository.sumOccupiedLengthMinutesActiveDuringPeriod(resolvedStart, resolvedEnd);
        BigDecimal totalOccLengthMinutes = (occReserva != null ? occReserva : BigDecimal.ZERO)
            .add(occRapida != null ? occRapida : BigDecimal.ZERO);

        BigDecimal avgOccupiedMeters = totalOccLengthMinutes
            .divide(BigDecimal.valueOf(periodMinutes), 6, RoundingMode.HALF_UP);

        BigDecimal ratePercent = avgOccupiedMeters
            .divide(BigDecimal.valueOf(availableMeters), 6, RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100));

        return new LengthOccupancyStatsDTO(
            availableMeters,
            avgOccupiedMeters.doubleValue(),
            ratePercent.doubleValue()
        );
    }

    private List<VehicleRouteReportDTO> getVehicleRoutes(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<VehicleRouteEventProjection> events = reservaRepository.getVehicleRouteEventsDuringPeriod(resolvedStart, resolvedEnd);
        if (events == null || events.isEmpty()) {
            return List.of();
        }

        Map<String, List<VehicleRouteStopDTO>> byPlate = new LinkedHashMap<>();
        for (VehicleRouteEventProjection e : events) {
            if (e == null || e.getPlaca() == null) {
                continue;
            }
            byPlate.computeIfAbsent(e.getPlaca(), k -> new java.util.ArrayList<>())
                .add(new VehicleRouteStopDTO(
                    e.getSource(),
                    e.getInicio(),
                    e.getFim(),
                    e.getCidadeOrigem(),
                    e.getEntradaCidade(),
                    e.getVagaId(),
                    e.getVagaLabel()
                ));
        }

        return byPlate.entrySet().stream()
            .map(entry -> new VehicleRouteReportDTO(entry.getKey(), resolvedStart, resolvedEnd, entry.getValue()))
            .toList();
    }

    private OffsetDateTime resolveStartDate(OffsetDateTime startDate) {
        if (startDate != null) {
            return startDate;
        }
        return LocalDate.now(ZONE_ID).atStartOfDay(ZONE_ID).toOffsetDateTime();
    }

    private OffsetDateTime resolveEndDate(OffsetDateTime endDate) {
        if (endDate != null) {
            return endDate;
        }
        return LocalDate.now(ZONE_ID).atTime(23, 59, 59).atZone(ZONE_ID).toOffsetDateTime();
    }

    @Cacheable(value = "dashboard-kpi", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null")
    public DashboardKpiDTO getKpis(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        Long totalSlots = reservaRepository.countTotalSlots();
        Long activeReservations = reservaRepository.countActiveReservations(resolvedStart, resolvedEnd) + reservaRapidaRepository.countActiveReservations(resolvedStart, resolvedEnd);
        Long pendingReservations = reservaRepository.countPendingReservations(resolvedStart, resolvedEnd);
        Long completedReservations = reservaRepository.countCompletedReservations(resolvedStart, resolvedEnd) + reservaRapidaRepository.countCompletedReservations(resolvedStart, resolvedEnd);
        Long canceledReservations = reservaRepository.countCanceledReservations(resolvedStart, resolvedEnd);
        Long removedReservations = reservaRepository.countRemovedReservations(resolvedStart, resolvedEnd) + reservaRapidaRepository.countRemovedReservations(resolvedStart, resolvedEnd);
        Long totalReservations = reservaRepository.countTotalReservationsInPeriod(resolvedStart, resolvedEnd) + reservaRapidaRepository.countTotalReservationsInPeriod(resolvedStart, resolvedEnd);
        Long multipleSlotReservations = reservaRepository.countMultipleSlotReservations(resolvedStart, resolvedEnd);

        Double occupancyRate = totalSlots != null && totalSlots > 0
            ? (double) activeReservations / totalSlots * 100
            : 0.0;

        return new DashboardKpiDTO(
            toInt(totalSlots),
            toInt(activeReservations),
            toInt(pendingReservations),
            occupancyRate,
            toInt(completedReservations),
            toInt(canceledReservations),
            toInt(removedReservations),
            toInt(totalReservations),
            toInt(multipleSlotReservations),
            resolvedStart,
            resolvedEnd
        );
    }

    @Cacheable(value = "dashboard-vehicle-types", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null || #result.isEmpty()")
    public List<VehicleTypeStatDTO> getVehicleTypeStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<Map<String, Object>> results = reservaRepository.getVehicleTypeStats(resolvedStart, resolvedEnd);
        
        return results.stream()
            .map(row -> new VehicleTypeStatDTO(
                (String) row.get("tipo"),
                toInt((Number) row.get("count")),
                toInt((Number) row.get("uniqueVehicles"))
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-districts", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getDistrictStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<Map<String, Object>> results = reservaRepository.getDistrictStats(resolvedStart, resolvedEnd);
        
        return results.stream()
            .map(row -> new LocationStatDTO(
                (String) row.get("name"),
                "district",
                toInt((Number) row.get("count"))
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-origins", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getOriginStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<Map<String, Object>> results = reservaRepository.getOriginStats(resolvedStart, resolvedEnd);
        
        return results.stream()
            .map(row -> new LocationStatDTO(
                (String) row.get("name"),
                "origin",
                toInt((Number) row.get("count"))
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-entry-origins", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getEntryOriginStats(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<Map<String, Object>> results = reservaRepository.getEntryOriginStats(resolvedStart, resolvedEnd);
        
        return results.stream()
            .map(row -> new LocationStatDTO(
                (String) row.get("name"),
                "entry-origin",
                toInt((Number) row.get("count"))
            ))
            .collect(Collectors.toList());
    }
    @Cacheable(value = "dashboard-most-used", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getMostUsedVagas(OffsetDateTime startDate, OffsetDateTime endDate) {
        OffsetDateTime resolvedStart = resolveStartDate(startDate);
        OffsetDateTime resolvedEnd = resolveEndDate(endDate);

        List<Object[]> results = reservaRepository.getMostUsedVagas(resolvedStart, resolvedEnd);
        
        if (results == null || results.isEmpty()) {
            return List.of();
        }
        
        return results.stream()
    .map(r -> new LocationStatDTO(
        (String) r[0],                
        "most-used",
        toInt((Number) r[1])
    ))
    .toList();

    }

    @Cacheable(value = "dashboard-summary", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null")
    public DashboardSummaryDTO getSummary(OffsetDateTime startDate, OffsetDateTime endDate) {
        return new DashboardSummaryDTO(
            getKpis(startDate, endDate),
            getVehicleTypeStats(startDate, endDate),
            getDistrictStats(startDate, endDate),
            getOriginStats(startDate, endDate),
            getEntryOriginStats(startDate, endDate),
            getMostUsedVagas(startDate, endDate),
            getStayDurationStats(startDate, endDate),
            getActiveDuringPeriodStats(startDate, endDate),
            getLengthOccupancyStats(startDate, endDate),
            getVehicleRoutes(startDate, endDate)
        );
    }

    private static int toInt(Long value) {
        return value == null ? 0 : value.intValue();
    }

    private static int toInt(Number value) {
        return value == null ? 0 : value.intValue();
    }
}
