package frontlinesms2.camel.clickatell

import frontlinesms2.*
import org.apache.camel.*
import frontlinesms2.camel.exception.*

class ClickatellPostProcessor implements Processor {
	public void process(Exchange exchange) throws Exception {
		def log = { println "ClickatellPostProcessor.process() : $it" }
		log 'ENTRY'
		log "in.body:" + exchange.in.body
		byte[] bytes = exchange.in.getBody(byte[].class);
		log "in.body as byte[]:" + bytes
		String text = new String(bytes, "UTF-8").trim();
		log "in.body as byte[] as String:" + text
		log "in.body got as a string" + exchange.in.getBody(String.class)
		if(text ==~ "ID:.*") log "message sent successfully"
		else {
			def m = (text =~ /ERR:\s*(\d+),\s*(.*)/)
			if(m.matches()) {
				switch (m[0][1]) {
					case "001"://Invalid Login
						throw new AuthenticationException("Clickatell gateway error: ${m[0][2]}")
					case "108"://Missing or invalid Api_Id
						throw new InvalidApiIdException("Clickatell gateway error: ${m[0][2]}")
					default:
						throw new RuntimeException("Clickatell gateway error: ${m[0][2]}")
				}
			} else throw new RuntimeException("Unexpected response from Clickatell gateway: $text")
		}
		log 'EXIT'
	}
}