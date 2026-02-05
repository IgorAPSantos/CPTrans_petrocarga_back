package com.cptrans.petrocarga.repositories.projections;

import java.math.BigDecimal;

public interface StayDurationAggProjection {
    Long getTotalCount();

    BigDecimal getSumMinutes();

    BigDecimal getMinMinutes();

    BigDecimal getMaxMinutes();
}
