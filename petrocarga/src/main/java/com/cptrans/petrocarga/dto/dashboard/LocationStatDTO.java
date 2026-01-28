package com.cptrans.petrocarga.dto.dashboard;

public class LocationStatDTO {
    private String name;
    private String type;
    private Long reservationCount;

    public LocationStatDTO() {
    }

    public LocationStatDTO(String name, String type, Long reservationCount) {
        this.name = name;
        this.type = type;
        this.reservationCount = reservationCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getReservationCount() {
        return reservationCount;
    }

    public void setReservationCount(Long reservationCount) {
        this.reservationCount = reservationCount;
    }
}
