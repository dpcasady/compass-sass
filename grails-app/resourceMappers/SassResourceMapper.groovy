import org.grails.plugin.resource.mapper.MapperPhase
import grails.plugins.sass.JavaProcessKiller
import grails.plugins.sass.CompassInvoker
import grails.util.GrailsUtil
//import grails.util.Environment

class SassResourceMapper {
    def grailsApplication
    def phase = MapperPhase.GENERATION

    static defaultExcludes = ['**/*.js', '**/*.png', '**/*.gif', '**/*.jpg', '**/*.jpeg', '**/*.gz', '**/*.zip']
    static defaultIncludes = ['**/*.scss', '**/*.sass']

    private static String SASS_FILE_EXTENSIONS = ['.scss', '.sass']

    private CompassInvoker compassInvoker

    public SassResourceMapper() {
        compassInvoker = new CompassInvoker(getConfig(), new JavaProcessKiller())
    }

    def map(resource, config) {
        File originalFile = resource.processedFile

        if (resource.sourceUrl && isFileSassFile(originalFile)) {
            File input = getOriginalFileSystemFile(resource.sourceUrl);
            File output = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))
            compassInvoker.compileSingleFile(input, output)

            resource.processedFile = output

            resource.contentType = 'text/css'
            resource.sourceUrlExtension = 'css'
            resource.tagAttributes.rel = 'stylesheet'
            resource.actualUrl = generateCompiledFileFromOriginal(resource.originalUrl)
        }
    }

    private ConfigObject getConfig() {
        def config = new ConfigObject()
        def classLoader = new GroovyClassLoader(getClass().classLoader)
        //config.merge(new ConfigSlurper(Environment.getCurrent().getName()).parse(classLoader.loadClass('DefaultGrassConfig')))
        config.merge(new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('DefaultGrassConfig')))
        try {
            //new ConfigSlurper(Environment.getCurrent().getName()).parse(classLoader.loadClass('GrassConfig'))
            new ConfigSlurper(GrailsUtil.environment).parse(classLoader.loadClass('GrassConfig'))
        }
        catch (Exception ignored) {
        }

        return config
    }

    private boolean isFileSassFile(File file) {
        return SASS_FILE_EXTENSIONS.any { file.name.toLowerCase().endsWith(it) }
    }

    private String generateCompiledFileFromOriginal(String original) {
        original = original.replaceAll(/(?i)\.sass/, '.css')
        original = original.replaceAll(/(?i)\.scss/, '.css')
        original
    }

    private File getOriginalFileSystemFile(String sourcePath) {
        grailsApplication.parentContext.getResource(sourcePath).file
    }
}
