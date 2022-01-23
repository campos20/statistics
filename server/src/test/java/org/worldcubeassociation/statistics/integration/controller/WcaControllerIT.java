package org.worldcubeassociation.statistics.integration.controller;

import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.worldcubeassociation.statistics.integration.AbstractTest;

import java.util.Map;
import java.util.stream.Stream;

import static io.restassured.RestAssured.given;

@DisplayName("WCA")
public class WcaControllerIT extends AbstractTest {
    private static final String BASE_PATH = "/wca/";

    @DisplayName("WCA authentication")
    @MethodSource("authenticationArguments")
    @ParameterizedTest(name = "index {0} status {1} params {2} reason {3}")
    public void authentication(int index, HttpStatus status, Map<String, Object> params, String reason) {
        Response response = given()
                .spec(super.SPEC)
                .when()
                .queryParams(params)
                .get(BASE_PATH + "authentication")
                .then()
                .statusCode(status.value())
                .extract()
                .response();

        super.validateResponse(index, response);
    }

    private static Stream<Arguments> authenticationArguments() {
        return Stream.of(
                Arguments.of(0, HttpStatus.OK, Map.of("frontendHost", "https://statistics.worldcubeassociation.org/"), "Happy path"),
                Arguments.of(1, HttpStatus.OK, Map.of("frontendHost", "http://localhost:3000"), "Happy path for local test"),
                Arguments.of(2, HttpStatus.BAD_REQUEST, Map.of("frontendHost", ""), "No frontend host")
        );
    }

    @DisplayName("WCA user info")
    @MethodSource("userInfoArguments")
    @ParameterizedTest(name = "index {0} status {1} token {2} reason {3}")
    public void userInfo(int index, HttpStatus status, String token, String reason) {
        Response response = given()
                .spec(super.SPEC)
                .header("Authorization", token)
                .when()
                .get(BASE_PATH + "user")
                .then()
                .statusCode(status.value())
                .extract()
                .response();

        super.validateResponse(index, response);
    }

    private static Stream<Arguments> userInfoArguments() {
        return Stream.of(
                Arguments.of(0, HttpStatus.OK, "Bearer token", "Happy path"),
                Arguments.of(1, HttpStatus.UNAUTHORIZED, "", "Unauthorized")
        );
    }

}
