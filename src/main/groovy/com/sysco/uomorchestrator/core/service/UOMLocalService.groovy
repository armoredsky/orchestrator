package com.sysco.uomorchestrator.core.service

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.api.product.ProductListRepresentation
import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation
import com.sysco.uomorchestrator.core.model.ProductList

class UOMLocalService {
    private final AccountService accountService
    private final ProductListService productListService
    private final OrderService orderService

    @Inject
    UOMLocalService(AccountService accountService, ProductListService productListService, OrderService orderService) {
        this.orderService = orderService
        this.productListService = productListService
        this.accountService = accountService
    }

    UOMLocalAccountRepresentation loadAccount(String customerNumber) {
        AccountRepresentation account = accountService.getByCustomerNumber(customerNumber)
        List<ProductList> productLists = productListService.findAllByAccount(account.id)
        List<OrderRepresentation> orders = orderService.findAllByAccount(account.id)

        new UOMLocalAccountRepresentation(
            account: account,
            productLists: productLists.collect { productList -> new ProductListRepresentation(productList) },
            openOrder: orders.find { order -> order.orderIsOpen() } ?: orderService.open(account.id),
            pastOrders: orders.findAll { order -> !order.orderIsOpen() }
        )
    }
}
