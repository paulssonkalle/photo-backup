package com.kallepaulsson.aws.photobackup.models;

import java.time.YearMonth;

public class BackupDate {

    private YearMonth yearMonth;

    public BackupDate(YearMonth yearMonth) {
        this.yearMonth = yearMonth;
    }

    public String getYear() {
        return String.valueOf(yearMonth.getYear());
    }

    public String getMonth() {
        return String.format("%02d", yearMonth.getMonthValue());
    }
}
