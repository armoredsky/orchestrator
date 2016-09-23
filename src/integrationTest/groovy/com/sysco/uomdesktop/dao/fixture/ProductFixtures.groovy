package com.sysco.uomdesktop.dao.fixture

import com.sysco.uomorchestrator.core.model.Product

class ProductFixtures {

    public static final Map<String,String> product1Values = [
        name: 'Apple',
        supc: "1234567",
        brand: "Foo Brand",
        pack: '12',
        size: '32 oz'
    ]

    public static final Map<String,String> product2Values = [
        name: 'Orange',
        supc: "7654321",
        brand: "Bar Brand",
        pack: '24',
        size: '64 oz'
    ]

    public static final List products = [new Product(product1Values), new Product(product2Values)]

    public static final productListValues = [
        name: 'Apples and Oranges',
        products: products
    ]
}
