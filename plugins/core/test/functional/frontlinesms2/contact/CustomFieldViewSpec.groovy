package frontlinesms2.contact

import frontlinesms2.*

import geb.Browser

class CustomFieldViewSpec extends ContactBaseSpec {
	def setup() {
		createTestContacts()
		createTestCustomFields()
	}
	
	def "'add new custom field' is shown in dropdown and redirects to create page"() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactAll, bob
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with no value for that contact are shown in dropdown'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactAll, bob
		then:
			singleContactDetails.customFields == ['na', 'lake', 'add-new']
	}

	def 'custom fields with value for that contact are shown in list of details'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactAll, bob
		then:
			singleContactDetails.contactsCustomFields == ['town']
	}

	def 'clicking an existing custom field in dropdown adds it to list with blank value'() {
		given:
			def bob = Contact.findByName("Bob")
		when:
			to PageContactAll, bob
		then:
			singleContactDetails.addCustomField 'lake'
			def customFeild = singleContactDetails.customField 'lake'
			customFeild.displayed
			customFeild.value() == ""
	}

	def 'clicking X next to custom field in list removes it from visible list, but does not change database iff no other action is taken'() {
		when:
			def bob = Contact.findByName("Bob")
			bob.addToCustomFields(name:'lake', value: 'Erie').save(failOnError: true, flush: true)
			def originalFields = bob.customFields
			to PageContactShowBob
			def lstFields = $("#custom-field-list")
			assert lstFields.children().children('label').size() == 2
			lstFields.find('a').first().click()
			def lstUpdatedFields = $("#custom-field-list")
		then:
			lstUpdatedFields.children().children('label').size() == 1
			lstUpdatedFields.children().children('label').text() == 'town'
			bob.refresh().customFields == originalFields
	}

	def 'clicking X next to custom field in list then saving removes it from  database'() {
		when:
			to PageContactShowBob
			def lstFields = $("#custom-field-list")
			lstFields.find('a').first().click()
			$("#contact-editor #update-single").click()
		then:
			waitFor { !CustomField.findByContact(Contact.findByName("Bob")) }
	}

	def 'clicking save actually adds field to contact in database iff value is filled in'() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('lake').click()
			def inputField =  $("#contact-editor").find('input', name:'lake')
			inputField.value('erie')
			$("#contact-editor #update-single").click()
			go "contact/show/${bob.id}"
			def updatedList = $("#custom-field-list").children().children('label').collect() { it.text() }
		then:
			updatedList == ['lake', 'town']
	}

	def "clicking save doesn't add field to contact in database if there is a blank value for field"() {
		when:
			def bob = Contact.findByName("Bob")
			go "contact/show/${bob.id}"
		then:
			at PageContactShowBob
		when:
			fieldSelecter.value('lake')
			$("#contact-details #update-single").click()
		then:
			bob.refresh()
			bob.customFields.name == ['town']
	}
}

