package frontlinesms2.controller

import frontlinesms2.*
import spock.lang.*

class StatusControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	
	def setup() {
		controller = new StatusController()
		[new SmslibFconnection(name:'MTN Dongle', port:'stormyPort'),
				new EmailFconnection(name:'Miriam\'s Clickatell account', receiveProtocol:EmailReceiveProtocol.IMAPS, serverName:'imap.zoho.com',
						serverPort:993, username:'mr.testy@zoho.com', password:'mister')].each() {
			it.save(flush:true, failOnError: true)
			}
	}
	
	def "should return a list of all available connections"() {
		when:
			def model = controller.show()
		then:
			model.connectionInstanceTotal == 2
			model.connectionInstanceList == [SmslibFconnection.findByName('MTN Dongle'), EmailFconnection.findByUsername('mr.testy@zoho.com')]
		when:
			SmslibFconnection.findByName('MTN Dongle').delete(flush:true)
			model = controller.show()
		then:
			model.connectionInstanceTotal == 1
			model.connectionInstanceList == [EmailFconnection.findByUsername('mr.testy@zoho.com')]
	}
	
	def "message start dates should be inclusive"() {
		given:
			def sentDate = createDate(2011, 10, 18, 23, 58, 59)
			new Fmessage(text:'', src:"src1", date:createDate(2011, 10, 18, 0, 0, 1), inbound:true).save(flush:true, failOnError:true)
			def m2 = new Fmessage(text:'', src:"src2", date:sentDate, hasSent:true, inbound: false)
			def d = new Dispatch(dst:'123', status:DispatchStatus.SENT, dateSent:sentDate)
			m2.addToDispatches(d)
			m2.save(flush:true, failOnError:true)
		when:
			// TODO set start and end dates to the same day as messages were sent
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 18, 23, 59, 59)
			def model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
			
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 23, 58, 59)
			controller.params.endDate = createDate(2011, 10, 19, 0, 0, 0)
			model = controller.show()
		then:
			model.messageStats.sent == [1]
			model.messageStats.received == [1]
		when:
			controller.params.rangeOption = "between-dates"
			controller.params.startDate = createDate(2011, 10, 18, 0, 0, 0)
			controller.params.endDate = createDate(2011, 10, 19, 23, 57, 59)
			model = controller.show()
		then:
			model.messageStats.sent == [1, 0]
			model.messageStats.received == [1, 0]
	}
	
	@Unroll
	def "show action should return a list of filtered messages for the traffic graph"() {
		setup:
			createFilterTestData()
			def pollAndAnnouncement = [Activity.findByName('test-announcement'), Poll.findByName('This is a poll')]
			def testFolders = [Folder.findByName('test-folder')]
			controller.params << davidsParams
			controller.params.activityId = MessageOwner.findByName(ownerName)?.id
		when:
			def model = controller.show()
		then:
			model.activityInstanceList.containsAll(pollAndAnnouncement)
			model.folderInstanceList == testFolders
			model.messageStats.sent == sent
			model.messageStats.received == received
		where:
			davidsParams																| ownerName				| sent 		| received
			[rangeOption:"between-dates", startDate:new Date()-2, endDate:new Date()]	| ''					| [2, 0, 1]	| [0 , 0, 8]
			[rangeOption:"between-dates", startDate:new Date()-2, endDate:new Date()]	| 'test-announcement'	| [0, 0, 1]	| [0 , 0, 2]
			[rangeOption:"between-dates", startDate:new Date()-2, endDate:new Date()] 	| 'test-folder'			| [0, 0, 0]	| [0 , 0, 1]
			[rangeOption:"between-dates", startDate:new Date()-2, endDate:new Date()]	| 'This is a poll'		| [0, 0, 0]	| [0 , 0, 2]
	}
	
	def createDate(int year, int month, int date, int hour=0, int minute=0, int second=0) {
		def calc = Calendar.getInstance()
		calc.set(year, month, date, hour, minute, second)
		calc.getTime()
	}
	
	def createFilterTestData() {
		3.times { Fmessage.build(src:'Bob', text:'I like manchester') }
		new Fmessage(text:"sent message 1", inbound:false, date:new Date()-2)
				.addToDispatches(dst:'12345', status:DispatchStatus.SENT, dateSent:new Date()-2)
				.save(failOnError:true, flush:true)
		new Fmessage(text:"sent message 2", inbound:false, date:new Date()-2)
				.addToDispatches(dst: '34523', status: DispatchStatus.SENT, dateSent:new Date()-2)
				.save(failOnError:true, flush:true)

		def p = new Poll(name:'This is a poll')
		p.editResponses(choiceA: 'Manchester', choiceB:'Barcelona')
		p.save(failOnError:true, flush:true)
		PollResponse.findByValue('Manchester').addToMessages(Fmessage.build(src:'Bob', text:'hi Bob', starred:true))
		PollResponse.findByValue('Barcelona').addToMessages(Fmessage.build(src:'Jim', text:'hi Bob'))
		p.save(failOnError:true, flush:true)
		
		Folder.build(name:'test-folder')
				.addToMessages(Fmessage.build(src:'src'))
				.save(failOnError:true, flush:true)
		
		def announcementMessage = new Fmessage(text:"Test announcement", inbound:false)
				.addToDispatches(dst: '12345', status: DispatchStatus.SENT, dateSent:new Date())
				.save(failOnError:true, flush:true)
		Announcement.build(name:'test-announcement')
				.addToMessages(Fmessage.build(src:'src'))
				.addToMessages(announcementMessage)
				.addToMessages(Fmessage.build(src:'src'))
				.save(failOnError:true, flush:true)
	}
}
