<%@ page contentType="text/html;charset=UTF-8" %>
<g:if test="${contactInstanceTotal > 0}">
	<ul id="main-list">
		<g:if test="${contactInstanceTotal}">
			<li>
				<fsms:checkBox name="contact-select" class="contact-select" id="contact-select-all" value="0" checked="false" onclick="check_list.checkAll('contact')" />
				<label for="contact-select" class="contact-name"><g:message code="contact.name.label"/></label>
			</li>
		</g:if>
		<g:if test="${contactInstance && !contactInstance.id}">
			<li class="selected" id="newContact">
				<g:checkBox disabled="disabled" class="contact-select" name='new-contact-select'/>
				<a disabled="disabled" class="contact-name" href="#"><g:message code="contact.new"/></a>
			</li>
		</g:if>
		<g:each in="${contactInstanceList}" status="i" var="c">
			<li class="${c.id==contactInstance?.id? 'selected': ''} contact-preview" id="contact-${c.id}">
				<g:checkBox name="contact-select" class="contact-select contact-select-checkbox" id="contact-select-${c.id}"
						checked="${params.checkedId==c.id}" value="${c.id}" onclick="check_list.itemCheckChanged('contact', ${c.id})"/>
				<g:if test="${contactsSection instanceof frontlinesms2.Group}">
					<g:set var="contactLinkParams" value="[groupId:contactsSection.id]"/>
				</g:if>
				<g:elseif test="${contactsSection instanceof frontlinesms2.SmartGroup}">
					<g:set var="contactLinkParams" value="[smartGroupId:contactsSection.id]"/>
				</g:elseif>
				<g:else><g:set var="contactLinkParams" value="[:]"/></g:else>
				<g:link class="displayName-${c.id} contact-name" action="show" params="${contactLinkParams + [contactId:c.id, sort:params.sort, offset:params.offset]}">
					${c.name?:c.mobile?.toPrettyPhoneNumber()?:'[No Name]'}
				</g:link>

			</li>
		</g:each>
	</ul>
</g:if>
<g:else>
	<div id="main-list">
		 <p class="no-content"><g:message code="contact.list.no.contact"/></p>
	</div>
</g:else>

