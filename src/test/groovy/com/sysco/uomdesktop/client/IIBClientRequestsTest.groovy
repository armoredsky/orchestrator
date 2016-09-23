package com.sysco.uomdesktop.client

import com.sysco.uomorchestrator.api.customer.AccountRepresentation
import com.sysco.uomorchestrator.client.IIBClientRequests
import com.sysco.uomorchestrator.core.model.LineItem
import com.sysco.uomorchestrator.core.model.Order
import com.sysco.uomorchestrator.util.ProvidesUuids
import org.joda.time.DateTime
import org.joda.time.DateTimeZone
import org.joda.time.LocalDate
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccountRepresentation
import static com.sysco.uomdesktop.support.mothers.LineItemMother.aLineItem
import static com.sysco.uomdesktop.support.mothers.OrderMother.anOrder
import static com.sysco.uomdesktop.support.mothers.ProductMother.aProduct

class IIBClientRequestsTest extends Specification {
    void 'builds a request for an order submission to IIB'() {
        given:
        UUID uuid = UUID.randomUUID()
        ProvidesUuids providesUuids = Mock(ProvidesUuids) { get() >> uuid }

        and:
        AccountRepresentation account = anAccountRepresentation()

        and:
        DateTime someDateTime = new DateTime(2016, 8, 15, 14, 0, 32, 456, DateTimeZone.UTC)
        LocalDate deliveryDate = new LocalDate(2016, 8, 16)

        and:
        LineItem lineItem1 = aLineItem(quantity: 10, product: aProduct(supc: '0001234'))
        LineItem lineItem2 = aLineItem(quantity: 20, product: aProduct(supc: '0005678'))
        Order order = anOrder(referenceNumber: '5555555', submitDate: null, lineItems: [lineItem1, lineItem2], accountId: account.id)

        when:
        IIBClientRequests iIBClientRequests = new IIBClientRequests(providesUuids)
        Map request = iIBClientRequests.submitOrder(order, someDateTime, deliveryDate, account)

        then:
        request == [
            Orders: [
                Order: [
                    [
                        OrderHeader: [
                            OpcoNo        : account.opCo,
                            CustomerNo    : account.customerNumber,
                            UOMOrderNo    : 5555555,
                            OrderSuffix   : '01',
                            OrderType     : 0,
                            OrderSourceNo : 'W',
                            OrderDate     : 20160815,
                            OrderTime     : 140032,
                            BatchDate     : 20160815,
                            BatchTime     : 140032,
                            NumberOfLines : 2,
                            DeliveryDate  : 20160816,
                            BatchNo       : 5555555,
                            TransmitID    : 'W',
                            OrderQuantity : 2,
                            ERPReferenceNo: 87654320
                        ],
                        OrderLines: [
                            LineItem: [
                                [
                                    LineNo: 1,
                                    SUPC: '0001234',
                                    Quantity: 10,
                                    Price: '9.99',
                                    QuantityDemand: 10,
                                    SpecialPrice: 'N',
                                    IsSplit: false,
                                    IsSubstitutionItem: false,
                                    IsAllocationOverriden: false,
                                    IsPartialFill: false,
                                    IsShipIfAvailable: false,
                                    IsAutoSubstitution: false
                                ],
                                [
                                    LineNo: 2,
                                    SUPC: '0005678',
                                    Quantity: 20,
                                    Price: '9.99',
                                    QuantityDemand: 20,
                                    SpecialPrice: 'N',
                                    IsSplit: false,
                                    IsSubstitutionItem: false,
                                    IsAllocationOverriden: false,
                                    IsPartialFill: false,
                                    IsShipIfAvailable: false,
                                    IsAutoSubstitution: false
                                ]
                            ]
                        ],
                    ]
                ],
                RequestTrackerId: "UOM-DT-${uuid}"
            ]
        ]
    }
}
