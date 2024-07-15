package Tests;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.example.models.Board;
import org.example.services.BoardService;
import org.example.utils.JSONReader;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;

import static io.restassured.RestAssured.expect;
import static org.testng.Assert.*;

public class BoardTests  {

    private String boardID;
    private BoardService boardService;
    private ResponseSpecification boardResSpecs;


    @BeforeMethod
    public void setup() {
        boardService = new BoardService();
        boardResSpecs = expect().statusCode(200).contentType(ContentType.JSON);
    }

    @DataProvider(name = "boardData")
    public Object[][] boardData() {
        return new Object[][] {
                {"Test Board CREATED", "Description for test board","6690152d94b5acd65d045b25"},
                // Add more test data sets as needed
        };
    }

    @Test(dataProvider = "boardData", priority = 1)
    public void testCreateBoard(String name, String desc,String idOrganization) throws IOException {
        Board newBoard = new Board();
        newBoard.setName(name);
        newBoard.setDesc(desc);
        newBoard.setIdOrganization(idOrganization);

        Response boardResponse = boardService.createBoard(newBoard);
        newBoard = boardResponse.then().spec(boardResSpecs).extract().as(Board.class);

        assertNotNull( newBoard.getId(), "Board ID should not be null");

        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", newBoard);
    }

    @Test(priority = 2)
    public void testRetrieveBoard() throws IOException {
        Board expectedBoard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", Board.class);

        Response boardResponse = boardService.retrieveBoard(expectedBoard.getId());
        Board actualBoard = boardResponse.then().spec(boardResSpecs).extract().as(Board.class);

        assertEquals(actualBoard.getIdOrganization(), expectedBoard.getIdOrganization());
        assertEquals(actualBoard.getName(), expectedBoard.getName());
        assertEquals(actualBoard.getDesc(), expectedBoard.getDesc());

    }

    @Test(priority = 3)
    public void testUpdateBoard() throws IOException {
        Board updatedBoard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", Board.class);
        updatedBoard.setName("Test Board UPDATED");

        Response boardResponse = boardService.updateBoard(updatedBoard);
        updatedBoard = boardResponse.then().spec(boardResSpecs).extract().as(Board.class);

        Board oldBoard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", Board.class);
        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", updatedBoard);

        assertNotEquals(updatedBoard.getName(),oldBoard.getName());
        assertEquals(oldBoard.getDesc(), updatedBoard.getDesc());

    }

    @Test(dependsOnMethods = {"testCreateBoard"},priority = 4)
    public void testDeleteBoard() throws IOException {
        Board badBoard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", Board.class);

        // Delete card
        Response response = boardService.deleteBoard(badBoard);
        response.then().spec(boardResSpecs);

        // Ensure the card is deleted
        Response getResponse = boardService.retrieveBoard(badBoard.getId());
        getResponse.then().statusCode(404); // Assuming Trello returns 404 for deleted cards
    }

    @Test(priority = 5)
    public void createBoardfromSampleInput() throws IOException {
        Board sample = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/sampleInputBoard.json", Board.class);

        Response boardResponse = boardService.createBoard(sample);
        sample = boardResponse.then().spec(boardResSpecs).extract().as(Board.class);

        assertNotNull( sample.getId(), "Board ID should not be null");

        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedCreateBoardResponse.json", sample);

    }
}
