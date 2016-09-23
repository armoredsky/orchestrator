package com.sysco.uomdesktop.repository.mapper

import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.core.model.OrderStatus
import com.sysco.uomorchestrator.repository.mapper.OrderMapper
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import spock.lang.Specification

import java.sql.Date
import java.sql.ResultSet
import java.sql.Timestamp

import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder

class OrderMapperTest extends Specification {
    OrderMapper orderMapper
    ResultSet mockResultSet

    void setup() {
        orderMapper = new OrderMapper()

        mockResultSet = Mock(ResultSet)
        mockResultSet.getString('status') >> 'OPEN'
    }

    void 'should create an order from a result set'() {
        given:
        UUID orderId = UUID.randomUUID()
        UUID accountId = UUID.randomUUID()

        and:
        mockResultSet.getObject('id', UUID) >> orderId
        mockResultSet.getObject('account_id', UUID) >> accountId
        mockResultSet.getString('status') >> 'OPEN'
        mockResultSet.getTimestamp('submit_date') >> new Timestamp(1471547117179)
        mockResultSet.getString('reference_number') >> '9999999'
        mockResultSet.getString('confirmation_number') >> '00001111'
        mockResultSet.getDate('delivery_date') >> new Date(1491547117479)

        when:
        Order order = orderMapper.map(0, mockResultSet, null)

        then:
        order == anOrder(
            id: orderId,
            accountId: accountId,
            status: OrderStatus.OPEN,
            submitDate: new DateTime(2016, 8, 18, 19, 5, 17, 179, DateTimeZone.UTC),
            referenceNumber: '9999999',
            confirmationNumber: '00001111',
            lineItems: null,
            deliveryDate: new LocalDate(2017, 4, 7)
        )
    }

    void 'should create an order with no submitted date from a result set'() {
        given:
        mockResultSet.getString('submit_date') >> null

        when:
        Order order = orderMapper.map(0, mockResultSet, null)

        then:
        order.submitDate == null
    }

    void 'should create an order with no delivery date from a result set'() {
        given:
        mockResultSet.getDate('delivery_date') >> null

        when:
        Order order = orderMapper.map(0, mockResultSet, null)

        then:
        order.deliveryDate == null
    }
}
