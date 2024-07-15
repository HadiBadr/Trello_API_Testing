package org.example.services;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.example.models.Board;

import static io.restassured.RestAssured.given;

public class BoardService {

    private final RequestSpecification reqSpec;
    private static final String API_Token = System.getenv("Trello_API_Token");
    private static final String API_KEY = System.getenv("Trello_API_Key");


    public BoardService() {

        if (API_Token == null || API_KEY == null) {
            throw new IllegalArgumentException("Environment variables API_Token or API_KEY are not set");
        }
        this.reqSpec = given()
                .baseUri("https://api.trello.com/1")
                .queryParam("key",API_KEY )
                .queryParam("token", API_Token)
                .contentType(ContentType.JSON);
    }

    public Response createBoard(Board board){
        return RestAssured
                .given()
                .spec(reqSpec)
                .body(board)
                .when()
                .post("/boards");
    }

    public Response retrieveBoard(String id){
        return RestAssured
                .given()
                .spec(reqSpec)
                .when()
                .get("/boards/" + id);
    }

    public Response deleteBoard(Board board){
       return   given()
                .spec(reqSpec)
                .when()
                .delete("/boards/" + board.getId());
    }

    public Response updateBoard(Board board){
        return RestAssured
                .given()
                .spec(reqSpec)
                .body(board)
                .when()
                .put("/boards/"+board.getId());
    }

}
