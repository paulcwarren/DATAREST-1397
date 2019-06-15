package com.example.demo;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class DemoApplicationTests {

	@Configuration
	static class CorsConfig extends DemoApplication.JpaInfrastructureConfig {

		@Bean
		RepositoryRestConfigurer repositoryRestConfigurer() {

			return RepositoryRestConfigurer.withConfig(config -> {

				// comment this cors configuration back in, re-run the test and it will pass
//				config.getCorsRegistry().addMapping("/books/**") //
//						.allowedMethods("GET", "PUT", "POST") //
//						.allowedOrigins("http://far.far.away");
			});
		}
	}

	@Autowired
	private WebApplicationContext context;

	protected MockMvc mvc;

	@Test
	void contextLoads() throws Exception {
		this.mvc = MockMvcBuilders.webAppContextSetup(context).build();

		mvc.perform(options("/authors").header(HttpHeaders.ORIGIN, "http://not.so.far.away")
				.header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")) //
				.andExpect(status().isOk()) //
				.andExpect(header().longValue(HttpHeaders.ACCESS_CONTROL_MAX_AGE, 1234)) //
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://not.so.far.away")) //
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true")) //
				.andExpect(header().string(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "GET,PATCH,POST"));
	}

}
