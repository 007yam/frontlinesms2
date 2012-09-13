package frontlinesms2.services

import spock.lang.*
import grails.test.mixin.*

import frontlinesms2.*

import org.apache.camel.CamelContext
import org.apache.camel.Exchange

@TestFor(FconnectionService)
@Mock([LogEntry, Dispatch, Fmessage, SystemNotification])
class FconnectionServiceSpec extends Specification {
	def context

	def setup() {
		context = Mock(CamelContext)
		service.camelContext = context
		def i18nUtilService = Mock(I18nUtilService)
		i18nUtilService.getMessage(_) >> { args -> args.code[0] }
		service.i18nUtilService = i18nUtilService
	}

	def 'Unconnected Fconnection gives a status of NOT_CONNECTED'() {
		given:
			context.routes >> []
			def notConnected = Mock(Fconnection)
		when:
			def status = service.getConnectionStatus(notConnected)
		then:
			status == ConnectionStatus.NOT_CONNECTED
	}
	
	def 'Connected Fconnection gives a status of CONNECTED'() {
		given:
			def connected = mockFconnection(1)
			def notConnected = mockFconnection(2)
			def alsoConnected = mockFconnection(3)
			context.routes >> ["in-1", "out-3"].collect { [id:it] }
		when:
			true
		then:
			service.getConnectionStatus(connected) == ConnectionStatus.CONNECTED
			service.getConnectionStatus(notConnected) == ConnectionStatus.NOT_CONNECTED
			service.getConnectionStatus(alsoConnected) == ConnectionStatus.CONNECTED
	}
	
	@Unroll
	def 'test route statuses'() {
		given:
			context.routes >> routeNames.collect { [id:it] }
			def c = mockFconnection(id)
		expect:
			service.getConnectionStatus(c) == expectedStatus
		where:
			id | routeNames                 | expectedStatus
			1  | []                         | ConnectionStatus.NOT_CONNECTED
			1  | ['in-2', 'out-3']          | ConnectionStatus.NOT_CONNECTED
			1  | ['in-1']                   | ConnectionStatus.CONNECTED
			1  | ['out-1']                  | ConnectionStatus.CONNECTED
			1  | ['in-1', 'out-1']          | ConnectionStatus.CONNECTED
			1  | ['in-1', 'out-internet-1'] | ConnectionStatus.CONNECTED
			1  | ['in-1', 'out-modem-1']    | ConnectionStatus.CONNECTED
			1  | ['out-internet-1']         | ConnectionStatus.CONNECTED
			1  | ['out-modem-1']            | ConnectionStatus.CONNECTED
	}

	def 'creating a SMSLib route should stop detection on the corresponding port'() {
		given:
			final String PORT = 'COM77'
			DeviceDetectionService detector = Mock()
			service.deviceDetectionService = detector
			Fconnection f = new SmslibFconnection(port:PORT)
		when:
			service.createRoutes(f)
		then:
			1 * detector.stopFor(PORT)
	}
	
	def 'creating a non-smslib route should not affect the device detector'() {
		given:
			DeviceDetectionService detector = Mock()
			service.deviceDetectionService = detector
			Fconnection f = Mock()
			f.getCamelProducerAddress() >> 'nothing'
		when:
			service.createRoutes(f)
		then:
			0 * detector.stopFor(_)
	}

	def 'Routes supplied by the Fconnection are added to the camel context'() {
		given:
			def c = Mock(Fconnection)
			c.routeDefinitions >> [[id:'in-mock'], [id:'out-mock']]
		when:
			service.createRoutes(c)
		then:
			1 * context.addRouteDefinitions { it*.id.sort() == ['in-mock', 'out-mock'] }
	}
	
	@Unroll
	def 'handleDisconnection should trigger shutdown of related Fconnection'() {
		given:
			int jobRouteId
			RouteDestroyJob.metaClass.static.triggerNow = { Map args ->
				jobRouteId = args.routeId
			}
			Exchange exchange = Mock()
			exchange.fromRouteId >> routeId
		when:
			service.handleDisconnection(exchange)
		then:
			jobRouteId == connectionId
		where:
			routeId          | connectionId
			"out-1"          | 1
			"out-internet-2" | 2
			"out-modem-3"    | 3
			"in-4"           | 4
	}

	@Unroll
	def 'destroyRoutes should stop and remove all relevant routes'() {
		given:
			context.routes >> (relatedRoutes + unrelatedRoutes).collect { [id:it] }
		when:
			service.destroyRoutes(1)
		then:
			relatedRoutes.size() * context.stopRoute(_)
			relatedRoutes.size() * context.removeRoute(_)
		where:
			relatedRoutes      | unrelatedRoutes
			['in-1']           | []
			['out-1']          | []
			['in-1', 'out-1']  | []
			['in-1', 'out-1']  | ['in-2', 'out-3']
			['out-modem-1']    | ['in-2', 'out-modem-3']
			['out-internet-1'] | ['in-2', 'out-modem-3']
			['out-internet-1'] | ['in-2', 'out-internet-3']
	}

	private def mockFconnection(int id) {
		Fconnection c = Mock()
		c.id >> id
		return c
	}
}
