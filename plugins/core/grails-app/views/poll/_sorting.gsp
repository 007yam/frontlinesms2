<h2><g:message code="poll.sort.header"/></h2>
<div class="info">
	<p><g:message code="poll.sort.description"/></p>
</div>
<div class="input">
	<ul class="select">
		<li>
			<label for="enableKeyword"><g:message code="poll.autosort.no.description"/></label>
			<g:radio name="enableKeyword" value="false" checked="${activityInstanceToEdit?.keyword? activityInstanceToEdit.keyword.value as boolean: true}"/>
		</li>
		<li>
			<label for="enableKeyword"><g:message code="poll.autosort.description"/></label>
			<g:radio name="enableKeyword" value="true" checked="${activityInstanceToEdit?.keyword? activityInstanceToEdit.keyword.value as boolean: false}"/>
			<g:textField name="keyword" id="poll-keyword" disabled="${activityInstanceToEdit && activityInstanceToEdit.keyword? false: true}" value="${activityInstanceToEdit?.keyword?.value}"/>
		</li>
	</ul>
</div>

<r:script>
	var enableKeyword = function() {
			var enabled = $(this).val() == 'true';
			if(enabled) {
				$('#poll-keyword').removeAttr("disabled");
				$('#poll-keyword').addClass("required");
			}
			else {
				$('#poll-keyword').attr("disabled", "disabled");
				$('#poll-keyword').removeClass("required");
				$('#poll-keyword').removeClass("error");
				$("label.error").remove();
			}
		};
	$("input[name='enableKeyword']").live("change", enableKeyword);
</r:script>