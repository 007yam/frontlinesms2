package frontlinesms2

class PollResponse {
	String key
	String value
	static belongsTo = [poll: Poll]
	static hasMany = [messages: Fmessage]
	List messages = []
	static transients = ['liveMessageCount']

	static mapping = {
		version false
	}
	
	static constraints = {
		value(blank:false, nullable:false, maxSize:255)
		poll(nullable:false)
		messages(nullable:true)
		key(nullable:true)
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
