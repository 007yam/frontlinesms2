<%@ page import="frontlinesms2.*" %>
<div class="confirm">
	<h2><g:message code="connection.confirm.header"/></h2>
	
	<g:each in="${Fconnection.getImplementations(params)}">
		<fsms:confirmTable instanceClass="${it}"/>
	</g:each>
</div>

