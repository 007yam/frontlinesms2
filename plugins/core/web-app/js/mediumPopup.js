function createModalBox(html) {
	var modalBox = $("<div id='modalBox'><div>");
	modalBox.html(html).appendTo(document.body);
	return modalBox;
}

function launchMediumPopup(title, html, btnFinishedText, submitAction) {
	var modalBox = createModalBox(html);
	modalBox.dialog({
			modal: true,
			width: 675,
			height: 500,
			title: title,
			buttons: [{ text:i18n("action.cancel"), click:cancel, id:"cancel" }, { text:i18n("action.back"), disabled:"true" },
					{ text:btnFinishedText, click:submitAction, id:"submit" }],
			close: function() { $(this).remove(); }
	});
	initializePopup(modalBox);
}

function launchMediumWizard(title, html, btnFinishedText, width, height) {
	launchMediumWizard(title, html, btnFinishedText, width, height, true);
}

function launchMediumWizard(title, html, btnFinishedText, width, height, closeOnSubmit) {
	closeWhenDone = (typeof closeOnSubmit == 'undefined' ? true : closeOnSubmit );
	var modalBox = createModalBox(html);
	$("#messageText").keyup();
	magicwand.init(modalBox.find('select[id^="magicwand-select"]'));
	modalBox.dialog({
		modal: true,
		title: title,
		minWidth: 675,
		minHeight: 500,
		width: width,
		height: height,
		buttons: [
			{ text:i18n("action.cancel"), click: cancel, id:"cancel" },
			{ text:i18n("action.back"), id:"disabledBack", disabled:true },
			{ text:i18n("action.back"), click:prevButton, id:"prevPage" },
			{ text:i18n("action.next"), click:nextButton, id:"nextPage" },
			{ text:btnFinishedText, click:closeWhenDone? submit: submitWithoutClose, id:"submit" }
		],
		close: function() { $(this).remove(); }
	});
	makeTabsUnfocusable();
	validateTabSelections(modalBox);
	changeButtons(getButtonToTabMappings(),  getCurrentTabDom());
	initializeTabContentWidgets();
	initializePopup();
}

function launchHelpWizard(html) {
	var modalBox = createModalBox(html);
	modalBox.addClass("help");
	$("#messageText").keyup();
	var height = $(window).height();
	var dialog = modalBox.dialog({
		modal: true,
		title: i18n("popup.help.title"),
		width: "95%",
		height: height,
		buttons: [
			{ text:i18n("action.close"), click:submit, id:"submit" }
		],
		close: function() { $(this).remove(); }
	});
	$(".ui-dialog").addClass("help");
	initializePopup();
}

function submitWithoutClose() {
	$("#submit").attr('disabled', 'disabled');
	if(tabValidates(getCurrentTab())) {
		$(this).find("form").submit();
	} else {
		$("#submit").removeAttr('disabled');
		$('.error-panel').show();
	}
}

function submit() {
	$("#submit").attr('disabled', 'disabled');
	if(tabValidates(getCurrentTab())) {
		$(this).find("form").submit();
		$(this).dialog('close');
	} else {
		$("#submit").removeAttr('disabled');
		$('.error-panel').show();
	}
}

function prevButton() {
	for (var i = 1; i <= getCurrentTabIndex(); i++) {
		var prevTab = getCurrentTabIndex() - i;
		if ($.inArray(prevTab, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', prevTab);
			break;
		}
	}
}

function nextButton() {
	for (var i = 1; i <= getTabLength(); i++) {
		var nextTab = getCurrentTabIndex() + i;
		if ($.inArray(nextTab, $("#tabs").tabs("option", "disabled")) == -1) {
			$("#tabs").tabs('select', nextTab);
			break;
		}
	}
}

function cancel() {
	$(this).dialog('close');
}

function validateTabSelections(dialog) {
	dialog.find('#tabs').tabs({select: function(event, ui) {
		if(ui.index > getCurrentTabIndex()) {
			validateAllPreviousTabs(ui.index);
			var thisTabValidates = tabValidates(getCurrentTab());
			if(thisTabValidates) {
				changeButtons(getButtonToTabMappings(), ui.index)
				if(thisTabValidates && $('.error-panel'))
					$('.error-panel').hide();
				$(ui.panel).find('input', 'textarea', 'textfield').first().focus();
			} else {
				$('.error-panel').show();
			}
			return thisTabValidates;
		} else {
			changeButtons(getButtonToTabMappings(), ui.index);
			return true;
		}
	}});
}

function tabValidates(tab) {
	return tab.contentWidget('validate');
}

function validateAllPreviousTabs(selectedTabIndex) {
	var i = 0;
	for(i; i < selectedTabIndex; i++) {
		if(!tabValidates($("#tabs").find('.ui-tabs-panel').eq(i))) {
			$('#tabs').tabs('select', i);
			//$('.error-panel').show();
			return false;
		}
	}
}

function changeButtons(buttonToTabMappings, tabIndex) {
	$.each(buttonToTabMappings, function(key, value) {
		if (value.indexOf(tabIndex) != -1)
			$(".ui-dialog-buttonpane #" + key).show()
		else
			$(".ui-dialog-buttonpane #" + key).hide()
	});
}

function range(first, last) {
	var a = [];
	for (var i = first; i <= last; i++)
		a.push(i);
	return a;
}

function makeTabsUnfocusable() {
	$("#tabs").find('input', 'textarea', 'textfield').first().focus();
	$('a[href^="#tabs"]').attr('tabindex', '-1');
}

function getTabLength() {
	return $('#tabs').tabs("length") - 1;
}

function getButtonToTabMappings() {
	return {
			"cancel": range(0, getTabLength()),
			"prevPage": range(1, getTabLength()),
			"nextPage": range(0, getTabLength() - 1),
			"submit": [getTabLength()],
			"disabledBack": [0]
	};
}

function getCurrentTab() {
	var selected = $("#tabs").tabs("option", "selected");
	return $("#tabs").find('.ui-tabs-panel').eq(selected)
}

function getCurrentTabDom() {
	return $('#tabs').tabs('option', 'selected');
}

function getCurrentTabIndex() {
	var tabWidget = $('#tabs').tabs();
	var current = tabWidget.tabs('option', 'selected');
	return current;
}

function initializeTabContentWidgets() {
	for(i=0; i <= getTabLength(); i++) {
		$("#tabs-" + (i + 1)).contentWidget();
	}
}

function disableTab(tabNumber) {
	$('#tabs').tabs("disable", tabNumber);
	$('.tabs-' + (tabNumber + 1)).addClass('disabled-tab');
}

function enableTab(tabNumber) {
	$('#tabs').tabs("enable", tabNumber);
	$('.tabs-' + (tabNumber + 1)).removeClass('disabled-tab');
}

function moveToRelativeTab(offset) {
	$('#tabs').tabs('select', getCurrentTabIndex() + offset);
}

$.widget("ui.contentWidget", {
	validate: function() {
		return this.options['validate'].call();                 
    },

    options: {validate: function() {return true;} }
});

//> FUNCTION-SPECIFIC METHODS
function messageResponseClick(messageType) {
	var configureTabs= "";
	var me = $(this);
	var src;
	if (messageType == 'Reply') {
		configureTabs = "tabs-1, tabs-3, tabs-4";
		var checkedMessageCount = getCheckedItemCount("message");
		if(checkedMessageCount > 0) {
			src = getCheckedList("message");
		}
		else{
			src = $("#message-src").val();
		}
	} else if(messageType == 'Forward') {
		var text = $("#single-message #message-detail-content p").text().trim();
	}
	var messageSection = $('input:hidden[name=messageSection]').val();
	
	$.ajax({
		type:'POST',
		data: {recipients: src, messageText: text, configureTabs: configureTabs},
		url: url_root + 'quickMessage/create',
		success: function(data, textStatus){ launchMediumWizard(messageType, data, i18n('action.send')); }
	});
}

function createSmartGroup() {
	$("#submit").attr('disabled', 'disabled');
	if(validateSmartGroup()) {
		$(this).find("form").submit();
		$(this).dialog('close');
	} else {
		$("#submit").removeAttr('disabled');
		$('.error-panel').show();
	}
}

function editConnection(id) {
	$.ajax({
		url: url_root + "connection/wizard/" + id,
		success: function(data){
			launchMediumWizard(i18n('connection.edit'), data, i18n('action.done'), 675, 500, false);
		}
	});
}
