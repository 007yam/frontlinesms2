<table id="main-list" class="archive">
	<thead>
		<tr>
			<th class="name"><g:message code="archive.activity.name"/></th>
			<th><g:message code="archive.activity.type"/></th>
			<th><g:message code="archive.activity.date"/></th>
			<th><g:message code="archive.activity.messages"/></th>
		</tr>
	</thead>
	<tbody>
		<g:if test="${activityInstanceTotal > 0}">
			<g:each in="${activityInstanceList}" var="a">
				<tr>
					<td>
						<g:link controller="archive" action="${a.shortName}" params="[messageSection:'activity', ownerId:a.id, viewingMessages:true]">
							${a.name}
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a.shortName}" params="[messageSection:'activity', ownerId:a.id, viewingMessages:true]">
							<g:message code="${a.shortName}.label"/>
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a.shortName}" params="[messageSection:'activity', ownerId:a.id, viewingMessages:true]">
							<fsms:unbroken>
								<g:formatDate date="${a.dateCreated}"/>
							</fsms:unbroken>
						</g:link>
					</td>
					<td>
						<g:link controller="archive" action="${a.shortName}" params="[messageSection:'activity', ownerId:a.id, viewingMessages:true]">
							${a.liveMessageCount}
						</g:link>
					</td>
				</tr>
			</g:each>
		</g:if>
		<g:else>
			<tr class="no-content">
				<td colspan="4">
					<g:message code="archive.activity.list.none"/>
				</td>
			</tr>
		</g:else>
	</tbody>
</table>

