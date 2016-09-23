package com.sysco.uomdesktop.service

import com.sysco.uomorchestrator.api.order.OrderRepresentation
import com.sysco.uomorchestrator.api.product.ProductListRepresentation
import com.sysco.uomorchestrator.api.uomlocal.UOMLocalAccountRepresentation
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomorchestrator.core.model.ProductList
import com.sysco.uomorchestrator.core.service.AccountService
import com.sysco.uomorchestrator.core.service.CalculatesDeliveryDates
import com.sysco.uomorchestrator.core.service.GeneratesOrderReferenceNumbers
import com.sysco.uomorchestrator.core.service.OrderService
import com.sysco.uomorchestrator.core.service.ProductListService
import com.sysco.uomorchestrator.core.service.UOMLocalService
import com.sysco.uomdesktop.dao.support.TestOnlyAccountDao
import com.sysco.uomdesktop.dao.support.TestOnlyOrderDao
import com.sysco.uomdesktop.dao.support.TestOnlyProductListDao
import com.sysco.uomorchestrator.repository.dao.AccountDao
import com.sysco.uomorchestrator.repository.dao.OrderDao
import com.sysco.uomorchestrator.repository.dao.ProductListDao
import com.sysco.uomdesktop.support.IntegrationTestBase
import com.sysco.uomdesktop.support.mothers.AccountMother
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import org.joda.time.LocalTime

import java.sql.Time

import static com.sysco.uomdesktop.support.mothers.OrderMother.aConfirmedOrder
import static com.sysco.uomdesktop.support.mothers.OrderMother.aSubmittedOrder
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOpenOrder
import static com.sysco.uomdesktop.support.mothers.ProductListMother.aProductList

class UOMLocalServiceTest extends IntegrationTestBase {
    UOMLocalService uomLocalService

    AccountService accountService
    ProductListService productListService

    CalculatesDeliveryDates stubCalculatesDeliveryDates
    GeneratesOrderReferenceNumbers generatesOrderReferenceNumbers

    TestOnlyAccountDao testOnlyAccountDao
    TestOnlyProductListDao testOnlyProductListDao
    TestOnlyOrderDao testOnlyOrderDao

    final String customerNumber = '1234567'
    final String customerName = 'customer name'
    final UUID accountId = UUID.randomUUID()

    void setup() {
        generatesOrderReferenceNumbers = Mock(GeneratesOrderReferenceNumbers)
        stubCalculatesDeliveryDates = Stub(CalculatesDeliveryDates)
        testOnlyAccountDao = dbi.onDemand(TestOnlyAccountDao)
        testOnlyProductListDao = dbi.onDemand(TestOnlyProductListDao)
        testOnlyOrderDao = dbi.onDemand(TestOnlyOrderDao)

        accountService = new AccountService(dbi.onDemand(AccountDao), stubCalculatesDeliveryDates)
        productListService = new ProductListService(dbi.onDemand(ProductListDao))
        OrderService orderService = new OrderService(dbi.onDemand(OrderDao), null, null, generatesOrderReferenceNumbers, null)

        uomLocalService = new UOMLocalService(accountService, productListService, orderService)
        testOnlyAccountDao.insertFull(accountId, '{"Monday": 0}', Time.valueOf('12:00:00'), '010', customerNumber, customerName)
    }

    void '#loadAccount: fetches an account by customer number'() {
        given:
        stubCalculatesDeliveryDates.nextAvailable(_ as Map, _ as LocalTime, _ as DateTimeZone) >> new LocalDate(2016, 9, 14)

        and:
        testOnlyOrderDao.insertOrder(anOpenOrder(accountId: accountId))

        when:
        UOMLocalAccountRepresentation account = uomLocalService.loadAccount(customerNumber)

        then:
        account.account == AccountMother.anAccountRepresentation(
            id: accountId,
            nextAvailableDeliveryDate: '2016-09-14',
            opCo: '010',
            customerNumber: customerNumber,
            customerName: customerName
        )
    }

    void "#loadAccount: fetches an account's product lists"() {
        given:
        ProductList productList = aProductList(accountId: accountId)
        ProductListRepresentation expectedProductList = new ProductListRepresentation(productList)

        and:
        testOnlyOrderDao.insertOrder(anOpenOrder(accountId: accountId))

        and:
        testOnlyProductListDao.insert(productList)

        when:
        UOMLocalAccountRepresentation account = uomLocalService.loadAccount(customerNumber)

        then:
        account.productLists.size() == 1
        account.productLists[0].id == expectedProductList.id
        account.productLists[0].name == expectedProductList.name
        account.productLists[0].products.size() == expectedProductList.products.size()
        account.productLists[0].products.containsAll(expectedProductList.products)
    }

    void "#loadAccount: fetches an account's open order"() {
        given:
        Order order = anOpenOrder(accountId: accountId, lineItems: [])
        testOnlyOrderDao.insertOrder(order)

        when:
        UOMLocalAccountRepresentation account = uomLocalService.loadAccount(customerNumber)

        then:
        account.openOrder == new OrderRepresentation(order)
    }

    void "#loadAccount: creates an open order if one does not exist"() {
        given:
        Order expectedOpenOrder = anOpenOrder(accountId: accountId)
        generatesOrderReferenceNumbers.next() >> expectedOpenOrder.referenceNumber

        when:
        UOMLocalAccountRepresentation account = uomLocalService.loadAccount(customerNumber)

        then:
        account.openOrder.id != null
        account.openOrder.accountId == accountId
        account.openOrder.status == OrderStatus.OPEN.name()
    }

    void '#loadAccount: gets all past orders for an account'() {
        given:
        Order submittedOrder = aSubmittedOrder(accountId: accountId)
        testOnlyOrderDao.insertOrder(submittedOrder)
        testOnlyProductListDao.insertProducts(submittedOrder.lineItems.product)
        testOnlyOrderDao.insertLineItem(submittedOrder.lineItems[0], submittedOrder.id, submittedOrder.lineItems[0].product.id)
        OrderRepresentation expectedSubmittedOrder = new OrderRepresentation(submittedOrder)

        and:
        Order confirmedOrder = aConfirmedOrder(accountId: accountId, lineItems: [])
        testOnlyOrderDao.insertOrder(confirmedOrder)
        OrderRepresentation expectedConfirmedOrder = new OrderRepresentation(confirmedOrder)

        when:
        UOMLocalAccountRepresentation account = uomLocalService.loadAccount(customerNumber)

        then:
        account.pastOrders.size() == 2
        account.pastOrders.containsAll(expectedSubmittedOrder, expectedConfirmedOrder)
    }
}
