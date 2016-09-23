package com.sysco.uomorchestrator.core.service

import org.joda.time.DateTime
import org.joda.time.DateTimeZone

class ClockService {
    DateTime currentDateTime() {
        currentDateTime(DateTimeZone.UTC)
    }

    DateTime currentDateTime(DateTimeZone timeZone) {
        DateTime.now(timeZone)
    }
}
