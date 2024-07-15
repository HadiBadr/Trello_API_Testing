package Tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.ResponseSpecification;
import org.example.models.List;
import org.example.services.ListService;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.example.utils.JSONReader;

import java.io.IOException;

import static io.restassured.RestAssured.expect;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;


public class ListTests {
    private ResponseSpecification listResSpecs;
    private ListService listService;

    @BeforeMethod
    public void setup() {
        listService = new ListService();
        listResSpecs = expect().statusCode(200).contentType(ContentType.JSON);
    }

    @DataProvider(name = "listData")
    public Object[][] listData() {
        return new Object[][]{
                {"Test list 1", "6690152d94b5acd65d045b25", "6690848c2c2723bfbc9f6d74"},
                // Add more test data sets as needed
        };
    }

    @Test(dataProvider = "listData", priority = 1)
    public void createlistTest(String name, String idOrganization, String idBoard) throws IOException {
        List testlist = new List();
        testlist.setName(name);
        testlist.setIdOrganization(idOrganization);
        testlist.setIdBoard(idBoard);

        // Create list
        Response listResponse = listService.createlist(testlist);
        List createdlist = listResponse.then().spec(listResSpecs).extract().as(List.class);

        assertNotNull(createdlist.getId(), "list ID should not be null");

        // Write created list to JSON file
        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedCreatelistResponse.json", createdlist);
    }

    @Test(priority = 2)
    public void getlistTest() throws IOException {
        // Read expected list from JSON
        List expectedlist = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreatelistResponse.json", List.class);

        // Retrieve list by ID
        Response response = listService.getlist(expectedlist.getId());
        List fetchedlist = response.then().spec(listResSpecs).extract().as(List.class);

        // Validate fetched list details
        assertEquals(fetchedlist.getId(), expectedlist.getId());
        assertEquals(fetchedlist.getId(), expectedlist.getId());
        assertEquals(fetchedlist.getName(), expectedlist.getName());

    }

    @Test(priority = 3)
    public void updatelistTest() throws IOException {
        // Read created list from JSON
        List createdlist = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreatelistResponse.json", List.class);

        // Update list details
        createdlist.setName("Updated Test list");
        Response response = listService.updatelist(createdlist.getId(), createdlist);
        List updatedlist = response.then().spec(listResSpecs).extract().as(List.class);

        // Write updated list to JSON file
        JSONReader.writeJsonFile("src/main/resources/ExpectedResponses/expectedUpdatelistResponse.json", updatedlist);

        // Validate update success
        assertEquals(updatedlist.getName(), createdlist.getName());

    }

    @Test(priority = 4, dependsOnMethods = "createlistTest")
    public void deletelistTest() throws IOException {
        // Read updated card from JSON
        List Badlist = JSONReader.readJsonFile("src/main/resources/ExpectedResponses/expectedCreatelistResponse.json", List.class);
        Badlist.setClosed(true);

        // Delete card
        Response response = listService.updatelist(Badlist.getId(), Badlist);
        response.then().spec(listResSpecs);


        // Ensure the card is deleted
        List updatedlist = response.then().spec(listResSpecs).extract().as(List.class);
        assertEquals(updatedlist.isClosed(), true);
    }






}