includeTargets << grailsScript("_GrailsInit")
includeTargets << new File(compassSassPluginDir, "scripts/_CompassGems.groovy")
includeTargets << new File(compassSassPluginDir, "scripts/_GetCompassInvoker.groovy")
includeTargets << new File(compassSassPluginDir, "scripts/_GemCommands.groovy")

target(updateCompass: 'Update compass and its required gems') {
    println "Ensuring Compass installed"
    compassGems.each {
        if (!isGemInstalled(it)) {
            installGem(it)
        }
    }

    println "Updating Compass plugins"
    compassGems.each {
        updateGem(it)
    }
}

setDefaultTarget(updateCompass)
