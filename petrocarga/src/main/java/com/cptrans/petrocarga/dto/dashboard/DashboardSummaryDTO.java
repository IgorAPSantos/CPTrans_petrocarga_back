package com.cptrans.petrocarga.dto.dashboard;

import java.util.List;

public class DashboardSummaryDTO {
    private DashboardKpiDTO kpis;
    private List<VehicleTypeStatDTO> vehicleTypes;
    private List<LocationStatDTO> districts;
    private List<LocationStatDTO> origins;
    private List<LocationStatDTO> entryOrigins;
    private List<LocationStatDTO> mostUsedVagas;
    private StayDurationStatsDTO stayDurationStats;
    private ActiveDuringPeriodStatsDTO activeDuringPeriodStats;
    private LengthOccupancyStatsDTO lengthOccupancyStats;
    private List<VehicleRouteReportDTO> vehicleRoutes;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas) {
        this(kpis, vehicleTypes, districts, origins, entryOrigins, mostUsedVagas, null, null, null, null);
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas,
            StayDurationStatsDTO stayDurationStats) {
        this(kpis, vehicleTypes, districts, origins, entryOrigins, mostUsedVagas, stayDurationStats, null, null, null);
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas,
            StayDurationStatsDTO stayDurationStats, ActiveDuringPeriodStatsDTO activeDuringPeriodStats) {
        this(kpis, vehicleTypes, districts, origins, entryOrigins, mostUsedVagas, stayDurationStats, activeDuringPeriodStats, null, null);
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas,
            StayDurationStatsDTO stayDurationStats, ActiveDuringPeriodStatsDTO activeDuringPeriodStats, LengthOccupancyStatsDTO lengthOccupancyStats) {
        this(kpis, vehicleTypes, districts, origins, entryOrigins, mostUsedVagas, stayDurationStats, activeDuringPeriodStats, lengthOccupancyStats, null);
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas,
            StayDurationStatsDTO stayDurationStats, ActiveDuringPeriodStatsDTO activeDuringPeriodStats, LengthOccupancyStatsDTO lengthOccupancyStats,
            List<VehicleRouteReportDTO> vehicleRoutes) {
        this.kpis = kpis;
        this.vehicleTypes = vehicleTypes;
        this.districts = districts;
        this.origins = origins;
        this.entryOrigins = entryOrigins;
        this.mostUsedVagas = mostUsedVagas;
        this.stayDurationStats = stayDurationStats;
        this.activeDuringPeriodStats = activeDuringPeriodStats;
        this.lengthOccupancyStats = lengthOccupancyStats;
        this.vehicleRoutes = vehicleRoutes;
    }

    public DashboardKpiDTO getKpis() {
        return kpis;
    }

    public void setKpis(DashboardKpiDTO kpis) {
        this.kpis = kpis;
    }

    public List<VehicleTypeStatDTO> getVehicleTypes() {
        return vehicleTypes;
    }

    public void setVehicleTypes(List<VehicleTypeStatDTO> vehicleTypes) {
        this.vehicleTypes = vehicleTypes;
    }

    public List<LocationStatDTO> getDistricts() {
        return districts;
    }

    public void setDistricts(List<LocationStatDTO> districts) {
        this.districts = districts;
    }

    public List<LocationStatDTO> getOrigins() {
        return origins;
    }

    public void setOrigins(List<LocationStatDTO> origins) {
        this.origins = origins;
    }

    public List<LocationStatDTO> getEntryOrigins() {
        return entryOrigins;
    }

    public void setEntryOrigins(List<LocationStatDTO> entryOrigins) {
        this.entryOrigins = entryOrigins;
    }

    public List<LocationStatDTO> getMostUsedVagas() {
        return mostUsedVagas;
    }

    public void setMostUsedVagas(List<LocationStatDTO> mostUsedVagas) {
        this.mostUsedVagas = mostUsedVagas;
    }

    public StayDurationStatsDTO getStayDurationStats() {
        return stayDurationStats;
    }

    public void setStayDurationStats(StayDurationStatsDTO stayDurationStats) {
        this.stayDurationStats = stayDurationStats;
    }

    public ActiveDuringPeriodStatsDTO getActiveDuringPeriodStats() {
        return activeDuringPeriodStats;
    }

    public void setActiveDuringPeriodStats(ActiveDuringPeriodStatsDTO activeDuringPeriodStats) {
        this.activeDuringPeriodStats = activeDuringPeriodStats;
    }

    public LengthOccupancyStatsDTO getLengthOccupancyStats() {
        return lengthOccupancyStats;
    }

    public void setLengthOccupancyStats(LengthOccupancyStatsDTO lengthOccupancyStats) {
        this.lengthOccupancyStats = lengthOccupancyStats;
    }

    public List<VehicleRouteReportDTO> getVehicleRoutes() {
        return vehicleRoutes;
    }

    public void setVehicleRoutes(List<VehicleRouteReportDTO> vehicleRoutes) {
        this.vehicleRoutes = vehicleRoutes;
    }

}