package com.sysco.uomdesktop.api

import static com.sysco.uomdesktop.support.mothers.ProductListMother.aProductListImportRequest
import static javax.ws.rs.core.Response.Status.CREATED
import static javax.ws.rs.core.Response.Status.OK
import static org.hamcrest.CoreMatchers.equalTo

class ImportProductListsFunctionalTest extends FunctionalTestBase {
    void 'imports product lists'() {
        when:
        String productListId = given()
            .body(aProductListImportRequest())
            .when()
            .post('/product-lists')
            .then()
            .statusCode(CREATED.statusCode)
            .extract()
            .path("productListCollection[0].id")

        then:
        given()
            .when()
            .get("/product-lists/${productListId}")
            .then()
            .statusCode(OK.statusCode)
            .body('id', equalTo(productListId))
    }
}
