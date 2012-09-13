package frontlinesms2

class PollResponse {
	String value
	String key
	String aliases
	static belongsTo = [poll: Poll]
	static hasMany = [messages: Fmessage]
	List messages = []
	static transients = ['liveMessageCount']

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:false, nullable:false, maxSize:255)
		key(nullable:true)
		aliases(blank:true,nullable:true)
		poll(nullable:false)
		messages(nullable:true)
	}
	
	void addToMessages(Fmessage message) {
		if(message.inbound) {
			this.poll.responses?.each {
				it.removeFromMessages(message)
			}
			this.messages.add(message)
			if (this.poll.messages == null)
				this.poll.messages = []
			this.poll.messages << message
			message.messageOwner = this.poll
			message.save()
		}
	}
	
	def getLiveMessageCount() {
		def m = 0
		this.messages.each {
			if(!it.isDeleted)
				m++
		}
		m
	}

//> FACTORY METHODS
	static PollResponse createUnknown() {
		new PollResponse(value:'Unknown', key:Poll.KEY_UNKNOWN)
	}
}
