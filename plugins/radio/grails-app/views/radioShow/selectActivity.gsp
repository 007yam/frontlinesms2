<%@ page import="frontlinesms2.radio.RadioShow" %>
<g:form controller="radioShow" action="addActivity">
	<g:hiddenField name="activityId" value="${ownerInstance.id}"/>
	<g:select class="radio-show-select" name='radioShowId' value=""
		    noSelection="${['':'Assign to Radio Show...']}"
		    from='${RadioShow.findAll()}'
		    optionKey="id" optionValue="name"/>
</g:form>