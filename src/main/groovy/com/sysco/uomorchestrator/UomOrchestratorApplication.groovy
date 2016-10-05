package com.sysco.uomorchestrator

import com.google.inject.Guice
import com.google.inject.Injector


import com.sysco.uomorchestrator.resources.UOMLocalGatewayResource
import io.dropwizard.Application
import io.dropwizard.configuration.EnvironmentVariableSubstitutor
import io.dropwizard.configuration.SubstitutingSourceProvider
import io.dropwizard.setup.Bootstrap
import io.dropwizard.setup.Environment
import org.eclipse.jetty.servlets.CrossOriginFilter

import javax.servlet.DispatcherType
import javax.servlet.FilterRegistration

class UomOrchestratorApplication extends Application<UomOrchestratorConfiguration> {

    static void main(String[] args) {
        new UomOrchestratorApplication().run(args)
    }

    @Override
    void initialize(Bootstrap<UomOrchestratorConfiguration> bootstrapConfiguration) {
        bootstrapConfiguration.setConfigurationSourceProvider(
            new SubstitutingSourceProvider(
                bootstrapConfiguration.getConfigurationSourceProvider(),
                new EnvironmentVariableSubstitutor(false))
        );
    }

    @Override
    void run(UomOrchestratorConfiguration configuration, Environment environment) throws Exception {
        Injector injector = Guice.createInjector(new UomOrchestratorGuiceModule(environment, configuration))

        environment.jersey().register(injector.getInstance(UOMLocalGatewayResource))

        FilterRegistration.Dynamic cors = environment.servlets().addFilter("CORS", CrossOriginFilter)

        cors.setInitParameter("allowedOrigins", "*")
        cors.setInitParameter("allowedHeaders", "X-Requested-With,Content-Type,Accept,Origin")
        cors.setInitParameter("allowedMethods", "OPTIONS,GET,PUT,POST,DELETE,HEAD")

        cors.addMappingForUrlPatterns(EnumSet.allOf(DispatcherType), true, "/*")
    }
}
