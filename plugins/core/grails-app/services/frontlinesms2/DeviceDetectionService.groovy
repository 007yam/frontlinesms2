package frontlinesms2

import grails.util.Environment
import net.frontlinesms.messaging.*

class DeviceDetectionService {
	static transactional = true

	def grailsApplication
	def detector
	
	def init() {
		def deviceDetectorListenerService = grailsApplication.mainContext.deviceDetectorListenerService
		detector = new AllModemsDetector(listener: deviceDetectorListenerService)
		
		if(Environment.current != Environment.TEST) detect()
	}

	def detect() {
		detector.refresh()
	}
	
	def reset() {
		detector.reset()
	}

	def getDetected() {
		detector.detectors.collect { DetectedDevice.create(it) }
	}
	
	def stopFor(String port) {
		println "DeviceDetectionService.stopFor($port)..."
		def detectorThread
		detector.detectors.each {
			println "Checking $it.portName..."
			if(it.portName == port) {
				detectorThread = it
			} else println "not the right port."
		}
		if(detectorThread && detectorThread!=Thread.currentThread()) {
			detectorThread.interrupt()
			try { detectorThread.join() } catch(InterruptedException _) {
				// we called interrupt
			}
		}
	}

	def isConnecting(String port) {
		def detectorThread
		detector.detectors.each {
			if(it.portName == port) {
				detectorThread = it
			}
		}
		def threadState
		if(detectorThread && detectorThread!=Thread.currentThread()) {
			threadState = detectorThread.getState()
		}
		return (detectorThread != null && threadState != Thread.State.TERMINATED)
	}
}
