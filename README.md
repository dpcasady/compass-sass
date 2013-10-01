# Compass Sass plugin
[Compass](http://compass-style.org/), [SASS and SCSS](http://sass-lang.com/) support for Grails

## Installation
Add the plugin to the `plugins` block of your `BuildConfig.groovy`:

```groovy
compile ":compass-sass:0.7"
```

## Prerequisites
To use this plugin, you will need to have [JRuby](http://jruby.org/) installed and on your shell's `PATH` environment variable. During installation, the plugin will check for the existence of Compass—if the gem isn't found, it will install it.
If you have RVM installed, the gem path might be incorrect unless RVM is set to use the "default" Ruby installation. If you're having issues,
try to manually install Compass into RVM, and restart your terminal to make sure JRuby picks up the PATH change.

```sh
$ rvm use --default (version)
$ gem install compass
```

## Usage
This plugin has two modes of operation for the automatic compilation of SASS/SCSS files. It can be configured by setting the `do_on_start` property to one of the following:

* **watch** - During `grails run-app`, Compass is invoked via jRuby to watch the configured source directory (default src/stylesheets) for changes to SASS/SCSS files, which will then compile and run on the fly.

* **resources** - Integration with the [Resources Plugin](http://grails.org/plugin/resources) by including something like the following in your ApplicationResources file, or wherever you are defining your resources. Resources will watch your SASS/SCSS files for changes and they'll be compiled for you.

```groovy
mymodule {
    resource url: '/sass/test.scss', attrs: [type: 'css'], disposition: 'head'
}
```

*Note: `attrs: [type: 'css']` is required for the resources plugin to pick-up the files. Files must end in .sass or .scss to get picked up by the SASS resource mapper.*

You can also set `do_on_start` to **compile**, which will only compile SASS/SCSS on the initial `grails run-app`.

The following grails commands are also available:

* **grails compile-css** - Compiles any SASS/SCSS files in src/stylesheets to web-app/css, both locations configurable in GrassConfig.groovy.
* **grails install-blueprint** - Installs blueprint from from the Compass gem.
* **grails update-compass** - Updates Compass and any of its dependent gems.

## Configuration
### GrassConfig.groovy

```groovy
grass {
    css_dir = "./web-app/css"
    sass_dir = "./src/stylesheets"
    images_dir = "./web-app/images"
    output_style = "compact"
    relative_assets = true
    line_comments = true
    preferred_syntax = "scss"
    do_on_start = "watch"
}
```

* **css_dir**: output directory for compiled CSS
* **sass_dir**: directory Compass uses for *compile-css* and real-time recompilation
* **images_dir**: *(Optional)* location for images referenced in CSS
* **output_style**: output format of the CSS
`Values: nested, expanded, compact, compressed`
* **relative_assets**: whether or not Compass will generate relative URLs
`Values: true, false`
* **line_comments**: whether or not Compass will generate debugging comments that display the original location of your selectors
`Values: true, false`
* **preferred\_syntax**: the output of *install-blueprint*
`Values: sass, scss`
* **do_on_start**: what Compass should do on run-app (watch, compile, or resources)

GrassConfig.groovy is an environment-aware config file, so you can customize the behavior by adding an environments block. The following would keep CSS files compressed except in your development environment:

```groovy
grass {
    css_dir = "./web-app/css"
    sass_dir = "./src/stylesheets"
    images_dir = "./web-app/images"
    output_style = "compressed"
    relative_assets = true
    line_comments = false
    preferred_syntax = "scss"
    do_on_start = "watch"
}

environments {
    development {
        grass {
            output_style = "compact"
            line_comments = true
        }
    }
}
```


## What if my team members/I don't have JRuby installed?
If JRuby is not installed, you will receive a warning during run-app, and SASS/SCSS files will not be compiled. As long as you check in compiled CSS, this isn't an issue for multiple-developer teams. If you want to use the resources plugin, everyone will need JRuby installed.

## License
Licensed under the Apache License, Version 2.0. See <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>
