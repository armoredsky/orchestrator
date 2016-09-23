package com.sysco.uomdesktop.support.mothers

import com.sysco.uomorchestrator.core.model.ProductList

import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProductImportRequest

class ProductListMother {
    static ProductList aProductList(Map overrides = [:]) {
        Map defaultProperties = [
            id: UUID.randomUUID(),
            name: 'My List',
            products: [aProduct(), aProduct()],
            accountId: UUID.randomUUID()
        ]
        new ProductList(defaultProperties + overrides)
    }

    static Map aProductListImportRequest(Map overrides = [:]) {
        Map defaultProperties = [
            products: [aProductImportRequest()]
        ]

        Map values = defaultProperties + overrides

        [
            productListCollection: [
                [
                    name: 'My List',
                    products: values.products
                ]
            ]
        ]
    }
}
