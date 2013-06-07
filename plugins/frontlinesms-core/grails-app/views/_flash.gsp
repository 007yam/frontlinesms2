<fsms:flashMessage/>
<g:hasErrors bean="${contactInstance}">
	<div class="flash errors">
		<fsms:renderErrors bean="${contactInstance}" as="list"/>
		<a class="hider hide-flash">x</a>
	</div>
</g:hasErrors>

<g:hasErrors bean="${groupInstance}">
	<div class="flash errors">
		<fsms:renderErrors bean="${groupInstance}" as="list"/>
		<a class="hider hide-flash">x</a>
	</div>
</g:hasErrors>

<g:hasErrors bean="${pollInstance}">
	<div class="flash errors">
		<fsms:renderErrors bean="${pollInstance}" as="list"/>
		<a class="hider hide-flash">x</a>
	</div>
</g:hasErrors>
<g:hasErrors bean="${folderInstance}">
	<div class="flash errors">
		<fsms:renderErrors bean="${folderInstance}" as="list"/>
		<a class="hider hide-flash">x</a>
	</div>
</g:hasErrors>
<r:script>
	$(function() {
		$('.hide-flash').live("click", function() {
			$(this).parent("div").slideUp(500);
			return true;
		});
	});
</r:script>
