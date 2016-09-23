package com.sysco.uomdesktop.support.mothers

import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import org.joda.time.DateTime
import org.joda.time.DateTimeZone

import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItemRepresentation
import static java.util.UUID.randomUUID
import static org.apache.commons.lang.RandomStringUtils.randomNumeric

class OrderMother {
    private static defaultProperties() {
        [
            id                : randomUUID(),
            referenceNumber   : randomNumeric(7),
            confirmationNumber: null,
            status            : OrderStatus.OPEN,
            submitDate        : null,
            deliveryDate      : null,
            lineItems         : [aLineItem()],
            accountId         : randomUUID()
        ]
    }

    private static defaultRepresentationProperties() {
        defaultProperties() + [lineItems: [aLineItemRepresentation()]]
    }

    static Order anOrder(Map overrides = [:]) {
        new Order(defaultProperties() + overrides)
    }

    static Order anOpenOrder(Map overrides = [:]) {
        new Order(defaultProperties() + [status: OrderStatus.OPEN] + overrides)
    }

    static Order aSubmittedOrder(Map overrides = [:]) {
        DateTime submitDate = new DateTime().withZone(DateTimeZone.UTC)
        Map properties = [
            status            : OrderStatus.SUBMITTED,
            submitDate        : submitDate,
            deliveryDate      : submitDate.plusDays(2).toLocalDate()
        ]
        new Order(defaultProperties() + properties + overrides)
    }

    static Order aConfirmedOrder(Map overrides = [:]) {
        DateTime submitDate = new DateTime().withZone(DateTimeZone.UTC)
        Map properties = [
            status      : OrderStatus.CONFIRMED,
            submitDate  : submitDate,
            deliveryDate: submitDate.plusDays(2).toLocalDate(),
            confirmationNumber: randomNumeric(7)
        ]
        new Order(defaultProperties() + properties + overrides)
    }

    static OrderRepresentation anOrderRepresentation(Map overrides = [:]) {
        new OrderRepresentation(defaultRepresentationProperties() + overrides)
    }

}
