package com.cptrans.petrocarga.dto.dashboard;

import java.util.List;

public class DashboardSummaryDTO {
    private DashboardKpiDTO kpis;
    private List<VehicleTypeStatDTO> vehicleTypes;
    private List<LocationStatDTO> districts;
    private List<LocationStatDTO> origins;
    private List<LocationStatDTO> entryOrigins;
    private List<LocationStatDTO> mostUsedVagas;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins, List<LocationStatDTO> mostUsedVagas) {
        this.kpis = kpis;
        this.vehicleTypes = vehicleTypes;
        this.districts = districts;
        this.origins = origins;
        this.entryOrigins = entryOrigins;
        this.mostUsedVagas = mostUsedVagas;
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

}