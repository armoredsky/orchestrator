package com.sysco.uomorchestrator.client

import com.google.inject.Inject
import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.util.ProvidesUuids
import org.joda.time.DateTime
import org.joda.time.LocalDate

class IIBClientRequests {
    private static final String DATE_FORMAT = 'yyyyMMdd'
    private static final String TIME_FORMAT = 'HHmmss'

    private final ProvidesUuids providesUuids

    @Inject
    IIBClientRequests(ProvidesUuids providesUuids) {
        this.providesUuids = providesUuids
    }

    Map submitOrder(Order order, DateTime submitDate, LocalDate deliveryDate, AccountRepresentation account) {
        long orderDate = formatDatetime(submitDate, DATE_FORMAT)
        long orderTime = formatDatetime(submitDate, TIME_FORMAT)
        long orderDeliveryDate = deliveryDate.toString(DATE_FORMAT) as long

        [
            Orders: [
                Order: [
                    [
                        OrderHeader: [
                            OpcoNo        : account.opCo,
                            CustomerNo    : account.customerNumber,
                            UOMOrderNo    : order.referenceNumber as long,
                            OrderSuffix   : '01',
                            OrderType     : 0,
                            OrderSourceNo : 'W',
                            OrderDate     : orderDate,
                            OrderTime     : orderTime,
                            BatchDate     : orderDate,
                            BatchTime     : orderTime,
                            NumberOfLines : order.lineItems.size(),
                            DeliveryDate  : orderDeliveryDate,
                            BatchNo       : order.referenceNumber as long,
                            TransmitID    : 'W',
                            OrderQuantity : order.lineItems.size(),
                            ERPReferenceNo: 87654320
                        ],
                        OrderLines: [
                            LineItem: order.lineItems.withIndex(1).collect { lineItem, index -> orderLineItem(lineItem, index) }
                        ]
                    ]
                ],
                RequestTrackerId: "UOM-DT-${providesUuids.get()}"
            ]
        ]
    }

    static Map orderLineItem(LineItem lineItem, int lineNumber) {
        [
            LineNo: lineNumber,
            SUPC: lineItem.product.supc,
            Quantity: lineItem.quantity,
            Price: '9.99',
            QuantityDemand: lineItem.quantity,
            SpecialPrice: 'N',
            IsSplit: false,
            IsSubstitutionItem: false,
            IsAllocationOverriden: false,
            IsPartialFill: false,
            IsShipIfAvailable: false,
            IsAutoSubstitution: false
        ]
    }

    static long formatDatetime(DateTime dateTime, String format) {
        dateTime.toString(format) as long
    }
}
