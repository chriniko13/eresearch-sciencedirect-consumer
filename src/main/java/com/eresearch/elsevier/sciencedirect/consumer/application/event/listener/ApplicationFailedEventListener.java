package com.eresearch.elsevier.sciencedirect.consumer.application.event.listener;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.log4j.Log4j;

/**
 * This listener listens for the event which is produced in case of exception
 * during the startup.
 * 
 * @author chriniko
 *
 */
@Log4j
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		// Note: add functionality according to your needs...
		log.info("~~~Application Failed Event Recognized~~~");
	}

}
