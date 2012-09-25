<div class="content ${contactsSection?.shortName?:'all'}">
	<g:if test="${contactsSection instanceof frontlinesms2.Group || contactsSection instanceof frontlinesms2.SmartGroup}">
		<h1>${contactsSection.name} (${contactInstanceTotal})</h1>
		<div class="header-buttons">
			<g:if test="${contactsSection instanceof frontlinesms2.Group}">
				<g:select class="dropdown" name="group-actions" from="${[message(code: 'group.rename'), message(code: 'group.delete')]}"
							keys="${['rename', 'delete']}"
							noSelection="${['': g.message(code:'group.moreactions')]}"/>
			</g:if>
			<g:else>
				<g:select class="dropdown" name="group-actions" from="${[message(code: 'group.rename'), message(code: 'group.edit'), message(code: 'group.delete')]}"
							keys="${['rename', 'edit', 'delete']}"
							noSelection="${['': g.message(code:'group.moreactions')]}"/>
			</g:else>
			<g:remoteLink class="btn" controller="export" action="contactWizard" params="[groupId:contactsSection?.id, contactsSection:contactsSection.shortName]" onSuccess="launchSmallPopup(i18n('smallpopup.contact.export.title'), data, i18n('action.export'))"><g:message code="action.export"/></g:remoteLink>
		</div>
	</g:if>
	<g:else>
		<h1><g:message code="contact.all.contacts" args="${[contactInstanceTotal]}"/></h1>
		<div class="header-buttons">
			<g:remoteLink class="btn" controller="export" action="contactWizard" onSuccess="launchSmallPopup(i18n('smallpopup.contact.export.title'), data, i18n('action.export'))"><g:message code="action.export"/></g:remoteLink>
		</div>
	</g:else>
</div>

