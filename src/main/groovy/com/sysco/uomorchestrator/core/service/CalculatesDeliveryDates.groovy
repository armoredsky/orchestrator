package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime

class CalculatesDeliveryDates {
    private final ClockService clockService

    @Inject
    CalculatesDeliveryDates(ClockService clockService) {
        this.clockService = clockService
    }

    LocalDate nextAvailable(Map<String, Days> deliverySchedule, LocalTime cutoffTime, DateTimeZone timeZone) {
        DateTime nowInTimeZone = clockService.currentDateTime(timeZone)

        DateTime nextDeliveryDate = rollToNextCutoff(nowInTimeZone, cutoffTime)

        boolean foundDeliveryDate = false
        while (!foundDeliveryDate) {
            nextDeliveryDate = nextDeliveryDate.plusDays(1)

            String dayOfTheWeek = nextDeliveryDate.dayOfWeek().getAsText()

            if (!isDeliveryDay(deliverySchedule, dayOfTheWeek)) {
                continue;
            }

            Days requiredLeadTime = deliverySchedule[dayOfTheWeek]
            foundDeliveryDate = isThereEnoughLeadTime(requiredLeadTime, nowInTimeZone, nextDeliveryDate)
        }

        new LocalDate(nextDeliveryDate)
    }

    private static boolean isDeliveryDay(Map<String, Days> deliverySchedule, String dayOfTheWeek) {
        deliverySchedule.containsKey(dayOfTheWeek)
    }

    private static DateTime rollToNextCutoff(DateTime nowInTimeZone, LocalTime cutoffTime) {
        DateTime todaysCutoff = nowInTimeZone.withTime(cutoffTime)

        if (nowInTimeZone <= todaysCutoff) {
            return new DateTime(todaysCutoff)
        } else {
            return todaysCutoff.plusDays(1)
        }
    }

    private static isThereEnoughLeadTime(Days requiredLeadTime, DateTime nowInTimeZone, DateTime nextDeliveryDate) {
        Days actualLeadTime = Days.daysBetween(nowInTimeZone, nextDeliveryDate)
        return actualLeadTime > requiredLeadTime
    }
}
