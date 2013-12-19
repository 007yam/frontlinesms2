package frontlinesms2

class MessageSendJob {
	def messageSendService

	def execute(context) {
		def ids = context.mergedJobDataMap.get('ids')
		def messages = TextMessage.getAll(ids)
		messages.each { m ->
			messageSendService.send(m)
		}
	}

	/** Send a message or messages in 30 seconds time */
	static defer(TextMessage message) {
		defer([message])
	}

	/** Send a message or messages in 30 seconds time */
	static defer(List messages) {
		def sendTime = new Date()
		use(groovy.time.TimeCategory) {
			sendTime += 30000
		}
		def args = [ids:messages*.id]
		MessageSendJob.schedule(sendTime, args)
	}
}

