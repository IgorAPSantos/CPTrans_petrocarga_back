package com.cptrans.petrocarga.services;

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
import com.cptrans.petrocarga.repositories.ReservaRepository;

@Service
public class DashboardService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Cacheable(value = "dashboard-kpi", unless = "#result == null")
    public DashboardKpiDTO getKpis() {
        Long totalSlots = reservaRepository.countTotalSlots();
        Long activeReservations = reservaRepository.countActiveReservations();
        Long completedReservations = reservaRepository.countCompletedReservations();
        Long canceledReservations = reservaRepository.countCanceledReservations();
        Long totalReservations = reservaRepository.count();

        Double occupancyRate = totalSlots > 0 
            ? (double) activeReservations / totalSlots * 100 
            : 0.0;

        return new DashboardKpiDTO(
            totalSlots,
            activeReservations,
            occupancyRate,
            completedReservations,
            canceledReservations,
            totalReservations
        );
    }

    @Cacheable(value = "dashboard-vehicle-types", unless = "#result == null || #result.isEmpty()")
    public List<VehicleTypeStatDTO> getVehicleTypeStats() {
        List<Map<String, Object>> results = reservaRepository.getVehicleTypeStats();
        
        return results.stream()
            .map(row -> new VehicleTypeStatDTO(
                (String) row.get("tipo"),
                ((Number) row.get("count")).longValue(),
                ((Number) row.get("uniqueVehicles")).longValue()
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-districts", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getDistrictStats() {
        List<Map<String, Object>> results = reservaRepository.getDistrictStats();
        
        return results.stream()
            .map(row -> new LocationStatDTO(
                (String) row.get("name"),
                "district",
                ((Number) row.get("count")).longValue()
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-origins", unless = "#result == null || #result.isEmpty()")
    public List<LocationStatDTO> getOriginStats() {
        List<Map<String, Object>> results = reservaRepository.getOriginStats();
        
        return results.stream()
            .map(row -> new LocationStatDTO(
                (String) row.get("name"),
                "origin",
                ((Number) row.get("count")).longValue()
            ))
            .collect(Collectors.toList());
    }

    @Cacheable(value = "dashboard-summary", unless = "#result == null")
    public DashboardSummaryDTO getSummary() {
        return new DashboardSummaryDTO(
            getKpis(),
            getVehicleTypeStats(),
            getDistrictStats(),
            getOriginStats()
        );
    }
}
