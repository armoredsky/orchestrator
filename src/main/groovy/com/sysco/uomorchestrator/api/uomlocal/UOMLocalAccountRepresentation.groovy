package com.sysco.uomorchestrator.api.uomlocal

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.api.product.ProductListRepresentation
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class UOMLocalAccountRepresentation {
    AccountRepresentation account
    List<ProductListRepresentation> productLists
    List<OrderRepresentation> pastOrders
    OrderRepresentation openOrder
}
