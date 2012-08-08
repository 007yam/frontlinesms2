package frontlinesms2.smartgroup

import frontlinesms2.*
import frontlinesms2.popup.*

class SmartGroupCreateSpec extends SmartGroupBaseSpec {
	def 'ADD MORE RULES button is visible in CREATE dialog'() {
		when:
			launchCreateDialog()
		then:
			addRuleButton.displayed
	}
	
	def 'One rule is created by default'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
	}

	def 'FINISH button is enabled by default'() {
		when:
			launchCreateDialog()
		then:
			!submit.disabled
	}

	def 'there is no BACK button'() {
		when:
			launchCreateDialog()
		then:
			previous.disabled
	}
	
	def 'SMART GROUP NAME FIELD is displayed'() {
		when:
			launchCreateDialog(null)
		then:
			smartGroupNameField.displayed
	}
	
	def 'error message is not displayed by default'() {
		when:
			launchCreateDialog(null)
		then:
			!error
	}
	
	def 'Clicking FINISH when no name is defined should display validation error'() {
		when:
			launchCreateDialog(null)
			submit.click()
		then:
			waitFor { error }
	}
	
	def 'ADD MORE RULES button should add one more rule'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:		
			addRule()
		then:
			rules.size() == 2
		when:
			addRule()
		then:
			rules.size() == 3
	}
	
	def 'can add new rule when previous rule does not validate'() {
		when:
			launchCreateDialog()
		then:
			rules.size() == 1
		when:
			addRule()
		then:
			rules.size() == 2
	}
	def "there is no remove button for first rule, except when other rules are displayed"() {
		when:
			launchCreateDialog()
		then:
			!removeRuleButtons[0].displayed
		when:
			addRule()
		then:
			removeRuleButtons[0].displayed
			removeRuleButtons[1].displayed
	}

	def "can remove old rule if it's not the first"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(1)
		then:
			rules.size() == 1
	}
	
	def "can remove first rule if there are other rules"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(0)
		then:
			rules.size() == 1
	}
	
	def "cannot remove lone first rule even if it was previously not first rule"() {
		when:
			launchCreateDialog()
			addRule()
		then:
			rules.size() == 2
		when:
			removeRule(0)
		then:
			rules.size() == 1
		when:
			removeRule(0)
		then:
			rules.size() == 1
	}
	
	def 'selecting PHONE NUMBER should set matcher text to STARTS WITH'() {
		when:
			launchCreateDialog()
		then:
			ruleField[0].value() == 'mobile'
			ruleMatchText[0] == 'starts with'
	}
	
	def 'selecting fields other than PHONE NUMBER should set matcher text to CONTAINS'() {
		when:
			launchCreateDialog()
		then:
			ruleField[0].value() == 'mobile'
			ruleMatchText[0] == 'starts with'
		when:
			ruleField[0].value('Contact name')
		then:
			ruleMatchText[0] == 'contains'
		when:
			ruleField[0].value('Phone number')
		then:
			ruleMatchText[0] == 'starts with'
	}
	
	def 'adding multiple rules on the same field should fail validation'() {
		when:
			launchCreateDialog()
			ruleValues[0].value('+44')
			addRule()
			ruleValues[1].value('+254')
			submit.click()
		then:
			waitFor { error }
	}

	def 'a single empty rule should fail validation'() {
		when:
			launchCreateDialog()
			submit.click()
		then:
			waitFor { error }
	}

	def 'a single empty rule followed by filled rules should fail validation'() {
		when:
			launchCreateDialog()
			addRule()
			ruleField[1].value('Contact name')
			ruleValues[1].value('bob')
			submit.click()
		then:
			waitFor { error }
	}

	def 'filled rule followed by empty rule should fail validation'() {
		when:
			launchCreateDialog()
			ruleField[0].value('Contact name')
			ruleValues[0].value('bob')
			addRule()
			submit.click()
		then:
			waitFor { error }
	}
	
	def 'successfully creating a smart group should show a flash message'() {
		when:
			launchCreateDialog()
			setRuleValue(0, '+44')
			submit.click()
		then:
			waitFor { flashMessage.text().contains('English Contacts') }
	}

	def 'successfully creating a smart group should add it to the smart groups menu'() {
		when:
			to PageSmartGroup
			launchCreateDialog('All the bobs!')
			ruleField[0].value('Contact name')
			setRuleValue(0, 'bob')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('All the bobs!').displayed }
	}
	
	def 'rules should include custom fields'() {
		given:
			['Town', 'Height'].each { CustomField.build(name:it) }
		when:
			launchCreateDialog('Field Dwellers')
		then:
			ruleField[0].find('option')*.text().containsAll(['Town', 'Height'])
	}
	
	def 'setting a custom field rule should persist to the smartgroup instance'() {
		given:
			['Town'].each { CustomField.build(name:it) }
		when:
			launchCreateDialog('Field Dwellers')
			ruleField[0].value('Town')
			setRuleValue(0, 'field')
			submit.click()
		then:
			at PageSmartGroup
			waitFor { bodyMenu.getSmartGroupLink('Field Dwellers') }
		when:
			def g = SmartGroup.findByName('Field Dwellers')
			println([g.name, g.contactName, g.mobile, g.email, g.notes, g.customFields])
		then:
			SmartGroup.findByName('Field Dwellers').customFields.size() == 1
			SmartGroup.findByName('Field Dwellers').customFields.every { it.name=='Town' && it.value=='field' }
	}
}
