package com.sysco.uomorchestrator

import com.google.inject.AbstractModule
import com.google.inject.Provides
import groovyx.net.http.RESTClient
import io.dropwizard.Configuration
import io.dropwizard.setup.Environment

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
        new RESTClient(configuration.orderServiceUrl)
    }
}
