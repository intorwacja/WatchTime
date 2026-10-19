package com.intor.watchtime.repository.projection;

import java.time.LocalDate;

public interface DailyMinutesProjection {

    LocalDate getDay();

    long getMinutes();
}
