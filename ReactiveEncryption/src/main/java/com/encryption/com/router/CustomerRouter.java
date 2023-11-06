package com.encryption.com.router;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.encryption.com.service.CustomerHandler;

@Configuration
public class CustomerRouter {

	@Autowired
	private CustomerHandler customerHandler;

    @Bean
    RouterFunction<ServerResponse> showMessage(){
		return RouterFunctions
				.route().POST("/encrypt", customerHandler::saveCustomerEncryption).build()
				.andRoute(RequestPredicates.GET("/customer/{id}").and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), customerHandler::getCustomerDecryption);
	}
}
