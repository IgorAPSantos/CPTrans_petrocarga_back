package com.cptrans.petrocarga.dto.dashboard;

public class StayDurationStatsDTO {
    private Double avgMinutes;
    private Double minMinutes;
    private Double maxMinutes;

    public StayDurationStatsDTO() {
    }

    public StayDurationStatsDTO(Double avgMinutes, Double minMinutes, Double maxMinutes) {
        this.avgMinutes = avgMinutes;
        this.minMinutes = minMinutes;
        this.maxMinutes = maxMinutes;
    }

    public Double getAvgMinutes() {
        return avgMinutes;
    }

    public void setAvgMinutes(Double avgMinutes) {
        this.avgMinutes = avgMinutes;
    }

    public Double getMinMinutes() {
        return minMinutes;
    }

    public void setMinMinutes(Double minMinutes) {
        this.minMinutes = minMinutes;
    }

    public Double getMaxMinutes() {
        return maxMinutes;
    }

    public void setMaxMinutes(Double maxMinutes) {
        this.maxMinutes = maxMinutes;
    }
}
