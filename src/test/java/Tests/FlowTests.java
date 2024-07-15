package Tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.example.models.Card;
import org.example.services.CardService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.expect;
import static org.testng.Assert.assertEquals;

public class FlowTests {
    private ResponseSpecification cardResSpecs;
    private CardService cardService;

    @BeforeMethod
    public void setup() {
        cardService = new CardService();
        cardResSpecs = expect().statusCode(200).contentType(ContentType.JSON);
    }

    @Test(priority = 7)
    public void createAndUpdateCardFlow()  {
        // Create a card
        Card testCard = new Card();
        testCard.setName("Create and Update Test Card");
        testCard.setDesc("Description for create and update flow");
        testCard.setIdList("6690a1208aa6abaaedf3c161");
        testCard.setIdBoard("6690848c2c2723bfbc9f6d74");

        Response createResponse = cardService.createCard(testCard);
        Card createdCard = createResponse.then().spec(cardResSpecs).extract().as(Card.class);

        // Assert creation success
        assertEquals(createdCard.getIdList(), testCard.getIdList());
        assertEquals(createdCard.getName(), testCard.getName());

        // Update the card
        createdCard.setName("Updated Test Card");
        Response updateResponse = cardService.updateCard(createdCard.getId(), createdCard);
        Card updatedCard = updateResponse.then().spec(cardResSpecs).extract().as(Card.class);

        // Assert update success
        assertEquals(updatedCard.getName(), createdCard.getName());

        // Clean up: Delete the card
        Response deleteResponse = cardService.deleteCard(createdCard.getId());
        deleteResponse.then().spec(cardResSpecs).statusCode(200);
    }
}
