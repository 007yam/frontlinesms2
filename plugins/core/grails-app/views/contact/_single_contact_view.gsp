<div id="single-contact" class="single-contact">
	<g:if test="${contactInstance}">
		<g:hiddenField name="contactId" value="${contactInstance?.id}"/>
	</g:if>
	<g:hiddenField name="groupsToAdd" value=","/>
	<g:hiddenField name="groupsToRemove" value=","/>
	<g:hiddenField name="fieldsToAdd" value=","/>
	<g:hiddenField name="fieldsToRemove" value=","/>
	<table>
		<tr>
			<td><label for="name"><g:message code="contact.name.label"/></label></td>
			<td><g:textField name="name" value="${contactInstance?.name}"/></td>
		</tr>
		<tr>
			<td><label for="mobile"><g:message code="contact.mobile.label"/></label></td>
			<td>
				<g:textField class="numberField" name="mobile" value="${contactInstance?.mobile?.trim()}" onchange="validateMobile(this)"/>
				<g:if test="${contactInstance?.mobile?.trim()}">
					<a class="remove-command not-custom-field" id="remove-mobile">
						<g:message code="contact.remove.mobile"/>
					</a>
					<g:remoteLink class="send-message" controller="quickMessage" action="create" params="[configureTabs: 'tabs-1,tabs-3', recipients: contactInstance?.mobile]" onSuccess="launchMediumWizard(i18n('wizard.send.message.title'), data, i18n('wizard.send'), true);">
						&nbsp;
					</g:remoteLink>
				</g:if>
				<p class="warning" style="display:none"><g:message code="contact.phonenumber.international.warning"/></p>
			</td>
		</tr>
		<tr>
			<td><label for="email"><g:message code="contact.email.label"/></label></td>
			<td>
				<g:textField name="email" value="${contactInstance?.email?.trim()}"/>
				<a class="remove-command not-custom-field" id="remove-email">&nbsp;</a>
			</td>
		</tr>
		<g:each in="${contactFieldInstanceList}" status="i" var="f">
			<tr class="input ${f==fieldInstance? 'selected': ''}">
				<td><label for="custom-field-${f.name}">${f.name}</label></td>
				<td>
					<input type="text" name="${f.name}" id="field-item-${f.name}" value="${f.value}"/>
					<a class="remove-command custom-field" id="remove-field-${f.id}">&nbsp;</a>
				</td>
			</tr>
		</g:each>
		<tr>
			<td></td>
			<td id="info-add" class="button-container">
				<select class="dropdown" id="new-field-dropdown" name="new-field-dropdown">
					<option class="not-field" value="na">
						<g:message code="contact.customfield.addmoreinformation"/>
					</option>
					<g:each in="${uniqueFieldInstanceList}" status="i" var="f">
						<option value="${f}">${f}</option>
					</g:each>
					<option class="create-custom-field" value='add-new'>
						<g:message code="contact.customfield.option.createnew"/>
					</option>
				</select>
			</td>
			<td></td>
		</tr>
		<tr id="note-area" class="input basic-info">
			<td><label for="notes"><g:message code="contact.notes.label"/></label></td>
			<td><g:textArea name="notes" id="notes" value="${contactInstance?.notes}"/></td>
		</tr>
		<tr id="group-section" class="input basic-info">
			<td><label for="groups"><g:message code="contact.groups.label"/></label></td>
			<td>
				<ul id="group-list">
					<g:each in="${contactGroupInstanceList}" status="i" var="g">
						<li class="${g == groupInstance ? 'selected' : ''}" groupName="${g.name}">
							<span>${g.name}</span><a class="remove-command" id="remove-group-${g.id}"><g:message code="contact.remove.from.group"/></a>
						</li>
					</g:each>
					<li id="no-groups" style="${contactGroupInstanceList?'display: none':''}">
						<p><g:message code="contact.notinanygroup.label"/></p>
					</li>
				</ul>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<select id="group-dropdown" name="group-dropdown" class="dropdown" onchange="selectmenuTools.snapback(this)">
					<option class="not-group"><g:message code="contact.add.to.group"/></option>
					<g:each in="${nonContactGroupInstanceList}" status="i" var="g">
						<option value="${g.id}">${g.name}</option>
					</g:each>
				</select>
			</td>
		</tr>
	</table>
	<div id="action-buttons" class="buttons">
		<g:if test="${contactInstance?.id}">
			<g:actionSubmit class="btn" id="update-single" action="update" value="${g.message(code:'action.save')}" disabled="disabled"/>
			<g:link class="cancel btn disabled"><g:message code="action.cancel"/></g:link>
		</g:if>
		<g:else>
			<g:actionSubmit class="btn" action="saveContact" value="${g.message(code:'action.save')}"/>
			<g:link class="cancel btn" action="index"><g:message code="action.cancel"/></g:link>
		</g:else>
		
		<g:if test="${contactInstance?.id}">
			<a id="btn_delete" onclick="launchConfirmationPopup(i18n('smallpopup.contact.delete.title'));" class="btn">
				<g:message code="action.delete"/>
			</a>
		</g:if>
	</div>
	<g:if test="${contactInstance && contactInstance.id}">
		<div id="message-stats">
			<h2><g:message code="contact.messages.label"/></h2>
			<ul>
				<li class="sent"><g:message code="contact.sent.messages" args="${[contactInstance?.outboundMessagesCount]}"/></li>
				<li class="received"><g:message code="contact.received.messages" args="${[contactInstance?.inboundMessagesCount]}"/></li>
			</ul>
			<g:link class="btn search" controller='search' action='result' params="[contactString: contactInstance?.name]">
				<g:message code="contact.search.messages"/>
			</g:link>
		</div>
	</g:if>
</div>
<r:script>
function refreshMessageStats(data) {
	var url = 'contact/messageStats';
	var numSent = $('#num-sent');
	var numRecieved = $('#num-recieved');
	$.getJSON(url_root + url, {id: "${contactInstance?.id}"},function(data) {
		numSent.text(numSent.text().replace(/\d{1,}/, data.outboundMessagesCount));
		numRecieved.text(numRecieved.text().replace(/\d{1,}/, data.inboundMessagesCount));
	});
}

$(function() {
	setInterval(refreshMessageStats, 15000);
});
</r:script>

