def createGrassConfigFile() {
    println "Creating GrassConfig.groovy if not already present"
    ant.copy(
            tofile: "${basedir}/grails-app/conf/GrassConfig.groovy", overwrite: false,
            file: "${compassSassPluginDir}/grails-app/conf/DefaultGrassConfig.groovy")
}
createGrassConfigFile()

includeTargets << new File(compassSassPluginDir, "scripts/_CompassGems.groovy")
includeTargets << new File(compassSassPluginDir, "scripts/_GetCompassInvoker.groovy")
includeTargets << new File(compassSassPluginDir, "scripts/_GemCommands.groovy")

boolean isJRubyInstalled() {
    try {
        println "JRuby version: ${getOutputFromCommand('jruby --version')}"
    }
    catch (Exception e) {
        return false
    }

    return true
}

println "Testing to see if JRuby is installed..."
if (!isJRubyInstalled()) {
    println '*' * 20
    println "JRuby could not be found on your system. Make sure it is on your path, or this plugin will not function properly"
    println '*' * 20
    return
}

compassGems.each { gem ->
    println "Testing to see if ${gem} gem is installed..."
    if (!isGemInstalled(gem)) {
        println "${gem} gem not found; attempting to install automatically..."
        installGem(gem)
    }
}
