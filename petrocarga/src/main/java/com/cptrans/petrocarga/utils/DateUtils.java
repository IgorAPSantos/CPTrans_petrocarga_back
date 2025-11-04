package com.cptrans.petrocarga.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class DateUtils {
    private DateUtils() {}

    public static LocalDate toLocalDateInBrazil(OffsetDateTime data) {
        if (data == null) return null;
        return data.atZoneSameInstant(ZoneOffset.of("-03:00")).toLocalDate();
    }
}
