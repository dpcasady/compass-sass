package grails.plugins.sass

class CompassInvokerTest extends GroovyTestCase {
    CompassInvoker compass

    def blueprintScssFiles = [new File("src/stylesheets/ie.scss"), new File("src/stylesheets/print.scss"), new File("src/stylesheets/screen.scss"), new File("src/stylesheets/partials/_base.scss")]
    def blueprintSassFiles = [new File("src/stylesheets/ie.sass"), new File("src/stylesheets/print.sass"), new File("src/stylesheets/screen.sass"), new File("src/stylesheets/partials/_base.sass")]
    def blueprintCssFiles = [
            new File('src/web-app/css/ie.css'),
            new File('src/web-app/css/print.css'),
            new File('src/web-app/css/screen.css')
    ]

    def validConfig = [
            grass:
            [
                    sass_dir: 'src/stylesheets',
                    css_dir: 'src/web-app/css',
                    images_dir: 'src/web-app/images',
                    relative_assets: true,
                    line_comments: true,
                    framework_output_type: 'sass'
            ]
    ]

    public void setUp() {
        compass = new CompassInvoker(new File("grails-app/conf/DefaultGrassConfig.groovy"), new JavaProcessKiller())
    }

    public void test_compile() {
        compass = new CompassInvoker(validConfig, new JavaProcessKiller())
        compass.installBlueprint()
        blueprintCssFiles*.delete()

        compass.compile() {}

        boolean someFileNotCreated = blueprintCssFiles.any { !it.exists() }
        assertFalse("One of ${blueprintCssFiles} not created", someFileNotCreated)
    }

    public void test_compile_no_images_dir() {
        def config = [
                grass:
                [
                        sass_dir: 'src/stylesheets',
                        css_dir: 'src/web-app/css',
                        relative_assets: true
                ]
        ]
        compass = new CompassInvoker(config, new JavaProcessKiller())
        compass.installBlueprint()
        blueprintCssFiles*.delete()

        compass.compile() {}

        boolean someFileNotCreated = blueprintCssFiles.any { !it.exists() }
        assertFalse("One of ${blueprintCssFiles} not created", someFileNotCreated)
    }

    public void test_compile_single_file() {
        File input = new File('web-app/sass/test.scss')
        assertTrue("Test setup is bad", input.exists())

        File output = new File('web-app/sass/out/test.css')
        output.delete()

        compass.compileSingleFile(input, output)

        assertTrue(output.exists())
    }

    public void test_compass_gem_is_installed() {
        def output = new ByteArrayOutputStream()

        Process p = compass.runCompassCommand(['--version'] as String[], new PrintStream(output))
        p.waitFor()

        String processOutput = output.toString()
        assertTrue("Compass gem does not seem to be runnable: $processOutput", (processOutput =~ /Compass \d\.\d\d/).find())
    }

    public void test_killing_compass_doesnt_leak_processes() {
        def javaProcessKiller = new JavaProcessKiller()
        int javaProcessCount = javaProcessKiller.getRunningJavaProcesses().size()
        compass.watch()
        compass.killCompass()
        int newJavaProcessCount = javaProcessKiller.getRunningJavaProcesses().size()

        assertTrue("Watch command is leaking processes", newJavaProcessCount <= javaProcessCount)
    }

    public void test_install_blueprint() {
        blueprintScssFiles*.delete()

        compass.installBlueprint()

        blueprintScssFiles.each { File file -> assertTrue("${file.name} was not created", file.exists()) }
    }

    public void test_install_blueprint_sass_output() {
        compass = new CompassInvoker(validConfig, new JavaProcessKiller())

        blueprintSassFiles*.delete()
        compass.installBlueprint()

        blueprintSassFiles.each { assertTrue("${it.name} was not created", it.exists())}
    }

    public void test_install_blueprint_framework_output_param_unnecessary() {
        def config = [
                grass:
                [
                        sass_dir: 'src/stylesheets',
                        css_dir: 'src/web-app/css',
                        images_dir: 'src/web-app/images',
                        relative_assets: true,
                ]
        ]

        compass = new CompassInvoker(config, new JavaProcessKiller())

        blueprintScssFiles*.delete()
        compass.installBlueprint()
        blueprintScssFiles.each { assertTrue("${it.name} was not created", it.exists())}
    }
}
