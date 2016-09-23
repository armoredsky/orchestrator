package com.sysco.uom.ftest

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class StartAppInBackground extends DefaultTask {

    String host
    String port
    String adminPort
    String databaseHost
    String database

    @TaskAction
    def startApp() {
        Process process = startAppProcess()
        project.extensions.extraProperties.properties.get('background').put('appProcess', process)

        new Thread(new LogReader(process.getInputStream())).start()

        waitForApplicationToStart()
    }

    private Process startAppProcess() {
        ProcessBuilder builder = new ProcessBuilder(
            'java', '-jar', 'build/libs/UomDesktopApplication.jar', 'server', './infrastructure/configuration.yml'
        )

        Map environment = builder.environment()
        environment.put('PORT', port)
        environment.put('ADMIN_PORT', adminPort)
        environment.put('DATABASE_HOST', databaseHost)
        environment.put('DATABASE', database)

        builder
            .directory(new File('.'))
            .redirectErrorStream(true)
            .start()
    }

    private void waitForApplicationToStart() {
        String healthcheckUrl = "${host}:${adminPort}/healthcheck"

        new ProcessBuilder(['bash', './bin/ensure_app_started.sh', healthcheckUrl])
            .directory(new File('.'))
            .start()
            .waitFor()
    }

    class LogReader implements Runnable {

        private final InputStream inputStream

        LogReader(InputStream inputStream) {
            this.inputStream = inputStream
        }

        @Override
        void run() {
            BufferedReader reader
            StringBuilder output = new StringBuilder()

            try {
                reader = new BufferedReader(new InputStreamReader(this.inputStream))

                String line
                while ((line = reader.readLine()) != null) {
                    output.append("\n").append(line)
                }
            }
            catch (IOException e) {
                println "Error while reading from background app output stream: ${e.getMessage()}"
            }
            finally {
                if (reader != null) {
                    reader.close()
                }

                println "\n\n************** Dumping background app's console and error streams **************"
                println output.toString()
                println "********************************* Done *****************************************\n\n"
            }
        }
    }
}
