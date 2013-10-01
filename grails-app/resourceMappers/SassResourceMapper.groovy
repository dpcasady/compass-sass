import org.grails.plugin.resource.mapper.MapperPhase
import grails.plugins.sass.JavaProcessKiller
import grails.plugins.sass.CompassInvoker
import grails.util.Environment

class SassResourceMapper {
    def grailsApplication
    def phase = MapperPhase.GENERATION

    static defaultExcludes = ['**/*.js', '**/*.png', '**/*.gif', '**/*.jpg', '**/*.jpeg', '**/*.gz', '**/*.zip']
    static defaultIncludes = ['**/*.scss', '**/*.sass']

    private static String SASS_FILE_EXTENSIONS = ['.scss', '.sass']

    private CompassInvoker compassInvoker

    SassResourceMapper() {
        compassInvoker = new CompassInvoker(getConfig(), new JavaProcessKiller())
    }

    def map(resource, config) {
        File originalFile = resource.processedFile

        if (resource.sourceUrl && isFileSassFile(originalFile)) {
            File input = getOriginalFileSystemFile(resource.sourceUrl)
            File targetFile = new File(generateCompiledFileFromOriginal(originalFile.absolutePath))
            compassInvoker.compileSingleFile(input, targetFile)

            resource.processedFile = targetFile
            resource.sourceUrlExtension = 'css'
            resource.tagAttributes.rel = 'stylesheet'
            resource.actualUrl = generateCompiledFileFromOriginal(resource.originalUrl)
            resource.contentType = 'text/css'
        }
    }

    private ConfigObject getConfig() {
        def config = new ConfigObject()
        def classLoader = new GroovyClassLoader(getClass().classLoader)
        config.merge(new ConfigSlurper(Environment.current.name).parse(classLoader.loadClass('DefaultGrassConfig')))
        try {
            new ConfigSlurper(Environment.current.name).parse(classLoader.loadClass('GrassConfig'))
        }
        catch (ignored) {
        }

        return config
    }

    private boolean isFileSassFile(File file) {
        return SASS_FILE_EXTENSIONS.any { file.name.toLowerCase().endsWith(it) }
    }

    private String generateCompiledFileFromOriginal(String original) {
        String renamed = original
        renamed = renamed.replaceAll(/(?i)\.sass/, '.css')
        renamed = renamed.replaceAll(/(?i)\.scss/, '.css')
        return renamed
    }

    private File getOriginalFileSystemFile(String sourcePath) {
        grailsApplication.parentContext.getResource(sourcePath).file
    }
}
