package com.cptrans.petrocarga.dto.dashboard;

import java.time.OffsetDateTime;

public class DashboardKpiDTO {
    private Long totalSlots;
    private Long activeReservations;
    private Double occupancyRate;
    private Long completedReservations;
    private Long canceledReservations;
    private Long totalReservations;
    private Long multipleSlotReservations;
    private OffsetDateTime startDate;
    private OffsetDateTime endDate;

    public DashboardKpiDTO() {
    }

    public DashboardKpiDTO(Long totalSlots, Long activeReservations, Double occupancyRate,
            Long completedReservations, Long canceledReservations, Long totalReservations,
            Long multipleSlotReservations, OffsetDateTime startDate, OffsetDateTime endDate) {
        this.totalSlots = totalSlots;
        this.activeReservations = activeReservations;
        this.occupancyRate = occupancyRate;
        this.completedReservations = completedReservations;
        this.canceledReservations = canceledReservations;
        this.totalReservations = totalReservations;
        this.multipleSlotReservations = multipleSlotReservations;
        this.startDate = startDate;
        this.endDate = endDate;
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

    public Long getMultipleSlotReservations() {
        return multipleSlotReservations;
    }

    public void setMultipleSlotReservations(Long multipleSlotReservations) {
        this.multipleSlotReservations = multipleSlotReservations;
    }

    public OffsetDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(OffsetDateTime startDate) {
        this.startDate = startDate;
    }

    public OffsetDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(OffsetDateTime endDate) {
        this.endDate = endDate;
    }
}
