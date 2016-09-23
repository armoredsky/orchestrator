package com.sysco.uomdesktop.client

import com.sysco.uomorchestrator.client.EndecaClient
import com.sysco.uomorchestrator.client.RESTEndecaClient
import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomorchestrator.core.model.Product
import groovyx.net.http.HttpResponseDecorator
import spock.lang.Specification

import static com.sysco.uomdesktop.support.mothers.AccountMother.anAccount

class EndecaClientTest extends Specification {

    void 'should get list of products'() {
        given:
        RESTEndecaClient restEndecaClient = Mock(RESTEndecaClient)
        EndecaClient endecaClient = new EndecaClient(restEndecaClient)
        Account account = anAccount(customerNumber: '00023')
        List<String> supcs = ['123', '456']
        HttpResponseDecorator endecaHttpResponse = Stub(HttpResponseDecorator) { getData() >> getSampleEndecaResponse(account, supcs) }

        when:
        List<Product> products = endecaClient.getProducts(account, supcs)

        then:
        1 * restEndecaClient.get([
            path : '/GService/json/browse',
            query: [N          : 0,
                    CustomerId : "${account.opCo}_${account.customerNumber}".toString(),
                    MaterialIds: supcs.join(','),
                    Opco       : "${account.opCo}".toString(),
                    Service    : true,
                    Nrpp       : 100]
        ]) >> endecaHttpResponse
        products.size() == 2
        products.sort() == [new Product(supc: '123', name: 'MUST STONE GRND SQZ BOTTLE', brand: 'INGFLER', pack: '6', size: '10Z'),
                            new Product(supc: '456', name: 'TRAY SRVING ELEGANT REFLECTION', brand: 'VOLLRTH', pack: '1', size: '16X11')].sort()
    }

    @SuppressWarnings('MethodSize')
    private static Map getSampleEndecaResponse(Account account, List supcs) {
        ["mainContent": [
            [
                "contents": [
                    [
                        "records": [
                            [
                                "attributes": [
                                    "isSoldAs_split"             : [
                                        "false"
                                    ],
                                    "isAvailable"                : [
                                        "false"
                                    ],
                                    "pBrand"                     : [
                                        "INGFLER"
                                    ],
                                    "category_label"             : [
                                        "MISC."
                                    ],
                                    "isCatchWeight"              : [
                                        "false"
                                    ],
                                    "flags"                      : [
                                        "Non-SyscoBranded"
                                    ],
                                    "description"                : [
                                        "Mustard Stone Ground Squeeze Bottle"
                                    ],
                                    "name"                       : [
                                        "MUST STONE GRND SQZ BOTTLE"
                                    ],
                                    "category_completeCategoryId": [
                                        "009002099"
                                    ],
                                    "type"                       : [
                                        "sysco"
                                    ],
                                    "replacementMaterialId"      : [
                                        "${supcs[0]}"
                                    ],
                                    "P_ID"                       : [
                                        "${account.opCo}_${supcs[0]}"
                                    ],
                                    "category_mainCategoryId"    : [
                                        "07"
                                    ],
                                    "opCo"                       : [
                                        "${account.opCo}"
                                    ],
                                    "split_max"                  : [
                                        "0"
                                    ],
                                    "storageFlag"                : [
                                        "D"
                                    ],
                                    "averageWeightPerCase"       : [
                                        "4.4500"
                                    ],
                                    "isSoldAs_case"              : [
                                        "true"
                                    ],
                                    "materialId"                 : [
                                        "${supcs[0]}"
                                    ],
                                    "packSize_pack"              : [
                                        "6"
                                    ],
                                    "canBuy"                     : [
                                        "false"
                                    ],
                                    "packSize_uom"               : [
                                        "CS or EA"
                                    ],
                                    "isOrderable"                : [
                                        "false"
                                    ],
                                    "split_min"                  : [
                                        "0"
                                    ],
                                    "packSize_size"              : [
                                        "10Z"
                                    ],
                                    "messages"                   : [
                                        "info:Demand status",
                                        "warning:Low on stock"
                                    ],
                                    "hasReplacement"             : [
                                        "false"
                                    ]
                                ]
                            ],
                            [
                                attributes: [
                                    isSoldAs_split             : [
                                        "false"
                                    ],
                                    isAvailable                : [
                                        "true"
                                    ],
                                    pBrand                     : [
                                        "VOLLRTH"
                                    ],
                                    category_label             : [
                                        "TRAYS/METAL"
                                    ],
                                    isCatchWeight              : [
                                        "false"
                                    ],
                                    flags                      : [
                                        "Non-SyscoBranded"
                                    ],
                                    description                : [
                                        "TRAY SERVING ELEGNT REFLC OVAL"
                                    ],
                                    category_completeCategoryId: [
                                        "014010078"
                                    ],
                                    type                       : [
                                        "sysco"
                                    ],
                                    replacementMaterialId      : [
                                        "${supcs[1]}"
                                    ],
                                    P_ID                       : [
                                        "${account.opCo}_${supcs[1]}"
                                    ],
                                    category_mainCategoryId    : [
                                        "10"
                                    ],
                                    opCo                       : [
                                        "${account.opCo}"
                                    ],
                                    split_max                  : [
                                        "0"
                                    ],
                                    storageFlag                : [
                                        "D"
                                    ],
                                    averageWeightPerCase       : [
                                        "2.3200"
                                    ],
                                    isSoldAs_case              : [
                                        "true"
                                    ],
                                    materialId                 : [
                                        "${supcs[1]}"
                                    ],
                                    packSize_pack              : [
                                        "1"
                                    ],
                                    canBuy                     : [
                                        "false"
                                    ],
                                    packSize_uom               : [
                                        "CS or EA"
                                    ],
                                    isOrderable                : [
                                        "false"
                                    ],
                                    split_min                  : [
                                        "0"
                                    ],
                                    packSize_size              : [
                                        "16X11"
                                    ],
                                    name                       : [
                                        "TRAY SRVING ELEGANT REFLECTION"
                                    ],
                                    messages                   : [
                                        "info:Non-stock"
                                    ],
                                    hasReplacement             : [
                                        "false"
                                    ]
                                ]
                            ]
                        ]
                    ]
                ]
            ]
        ]
        ]
    }
}
