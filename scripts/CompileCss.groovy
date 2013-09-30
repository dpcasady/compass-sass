includeTargets << grailsScript("_GrailsInit")
includeTargets << grailsScript("_GrailsSettings")
includeTargets << new File(compassSassPluginDir, "scripts/_GetCompassInvoker.groovy")

target(compileCss: "Compile sass/scss stylesheets") {
	compass.compile() { msg ->
		event("StatusError", [msg])
		exit(-1)
	}
}

setDefaultTarget(compileCss)
