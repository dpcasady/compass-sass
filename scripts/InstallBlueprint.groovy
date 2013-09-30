includeTargets << grailsScript("_GrailsInit")
includeTargets << new File(compassSassPluginDir, "scripts/_GetCompassInvoker.groovy")

target(installBlueprint: 'Install blueprint stylesheets') {
    boolean compassInstallationSuccessful = true
    try {
        compass.installBlueprint()
    }
    catch (Exception e) {
        println e.message
        compassInstallationSuccessful = false
    }

    if (compassInstallationSuccessful) {
        println '''
Blueprint has been installed!

SASS/SCSS stylesheets are recompiled automatically when running 'grails run-app'.
To compile sass stylesheets manually use 'grails compile-css'.

To import your new stylesheets add the following lines of HTML (or equivalent) to your gsp:

<head>
  <link href="${resource(dir:\'css\',file:\'screen.css\')}" media="screen, projection" rel="stylesheet" type="text/css" />
  <link href="${resource(dir:\'css\',file:\'print.css\')}" media="print" rel="stylesheet" type="text/css" />
  <!--[if lt IE 8]>
    <link href="${resource(dir:\'css\',file:\'ie.css\')}" media="screen, projection" rel="stylesheet" type="text/css" />
  <![endif]-->
</head>
'''
    }
    else {
        System.err.println('ERROR: Blueprint could not be installed.')
    }
}

setDefaultTarget(installBlueprint)
