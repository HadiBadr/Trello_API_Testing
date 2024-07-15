package org.example.services;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.models.Card;
import static io.restassured.RestAssured.given;

public class CardService {

    private final RequestSpecification reqSpec;
    private static final String API_Token = System.getenv("Trello_API_Token");
    private static final String API_KEY = System.getenv("Trello_API_Key");

    public CardService() {

        if (API_Token == null || API_KEY == null) {
            throw new IllegalArgumentException("Environment variables API_Token or API_KEY are not set");
        }

        this.reqSpec = given()
                .baseUri("https://api.trello.com/1")
                .queryParam("key", API_KEY)
                .queryParam("token", API_Token)
                .contentType(ContentType.JSON);
    }

    public Response createCard(Card card) {
        return given()
                .spec(reqSpec)
                .body(card)
                .when()
                .post("/cards");
    }

    public Response updateCard(String cardId, Card card) {
        return given()
                .spec(reqSpec)
                .body(card)
                .when()
                .put("/cards/" + cardId);
    }

    public Response deleteCard(String cardId) {
        return given()
                .spec(reqSpec)
                .when()
                .delete("/cards/" + cardId);
    }

    public Response getCard(String cardId) {
        return given()
                .spec(reqSpec)
                .when()
                .get("/cards/" + cardId);
    }
}