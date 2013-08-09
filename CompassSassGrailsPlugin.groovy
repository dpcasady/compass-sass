class CompassSassGrailsPlugin {
    def version = "0.7"
    def grailsVersion = "2.0 > *"
    def pluginExcludes = [
            'grails-app/views/*.gsp',
            'web-app/**/*',
            'src/web-app/**/*',
            '**/MyAppResources.groovy',
            'src/stylesheets/**/*'
    ]

    def title = "Compass Framework - SASS/SCSS support"
    def description = "Compass, SASS and SCSS support for Grails. Automatically compiles .scss/.sass during run-app, and adds other framework functionality."
    def documentation = "http://grails.org/plugin/compass-sass"

    def license = "APACHE"
    def developers = [
        [name: 'Joel Rosenberg'],
        [name: 'Stefan Kendall'],
        [name: 'Danny Casady', email: 'dpcasady@gmail.com']
    ]

    def issueManagement = [ system: "GitHub", url: "https://github.com/dpcasady/compass-sass/issues" ]
    def scm = [url: "https://github.com/dpcasady/compass-sass"]
}