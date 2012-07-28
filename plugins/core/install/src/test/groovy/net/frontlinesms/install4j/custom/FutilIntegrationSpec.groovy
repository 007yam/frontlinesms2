package net.frontlinesms.install4j.custom

import spock.lang.*

class FutilIntegrationSpec extends Specification {
	def setup() {
		// make sure the registration file does not exist
		def regFile = getRegistrationFile()
		if(regFile.exists()) regFile.delete()
		assert !regFile.exists()
	}

	def 'createRegistrationPropertiesFile should create parent folder if it doesnt exist'() {
		given:
			def settings = Futil.getResourceDirectory()
			assert settings.deleteDir()
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			settings.exists()
	}

	def 'registration file should be created at the approprtate location'() {
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			getRegistrationFile().exists()
	}

	def 'registration properties file should have some content'() {
		when:
			Futil.createRegistrationPropertiesFile('1234', true)
		then:
			getRegistrationFile().text
	}

	def "isRegistered writes correct value to file"(){
		when:
			Futil.setRegistered(writtenValue)
		then:
			getRegistrationFile().text.contains("registered="+readValue)
		where:
			writtenValue|	readValue
			"true"		|	"true"
			"false"		|	"false"
			"23234"		|	"23234"
	}

	private def getRegistrationFile() {
		new File(Futil.getResourceDirectory(), 'registration.properties')
	}
}

