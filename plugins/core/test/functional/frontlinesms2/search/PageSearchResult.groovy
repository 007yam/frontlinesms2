package frontlinesms2.search

class PageSearchResult extends PageSearch {
	static getUrl() { 'search/result/show' }
	static at = {
		title.startsWith("Results")
	}
	static content = {
		messagesSelect { $(".message-select") }
		archiveAllButton { $('#btn_archive_all') }
		multipleMessagesPanel { $('#multiple-messages') }
		replyToMultipleButton { $('#multiple-messages a')[0] }
		checkedMessageCount {
			def t = $("#checked-message-count").text()
			if(t != null) {
				return t - ' messages selected' as Integer
			} else {
				return $('.message-select:checked').size()
			}
		}
		messageList {$("#message-list tr")}
		displayNameLink { id->
			$("a.displayName-"+ id)
		}
	}
}
