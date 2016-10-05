package com.sysco.uomorchestrator

import io.dropwizard.Configuration
import io.dropwizard.db.DataSourceFactory
import org.hibernate.validator.constraints.NotEmpty

import javax.validation.constraints.NotNull

class UomOrchestratorConfiguration extends Configuration {
    @NotEmpty
    String orderServiceUrl
}
