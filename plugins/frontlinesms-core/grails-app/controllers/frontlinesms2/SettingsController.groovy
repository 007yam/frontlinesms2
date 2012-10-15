package frontlinesms2

class SettingsController {
	def i18nUtilService
	def grailsApplication
	
	def index() {
		redirect(action:'general')
	}
	
	def logs() {
		def logEntryList
		if(params.timePeriod && params.timePeriod != 'forever') {
			def timePeriod = new Date() - params.timePeriod.toInteger()
			logEntryList = LogEntry.findAllByDateGreaterThanEquals(timePeriod)
		} else {
			logEntryList = LogEntry.findAll()
		}
		[logEntryList:logEntryList,
				logEntryTotal:logEntryList.size()]
	}
	
	def logsWizard() {
		return [action: 'logs']
	}

	def general() {
		[currentLanguage:i18nUtilService.getCurrentLanguage(request),
				languageList:i18nUtilService.allTranslations]
	}

	def selectLocale() {
		i18nUtilService.setLocale(request, response, params.language?:'en')
		redirect view:'general'
	}

	def basicAuth() {
		
	}

	private def withFconnection(Closure c) {
		def connection = Fconnection.get(params.id)
		if(connection) {
			c connection
		} else {
			flash.message = message(code: 'default.not.found.message', args: [message(code: 'fconnection.label', default: 'Fconnection'), params.id])
			render(view:'show_connections', model: [fconnectionInstanceTotal: 0])
		}
	}
}

