package frontlinesms2.radio

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.*
import frontlinesms2.routing.CamelIntegrationSpec

class IncomingSmslibRouteSpec extends CamelIntegrationSpec {
	def fconnectionService
	def radioShowService
	
	String getTestRouteFrom() { '' }
	String getTestRouteTo() { '' }
	
	def "should translate a CIncomingMessage into a Fmessage then save it then add it to the running radio show"() {
		given:
			def mockPortHandler = MockModemUtils.createMockPortHandler([1:'0891534875001040F30414D0537AD91C7683A465B71E0000013020017560400CC7F79B0C6ABFE5EEB4FB0C'])
			// initialise mock serial device with message available
 			MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection(name:'test connection', port:'/def/test-modem', baud:9600).save(failOnError:true)
			def show = new RadioShow(name:"Health Show", isRunning:true).save(flush:true)
			fconnectionService.createRoutes(connection)
			
		when:	
			//start radio show
			assert show.showMessages.count() == 0
			// wait for message to be read from mock serial device
			while(mockPortHandler.messages.size() > 0) { Thread.sleep(50) }
			// wait for message to be processed
			Thread.sleep(5000) // TODO must be a neater way of doing this
			show.refresh()
		then:	
			// assert Fmessage is saved and has expected content
			Fmessage.findAll()*.text == ['Good morning']
			// assert RadioShow processor is called
			show.showMessages.count() == 1
			
		cleanup:	
			// stop route
			if(connection) fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
			//stop show
			radioShowService.stopShow()
			Fconnection.findAll()*.delete()
			RadioShow.findAll()*.delete()
	}
	
	@spock.lang.IgnoreRest
	def "should not save poll message to the running radio show"() {
		given:
			def mockPortHandler = MockModemUtils.createMockPortHandler(false, [1:'0891534875001040F30414D0537AD91C7683A465B71E0000013020017560400CC7F79B0C6ABFE5EEB4FB0C',
				2:'07915892000000F0040B915274204365F70000704021325224230AE6F79B2E0EB3D92032',
				3: '07915892000000F0040B915892214365F700007040213252242331493A283D0795C3F33C88FE06C9CB6132885EC6D341EDF27C1E3E97E7207B3A0C0A5241E377BB1D7693E72E'])
			// initialise mock serial device with message available
			 MockModemUtils.initialiseMockSerial(['/def/test-modem': new CommPortIdentifier("COM99",
					mockPortHandler)])
			// start route
			def connection = new SmslibFconnection(name:'test connection', port:'/def/test-modem', baud:9600).save(failOnError:true)
			def show = new RadioShow(name:"Health Show",isRunning:true).save(flush:true)
			def keyword = new Keyword(value: 'FOOTBALL')
			def poll = new Poll(name:'What football team do you like?', keyword: keyword)
			poll.addToResponses(key:'A', value:'manchester')
			poll.addToResponses(key:'B', value:'gor')
			poll.addToResponses(key:'C', value:'sofapaka')
			poll.addToResponses(key:'D', value:'arsenal')
			poll.addToResponses(PollResponse.createUnknown())
			poll.save(failOnError:true, flush:true)
			fconnectionService.createRoutes(connection)
			
		when:
			//start radio show
			assert show.showMessages.count() == 0
			// wait for message to be read from mock serial device
			while(mockPortHandler.receiveMessages) { sleep(100) }
			sleep 3000
			show.refresh()
		then:
			// assert Fmessage is saved and has expected content
			Fmessage.findAll()*.text == ['Good morning', 'football d', 'It is easy to read text messages via AT commands.']
			// assert RadioShow processor is called
			show.showMessages.count() == 2
			Fmessage.findAll()[1].messageOwner == PollResponse.findByValue('arsenal')
			
		cleanup:
			// stop route
			if(connection) fconnectionService.destroyRoutes(connection)
			// remove mock serial port
			MockSerial.reset()
	}
}
