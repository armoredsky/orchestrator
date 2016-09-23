# UOM Orchestrator

A Backend for Frontend service

[Build Pipeline] **add me**
[Sonar] **Add Me**

Environments:
- [QA] **Add Me**

## Dependencies
These versions are known to work

- java v1.8.0_91
- Docker version v1.11.2

## Project set up

1. Clone repository
```
$ mkdir <project location>
$ cd <project location>
$ git clone git@gitlab.com:uom-desktop/uom-orchestrator.git
```

2. Pull down dependencies
```
$ ./gradlew dependencies
```

## Running Locally

1. Start api
```
$ ./gradlew runLocal
$ ## OR
$ # ./gradlew runLocalWithMountebank (for running against Mountebank stubs)
```

## Modifiable Environment Variables
These environment variables are set to reasonable defaults, but you may override them.
- PORT (default: 8880)
- ADMIN_PORT (default: 8881)

## Logging
You can find application logs at logs/app.log
You can find access logs at logs/access.log
