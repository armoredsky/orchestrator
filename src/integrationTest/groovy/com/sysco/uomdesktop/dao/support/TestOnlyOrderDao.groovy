package com.sysco.uomdesktop.dao.support

import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.repository.jdbi.JodaDateTimeArgumentFactory
import com.sysco.uomorchestrator.repository.jdbi.JodaLocalDateArgumentFactory
import org.skife.jdbi.v2.sqlobject.Bind
import org.skife.jdbi.v2.sqlobject.BindBean
import org.skife.jdbi.v2.sqlobject.SqlUpdate
import org.skife.jdbi.v2.sqlobject.customizers.RegisterArgumentFactory

@RegisterArgumentFactory([JodaDateTimeArgumentFactory, JodaLocalDateArgumentFactory])
interface TestOnlyOrderDao {
    @SqlUpdate("""
        insert into uom.order (id, status, submit_date, reference_number, confirmation_number, delivery_date, account_id)
                       values (:order.id, :order.status, :order.submitDate, :order.referenceNumber, :order.confirmationNumber,
                               :order.deliveryDate, :order.accountId)
    """)
    void insertOrder(@BindBean('order') Order order)

    @SqlUpdate('insert into uom.order_line_item (id, order_id, product_id, quantity, price)  \
               values (:lineItem.id, :orderId, :productId, :lineItem.quantity, :lineItem.price)')
    void insertLineItem(@BindBean('lineItem') LineItem lineItem, @Bind('orderId') UUID orderId, @Bind('productId') UUID productId)
}
