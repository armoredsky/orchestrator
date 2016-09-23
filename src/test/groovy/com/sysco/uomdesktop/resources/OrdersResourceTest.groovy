package com.sysco.uomdesktop.resources

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.client.iiborder.response.IIBOrderSubmitValidationResult
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.service.OrderService
import com.sysco.uomorchestrator.exceptionmapper.IIBRequestFailedException
import com.sysco.uomorchestrator.exceptionmapper.IIBRequestFailedExceptionMapper
import com.sysco.uomorchestrator.resources.OrdersResource
import io.dropwizard.testing.junit.ResourceTestRule
import org.junit.Rule
import spock.lang.Specification

import javax.ws.rs.core.GenericType
import javax.ws.rs.core.Response

import static com.sysco.uomorchestrator.core.model.OrderStatus.OPEN
import static com.sysco.uomorchestrator.core.model.OrderStatus.SUBMITTED
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItemRepresentation
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrderRepresentation
import static java.util.UUID.randomUUID
import static javax.ws.rs.client.Entity.json

class OrdersResourceTest extends Specification {

    OrderService orderService = Mock(OrderService)

    @Rule
    ResourceTestRule resources = ResourceTestRule.builder()
        .addResource(new OrdersResource(orderService))
        .addProvider(IIBRequestFailedExceptionMapper)
        .build()

    void '#getByStatus: successfully gets orders by statuses'() {
        given:
        List<OrderRepresentation> orderRepresentations = [anOrderRepresentation(lineItems: [])]

        when:
        Response response = resources.client()
            .target("/orders?status=OPEN&status=SUBMITTED")
            .request()
            .get()

        then:
        1 * orderService.getOrdersByStatus([OPEN, SUBMITTED]) >> orderRepresentations

        response.getStatusInfo() == Response.Status.OK
        response.readEntity(new GenericType<List<OrderRepresentation>>(){}) == orderRepresentations
    }

    void '#submit: submits an order'() {
        given:
        UUID id = randomUUID()

        when:
        Response response = resources.client()
            .target("/orders/${id}/submit")
            .request()
            .post(null)

        then:
        1 * orderService.submit(id)
        response.getStatusInfo() == Response.Status.NO_CONTENT
    }

    void '#submit: is a bad request when the order fails to submit'() {
        given:
        UUID id = randomUUID()
        UUID accountId = randomUUID()

        when:
        Response response = resources.client()
            .target("/orders/${id}/submit?accountId=${accountId}")
            .request()
            .post(null)

        then:
        1 * orderService.submit(id) >> { throw new IIBRequestFailedException(Mock(IIBOrderSubmitValidationResult)) }
        response.getStatusInfo() == Response.Status.BAD_REQUEST
    }

    void '#update: successfully updates order line items'() {
        given:
        UUID orderId = randomUUID()
        List<LineItemRepresentation> lineItemRepresentations = [aLineItemRepresentation()]
        List<LineItem> lineItems = lineItemRepresentations.collect({ it -> new LineItem(it) })

        when:
        Response response = resources.client()
            .target("/orders/${orderId}/line-items")
            .request()
            .put(json(lineItemRepresentations))

        then:
        1 * orderService.updateLineItems(orderId, lineItems) >> lineItemRepresentations
        response.getStatusInfo() == Response.Status.OK
        response.readEntity(new GenericType<List<LineItemRepresentation>>(){}) == lineItemRepresentations
    }

    void '#delete: removes line item from order by product id'() {
        given:
        UUID orderId = randomUUID()
        UUID lineItemId = randomUUID()

        when:
        Response response = resources.client()
            .target("/orders/${orderId}/line-items/${lineItemId}")
            .request()
            .delete()

        then:
        1 * orderService.deleteLineItem(orderId, lineItemId)
        response.getStatusInfo() == Response.Status.NO_CONTENT
    }

}
