package grails.plugins.sass

class CompassInvokerTest extends GroovyTestCase {

    CompassInvoker compass
    def scssNames = ['ie', 'print', 'screen', 'partials/_base']

    def blueprintScssFiles = scssNames.collect {
        new File("src/stylesheets/${it}.scss")
    }

    def blueprintSassFiles = scssNames.collect {
        new File("src/stylesheets/${it}.sass")
    }

    def blueprintCssFiles = scssNames[0..2].collect {
        new File("src/web-app/css/${it}.css")
    }

    def validConfig = [
        grass: [
            sass_dir: 'src/stylesheets',
            css_dir: 'src/web-app/css',
            images_dir: 'src/web-app/images',
            relative_assets: true,
            output_style: 'expanded',
            line_comments: false,
            framework_output_type: 'sass'
        ]
    ]

    void setUp() {
        compass = new CompassInvoker(new File("grails-app/conf/DefaultGrassConfig.groovy"), new JavaProcessKiller())
    }

    void test_compile() {
        def compass = new CompassInvoker(validConfig, new JavaProcessKiller())
        compass.installBlueprint()
        blueprintCssFiles*.delete()

        compass.compile()

        boolean someFileNotCreated = blueprintCssFiles.any {
            !it.exists()
        }

        assertFalse("One of ${blueprintCssFiles} not created", someFileNotCreated)
    }

    void test_compile_no_images_dir() {
        def config = [
            grass: [
                sass_dir: 'src/stylesheets',
                css_dir: 'src/web-app/css',
                relative_assets: true
            ]
        ]

        def compass = new CompassInvoker(config, new JavaProcessKiller())
        compass.installBlueprint()
        blueprintCssFiles*.delete()

        compass.compile()

        boolean someFileNotCreated = blueprintCssFiles.any {
            !it.exists()
        }

        assertFalse("One of ${blueprintCssFiles} not created", someFileNotCreated)
    }

    void test_compile_single_file() {
        File input = new File('web-app/sass/test.scss')
        assertTrue("Test setup is bad", input.exists())

        File output = new File('web-app/sass/out/test.css')
        output.delete()
        compass.compileSingleFile(input, output)

        assertTrue(output.exists())
    }

    void test_line_comments_compile_flag() {
        def config = [
            grass: [
                sass_dir: 'src/stylesheets',
                css_dir: 'src/web-app/css',
                images_dir: 'src/web-app/images',
                relative_assets: true,
                output_style: 'expanded',
                line_comments: false,
                framework_output_type: 'sass'
            ]
        ]

        def compassWithoutLineComments = new CompassInvoker(config, new JavaProcessKiller())
        compassWithoutLineComments.forceRecompile = true
        compassWithoutLineComments.compile()
        File testCss = new File('src/web-app/css/test.css')

        assertFalse("Test file is being generated with line comments", testCss.text.contains('/*'))

        config.grass.line_comments = true
        def compassWithLineComments = new CompassInvoker(config, new JavaProcessKiller())
        compassWithLineComments.forceRecompile = true
        compassWithLineComments.compile()

        assertTrue("Test file is being generated without line comments", testCss.text.contains('/*'))
    }

    void test_compass_gem_is_installed() {
        def output = new ByteArrayOutputStream()

        Process p = compass.runCompassCommand(['--version'] as String[], new PrintStream(output))
        p.waitFor()

        String processOutput = output.toString()

        assertTrue("Compass gem does not seem to be runnable: $processOutput", (processOutput =~ /Compass \d\.\d\d/).find())
    }

    void test_killing_compass_doesnt_leak_processes() {
        def javaProcessKiller = new JavaProcessKiller()
        int javaProcessCount = javaProcessKiller.getRunningJavaProcesses().size()
        compass.watch()
        compass.killCompass()
        int newJavaProcessCount = javaProcessKiller.getRunningJavaProcesses().size()

        assertTrue("Watch command is leaking processes", newJavaProcessCount <= javaProcessCount)
    }

    void test_install_blueprint_scss_output() {
        def config = [
            grass: [
                sass_dir: 'src/stylesheets',
                css_dir: 'src/web-app/css',
                images_dir: 'src/web-app/images',
                relative_assets: true,
                output_style: 'compact',
                framework_output_type: 'scss'
            ]
        ]

        def compass = new CompassInvoker(config, new JavaProcessKiller())
        blueprintScssFiles*.delete()

        compass.installBlueprint()

        blueprintScssFiles.each {
            File file -> assertTrue("${file.name} was not created", file.exists())
        }
    }

    void test_install_blueprint_sass_output() {
        def compass = new CompassInvoker(validConfig, new JavaProcessKiller())

        blueprintSassFiles*.delete()
        compass.installBlueprint()

        blueprintSassFiles.each {
            assertTrue("${it.name} was not created", it.exists())
        }
    }

    void test_install_blueprint_framework_output_param_unnecessary() {
        def config = [
            grass: [
                sass_dir: 'src/stylesheets',
                css_dir: 'src/web-app/css',
                images_dir: 'src/web-app/images',
                relative_assets: true
            ]
        ]

        def compass = new CompassInvoker(config, new JavaProcessKiller())

        blueprintScssFiles*.delete()
        compass.installBlueprint()
        blueprintScssFiles.each {
            assertTrue("${it.name} was not created", it.exists())
        }
    }
}
