package com.sysco.uom.ftest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StopBackgroundApp extends DefaultTask {

    @TaskAction
    def stopApp() {
        Process process = project.extensions.extraProperties.properties.get('background').get('appProcess') as Process
        process.destroy()
    }
}
