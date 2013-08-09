package grails.plugins.sass

import grails.util.GrailsUtil

class CompassInvoker {
    def config
    def javaProcessKiller
    boolean forceRecompile = false

    public CompassInvoker(File grassConfigLocation, def javaProcessKiller) {
        this(new ConfigSlurper(GrailsUtil.environment).parse(grassConfigLocation.toURL()), javaProcessKiller)
    }

    public CompassInvoker(def config, def javaProcessKiller) {
        this.config = config
        this.javaProcessKiller = javaProcessKiller
    }

    public void compileSingleFile(File input, File output) {
        output.parentFile.mkdirs()

        def shells = [['sh', '-c'], ['cmd', '/c']]
        def changeDirCommand = "cd ${input.parent}"

        def shellArgs = ['jruby', '-S', 'compass', 'compile', input.name]
        shellArgs << '--sass-dir' << '.'
        shellArgs << '--css-dir' << output.parentFile.absolutePath
        shellArgs += getPreferenceArgs(null)

        def compassCompileCommand = shellArgs.join(' ')

        for (def shell in shells) {
            attemptExecutionInShell(shell, changeDirCommand, compassCompileCommand)
        }
    }

    private boolean attemptExecutionInShell(def shell, String changeDirCommand, String compassCompileCommand) {
        try {
            shell << (changeDirCommand + " && " + compassCompileCommand)
            def p = shell.execute()
            p.consumeProcessOutput(System.out, new PrintStream(new ByteArrayOutputStream()))
            p.waitFor()
        }
        catch (Exception e) {
            return false
        }

        return true
    }

    public void compile(callback) {
        println "Compiling sass stylesheets..."
        def p = runCompassCommand(['compile'] + getCompileArgs(callback))
        p?.waitFor()
    }

    public void watch() {
        runCompassCommandInThread(['watch'] + getCompileArgs(null))
    }

    public void execute() {
        def what = config.grass.containsKey("do_on_start") ? config.grass.do_on_start : "watch"
        switch (what) {
            case "compile":
                compile() { msg ->
                    event("StatusError", [msg])
                }
                break;
            case "watch":
                watch();
                break;
            case "print":
                String[] command = ['jruby', '-S', 'compass', ['watch'] + getCompileArgs(null)].flatten()
                System.out.append("Execute manually if needed: ${command.join(' ')}\n")
                break;
        }
    }

    public void installBlueprint() {
        def installBlueprintCommand = ['create', '--using', 'blueprint', '--syntax', (config.grass?.framework_output_type ?: "scss")]

        def images_dir = config.grass?.images_dir
        installBlueprintCommand << ['--sass-dir', config.grass.sass_dir, '--css-dir', config.grass.css_dir, '--javascripts-dir', 'js', (images_dir ? ['--images-dir', images_dir] : [])]

        def output = new ByteArrayOutputStream()
        runCompassCommand(installBlueprintCommand.flatten(), System.out, new PrintStream(output)).waitFor()
        String errorOutput = output.toString()

        def errorTexts = ['compass (LoadError)', 'Could not find RubyGem compass']
        errorTexts.each {
            if (errorOutput.contains(it)) {
                throw new Exception("Compass could not be loaded: ${it}")
            }
        }
    }

    protected Process runCompassCommand(def compassArgs, PrintStream output = System.out, PrintStream error = System.err) {
        String[] command = ['jruby', '-S', 'compass', compassArgs].flatten()
        output.append("Executing: ${command.join(' ')}\n")

        Process p = null
        try {
            p = command.execute()
            p.consumeProcessOutput(output, error)
        }
        catch (IOException e) {
            System.err.println("JRuby could not be started. Make sure 'jruby' exists on the PATH and try again.")
            System.err.println("No SCSS/SASS compilation will be performed.")
        }

        return p
    }

    private static boolean shutdownHookAdded = false

    protected def runCompassCommandInThread(def compassArgs) {
        if (!shutdownHookAdded) {
            addShutdownHookToKillCompass()
        }

        Thread.start {
            def process = runCompassCommand(compassArgs)
            process?.waitFor()
        }
    }

    protected void ensureParameterSet(parameter, message, callback) {
        if (!parameter && callback != null) {
            callback(message)
        }
    }

    protected addShutdownHookToKillCompass = {->
        Runtime.runtime.addShutdownHook {
            killCompass()
        }
    }

    protected def killCompass() {
        javaProcessKiller.killAllRegex(~/.*org[\.\/]jruby[\.\/]Main\s+-[sS]\s+compass.*/)
    }

    protected def getCompileArgs(callback) {
        def sass_dir = config.grass?.sass_dir
        def css_dir = config.grass?.css_dir

        ensureParameterSet sass_dir, "sass_dir is not set (GrassConfig.groovy)", callback
        ensureParameterSet css_dir, "css_dir is not set (GrassConfig.groovy)", callback

        return ['--sass-dir', "${sass_dir}", '--css-dir', "${css_dir}"] + getPreferenceArgs(callback)
    }

    protected def getPreferenceArgs(callback) {

        def images_dir = config.grass?.images_dir
        def relative_assets = config.grass?.relative_assets == null ? true : config.grass?.relative_assets
        def line_comments = config.grass?.line_comments == null ? true : config.grass?.line_comments
        def output_style = config.grass?.output_style ?: 'compact'

        ensureParameterSet output_style, "output_style is not set (GrassConfig.groovy)", callback

        def args = []

        args << '--output-style' << output_style
        if (images_dir) args << '--images-dir' << images_dir
        if (relative_assets) args << '--relative-assets'
        if (!line_comments) args << '--no-line-comments'
        if (forceRecompile) args << '--force'

        return args
    }
}
