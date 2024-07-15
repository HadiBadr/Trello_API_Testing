package Tests;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.example.models.Board;
import org.example.models.Card;
import org.example.models.List;
import org.example.services.BoardService;
import org.example.services.CardService;
import org.example.services.ListService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;


public class NegativeTests {

    private BoardService boardService;
    private CardService cardService;
    private ListService listService;

    @BeforeMethod
    public void setup(){
        boardService = new BoardService();
        cardService = new CardService();
        listService = new ListService();
    }

    @Test
    public void createBoardWithoutName() {
        Board board = new Board();
        Response response = boardService.createBoard(board);
        response.then().statusCode(400);
    }

    @Test
    public void updateNonExistentBoard() {
        String nonExistentBoardId = "nonexistent123";
        String newBoardName = "Nonexistent Board";

        //Response response = boardService.updateBoard(nonExistentBoardId, newBoardName, null);
        //response.then().statusCode(404);
    }
    @Test
    public void createCardWithInvalidListIdTest() {
        // Use invalid list ID
        Card testCard = new Card();
        testCard.setName("Invalid List ID Card");
        testCard.setDesc("Description for card with invalid list ID");
        testCard.setIdList("6690a1208aa6abaaedf3c165"); // Invalid list ID

        Response cardResponse = cardService.createCard(testCard);
        cardResponse.then().statusCode(404); // Expecting a not found status code
    }

    @Test
    public void deleteNonExistingCardTest() {
        // Attempt to delete non-existing card
        String nonExistingCardId = "6590a1208aa6abaaedf3c165";

        Response response = cardService.deleteCard(nonExistingCardId);
        response.then().statusCode(not(equalTo(200))); // Expecting a status code other than 200
    }

    @Test(priority = 5)
    public void createlistWithInvalidListIdTest() {
        // Use invalid list ID
        List testlist = new List();
        testlist.setName("Invalid List ID list");
        testlist.setId("6690a1208aa6abaaedf3c165"); // Invalid list ID

        Response cardResponse = listService.createlist(testlist);
        cardResponse.then().statusCode(400); // Expecting a not found status code
    }

        @Test(priority = 6)
    public void deleteNonExistinglistTest() {
        // Attempt to delete non-existing car
            List nonExistinglistId = new List();
            nonExistinglistId.setId("6590a1208aa6abaaedf3c165");

        Response response = listService.updatelist(nonExistinglistId.getId(),nonExistinglistId);
            response.then().statusCode(not(equalTo(200)));
    }

}
