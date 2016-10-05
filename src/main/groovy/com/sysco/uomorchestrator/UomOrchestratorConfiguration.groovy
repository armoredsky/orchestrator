package com.sysco.uomorchestrator

import io.dropwizard.Configuration
import org.hibernate.validator.constraints.NotEmpty

class UomOrchestratorConfiguration extends Configuration {
    @NotEmpty
    String orderServiceUrl
}
