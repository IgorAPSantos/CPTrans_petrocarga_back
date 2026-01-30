package com.cptrans.petrocarga.dto.dashboard;

import java.util.List;

public class DashboardSummaryDTO {
    private DashboardKpiDTO kpis;
    private List<VehicleTypeStatDTO> vehicleTypes;
    private List<LocationStatDTO> districts;
    private List<LocationStatDTO> origins;
    private List<LocationStatDTO> entryOrigins;

    public DashboardSummaryDTO() {
    }

    public DashboardSummaryDTO(DashboardKpiDTO kpis, List<VehicleTypeStatDTO> vehicleTypes,
            List<LocationStatDTO> districts, List<LocationStatDTO> origins, List<LocationStatDTO> entryOrigins) {
        this.kpis = kpis;
        this.vehicleTypes = vehicleTypes;
        this.districts = districts;
        this.origins = origins;
        this.entryOrigins = entryOrigins;
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
}
