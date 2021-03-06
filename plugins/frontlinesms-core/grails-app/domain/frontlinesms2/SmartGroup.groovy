package frontlinesms2

class SmartGroup {
//> STATIC PROPERTIES
	static final String shortName = 'smartgroup'
    static configFields = ['mobile', 'contactName', 'email', 'notes']
//> SMART GROUP PROPERTIES
	/** the name of this smart group itself.  This is mandatory. */
	String name
	
//> SEARCH PARAMS
	String contactName
	String mobile
	String email
	String notes
	
	static hasMany = [customFields: CustomField]
	
	static mapping = {
		customFields cascade: "all-delete-orphan"
	}
	
	static constraints = {
		contactName(nullable:true, validator:atLeastOneSearchParamValidator)
		mobile(nullable:true, validator:atLeastOneSearchParamValidator)
		email(nullable:true, validator:atLeastOneSearchParamValidator)
		notes(nullable:true, validator:atLeastOneSearchParamValidator)
	}
	
	static def atLeastOneSearchParamValidator = { val, obj ->
		obj.contactName || obj.mobile || obj.email || obj.notes || (obj.customFields && obj.customFields.each { it.value })
	}
	
	def getMembers() {
		getMembersByName(null, [:])
	}

	def countMembers() {
		return countMembersByName(null)
	}
	
	def getMembersByName(String searchString, Map pageParams) {
		def query = getMembersByNameQuery(searchString)
		query.where += " ORDER BY c.name ASC"
		Contact.findAll(query.where, query.params)
	}
	
	def countMembersByName(String searchString) {
		def query = getMembersByNameQuery(searchString)
		Contact.executeQuery("SELECT COUNT(c) $query.where", query.params)[0]
	}
	
	private def getMembersByNameQuery(String searchString) {
		def w = []
		def p = [:]
		
		if(searchString) {
			w << "(lower(c.name) LIKE lower(:nameSubSearch) OR c.mobile LIKE lower(:nameSubSearch))"
			p.nameSubSearch = "%$searchString%"
		}
		
		if(contactName) {
			w << "lower(c.name) LIKE lower(:contactName)"
			p.contactName = "%$contactName%"
		}
		
		if(mobile) {
			w << "c.mobile LIKE :mobile"
			p.mobile = "$mobile%"
		}
		
		if(email) {
			w << "lower(c.email) LIKE lower(:email)"
			p.email = "%$email%"
		}
		
		if(notes) {
			w << "lower(c.notes) LIKE lower(:notes)"
			p.notes = "%$notes%"
		}
		
		if(customFields) {
			customFields.each {
				// FIXME potential for injection via it.name?
				w << "c IN (SELECT DISTINCT cf.contact FROM CustomField AS cf WHERE \
cf.name=:custom_${it.name.replaceAll(' ', '_')}_name AND LOWER(cf.value) LIKE LOWER(:custom_${it.name.replaceAll(' ', '_')}_value))"
				p."custom_${it.name.replaceAll(' ', '_')}_name" = it.name
				p."custom_${it.name.replaceAll(' ', '_')}_value" = "%$it.value%"
			}
		}
		
		def where = w.join(' AND ')
		return [where:"FROM Contact AS c WHERE $where", params:p]
	}

	static def getMembersByNameIlike(id, String searchString, Map pageParams) {
		SmartGroup.get(id)?.getMembersByName(searchString, pageParams)
	}

	static def countMembersByNameIlike(id, String searchString) {
		SmartGroup.get(id)?.countMembersByName(searchString) ?: 0
	}
	
	static HashMap<String, List<String>> getGroupDetails() {
		SmartGroup.list().collectEntries { ["smartgroup-$it.id".toString(), [name:it.name,addresses:it.addresses]] }
	}
	
	def getAddresses() {
		(getMembers()*.mobile) - [null, '']
	}
}
