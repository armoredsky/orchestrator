package com.sysco.uomorchestrator.core.model

import com.sysco.uomorchestrator.core.validator.MustNotExceedMaximumPrice
import com.sysco.uomorchestrator.core.validator.MustNotExceedMaximumQuantity
import groovy.transform.EqualsAndHashCode
import org.joda.time.DateTime
import org.joda.time.LocalDate

@EqualsAndHashCode
class Order {
    UUID id
    String referenceNumber
    String confirmationNumber
    OrderStatus status
    DateTime submitDate
    LocalDate deliveryDate
    UUID accountId

    @MustNotExceedMaximumPrice(message = "TOTAL_PRICE_EXCEEDED_MAX")
    @MustNotExceedMaximumQuantity(message = "TOTAL_QUANTITY_EXCEEDED_MAX")
    List<LineItem> lineItems

    Order withLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems
        this
    }
}
