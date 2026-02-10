package com.example.api_gateway;

import com.example.api_gateway.filter.JwtValidationGatewayFilterFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public RouteLocator gatewayRoutes(RouteLocatorBuilder builder, JwtValidationGatewayFilterFactory jwtValidationFilter) {
		return builder.routes()
			.route("auth-service-route", r -> r
				.path("/login", "/validate")
				.uri("http://auth-service:4005"))
			.route("auth-service-auth-prefix-route", r -> r
				.path("/auth/**")
				.filters(f -> f.stripPrefix(1))
				.uri("http://auth-service:4005"))
			.route("patient-service-route", r -> r
				.path("/api/patients", "/api/patients/", "/api/patients/**")
				.filters(f -> f
					.stripPrefix(1)
					.filter(jwtValidationFilter.apply(new Object())))
				.uri("http://patient-service:4000"))
			.route("api-docs-patient-route", r -> r
				.path("/api-docs/patients")
				.filters(f -> f.rewritePath("/api-docs/patients", "/v3/api-docs"))
				.uri("http://patient-service:4000"))
			.route("api-docs-auth-route", r -> r
				.path("/api-docs/auth")
				.filters(f -> f.rewritePath("/api-docs/auth", "/v3/api-docs"))
				.uri("http://auth-service:4005"))
			.build();
	}

	@Bean
	public WebClient.Builder webClientBuilder() {
		return WebClient.builder();
	}
}
