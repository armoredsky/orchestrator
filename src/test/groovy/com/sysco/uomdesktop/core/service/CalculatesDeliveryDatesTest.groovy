package com.sysco.uomdesktop.core.service

import com.sysco.uomorchestrator.core.service.CalculatesDeliveryDates
import com.sysco.uomorchestrator.core.service.ClockService
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.Days
import org.joda.time.LocalDate
import org.joda.time.LocalTime
import spock.lang.Specification

class CalculatesDeliveryDatesTest extends Specification {
    private static final LocalTime FIVE_PM = new LocalTime(12 + 5, 0, 0)
    private static final LocalTime CUTOFF_TIME = FIVE_PM

    private static final DateTimeZone CENTRAL = DateTimeZone.forID('America/Chicago')

    CalculatesDeliveryDates calculatesDeliveryDates
    ClockService clockService

    void setup() {
        clockService = Mock(ClockService)

        calculatesDeliveryDates = new CalculatesDeliveryDates(clockService)
    }

    void 'is Friday when the current time is before the cutoff time on Thursday'() {
        given:
        Map deliverySchedule = ['Friday': Days.ZERO]

        currentTimeIs(new DateTime(2016, 9, 1, hoursBeforeCutoff(3), 0, CENTRAL), CENTRAL)

        when:
        LocalDate deliveryDate = new CalculatesDeliveryDates(clockService).nextAvailable(deliverySchedule, CUTOFF_TIME, CENTRAL)

        then:
        deliveryDate == new LocalDate(2016, 9, 2)
    }

    void 'is Saturday when the current time is before the cutoff time on Thursday but Friday is not a delivery day'() {
        given:
        Map deliverySchedule = ['Saturday': Days.ZERO]

        currentTimeIs(new DateTime(2016, 9, 1, hoursBeforeCutoff(3), 0, CENTRAL), CENTRAL)

        when:
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(deliverySchedule, CUTOFF_TIME, CENTRAL)

        then:
        deliveryDate == new LocalDate(2016, 9, 3)
    }

    void 'is Saturday when the current time is after the cutoff time on Thursday, even though Friday is a delivery day'() {
        given:
        Map deliverySchedule = ['Friday': Days.ZERO, 'Saturday': Days.ZERO]

        currentTimeIs(new DateTime(2016, 9, 1, hoursAfterCutoff(4), 0, CENTRAL), CENTRAL)

        when:
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(deliverySchedule, CUTOFF_TIME, CENTRAL)

        then:
        deliveryDate == new LocalDate(2016, 9, 3)
    }

    void 'is Saturday when the current time is before the cutoff time on Thursday, and Friday needs 1 day of lead time'() {
        given:
        Map deliverySchedule = ['Friday': Days.ONE, 'Saturday': Days.ZERO]

        and:
        currentTimeIs(new DateTime(2016, 9, 1, hoursBeforeCutoff(3), 0, CENTRAL), CENTRAL)

        when:
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(deliverySchedule, CUTOFF_TIME, CENTRAL)

        then:
        deliveryDate == new LocalDate(2016, 9, 3)
    }

    void 'rolls around a full week if I missed my only delivery day'() {
        given:
        Map deliverySchedule = ['Friday': Days.TWO]

        and:
        currentTimeIs(new DateTime(2016, 8, 31, hoursBeforeCutoff(3), 0, CENTRAL), CENTRAL)

        when:
        LocalDate deliveryDate = calculatesDeliveryDates.nextAvailable(deliverySchedule, CUTOFF_TIME, CENTRAL)

        then:
        deliveryDate == new LocalDate(2016, 9, 9)
    }

    private DateTime currentTimeIs(DateTime currentTime, DateTimeZone timeZone) {
        clockService.currentDateTime(timeZone) >> currentTime
        currentTime
    }

    private static hoursBeforeCutoff(int hours) {
        CUTOFF_TIME.minusHours(hours).hourOfDay
    }
    private static hoursAfterCutoff(int hours) {
        CUTOFF_TIME.plusHours(hours).hourOfDay
    }
}
