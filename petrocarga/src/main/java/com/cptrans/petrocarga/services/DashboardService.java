package com.cptrans.petrocarga.services;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.cptrans.petrocarga.dto.dashboard.DashboardKpiDTO;
import com.cptrans.petrocarga.dto.dashboard.DashboardSummaryDTO;
import com.cptrans.petrocarga.dto.dashboard.LocationStatDTO;
import com.cptrans.petrocarga.dto.dashboard.VehicleTypeStatDTO;
import com.cptrans.petrocarga.repositories.ReservaRapidaRepository;
import com.cptrans.petrocarga.repositories.ReservaRepository;

@Service
public class DashboardService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ReservaRapidaRepository reservaRapidaRepository;

    private static final ZoneId ZONE_ID = ZoneId.of("America/Sao_Paulo");

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

        Double occupancyRate = totalSlots > 0 
            ? (double) activeReservations / totalSlots * 100 
            : 0.0;

        return new DashboardKpiDTO(
            totalSlots,
            activeReservations,
            pendingReservations,
            occupancyRate,
            completedReservations,
            canceledReservations,
            removedReservations,
            totalReservations,
            multipleSlotReservations,
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
                ((Number) row.get("count")).longValue(),
                ((Number) row.get("uniqueVehicles")).longValue()
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
                ((Number) row.get("count")).longValue()
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
                ((Number) row.get("count")).longValue()
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
                ((Number) row.get("count")).longValue()
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-summary", key = "#startDate?.toString() + '-' + #endDate?.toString()", unless = "#result == null")
    public DashboardSummaryDTO getSummary(OffsetDateTime startDate, OffsetDateTime endDate) {
        return new DashboardSummaryDTO(
            getKpis(startDate, endDate),
            getVehicleTypeStats(startDate, endDate),
            getDistrictStats(startDate, endDate),
            getOriginStats(startDate, endDate),
            getEntryOriginStats(startDate, endDate)
        );
    }
}
