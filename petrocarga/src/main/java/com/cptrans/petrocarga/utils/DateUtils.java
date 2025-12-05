package com.cptrans.petrocarga.utils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class DateUtils {
    public static final ZoneId FUSO_BRASIL = ZoneOffset.of("-03:00");
    public static LocalDate toLocalDateInBrazil(OffsetDateTime data) {
        if (data == null) return null;
        return data.atZoneSameInstant(ZoneOffset.of("-03:00")).toLocalDate();
    }
}
