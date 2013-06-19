# Compass Sass plugin
Compass, SASS and SCSS support for Grails

## Installation
```sh
$ grails install-plugin compass-sass
```

## Pre-Requisites
To use this plugin, you will need to have jRuby installed and on the PATH. During installation, the plugin will check for the existence of Compass, and if the gem isn't installed, it will install it.
If you have RVM installed, the gem path is incorrect unless rvm is set to use the "default" ruby. If you're having issues,
try to manually install compass into RVM, and restart your terminal to make sure JRuby picks up the PATH change.
```sh
$ rvm use --default (version)
$ gem install compass
```

## Usage
After installing, you're done. During grails run-app, compass is started to watch the configured source directory (default src/stylesheets) for changes to SASS or SCSS files, which lets you compile and run on the fly. This is the only time compass is invoked automatically. The following commands are also available:

* **grails compile-css** - Compiles any SASS/SCSS files in src/stylesheets to web-app/css, both locations configurable in GrassConfig.groovy.
* **grails install-blueprint** - Installs blueprint from from the compass gem.
* **grails update-compass** - Updates compass and any of its dependent gems.

## Resources integration
You can also use the <a href='http://grails.org/plugin/resources'>resources plugin</a> with the following stanza:
```groovy
mymodule {
    resource url: '/sass/test.scss', attrs: [type: 'css'], disposition: 'head'
}
```

**Note:** attrs: [type: 'css'] is required for the resources plugin to pick-up the files. Files must end in .sass or .scss to get picked up by the SASS resource mapper.

## Configuration
### GrassConfig.groovy
```groovy
grass {
    sass_dir = "./src/stylesheets"
    css_dir = "./web-app/css"
    relative_assets = true
    output_style = "compact"
    framework_output_type = "scss"

    line_comments = true
    images_dir = "./web-app/images"
}
```


* **sass_dir**: Directory compass uses for *compile-css* and real-time recompilation during run-app.
* **css_dir**: Output directory for compiled CSS.
* **relative_assets**: Whether or not compass will generate relative URLs.
`Values: true, false`
* **line_comments**: Whether or not compass will generate debugging comments that display the original location of your selectors.
`Values: true, false`
* **output_style**: The output format of the CSS.
`Values: nested, expanded, compact, compressed`
* **framework\_output\_type**: The output of install-blueprint.
`Values: sass, scss`
* **images_dir**: *(Optional)* Location for images referenced in CSS.

GrassConfig.groovy is an environment-aware config file, so you can customize the behavior by adding an environments block. The following would keep CSS files compressed except in your development environment:

```groovy
grass {
    sass_dir = "./src/stylesheets"
    css_dir = "./web-app/css"
    images_dir = "./web-app/images"
    relative_assets = true
    framework_output_type = "scss"
    line_comments = false
    output_style = "compressed"
}

environments {
    development {
        grass {
            line_comments = true
            output_style = "compact"
        }
    }
}
```


## What if my team members/I don't have jRuby installed?
If jRuby is not installed, you will receive a warning during run-app, and SASS/SCSS files will not be compiled. As long as you check in compiled CSS, this isn't an issue for multiple-developer teams. If you want to use the resources plugin, everyone will need jRuby installed.

## License
Licensed under the Apache License, Version 2.0. See <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
