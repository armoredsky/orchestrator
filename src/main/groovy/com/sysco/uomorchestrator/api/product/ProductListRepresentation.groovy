package com.sysco.uomorchestrator.api.product

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ProductListRepresentation {
    String id
    String name
    List<ProductRepresentation> products
}
