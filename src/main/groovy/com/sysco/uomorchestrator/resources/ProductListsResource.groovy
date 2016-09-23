package com.sysco.uomorchestrator.resources

import com.google.inject.Inject
import com.sysco.uomdesktop.core.service.ProductListService
import com.sysco.uomorchestrator.api.product.ProductListsRepresentation
import com.sysco.uomorchestrator.core.service.ProductListService

import javax.ws.rs.*
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@SuppressWarnings('DuplicateStringLiteral')
@Path('/product-lists')
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
class ProductListsResource {

    private final ProductListService productListService

    @Inject
    ProductListsResource(ProductListService productListService) {
        this.productListService = productListService
    }

    @POST
    def save(ProductListsRepresentation productListsResource) {
        ProductListsRepresentation productListResponse = productListService.save(productListsResource)
        return Response.created(new URI('/productLists')).entity(productListResponse).build()
    }

    @GET
    @Path("/{id}")
    def get(@PathParam('id') String id) {
        Response.ok()
            .entity(productListService.getById(UUID.fromString(id)))
            .build()
    }
}
