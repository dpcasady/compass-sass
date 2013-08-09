includeTargets << new File("${compassSassPluginDir}/scripts/_GetCompassInvoker.groovy")

eventConfigureTomcat = {
    compass.killCompass()
    compass.execute()
}

