package frontlinesms2

class Poll extends Activity {
//> CONSTANTS
	private static final String ALPHABET = ('A'..'Z').join()
	static final String KEY_UNKNOWN = 'unknown'
	static String getShortName() { 'poll' }

//> SERVICES
	def messageSendService

//> PROPERTIES
	static hasOne = [keyword: Keyword]
	String autoreplyText
	String question
	boolean yesNo
	List responses
	static hasMany = [responses: PollResponse]

//> SETTINGS
	static transients = ['unknown']
	
	static mapping = {
		keyword cascade: 'all'
		version false
	}
			
	static constraints = {
		name(blank:false, maxSize:255, validator: { val, obj ->
			def similarName = Poll.findByNameIlike(val)
			return !similarName|| obj.id == similarName.id
			})
		responses(validator: { val, obj ->
			val?.size() > 2 &&
					(val*.value as Set)?.size() == val?.size()
		})
		autoreplyText(nullable:true, blank:false)
		question(nullable:true)
		keyword(nullable:true)
	}

//> ACCESSORS
	def getUnknown() {
		responses.find { it.key == KEY_UNKNOWN } }
	
	Poll addToMessages(Fmessage message) {
		if(!messages) messages = []
		messages << message
		message.messageOwner = this
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
			this.unknown.messages.add(message)
		}
		this
	}
	
	Poll removeFromMessages(Fmessage message) {
		this.messages?.remove(message)
		if(message.inbound) {
			this.responses.each {
				it.removeFromMessages(message)
			}
		}
		message.messageOwner = null
		this
	}
	
	def getResponseStats() {
		def totalMessageCount = getActivityMessages(false, false).count()
		responses.sort {it.key?.toLowerCase()}.collect {
			def messageCount = it.liveMessageCount
			[id: it.id,
					value: it.value,
					count: messageCount,
					percent: totalMessageCount ? new BigDecimal(messageCount * 100 / totalMessageCount).setScale(2,BigDecimal.ROUND_HALF_UP) : 0]
		}
	}
	
	def editResponses(attrs) {
		if(attrs.pollType == 'yesNo' && !this.responses) {
			this.addToResponses(value:'Yes', key:'A', aliases:extractAliases(attrs, 'A'))
			this.addToResponses(value:'No', key:'B', aliases:extractAliases(attrs, 'B'))
			this.yesNo = true
		} else {
			def choices = attrs.findAll { it ==~ /choice[A-E]=.*/ }
			choices.each { k, v ->
				k = k.substring('choice'.size())
				def found = responses.find { it.key == k }
				if(found) {
					found.value = v
					found.aliases = extractAliases(attrs, k)
					println "######### Saved ######## ${found.aliases}"
				} else if(v?.trim()) this.addToResponses(value:v, key:k , aliases:extractAliases(attrs, k))
			}
		}
		if(!this.unknown) {
			this.addToResponses(PollResponse.createUnknown())
		}
	}
	
	def extractAliases(attrs, String k) {
		def raw = attrs["alias$k"]
		if(raw) raw.toUpperCase().replaceAll(/\s/, "").split(",").findAll { it }.join(", ")
	}

	def deleteResponse(PollResponse response) {
		response.messages.findAll { message ->
			this.unknown.messages.add(message)
		}
		this.removeFromResponses(response)
		response.delete()
		this
	}

	def processKeyword(Fmessage message, boolean exactMatch) {
		def response = getPollResponse(message, exactMatch)
		response.addToMessages(message)
		response.save()
		def poll = this
		if(poll.autoreplyText) {
			def params = [:]
			params.addresses = message.src
			params.messageText = poll.autoreplyText
			def outgoingMessage = messageSendService.createOutgoingMessage(params)
			poll.addToMessages(outgoingMessage)
			messageSendService.send(outgoingMessage)
			poll.save()
		}
	}
	
//> PRIVATE HELPERS
	private PollResponse getPollResponse(Fmessage message, boolean exactMatch) {
		def option
		def words = message.text.trim().toUpperCase().split(/\s+/)
		if(exactMatch) {
			if(words.size() < 2) return this.unknown
			option = words[1]
		} else {
			option = words[0][-1]
		}
		return responses.find { it!=this.unknown && it.aliases?.split(",")?.contains(option) }?: this.unknown
	}
}

