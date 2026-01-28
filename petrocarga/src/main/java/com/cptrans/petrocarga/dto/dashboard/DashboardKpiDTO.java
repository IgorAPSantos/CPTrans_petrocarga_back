package com.cptrans.petrocarga.dto.dashboard;

public class DashboardKpiDTO {
    private Long totalSlots;
    private Long activeReservations;
    private Double occupancyRate;
    private Long completedReservations;
    private Long canceledReservations;
    private Long totalReservations;

    public DashboardKpiDTO() {
    }

    public DashboardKpiDTO(Long totalSlots, Long activeReservations, Double occupancyRate,
            Long completedReservations, Long canceledReservations, Long totalReservations) {
        this.totalSlots = totalSlots;
        this.activeReservations = activeReservations;
        this.occupancyRate = occupancyRate;
        this.completedReservations = completedReservations;
        this.canceledReservations = canceledReservations;
        this.totalReservations = totalReservations;
    }

    public Long getTotalSlots() {
        return totalSlots;
    }

    public void setTotalSlots(Long totalSlots) {
        this.totalSlots = totalSlots;
    }

    public Long getActiveReservations() {
        return activeReservations;
    }

    public void setActiveReservations(Long activeReservations) {
        this.activeReservations = activeReservations;
    }

    public Double getOccupancyRate() {
        return occupancyRate;
    }

    public void setOccupancyRate(Double occupancyRate) {
        this.occupancyRate = occupancyRate;
    }

    public Long getCompletedReservations() {
        return completedReservations;
    }

    public void setCompletedReservations(Long completedReservations) {
        this.completedReservations = completedReservations;
    }

    public Long getCanceledReservations() {
        return canceledReservations;
    }

    public void setCanceledReservations(Long canceledReservations) {
        this.canceledReservations = canceledReservations;
    }

    public Long getTotalReservations() {
        return totalReservations;
    }

    public void setTotalReservations(Long totalReservations) {
        this.totalReservations = totalReservations;
    }
}
