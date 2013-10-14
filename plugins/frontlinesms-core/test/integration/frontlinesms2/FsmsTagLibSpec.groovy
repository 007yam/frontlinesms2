package frontlinesms2

import spock.lang.*
import grails.plugin.spock.*

class FsmsTagLibSpec extends GroovyPagesSpec {
	def "confirmTypeRow generates a row that is properly internationalized"() {
		setup:
			def clazz = new TestFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTypeRow instanceClass="${clazz}" />'
		then:
			output.contains '<tr><td class="field-label">smslib.type.label</td><td id="confirm-type"></td></tr>'
	}
	
	def "INPUTS should generate input fields for all configFields"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.every {type -> output.contains "name=\"${clazz.shortName + type}\"" }
	}
	
	def "INPUT creates a textfield for a string field"() {
		setup:
			def clazz = new SmslibFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="name" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains 'input type="text" field="name"'
	}
	
	def "INPUT creates a select dropdown for an enum object"() {
		setup:
			def clazz = new EmailFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="receiveProtocol" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains 'select name="emailreceiveProtocol" field="receiveProtocol"'
			output.contains '</select>'
	}
	
	def "INPUT creates a checkbox for boolean fields"() {
		setup:
			def clazz = new IntelliSmsFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:input field="sendEnabled" instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			output.contains 'input type="checkbox" name="intellismssendEnabled"'
	}
	
	def "INPUTS generates subsections for a field Map"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", sendEnabled:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
			def configFields = clazz.configFields
		then:
			configFields.each {k,v -> if(v) output.contains("<div id=\"$k-subsection\">")}
			clazz.configFields.sendEnabled.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
			clazz.configFields.receiveEnabled.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
	}
	
	def "confirmTable generates all the details of a CrazyFconnection"() {
		setup:
			def clazz = new CrazyFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.each { k,v -> 
				output.contains("confirm-$k")
				v?.each { output.contains "confirm-$it"}
			}
	}
	
	def "confirmTable generates all the details of an intellismsFconnection"() {
		setup:
			def clazz = new IntelliSmsFconnection(name:"Test", sendEnabled:true, username:"test_acc", password:"test")
		when:
			params = [clazz:clazz]
			template = '<fsms:confirmTable instanceClass="${clazz.class}" instance="${clazz}" />'
		then:
			clazz.configFields.each { k,v -> 
				output.contains("confirm-$k")
				v?.each { output.contains "confirm-$it"}
			}
	}
	
	def "INPUTS generates subsections for a CrazyFconnection"() {
		setup:
			def clazz = new CrazyFconnection()
		when:
			params = [clazz:clazz]
			template = '<fsms:inputs instanceClass="${clazz.class}" instance="${clazz}" />'
			def configFields = clazz.configFields
		then:
			configFields.each {k,v -> if(v) output.contains("<div id=\"$k-subsection\">")}
			clazz.configFields.mobicash.each {k,v -> output.contains "field=\"$k\" class=\"$k-subsection-member\""}
			clazz.configFields.mobicash.bank.each { output.contains "field=\"$it\" class=\"$it-subsection-member\""}
	}
	
}

class TestFconnection {
	String name
	static configFields = ["name"]
	String simpleName = "smslibfconnection"
	static String getShortName() { 'smslib' }
}

class CrazyFconnection {
	static configFields = [
			squid:null,
			mobicash:[
						bank:['accountNumber', 'sortCode'],
						handphone:['network', 'emailReceiveProtocol']]]
	static typeFields = ["mobicash", "bank", "handphone"]
	static String getShortName() { 'crazy' }
	static constraints = [
		squid:[
			blank:false,
			nullable:false
		]
	].withDefault { [nullable:false, blank:true]}
	
	boolean squid
	String simpleName = "crazyfconnection"
	String name
	String bank
	String handphone
	String accountNumber
	String sortCode
	String network
	String emailReceiveProtocol
	
	boolean mobicash
}


