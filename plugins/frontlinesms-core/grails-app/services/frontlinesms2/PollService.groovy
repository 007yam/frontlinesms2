package frontlinesms2

class PollService{
	def messageSendService
	
	static transactional = true
	def saveInstance(poll, params) {
		// FIXME this should use withPoll to shorten and DRY the code, but it causes cascade errors as referenced here:
		// http://grails.1312388.n4.nabble.com/Cascade-problem-with-hasOne-relationship-td4495102.html
		println "Poll Params ::${params}"
		poll.name = params.name ?: poll.name
		poll.autoreplyText = params.enableAutoreply? (params.autoreplyText ?: poll.autoreplyText): null
		poll.question = params.question ?: poll.question
		poll.sentMessageText = params.messageText ?: poll.sentMessageText
		poll.editResponses(params)
		poll.keywords?.clear()
		poll.save(failOnError:true, flush:true)
		println "#### Round 1 Save!!"
		if(params.enableKeyword == "true"){
			poll.editKeywords(params)
		}else{
			poll.noKeyword()
		}
		poll.save(failOnError:true)
		println "#### Round 2 Save!!"
		println "Keywords ${poll.keywords*.value}"
		if(!params.dontSendMessage && !poll.archived) {
			println "Sending a message as part of saving poll"
			def message = messageSendService.createOutgoingMessage(params)
			message.save()
			poll.addToMessages(message)
			MessageSendJob.defer(message)
		}
		poll.save(failOnError:true)
		poll
	}
}