package com.intor.watchtime.repository.projection;

import java.time.LocalDate;

public interface UserStreakProjection {

    LocalDate getLastWatchedDay();

    LocalDate getStreakStart();
}
