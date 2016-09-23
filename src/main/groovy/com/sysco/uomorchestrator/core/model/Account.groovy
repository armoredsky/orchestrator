package com.sysco.uomorchestrator.core.model

import groovy.transform.EqualsAndHashCode
import org.joda.time.Days
import org.joda.time.LocalTime

@EqualsAndHashCode
class Account {
    UUID id
    Map<String, Days> deliveryCalendar
    LocalTime cutoffTime
    String opCo
    String customerNumber
    String customerName
}
