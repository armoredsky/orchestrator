package com.sysco.uomorchestrator.client

import com.google.inject.Inject
import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomorchestrator.core.model.Account
import com.sysco.uomorchestrator.core.model.Product
import groovyx.net.http.RESTClient

class EndecaClient {

    RESTClient restClient

    @Inject
    EndecaClient(RESTEndecaClient restEndecaClient) {
        this.restClient = restEndecaClient
    }

    List<Product> getProducts(Account account, List<String> supcs) {
        def response = restClient.get([
            path : '/GService/json/browse',
            query: [N          : 0,
                    CustomerId : "${account.opCo}_${account.customerNumber}".toString(),
                    MaterialIds: supcs.join(','),
                    Opco       : "${account.opCo}".toString(),
                    Service    : true,
                    Nrpp       : 100]
        ])
        response.getData().mainContent[0].contents[0].records.attributes.collect { attribute ->
            String supc = attribute.materialId.first()
            String name = attribute.name.first()
            String brand = attribute.pBrand.first()
            String pack = attribute.packSize_pack.first()
            String size = attribute.packSize_size.first()
            new Product(supc: supc, name: name, brand: brand, pack: pack, size: size)
        }
    }
}
