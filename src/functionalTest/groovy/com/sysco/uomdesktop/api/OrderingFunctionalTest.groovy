package com.sysco.uomdesktop.api

import com.sysco.uomorchestrator.api.order.LineItemRepresentation
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomdesktop.support.TestOnlyAccountDao
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse

import static com.sysco.uomdesktop.support.DbSupport.DB
import static com.sysco.uomdesktop.support.mothers.IIBOrderConfirmationMother.iibOrderConfirmation
import static com.sysco.uomdesktop.support.mothers.ProductListMother.aProductListImportRequest
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProductImportRequest
import static javax.ws.rs.core.Response.Status.*
import static org.hamcrest.CoreMatchers.*

class OrderingFunctionalTest extends FunctionalTestBase {
    private static final String CUSTOMER_NUMBER = '006528'

    void 'places an order'() {
        given:
        generateIIBStub('iib_success_response.json')

        and:
        anAccountExists(CUSTOMER_NUMBER)

        // TODO: This does not associate the product list with the account, but seems like it does not have to...
        Map product = importAProductList()

        when:
        def (String orderId, String referenceNumber) = loadAccountAndOpenAnOrder(CUSTOMER_NUMBER)

        then:
        expectTheOrderToHaveStatus(orderId, OrderStatus.OPEN)

        when:
        addALineItemToTheOrder(orderId, product)

        and:
        submitTheOrder(orderId)

        then:
        expectTheOrderToHaveStatus(orderId, OrderStatus.SUBMITTED)

        when:
        confirmTheOrderFromIIB(referenceNumber)

        then:
        expectTheOrderToHaveStatus(orderId, OrderStatus.CONFIRMED)
    }

    private static UUID anAccountExists(String customerNumber) {
        TestOnlyAccountDao testOnlyAccountDao = DB.onDemand(TestOnlyAccountDao)
        testOnlyAccountDao.insert(customerNumber)
    }

    private static Map importAProductList() {
        Map product = aProductImportRequest()
        Map productLists = aProductListImportRequest(products: [product])

        String productId = given()
            .body(productLists)
            .when()
            .post('/product-lists')
            .then()
            .statusCode(CREATED.statusCode)
            .extract()
            .path("productListCollection[0].products[0].id")

        product + [id: productId]
    }

    private static String[] loadAccountAndOpenAnOrder(String customerNumber) {
        Response orderRepresentation = given()
            .when()
            .get("/uomlocal-gateway/accounts?customerNumber=${customerNumber}")
            .then()
            .statusCode(OK.statusCode)
            .body('account.id', notNullValue())
            .body('openOrder', notNullValue())
            .extract()
            .response()

        [orderRepresentation.path('openOrder.id'), orderRepresentation.path('openOrder.referenceNumber')]
    }

    private static LineItemRepresentation addALineItemToTheOrder(String orderId, Map product) {
        LineItemRepresentation lineItem = new LineItemRepresentation(
            [product: product, quantity: 3, price: 9.99]
        )

        given()
            .body([lineItem])
            .when()
            .put("/orders/${orderId}/line-items")
            .then()
            .statusCode(OK.statusCode)
            .body("[0].product", equalTo(product))
            .body("[0].quantity", equalTo(lineItem.quantity))

        given()
            .when()
            .get("/orders?status=OPEN")
            .then()
            .statusCode(OK.statusCode)
            .body("[0].lineItems[0].product.id", equalTo(product.id))

        lineItem
    }

    private static void submitTheOrder(String orderId) {
        given()
            .when()
            .post("/orders/${orderId}/submit")
            .then()
            .statusCode(NO_CONTENT.statusCode)
    }

    private static void confirmTheOrderFromIIB(String referenceNumber) {
        Map orderConfirmation = iibOrderConfirmation(UOMOrderNo: referenceNumber)

        given()
            .body(orderConfirmation)
            .when()
            .post('/iib-gateway/confirm-order')
            .then()
            .statusCode(OK.statusCode)
    }

    private static ValidatableResponse expectTheOrderToHaveStatus(String orderId, OrderStatus status) {
        given()
            .when()
            .get("/orders?status=${status}")
            .then()
            .statusCode(OK.statusCode)
            .body('id', hasItem(orderId))
    }
}
