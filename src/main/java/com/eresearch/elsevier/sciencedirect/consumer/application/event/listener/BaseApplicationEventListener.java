package com.eresearch.elsevier.sciencedirect.consumer.application.event.listener;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import lombok.extern.log4j.Log4j;

/**
 * This is a basic application listener which logs all application events which
 * take place. It is more used for investigation & logging purposes.
 * 
 * Listens for every produced event.
 * 
 * @author chriniko
 *
 */
@Log4j
public class BaseApplicationEventListener implements ApplicationListener<ApplicationEvent> {

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// Note: enrich this functionality based on your needs...
		log.info("#### > " + event.getClass().getCanonicalName());
	}

}
