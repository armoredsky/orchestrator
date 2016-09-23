package com.sysco.uomorchestrator

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import com.sysco.uomorchestrator.client.RESTEndecaClient
import com.sysco.uomorchestrator.repository.jdbi.JodaDateTimeArgumentFactory
import com.sysco.uomorchestrator.repository.jdbi.JodaLocalDateArgumentFactory
import com.sysco.uomorchestrator.core.service.GeneratesOrderReferenceNumbers
import com.sysco.uomorchestrator.repository.dao.AccountDao
import com.sysco.uomorchestrator.repository.dao.OrderDao
import com.sysco.uomorchestrator.repository.dao.ProductDao
import com.sysco.uomorchestrator.repository.dao.ProductListDao
import groovyx.net.http.RESTClient
import io.dropwizard.Configuration
import io.dropwizard.jdbi.DBIFactory
import io.dropwizard.setup.Environment
import org.skife.jdbi.v2.DBI

class UomOrchestratorGuiceModule extends AbstractModule {

    final Environment environment
    final UomOrchestratorConfiguration configuration

    UomOrchestratorGuiceModule(Environment environment, UomOrchestratorConfiguration configuration) {
        this.environment = environment
        this.configuration = configuration
    }

    @Override
    void configure() {
        bind(Configuration).toInstance(configuration)
        bind(Environment).toInstance(environment)
    }

    @Provides
    RESTClient prepareRESTClient() {
        new RESTClient(configuration.iibUrl)
    }

    @Provides
    RESTEndecaClient prepareRESTEndecaClient() {
        new RESTEndecaClient('http://localhost:8003')
    }
}
