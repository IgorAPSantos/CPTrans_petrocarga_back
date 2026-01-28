package com.cptrans.petrocarga.dto.dashboard;

public class VehicleTypeStatDTO {
    private String type;
    private Long count;
    private Long uniqueVehicles;

    public VehicleTypeStatDTO() {
    }

    public VehicleTypeStatDTO(String type, Long count, Long uniqueVehicles) {
        this.type = type;
        this.count = count;
        this.uniqueVehicles = uniqueVehicles;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getUniqueVehicles() {
        return uniqueVehicles;
    }

    public void setUniqueVehicles(Long uniqueVehicles) {
        this.uniqueVehicles = uniqueVehicles;
    }
}
