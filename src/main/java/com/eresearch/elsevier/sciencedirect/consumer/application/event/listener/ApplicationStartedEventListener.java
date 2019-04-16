package com.eresearch.elsevier.sciencedirect.consumer.application.event.listener;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.log4j.Log4j;

/**
 * This listener listens for the event which is produced at the start of the
 * application.
 * 
 * @author chriniko
 *
 */
@Log4j
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {

	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		// Note: add functionality according to your needs...
		log.info("~~~Application Started(Loading)~~~");
	}

}
