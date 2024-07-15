package Tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.example.models.Card;
import org.example.services.CardService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.example.utils.JSONReader;

import java.io.IOException;

import static io.restassured.RestAssured.expect;
import static org.hamcrest.Matchers.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class CardTests {

    private ResponseSpecification cardResSpecs;
    private CardService cardService;

    @BeforeMethod
    public void setup() {
        cardService = new CardService();
        cardResSpecs = expect().statusCode(200).contentType(ContentType.JSON);
    }

    @DataProvider(name = "cardData")
    public Object[][] cardData() {
        return new Object[][] {
                {"Test Card 1", "Description for test card 1", "6690a1208aa6abaaedf3c161", "6690848c2c2723bfbc9f6d74"},
                {"Test Card 2", "Description for test card 2", "6690a1208aa6abaaedf3c161", "6690848c2c2723bfbc9f6d74"},
                // Add more test data sets as needed
        };
    }

    @Test(dataProvider = "cardData", priority = 1)
    public void createCardTest(String name, String desc, String idList, String idBoard) throws IOException {
        Card createdCard = new Card();
        createdCard.setName(name);
        createdCard.setDesc(desc);
        createdCard.setIdList(idList);
        createdCard.setIdBoard(idBoard);

        // Create card
        Response cardResponse = cardService.createCard(createdCard);
        createdCard = cardResponse.then().spec(cardResSpecs).extract().as(Card.class);

        assertNotNull(createdCard.getId(), "Card ID should not be null");

        // Write created card to JSON file
        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedCreateCardResponse.json", createdCard);
    }

    @Test(priority = 2, dependsOnMethods = "createCardTest")
    public void getCardTest() throws IOException {
        // Read expected card from JSON
        Card expectedCard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateCardResponse.json", Card.class);

        // Retrieve card by ID
        Response response = cardService.getCard(expectedCard.getId());
        Card fetchedCard = response.then().spec(cardResSpecs).extract().as(Card.class);

        // Validate fetched card details
        assertEquals(fetchedCard.getId(), expectedCard.getId());
        assertEquals(fetchedCard.getIdList(), expectedCard.getIdList());
        assertEquals(fetchedCard.getName(), expectedCard.getName());
        assertEquals(fetchedCard.getDesc(), expectedCard.getDesc());
    }

    @Test(priority = 3, dependsOnMethods = "createCardTest")
    public void updateCardTest() throws IOException {
        // Read created card from JSON
        Card createdCard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateCardResponse.json", Card.class);

        // Update card details
        createdCard.setName("Updated Test Card");
        Response response = cardService.updateCard(createdCard.getId(), createdCard);
        Card updatedCard = response.then().spec(cardResSpecs).extract().as(Card.class);

        // Write updated card to JSON file
        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedUpdateCardResponse.json", updatedCard);

        // Validate update success
        assertEquals(updatedCard.getName(), createdCard.getName());
        assertEquals(updatedCard.getDesc(), createdCard.getDesc());

    }

    @Test(priority = 4, dependsOnMethods = "createCardTest")
    public void deleteCardTest() throws IOException {
        // Read updated card from JSON
        Card updatedCard = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreateCardResponse.json", Card.class);

        // Delete card
        Response response = cardService.deleteCard(updatedCard.getId());
        response.then().spec(cardResSpecs);

        // Ensure the card is deleted
        Response getResponse = cardService.getCard(updatedCard.getId());
        getResponse.then().statusCode(404); // Assuming Trello returns 404 for deleted cards
    }




}