package grails.plugins.sass

class CompassInvoker {
    def config
    def javaProcessKiller

    public CompassInvoker(File grassConfigLocation, def javaProcessKiller) {
        this(new ConfigSlurper().parse(grassConfigLocation.toURL()), javaProcessKiller)
    }

    public CompassInvoker(def config, def javaProcessKiller) {
        this.config = config
        this.javaProcessKiller = javaProcessKiller
    }

    public void compileSingleFile(File input, File output) {
        output.parentFile.mkdirs()

        def shells = [['sh', '-c'], ['cmd', '/c']]
        def changeDirCommand = "cd ${input.parent}"
        def compassCompileCommand =
        "jruby -S compass compile ${input.name} --sass-dir . --css-dir ${output.parentFile.absolutePath} --output-style compressed"

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
        def sass_dir = config.grass?.sass_dir
        def css_dir = config.grass?.css_dir
        def images_dir = config.grass?.images_dir
        def relative_assets = config.grass?.relative_assets == null ? true : config.compass?.relative_assets
        def line_comments = config.grass?.line_comments == null ? true : config.grass?.line_comments
        def output_style = config.grass?.output_style ?: 'compact'

        ensureParameterSet sass_dir, "sass_dir is not set (GrassConfig.groovy)", callback
        ensureParameterSet css_dir, "css_dir is not set (GrassConfig.groovy)", callback
        ensureParameterSet output_style, "output_style is not set (GrassConfig.groovy)", callback

        println "Compiling sass stylesheets..."

        def sassCompileCommandLineArgs = ['compile',
                '--sass-dir', "${sass_dir}",
                '--css-dir', "${css_dir}",
                images_dir ? ['--images-dir', "${images_dir}"] : [],
                relative_assets ? "--relative-assets" : "",
                line_comments ? "" : "--no-line-comments",
                '--output-style', "${output_style}"].flatten()

        def p = runCompassCommand(sassCompileCommandLineArgs)
        p?.waitFor()
    }

    public void watch() {
        def images_dir = config.grass?.images_dir
        runCompassCommandInThread(['watch', '--sass-dir', config.grass.sass_dir,
                '--css-dir', config.grass.css_dir, (images_dir ? ['--images-dir', images_dir] : []),
                '--output-style', config.grass.output_style].flatten())
    }

    public void installBlueprint() {
        def installBlueprintCommand = ['create', '--using', 'blueprint']

        if (config.grass?.framework_output_type == 'sass') {
            installBlueprintCommand << ['--syntax', 'sass']
        }

        def images_dir = config.grass?.images_dir
        installBlueprintCommand << ['--sass-dir', config.grass.sass_dir, '--css-dir', config.grass.css_dir, '--javascripts-dir', 'js', (images_dir ? ['--images-dir', images_dir] : [])]

        def output = new ByteArrayOutputStream()
        runCompassCommand(installBlueprintCommand.flatten(), System.out, new PrintStream(output)).waitFor()
        String errorOutput = output.toString()

        def errorTexts = ['compass (LoadError)', 'Could not find RubyGem compass']
        if (errorTexts.any { errorOutput.contains(it)}) {
            throw new Exception("Compass could not be loaded.")
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
        if (!parameter) {
            callback(message)
        }
    }

    protected addShutdownHookToKillCompass = {->
        Runtime.runtime.addShutdownHook {
            killCompass()
        }
    }

    protected def killCompass() {
        javaProcessKiller.killAll('org/jruby/Main -S compass')
    }
}
