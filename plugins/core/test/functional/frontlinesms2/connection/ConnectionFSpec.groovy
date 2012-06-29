package frontlinesms2.connection

import spock.lang.*

import frontlinesms2.*
import frontlinesms2.dev.MockModemUtils

import serial.mock.MockSerial
import serial.mock.CommPortIdentifier

class ConnectionFSpec extends grails.plugin.geb.GebSpec {
	def 'When there are no connections, this is explained to the user'() {
		when:
			to ConnectionPage
		then:
			connectionList.displayed
			connectionList.text().contains('You have no connections configured.')
	}
	
	def 'There is a Not Connected label shown for inactive connection'() {
		when:
			createTestEmailConnection()
			to ConnectionPage
		then:
			connectionList.status == "Not connected"
	}

	def 'there is a DELETE button shown for inactive connection'() {
		when:
			createTestEmailConnection()
			to ConnectionPage
		then:
			connectionList.btnDelete.displayed
	}
	
	def 'should show "create route" button for inactive connection '() {
		when:
			createTestEmailConnection()
			to ConnectionPage
		then:
			connectionList.btnCreateRoute.displayed
	}

	def 'DELETE button should remove selected fconnection from the list'() {
		given:
			createTestEmailConnection()
			to ConnectionPage
		when:
			connectionList.btnDelete.click()
		then:
			errorMessages.contains("Connection test email connection was deleted.")
			connectionList.text().contains('You have no connections configured.')
	}

	def 'Send test message button for particular connection appears when that connection is selected and started'() {
		given:
			def testConnection = createTestSmsConnection()
			SmslibFconnection.build(name:"test modem", port:"COM2", baud:11200)
		when:
			to ConnectionPage
			waitFor{ connectionList.btnCreateRoute.displayed }
		then:
			!connectionList.btnTestRoute.displayed
		when:
			connectionList.btnCreateRoute.click()
		then:
			waitFor('slow') { connectionList.status == "Connected" }
			waitFor { connectionList.btnTestRoute.displayed }
			connectionList.btnTestRoute.@href == "/connection/createTest/${testConnection.id}"
	}

	def 'delete button is not displayed for a connected Fconnection'() {
		given:
			def testConnection = createTestSmsConnection()
			SmslibFconnection.build(name:"test modem", port:"COM2", baud:11200)
		when:
			to ConnectionPage
			connectionList.btnCreateRoute.click()
		then:
			waitFor('slow') { connectionList.status == "Connected" }
			!connectionList.btnDelete.displayed
	}
	
	def 'The first connection in the connection list page is selected'() {
		when: 
			createTestEmailConnection()
			to ConnectionPage
		then:
			connectionList.selectedConnection.size() == 1
	}
		
	def 'creating a new fconnection causes a refresh of the connections list'(){
		given:
			createTestEmailConnection()
		when:
			to ConnectionPage
			connectionList.btnNewConnection.click()
		then:
			waitFor('very slow') { at ConnectionDialog }
		when:
			nextPageButton.click()
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "COM2"
			connectionForm.smslibbaud = "9600"
			nextPageButton.click()
		then:
			confirmName.text() == "name"
			confirmPort.text() == "COM2"
			confirmType.text() == "Phone/Modem"
		when:
			doneButton.click()
		then:
			waitFor { selectedConnection.text().contains('name') }
			println "TEXT: ${lstConnections.find('li')*.text()}"
			lstConnections.find('li').size() == 2
	}

	def 'dialog should not close after confirmation screen unless save is successful'(){
		given:
			to ConnectionPage
			connectionList.btnNewConnection.click()
			waitFor { at ConnectionDialog }
			nextPageButton.click()
			connectionForm.smslibname = "name"
			connectionForm.smslibport = "port"
			connectionForm.smslibbaud = "wrongBaud"
			nextPageButton.click()
		when:
			doneButton.click()
		then:
			waitFor{ $('.error-panel').displayed }
			$('.error-panel').text() == 'baud must be a valid number'
			at ConnectionDialog
			confirmName.text() == "name"
			confirmPort.text() == "port"
			confirmType.text() == "Phone/Modem"
	}
	
	def 'can setup a new IntelliSMS account'() {
		when:
			to ConnectionPage
			connectionList.btnNewConnection.click()
		then:
			waitFor { at ConnectionDialog }
		when:
			$("#connectionType").value("intellisms").jquery.trigger("click")

			nextPageButton.click()
			connectionForm.intellismssend = true
			connectionForm.intellismsname = "New IntelliSMS Connection"
			connectionForm.intellismsusername = "test"
			connectionForm.intellismspassword = "1234"
			nextPageButton.click()
		then:
			confirmIntelliSmsConnectionName.text() == "New IntelliSMS Connection"
			confirmIntelliSmsUserName.text() == "test"
			confirmIntelliSmsType.text() == "IntelliSms Account"
		when:
			doneButton.click()
		then:
			waitFor { selectedConnection.text().contains('New IntelliSMS Connection') }
	}
	
	def 'clicking Send test message takes us to a page with default message and empty recieving number field'() {
		given:
			def email = createTestEmailConnection()
		when:
			go "connection/createTest/${email.id}"
		then:
			assertFieldDetailsCorrect('addresses', 'Number', '')
			assertFieldDetailsCorrect('messageText', 'Message', "Congratulations from FrontlineSMS \\o/ you have successfully configured ${email.name} to send SMS \\o/")
	}

	def assertFieldDetailsCorrect(fieldName, labelText, expectedValue) {
		def label = $('label', for:fieldName)
		assert label.text() == labelText
		assert label.@for == fieldName
		def input
		if (fieldName == 'addresses') {
			input = $('input', name: fieldName)
		} else {
			input = $('textarea', name: fieldName)
		}
		assert input.@name == fieldName
		assert input.@id == fieldName
		assert input.@value  == expectedValue
		true
	}

	def createTestConnections() {
		createTestEmailConnection()
		createTestSmsConnection()
	}
	
	def createTestEmailConnection() {
		EmailFconnection.build(name:'test email connection',
				receiveProtocol:EmailReceiveProtocol.IMAPS,
				serverName:'imap.zoho.com', serverPort:993,
				username:'mr.testy@zoho.com', password:'mter')
	}
	
	def createTestSmsConnection() {
		MockModemUtils.initialiseMockSerial([
				COM99:new CommPortIdentifier('COM99', MockModemUtils.createMockPortHandler())])
		SmslibFconnection.build(name:'MTN Dongle', port:'COM99')
	}
}

