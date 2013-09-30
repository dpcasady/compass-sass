GroovyClassLoader loader = new GroovyClassLoader(getClass().getClassLoader())
Class compassInvokerClass = loader.parseClass(
        new File(compassSassPluginDir, "src/groovy/grails/plugins/sass/CompassInvoker.groovy"))
Class javaProcessKillerClass = loader.parseClass(
        new File(compassSassPluginDir, "src/groovy/grails/plugins/sass/JavaProcessKiller.groovy"))

def configFile = new File(basedir, "grails-app/conf/GrassConfig.groovy")
if (!configFile.exists()) {
    configFile = new File(compassSassPluginDir, "grails-app/conf/DefaultGrassConfig.groovy")
}
compass = compassInvokerClass.newInstance(configFile, javaProcessKillerClass.newInstance())
