class CoreGrailsPlugin {
    def version = "2.0-SNAPSHOT"
    def grailsVersion = "2.0.3 > *"
    def dependsOn = [csv:"0.3.1", jquery:"1.7.1", routing:"1.2.0"]
	def pluginExcludes = ["grails-app/views/error.gsp",
			"grails-app/conf/CoreBootStrap.groovy",
			"grails-app/conf/CoreUrlMappings.groovy"]
    def author = "FrontlineSMS team"
    def authorEmail = "dev@frontlinesms.com"
    def title = "FrontlineSMS Core"
    def description = ""
    def documentation = "https://github.com/frontlinesms/frontlinesms2"
}
