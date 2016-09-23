package com.sysco.uomdesktop.support.mothers

import org.joda.time.LocalDate

import static org.apache.commons.lang.RandomStringUtils.randomNumeric

class IIBOrderConfirmationMother {
    static Map iibOrderConfirmation(Map overrides = [:]) {
        Map defaultProperties = [
            UOMOrderNo: randomNumeric(7),
            ERPOrderNumber: randomNumeric(8),
            DeliveryDate: LocalDate.now().toString("YYMMdd")
        ]

        Map values = defaultProperties + overrides

        [
            OrderResponse: [
                OrderHeader: [
                    UOMOrderNo: values.UOMOrderNo,
                    ERPOrderNumber: values.ERPOrderNumber,
                    DeliveryDate: values.DeliveryDate
                ]
            ]
        ]
    }
}
