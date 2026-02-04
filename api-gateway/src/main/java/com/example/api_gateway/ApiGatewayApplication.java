package com.example.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
		return builder.routes()
			.route("patient-service-route", r -> r
				.path("/api/patients", "/api/patients/", "/api/patients/**")
				.filters(f -> f.stripPrefix(1))
				.uri("http://patient-service:4000"))
			.route("api-docs-patient-route", r -> r
				.path("/api-docs/patients")
				.filters(f -> f.rewritePath("/api-docs/patients", "/v3/api-docs"))
				.uri("http://patient-service:4000"))
			.build();
	}
}
