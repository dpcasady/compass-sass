class CompassSassGrailsPlugin {
    def version = "0.7"
    def grailsVersion = "1.3.7 > *"
    def dependsOn = [resources: '1.0.2 > *']
    def pluginExcludes = [
            'grails-app/views/*.gsp',
            'web-app/**/*',
            'src/web-app/**/*',
            '**/MyAppResources.groovy',
            'src/stylesheets/**/*'
    ]

    def title = "Compass Framework - SASS/SCSS support"
    def author = "Danny Casady"
    def authorEmail = "dpcasady@gmail.com"
    def description = "Compass, SASS and SCSS support for Grails. Automatically compiles .scss/.sass during run-app, and adds other framework functionality."
    def documentation = "http://grails.org/plugin/compass-sass"
    def issueManagement = [ system: "GitHub", url: "https://github.com/dpcasady/compass-sass/issues" ]
    def scm = [url: "https://github.com/dpcasady/compass-sass"]

    def license = "APACHE"
    def developers = [
            [name:'Joel Rosenberg'],
            [name:'Stefan Kendall']
    ]
}