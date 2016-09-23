package com.sysco.uom.mountebank

import groovy.json.StringEscapeUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.mbtest.javabank.Client
import org.mbtest.javabank.fluent.ImposterBuilder
import org.mbtest.javabank.http.imposters.Imposter

class CreateImposters extends DefaultTask {
    private final static String BASE_STUB_PATH = './buildSrc/src/main/groovy/com/sysco/uom/mountebank/stubResponses'

    private final static String MOUNTEBANK_URL = 'http://localhost:2525'
    private final static Client MOUNTEBANK_CLIENT = new Client(MOUNTEBANK_URL)

    private static final Map<String, Integer> IMPOSTERS = [
        'iib'          : 8001,
        'cloud_pricing': 8002
    ]

    private static final int ENDECA_PORT = 8003
    private static final List PRODUCTS = [
        new ProductData(supc: '0785127', name: 'OYSTER BRD HMSTY CHN', brand: 'BOUDRX', pack: '5', size: '2 LB'),
        new ProductData(supc: '1254069', name: 'POTATO RED #1 FRESH SZ A', brand: 'PACKER', pack: '1', size: '50 LB'),
        new ProductData(supc: '1283118', name: 'GREEN COLLARD FRESH', brand: 'PACKER', pack: '1', size: 'BUSHEL'),
        new ProductData(supc: '1358084', name: 'FLOUNDER FILET IQF 5 OZ', brand: 'PACKER', pack: '1', size: '10 LB'),
        new ProductData(supc: '1491810', name: 'CABBAGE GREEN FRSH CARTON MED', brand: 'PACKER', pack: '1', size: '50 LB'),
        new ProductData(supc: '1845197', name: 'PELLMAN CAKE LYR CARROT 9IN', brand: 'PELLMAN', pack: '4', size: '60 OZ'),
        new ProductData(supc: '1972041', name: 'PIE SWEET POTATO T&S 10"', brand: 'SYS CLS', pack: '6', size: '40 OZ'),
        new ProductData(supc: '2198802', name: 'YAM JUMBO FRESH', brand: 'PACKER', pack: '1', size: '40 LB'),
        new ProductData(supc: '2442416', name: 'PORK SPARE RIB 4.5/DN', brand: 'HATFLD', pack: '1', size: '30#AVG'),
        new ProductData(supc: '2795973', name: 'COBBLER PEACH RTB', brand: 'SYS CLS', pack: '4', size: '5.5LB')
    ]

    @Input
    String response

    @TaskAction
    void generateStub() {
        IMPOSTERS.each { String service, Integer port ->
            MOUNTEBANK_CLIENT.deleteImposter(port)

            Imposter imposter = makeImposter(port, service)
            MOUNTEBANK_CLIENT.createImposter(imposter)
        }

        MOUNTEBANK_CLIENT.deleteImposter(ENDECA_PORT)
        Imposter imposter = makeProductsImposter()
        MOUNTEBANK_CLIENT.createImposter(imposter)
    }

    public Imposter makeImposter(int port, String service) {
        // @formatter:off
        new ImposterBuilder()
            .onPort(port)
                .stub()
                    .response()
                        .is()
                        .header("Content-Type", "application/json")
                        .header("Access-Control-Allow-Origin", "*")
                        .header("Access-Control-Allow-Methods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")
                        .header("Access-Control-Allow-Headers", "X-Requested-With,Content-Type,Accept,Origin,clientId")
                        .body(new File("${BASE_STUB_PATH}/${service}_${response.toLowerCase()}_response.json"))
                    .end()
                .end()
            .end()
        .build()
        // @formatter:on
    }

    public static Imposter makeProductsImposter() {
        List<String> records = PRODUCTS.collect { ProductData data ->
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
