package com.sysco.uomdesktop.api

import com.sysco.uomorchestrator.core.model.Product
import com.sysco.uomdesktop.support.DbSupport
import groovy.json.StringEscapeUtils
import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.mbtest.javabank.Client
import org.mbtest.javabank.fluent.ImposterBuilder
import org.mbtest.javabank.http.imposters.Imposter
import spock.lang.Specification

class FunctionalTestBase extends Specification {
    private final static String BASE_STUB_PATH = './buildSrc/src/main/groovy/com/sysco/uom/mountebank/stubResponses'

    private final String mountebankUrl = 'http://localhost:2525'
    private final Client mountebankClient = new Client(mountebankUrl)
    private final int IIB_PORT = 8001

    private static final int ENDECA_PORT = 8003
    private static final List PRODUCTS = [
        new Product(supc: '0785127', name: 'OYSTER BRD HMSTY CHN', brand: 'BOUDRX', pack: '5', size: '2 LB'),
        new Product(supc: '1254069', name: 'POTATO RED #1 FRESH SZ A', brand: 'PACKER', pack: '1', size: '50 LB'),
        new Product(supc: '1283118', name: 'GREEN COLLARD FRESH', brand: 'PACKER', pack: '1', size: 'BUSHEL'),
        new Product(supc: '1358084', name: 'FLOUNDER FILET IQF 5 OZ', brand: 'PACKER', pack: '1', size: '10 LB'),
        new Product(supc: '1491810', name: 'CABBAGE GREEN FRSH CARTON MED', brand: 'PACKER', pack: '1', size: '50 LB'),
        new Product(supc: '1845197', name: 'PELLMAN CAKE LYR CARROT 9IN', brand: 'PELLMAN', pack: '4', size: '60 OZ'),
        new Product(supc: '1972041', name: 'PIE SWEET POTATO T&S 10"', brand: 'SYS CLS', pack: '6', size: '40 OZ'),
        new Product(supc: '2198802', name: 'YAM JUMBO FRESH', brand: 'PACKER', pack: '1', size: '40 LB'),
        new Product(supc: '2442416', name: 'PORK SPARE RIB 4.5/DN', brand: 'HATFLD', pack: '1', size: '30#AVG'),
        new Product(supc: '2795973', name: 'COBBLER PEACH RTB', brand: 'SYS CLS', pack: '4', size: '5.5LB')
    ]

    void setup() {
        DbSupport.truncateTables()

        RestAssured.port = Integer.valueOf(System.getenv('PORT'))
    }

    static RequestSpecification given() {
        RestAssured.given()
            .basePath('/api')
            .contentType(ContentType.JSON)
    }

    void generateIIBStub(String filename) {
        Imposter imposter = makeImposterOnPort(IIB_PORT, filename)
        mountebankClient.deleteImposter(IIB_PORT)
        mountebankClient.createImposter(imposter)
    }

    private static Imposter makeImposterOnPort(int port, String filename) {
        new ImposterBuilder()
            .onPort(port)
                .stub()
                .response()
                    .is()
                        .header("Content-Type", "application/json")
                        .body(new File("src/functionalTest/groovy/com/sysco/uomdesktop/api/stubResponses/${filename}"))
                        .end()
                    .end()
                .end()
            .build()
    }

    public static Imposter makeProductsImposter() {
        List<String> records = PRODUCTS.collect { Product data ->
            new File("${BASE_STUB_PATH}/endeca_product_record.template.json").text
                .replace("%SUPC%", StringEscapeUtils.escapeJava(data.supc))
                .replace("%NAME%", StringEscapeUtils.escapeJava(data.name))
                .replace("%BRAND%", StringEscapeUtils.escapeJava(data.brand))
                .replace("%PACK%", StringEscapeUtils.escapeJava(data.pack))
                .replace("%SIZE%", StringEscapeUtils.escapeJava(data.size))
        }

        String template = new File("${BASE_STUB_PATH}/endeca_get_products_success_response.template.json").text
        String body = template.replace("%RECORDS", records.toString())

        // @formatter:off
        new ImposterBuilder()
            .onPort(ENDECA_PORT)
                .stub()
                    .response()
                        .is()
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")
                        .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin,clientId")
                        .body(body)
                    .end()
                .end()
            .end()
        .build()
        // @formatter:on
    }
}
