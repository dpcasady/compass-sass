grails.project.work.dir = 'target'

grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    plugins {
        build ":tomcat:$grailsVersion", ':release:2.2.1', ':rest-client-builder:1.0.3', {
            export = false
        }

        runtime ':resources:1.2'
    }
}
