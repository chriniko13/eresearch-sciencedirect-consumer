package com.eresearch.elsevier.sciencedirect.consumer.application.event.listener;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.log4j.Log4j;

/**
 * This listener listens for the event which is produced after the bean
 * definitions.
 * 
 * @author chriniko
 *
 */
@Log4j
public class ApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {

	@Override
	public void onApplicationEvent(ApplicationPreparedEvent event) {
		// Note: add functionality according to your needs...
		log.info("~~~Application Bean Definitions Completed~~~");
	}

}
