package frontlinesms2

import spock.lang.*
import grails.test.mixin.*

@TestFor(IntelliSmsFconnection)
class IntelliSmsFconnectionSpec extends Specification {
	def 'creating a sendOnly IntelliSmsFconnection validates'() {
		when:"send property not set"
			def intellismsConn = new IntelliSmsFconnection(name:"test", username:"test", password:"****")
		then:
			!intellismsConn.validate()
		when:"username and password not set"
			intellismsConn = new IntelliSmsFconnection(name:"test", sendEnabled:true)
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn = new IntelliSmsFconnection(sendEnabled:true, name:"test", username:"test", password:"****")
		then:
			intellismsConn.save()
	}
	
	def 'creating a receiveOnly IntelliSmsFconnection validates'() {
		when:"receive property not set"
			def intellismsConn = new IntelliSmsFconnection(name:"test", serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP)
		then:
			!intellismsConn.validate()
		when:"email fields not set"
			intellismsConn = new IntelliSmsFconnection(receiveEnabled:true, name:"test", username:"test", password:"****")
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receiveEnabled:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP)
		then:
			intellismsConn.save()
	}
	
	def 'creating a send and receive IntelliSmsFconnection validates'() {
		when:"receive property not set"
			def intellismsConn = new IntelliSmsFconnection(receiveEnabled: true, sendEnabled:true) 
		then:
			!intellismsConn.validate()
		when:"email fields not set"
			intellismsConn = new IntelliSmsFconnection(receiveEnabled:true, name:"test", username:"test", password:"****")
		then:
			!intellismsConn.save()
			intellismsConn.hasErrors()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP, sendEnabled:true, password:"***")
		then:
			!intellismsConn.save()
		when:
			intellismsConn =  new IntelliSmsFconnection(name:"test", receive:true, serverName:"imap.gmail.com", serverPort:"993", emailUserName:"test",emailPassword:"****", receiveProtocol:EmailReceiveProtocol.IMAP, sendEnabled:true, username:"test", password:"***")
		then:
			intellismsConn.save()
	}

	def "getNonnullableConfigFields should return a list of nonnullable fields"() {
		setup:
			MetaClassModifiers.addMapMethods()
			mockForConstraintsTests(IntelliSmsFconnection)
		when:
			def configFields = IntelliSmsFconnection.configFields
			def conn = Fconnection.getNonnullableConfigFields(IntelliSmsFconnection)
		then:
			conn instanceof List
	}
		
}
