package com.sysco.uomdesktop.core.service

import com.sysco.uomorchestrator.core.service.GeneratesOrderReferenceNumbers
import spock.lang.Specification

class GeneratesOrderReferenceNumbersTest extends Specification {

    Random random
    GeneratesOrderReferenceNumbers generatesOrderReferenceNumbers

    void setup() {
        random = Mock(Random)

        generatesOrderReferenceNumbers = new GeneratesOrderReferenceNumbers(random)
    }

    void 'generates a random number within the allowable range'() {
        given:
        random.nextInt(10_000_000) >> 1234567

        when:
        String referenceNumber = generatesOrderReferenceNumbers.next()

        then:
        referenceNumber == '1234567'
    }

    void 'left pads numbers to ensure 7 digits'() {
        given:
        random.nextInt(10_000_000) >> 56

        when:
        String referenceNumber = generatesOrderReferenceNumbers.next()

        then:
        referenceNumber == '0000056'
    }
}
