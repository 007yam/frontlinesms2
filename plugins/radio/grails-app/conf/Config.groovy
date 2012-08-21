import org.apache.log4j.ConsoleAppender
import org.apache.log4j.RollingFileAppender
// configuration for plugin testing - will not be included in the plugin zip
grails.project.groupId = "frontlinesms2.radio" // change this to alter the default package name and Maven publishing destination
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = false
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
					  xml: ['text/xml', 'application/xml'],
					  text: 'text/plain',
					  js: 'text/javascript',
					  rss: 'application/rss+xml',
					  atom: 'application/atom+xml',
					  css: 'text/css',
					  csv: 'text/csv',
					  pdf: 'application/pdf',
					  all: '*/*',
					  json: ['application/json','text/json'],
					  form: 'application/x-www-form-urlencoded',
					  multipartForm: 'multipart/form-data'
					]

// URL Mapping Cache Max Size, defaults to 5000
//grails.urlmapping.cache.maxsize = 1000

// The default codec used to encode data with ${}
grails.views.default.codec = "none" // none, html, base64
grails.views.gsp.encoding = "UTF-8"
grails.converters.encoding = "UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = true
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// jquery plugin
grails.views.javascript.library = "jquery"

//Enable automatic database migrations
grails.plugin.databasemigration.updateOnStart = true
grails.plugin.databasemigration.updateOnStartFileNames = ['changelog.groovy']

// pagination
grails.views.pagination.max = 50

grails.config.locations = []
grails.config.locations << "file:${frontlinesms2.ResourceUtils.resourcePath}/log4j.groovy"
log4j = {
	environments {
		def layout = pattern(conversionPattern:'%d %-5p [%c{2}] %m%n')
		production {
			def conf = frontlinesms2.ResourceUtils.resourcePath
			println "Logging conf dir: $conf"
			rollingFile name:"prod",
					file:"$conf/standard.log",
					maxFileSize:10240000,
					threshold:org.apache.log4j.Level.INFO
			rollingFile name:"prod-stacktrace",
					file:"$conf/stacktrace.log",
					maxFileSize:10240000,
					threshold:org.apache.log4j.Level.WARN
		}
		development { console name:'dev', threshold:org.apache.log4j.Level.INFO }
		test { console name:'test', threshold:org.apache.log4j.Level.INFO }
	}

	root {
		all 'dev', 'test', 'prod', 'prod-stacktrace'
	}


	all 'org.codehaus.groovy.grails.web.servlet',  //  controllers
		'org.codehaus.groovy.grails.web.pages', //  GSP
		'org.codehaus.groovy.grails.web.sitemesh', //  layouts
		'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
		'org.codehaus.groovy.grails.web.mapping', // URL mapping
		'org.codehaus.groovy.grails.commons', // core / classloading
		'org.codehaus.groovy.grails.plugins', // plugins
		'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
		'org.springframework',
		'org.hibernate',
		'net.sf.ehcache.hibernate'
}

// Added by the JQuery Validation plugin:
jqueryValidation.packed = true
jqueryValidation.cdn = false  // false or "microsoft"
jqueryValidation.additionalMethods = false

frontlinesms.plugins=['core', 'radio']

