package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

import grails.buildtestdata.mixin.Build

@TestFor(Poll)
@Mock(PollResponse)
@Build(Fmessage)
class PollSpec extends Specification {
	/** some responses that should pass validation */
	def OK_RESPONSES = [new PollResponse(value: "one"), new PollResponse(value: "two")]
	private static final String TEST_NUMBER = "+2345678"
	
	def setup() {
		// Not sure why this is necessary with Test Mixins, but it seems to be:
		PollResponse.metaClass.removeFromMessages = { m ->
			delegate.messages.remove(m)
			m.messageOwner = null
		}
	}

	@Unroll
	def 'poll must have at least three responses'() {
		given:
			def p = new Poll(name:'test poll')
			p.responses = []
			responseCount.times { p.responses << new PollResponse(value:"r-$it", key:"$it") }
		expect:
			p.validate() == valid
		where:
			responseCount | valid
			0             | false
			1             | false
			2             | false
			3             | true
	}

	def "poll auto-reply cannot be blank"() {
		when:
			def poll = new Poll(title:"title", autoReplyText:" ", responses:OK_RESPONSES)
		then:
			!poll.validate()
	}

	@Unroll
	def 'processKeyword should assign messages to the appropriate response'() {
		given:
			def pollAndResponses = createPoll(validResponseCount)
			def poll = pollAndResponses.poll
			def responses = pollAndResponses.responses
			def m = Fmessage.build(text:messageText)
		when:
			poll.processKeyword(m, exactMatch)
		then:
			responses[response].messages == [m]
			!poll.messages?.contains(m)
		where:
			messageText            | exactMatch | validResponseCount | response
			'k c'                  | true       | 3                  | 'C'
			'k'                    | true       | 3                  | Poll.KEY_UNKNOWN
			'word a'               | true       | 3                  | 'A'
			'word b'               | true       | 3                  | 'B'
			'word c'               | true       | 3                  | 'C'
			'word d'               | true       | 3                  | Poll.KEY_UNKNOWN
			'word averylongword'   | true       | 3                  | Poll.KEY_UNKNOWN
			'    word a response ' | true       | 3                  | 'A'
			'\r\nword a match'     | true       | 3                  | 'A'
			'wordA'                | false      | 3                  | 'A'
			'wordA with more words'| false      | 3                  | 'A'
			'oneword'              | true       | 3                  | Poll.KEY_UNKNOWN
			'oneword'              | false      | 3                  | Poll.KEY_UNKNOWN
			'two words'            | false      | 3                  | Poll.KEY_UNKNOWN
			'two bords'            | false      | 3                  | Poll.KEY_UNKNOWN
			'keyword c'            | false      | 2                  | Poll.KEY_UNKNOWN
	}

	def 'processKeyword should send autoreply if one is present'() {
		given:
			def poll = createPoll(3).poll
			def sendService = Mock(MessageSendService)
			poll.messageSendService = sendService
			poll.autoreplyText = "some reply text"

			def replyMessage = Fmessage.build(text:"woteva")
			sendService.createOutgoingMessage({ params ->
				params.addresses==TEST_NUMBER && params.messageText=='some reply text'
			}) >> replyMessage

			def inMessage = Fmessage.build(text:"message text", src:TEST_NUMBER)
		when:
			poll.processKeyword(inMessage, true)
		then:
			1 * sendService.send(replyMessage)
	}
	
	def 'edit responses should create responses which do no exist'() {
		given:
			def poll = new Poll()
			def params = [choiceA:'eh', choiceB:'bee',
					choiceC:'sea', choiceD:'dee']
		when:
			poll.editResponses(params)
		then:
			poll.responses*.key == ['A', 'B', 'C', 'D', Poll.KEY_UNKNOWN]
	}

	def 'removing a message from a poll should remove it from poll.messages'() {
		given:
			Fmessage m = new Fmessage()
			Poll p = new Poll()
					.addToMessages(m)
		when:
			p.removeFromMessages(m)
		then:
			!p.messages
	}

	def 'ill-formated submitted aliases should be formated neatly'(){
		given:
			def p =  new Poll()
		expect:
			p.addAlias(attrs, 'B') == validAliases
		where:
			attrs							| validAliases
			[aliasB: ",,,,"] 				| ""
			[aliasB: "   "] 				| ""
			[aliasB: "a,,,b"] 				| "A, B, "
			[aliasB: ",,,,a  ,,,,b,,,   "] 	| "A, B, "
	}

	private def createPoll(int validResponseCount) {
		def p = new Poll()
		def responses = [unknown:PollResponse.createUnknown()]
		p.addToResponses(responses.unknown)
		for(i in 0..<validResponseCount) {
			def key = ('A'..'C')[i]
			def r = new PollResponse(key:key, value:"mock-response-$i")
			responses[key] = r
			p.addToResponses(r)
		}
		return [poll:p, responses:responses]
	}
}

