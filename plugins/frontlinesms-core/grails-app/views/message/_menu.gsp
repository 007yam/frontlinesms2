<%@ page contentType="text/html;charset=UTF-8" import="frontlinesms2.Fmessage" %>
<fsms:menu class="messages">
	<fsms:submenu code="fmessage.header" class="messages">
		<fsms:menuitem class="" selected="${messageSection=='inbox'}" controller="message" action="inbox" code="fmessage.section.inbox" entitytype="inbox">
			<fsms:unreadCount unreadCount="${Fmessage.countUnreadMessages()}"/>
		</fsms:menuitem>
		<fsms:menuitem class="" selected="${messageSection=='sent'}" controller="message" action="sent" code="fmessage.section.sent"/>
		<fsms:menuitem class="" selected="${messageSection=='pending'}" controller="message" action="pending" code="fmessage.section.pending"/>
		<fsms:menuitem class="" selected="${messageSection=='trash'}" controller="message" action="trash" code="fmessage.section.trash"/>
	</fsms:submenu>

	<fsms:submenu code="activities.header" class="activities">
		<g:each in="${activityInstanceList}" status="i" var="a">
			<fsms:menuitem class="" selected="${a == ownerInstance}" controller="message" action="activity" code="${a.shortName.toLowerCase()}.title" msgargs="${[a.name]}" params="[ownerId: a.id]" entitytype="activity" entityid="${a.id}">
				<fsms:unreadCount unreadCount="${a.liveMessageCount}"/>
			</fsms:menuitem>
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<g:remoteLink class="btn create" controller="activity" action="create_new_activity" id="create-new-activity" onLoading="showThinking();" onSuccess="hideThinking(); mediumPopup.launchMediumPopup(i18n('popup.activity.create'), data, (i18n('action.next')), chooseActivity);">
						<g:message code="activities.create"/>
			</g:remoteLink>
		</fsms:menuitem>
	</fsms:submenu>
	<fsms:submenu code="folder.header" class="folders">
		<g:each in="${folderInstanceList}" status="i" var="f">
			<fsms:menuitem class="" selected="${f == ownerInstance}" controller="message" action="folder" string="${f.name}" params="[ownerId: f.id]" entitytype="folder" entityid="${f.id}">
				<fsms:unreadCount unreadCount="${f.liveMessageCount}"/>
			</fsms:menuitem>
		</g:each>
		<fsms:menuitem bodyOnly="true" class="create">
			<g:remoteLink class="btn create" controller="folder" action="create" onLoading="showThinking();" onSuccess="hideThinking(); launchSmallPopup(i18n('smallpopup.folder.title'), data, i18n('action.create'),'validate');">
				<g:message code="folder.create"/>
			</g:remoteLink>
		</fsms:menuitem>
	</fsms:submenu>
</fsms:menu>

