<div id="multiple-contacts" class="multiple-contact" style="display:none">
	<h2 id="checked-contact-count">&nbsp;</h2>
	<table id="multiple-contact-info">
		<tr>
			<td><label for="multi-group-dropdown"><g:message code="contact.groups.header"/></label></td>
			<td>
				<ul id='multi-group-list'>
					<g:each in="${sharedGroupInstanceList}" status="i" var="g">
						<li id="${g.name}" class="${g == groupInstance ? 'selected' : ''}">
							<span>${g.name}</span>
							<a class="remove-group remove-command" id="remove-group-${g.id}"></a>
						</li>
					</g:each>
				</ul>
			</td>
		</tr>
		<tr>
			<td></td>
			<td>
				<g:select name="multi-group-dropdown"
						class="dropdown"
						onchange="selectmenuTools.snapback(this)"
						noSelection="['_':g.message(code:'contact.add.to.group')]"
						from="${nonSharedGroupInstanceList}"
						optionKey="id"
						optionValue="name"/>
			</td>
		</tr>
	</table>
	<div id="action-buttons" class="buttons">
		<g:actionSubmit class="btn" id="update-all" action="updateMultipleContacts" value="${g.message(code:'action.save.all')}" disabled="disabled" />
		<g:link class="cancel btn" disabled="disabled"><g:message code="action.cancel"/></g:link>
		<fsms:popup class="btn" id="btn_delete_all" popupCall="launchConfirmationPopup(i18n('action.delete.all'));">
			<g:message code="action.delete.all"/>
		</fsms:popup>
	</div>
</div>
