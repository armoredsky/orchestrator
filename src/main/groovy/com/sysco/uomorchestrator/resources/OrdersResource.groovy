package com.sysco.uomorchestrator.resources

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomorchestrator.core.service.OrderService

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path('/orders')
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class OrdersResource {

    private final OrderService orderService

    @Inject
    OrdersResource(OrderService orderService) {
        this.orderService = orderService
    }

    @GET
    def getByStatus(@QueryParam('status') List<OrderStatus> statuses) {
        List<OrderRepresentation> orders = orderService.getOrdersByStatus(statuses)
        Response
            .ok()
            .entity(orders)
            .build()
    }

    @POST
    def save(@QueryParam('accountId') UUID accountId) {
        OrderRepresentation createdOrderResource = orderService.open(accountId)
        Response
            .status(Response.Status.CREATED)
            .entity(createdOrderResource)
            .build()
    }

    @PUT
    @Path('/{id}/line-items')
    def update(List<LineItemRepresentation> lineItemRepresentations, @PathParam('id') UUID id) {
        List<LineItem> lineItems = lineItemRepresentations.collect { lineItemRep -> new LineItem(lineItemRep) }

        List<LineItemRepresentation> updatedLineItems = orderService.updateLineItems(id, lineItems)

        Response.status(Response.Status.OK).entity(updatedLineItems).build()
    }

    @DELETE
    @Path('/{id}/line-items/{lineItemId}')
    def delete(@PathParam('id') UUID id, @PathParam('lineItemId') UUID lineItemId) {
        orderService.deleteLineItem(id, lineItemId)

        Response.noContent().build()
    }

    @POST
    @Path('/{id}/submit')
    def submit(@PathParam('id') UUID id) {
        orderService.submit(id)

        Response.noContent().build()
    }
}

