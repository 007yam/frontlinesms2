$(document.documentElement).keyup(function (event) {
	var key;
	if(event) { // mozilla
		key = event.which;
	} else {
		key = event.keyCode;
	}
	
	if(key === 38) {
		showPreviousRow();
		event.stopPropagation();
		return false;
	}
	
	if(key === 40) {
		showNextRow();
		event.stopPropagation();
		return false;
	}
});

function changeRowSelection(oldSelected, newSelected) {
	var newSelectedId;
	newSelectedId = newSelected.attr("id");
	if(newSelectedId) {
		oldSelected.removeClass("selected");
		newSelected.addClass("selected");
		if(newSelectedId.indexOf("activity") === -1) {
			if($("input:hidden[name=messageSection]").val() !== "trash") {
				updateSingleCheckedDetails("message", newSelectedId.substring("message-".length), newSelected);
				oldSelected.find(".message-select-checkbox").prop("checked", false);
				newSelected.find(".message-select-checkbox").prop("checked", true);
			}
		}
	}
}

function showPreviousRow() {
	var selectedRow, previousRow;
	if(countSelectedMessages() === 1) {
		selectedRow = $('tr.selected');
		previousRow = selectedRow.prevAll("tr:first");
		changeRowSelection(selectedRow, previousRow);
	} else { $("#main-list tbody tr:first").addClass("selected"); }
}

function showNextRow() {
	var selectedRow, nextRow;
	if(countSelectedMessages() === 1) {
		selectedRow = $('tr.selected');
		nextRow = selectedRow.nextAll("tr:first");
		changeRowSelection(selectedRow, nextRow);
	} else { $("#main-list tbody tr:first").addClass("selected"); }
}

function countSelectedMessages() {
    return $('tr.selected').size();
}

