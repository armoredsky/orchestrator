package com.sysco.uomorchestrator.core.service

import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation

class UOMLocalService {


//    UOMLocalAccountRepresentation loadAccount(String customerNumber) {
//        AccountRepresentation account = accountService.getByCustomerNumber(customerNumber)
//        List<ProductList> productLists = productListService.findAllByAccount(account.id)
//        List<OrderRepresentation> orders = orderService.findAllByAccount(account.id)
//
//        new UOMLocalAccountRepresentation(
//            account: account,
//            productLists: productLists.collect { productList -> new ProductListRepresentation(productList) },
//            openOrder: orders.find { order -> order.orderIsOpen() } ?: orderService.open(account.id),
//            pastOrders: orders.findAll { order -> !order.orderIsOpen() }
//        )
//    }

    UOMLocalAccountRepresentation loadAccount(String customerNumber) {
        //call UOM-OrderService LoadAccounts using OrderServiceClient
        //return UOMLocalAccountRepresentation
        null
    }
}
