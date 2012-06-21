<%@ page import="frontlinesms2.*" %>
<div class="header-buttons">
	<fsms:quickMessage class="section-action-button activity-btn btn"/>
	<g:if test="${params.controller!='archive'}">
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="archive" id="${ownerInstance?.id}"><g:message code="fmessage.activity.archive" args="${[ownerInstance?.shortName]}"/></g:link>
	</g:if>
	<g:else>
		<g:link class="activity-btn btn" controller="${ownerInstance?.shortName}" action="unarchive" id="${ownerInstance?.id}"><g:message code="fmessage.unarchive" args="${[ownerInstance?.shortName]}"/></g:link>
	</g:else>
	<g:select class="dropdown more-actions activity-btn" name="more-actions"  
			from="${['export', 'rename', 'delete'] + (ownerInstance.editable?['edit']:[]) + (ownerInstance instanceof Activity? ['radio.show']:[])}"
			noSelection="${['': g.message(code:'fmessage.moreactions')]}"
			valueMessagePrefix="${ownerInstance.shortName}.moreactions"/>
</div>
<r:script>
function radioShowAction() {
	$.ajax({
		type:'GET',
		url: url_root + 'radioShow/selectActivity',
		data: {ownerId: $("#ownerId").val()},
		success: function(data) {
			launchSmallPopup('Add to Show', data, 'Add');
	}})
}
</r:script>
