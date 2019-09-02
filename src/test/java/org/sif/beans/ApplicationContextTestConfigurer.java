package org.sif.beans;

import org.sif.beans.persistence.jpa.PersistenceManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackages = "org.sif.beans")
public class ApplicationContextTestConfigurer {

	@Bean
	PersistenceManager getPersistenceManager() {
		return mock(PersistenceManager.class);
	}
}
