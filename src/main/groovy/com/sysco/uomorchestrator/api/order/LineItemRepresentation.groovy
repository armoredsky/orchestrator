package com.sysco.uomorchestrator.api.order

import com.sysco.uomorchestrator.api.common.ProductRepresentation
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class LineItemRepresentation {
    UUID id
    int quantity
    String price
    ProductRepresentation product

}
