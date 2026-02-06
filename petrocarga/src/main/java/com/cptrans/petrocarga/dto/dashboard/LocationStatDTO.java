package com.cptrans.petrocarga.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationStatDTO {
    private String name;
    private String type;
    private Integer reservationCount;
}
