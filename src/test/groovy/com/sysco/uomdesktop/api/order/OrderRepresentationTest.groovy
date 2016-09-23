package com.sysco.uomdesktop.api.order

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import org.joda.time.DateTime
import org.joda.time.LocalDate
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder
import static org.apache.commons.lang.RandomStringUtils.randomNumeric

class OrderRepresentationTest extends Specification {

    void 'builds an OrderRepresentation from an Order' () {
        given:
        UUID id = UUID.randomUUID()
        LocalDate deliveryDate = new LocalDate(2016, 8, 9)
        DateTime submitDate = DateTime.now()
        String referenceNumber = randomNumeric(7)
        String confirmationNumber = randomNumeric(8)
        UUID accountId = UUID.randomUUID()

        and:
        Order order = anOrder(
            id: id,
            status: OrderStatus.CONFIRMED,
            referenceNumber: referenceNumber,
            confirmationNumber: confirmationNumber,
            submitDate: submitDate,
            deliveryDate: deliveryDate,
            accountId: accountId
        )

        when:
        OrderRepresentation orderRepresentation = new OrderRepresentation(order)

        then:
        orderRepresentation == new OrderRepresentation(
            id: id,
            referenceNumber: referenceNumber,
            confirmationNumber: confirmationNumber,
            status: OrderStatus.CONFIRMED,
            submitDate: submitDate,
            deliveryDate: '2016-08-09',
            lineItems: order.lineItems.collect{ new LineItemRepresentation(it) },
            accountId: accountId
        )
    }

    void 'give an OrderRepresentation an empty lineItems when it was null' () {
        given:
        Order order = anOrder(lineItems: null)

        when:
        OrderRepresentation orderRepresentation = new OrderRepresentation(order)

        then:
        orderRepresentation.lineItems == []
    }
}
