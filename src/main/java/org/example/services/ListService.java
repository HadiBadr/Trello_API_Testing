package org.example.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.models.List;
import static io.restassured.RestAssured.given;

public class ListService {

    private final RequestSpecification reqSpec;
    private static final String API_Token = System.getenv("Trello_API_Token");
    private static final String API_KEY = System.getenv("Trello_API_Key");

    public ListService() {
        if (API_Token == null || API_KEY == null) {
            throw new IllegalArgumentException("Environment variables API_Token or API_KEY are not set");
        }
        this.reqSpec = given()
                .baseUri("https://api.trello.com/1")
                .queryParam("key", API_KEY)
                .queryParam("token", API_Token)
                .contentType(ContentType.JSON);
    }

    public Response createlist(List list) {
        return given()
                .spec(reqSpec)
                .body(list)
                .when()
                .post("/lists");
    }

    public Response updatelist(String listId, List list) {
        return given()
                .spec(reqSpec)
                .body(list)
                .when()
                .put("/lists/" + listId);
    }

    public Response getlist(String listId) {
        return given()
                .spec(reqSpec)
                .when()
                .get("/lists/" + listId);
    }
}