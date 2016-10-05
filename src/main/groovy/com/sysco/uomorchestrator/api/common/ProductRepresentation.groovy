package com.sysco.uomorchestrator.api.common

import com.fasterxml.jackson.annotation.JsonInclude
import com.sysco.uomorchestrator.core.model.Product
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ProductRepresentation {
    String id
    String name
    String supc
    String brand
    String pack
    String size
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String price
}

