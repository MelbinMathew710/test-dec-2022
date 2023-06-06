package com.testdec2022.demo;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class HelloWorldApplicationTests {

    @LocalServerPort
    private int port;

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13.2")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Value("${spring.datasource.url}")
    private String dataSourceUrl;

    @BeforeAll
    public static void setUp() {
        postgreSQLContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        postgreSQLContainer.stop();
    }

    @Test
    public void helloWorldTest() {
        given()
                .port(port)
                .when()
                .get("/")
                .then()
                .statusCode(200)
                .body(equalTo("Hello, World!"));
    }
}