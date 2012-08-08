package frontlinesms2.controller

import frontlinesms2.*

class FolderControllerISpec extends grails.plugin.spock.IntegrationSpec {
	def controller
	def trashService

	def setup() {
		controller = new FolderController()
		controller.trashService = trashService
	}
	
	def "can delete a folder to send it to the trash"() {
		setup:
			def folder = new Folder(name: 'Who is badder?').save(failOnError:true, flush:true)
		when:
			assert Folder.findAllByDeleted(false) == [folder]
			controller.params.id  = folder.id
			controller.delete()
		then:
			Folder.findAllByDeleted(true) == [folder]
			Folder.findAllByDeleted(false) == []
	}
	
	def "can restore a folder to move out of the trash"() {
		setup:
			def folder = new Folder(name: 'Who is badder?')
			folder.deleted = true
			folder.save(failOnError:true, flush:true)
		when:
			assert Folder.findAllByDeleted(true) == [folder]
			controller.params.id  = folder.id
			controller.restore()
		then:
			Folder.findAllByDeleted(false) == [folder]
			Folder.findAllByDeleted(true) == []
	}

	def "Restoring a folder restores its messages as well"() {
		setup:
			def folder = new Folder(name: 'test')
			def message = new Fmessage(src:'Bob', text:'I like manchester', inbound:true, date: new Date()).save(failOnError: true, flush:true)
			folder.addToMessages(message)
			folder.save(failOnError:true, flush:true)
			folder.deleted = true
			folder.save(failOnError:true, flush:true)
		when:
			assert Folder.findAllByDeleted(true) == [folder]
			controller.params.id  = folder.id
			controller.restore()
		then:
			Folder.findAllByDeleted(false) == [folder]
			Folder.findAllByDeleted(true) == []
			folder.getFolderMessages([:]).find {message}

	}
}

