package com.sysco.uomdesktop.support.mothers

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import com.sysco.uomorchestrator.core.model.Product

class ProductMother {
    static Product aProduct(Map overrides = [:]) {
        Map defaultProperties = [
            id: UUID.randomUUID(),
            name: 'Broccoli Florets',
            supc: '4361432',
            brand: 'Sysco Veggies',
            pack: '25',
            size: '5 lbs'
        ]

        new Product(defaultProperties + overrides)
    }

    static ProductRepresentation aProductRepresentation(Map overrides = [:]) {
        Map defaultProperties = [
            id: UUID.randomUUID().toString(),
            name: 'Broccoli Florets',
            supc: '4361432',
            brand: 'Sysco Veggies',
            pack: '25',
            size: '5 lbs'
        ]

        new ProductRepresentation(defaultProperties + overrides)
    }

    static Map aProductImportRequest(Map overrides = [:]) {
        Map defaultProperties = [
            name: 'Cheerios',
            supc: '0003456',
            brand: 'Kelloggs',
            pack: '4 lb',
            size: '12'
        ]

        defaultProperties + overrides
    }
}
