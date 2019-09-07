package com.example.bookshelf;



import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static io.restassured.RestAssured.with;

public class BookshelfAppTest {

    private final static String BOOK_1 =
            "{\"title\": \"Alladyna\"," +
            "\"author\":\"Adam Slodowa\"," +
            "\"publishingHouse\":\"Muza\"," +
            "\"pagesSum\":132," +
            "\"yearOfPublished\":2014}";
    private final static String BOOK_2 =
            "{\"title\": \"Katarynka\"," +
            "\"author\":\"Boleslaw Prus\"," +
            "\"publishingHouse\":\"Nasza ksiegarnia\"," +
            "\"pagesSum\":345," +
            "\"yearOfPublished\":1995}";

    private static final int APP_PORT = 8090;

    private BookshelfApp bookshelfApp;

    @BeforeAll
    public static void beforeAll(){
        RestAssured.port = APP_PORT;
    }

    @BeforeEach
    public void beforeEach() throws IOException {
        bookshelfApp = new BookshelfApp(APP_PORT);
    }

    @AfterEach
    public void afterEach(){
        bookshelfApp.stop();
    }

    @Test
    public void addMethod_correctBody_shouldReturnStatus200() throws Exception {
        with().body(BOOK_1).when().post("/book/add").then().statusCode(200).body(startsWith("Book has been successfully added. id ="));
    }

    @Test
    public void addMethod_fieldTypeMismatch_shouldReturnStatus500() {
        String bookWithFieldTypeMismatch =
                "{\"title\": \"Katarynka\"," +
                "\"author\":\"Boleslaw Prus\"," +
                "\"publishingHouse\":\"Nasza ksiegarnia\"," +
                "\"pagesSum\":page345," +
                "\"yearOfPublished\":1995}";
        with().body(bookWithFieldTypeMismatch).when().post("/book/add").then().statusCode(500);
    }
    @Test
    public void addMethod_unexpectedField_shouldReturnStatus500(){
        with().body("\"numberOfCharpers\":10").when().post("/book/add").then().statusCode(500);
    }

    private long addBookAndGetId(String json){
        String responseText = with().body(json)
                .when().post("/book/add")
                .then().statusCode(200).body(startsWith("Book has been successfully added. id ="))
                .extract().body().asString();

        String idString = responseText.substring(responseText.indexOf("=")+1);

        return Long.parseLong(idString);
    }

    @Test
    public void getMethod_correctBookIdParam_shouldReturnStatus500(){
        long bookId1 = addBookAndGetId(BOOK_1);
        long bookId2 = addBookAndGetId(BOOK_2);

        with().param("id", bookId1 )
                .when().get("/book/get")
                .then().statusCode(200)
                .body("id",equalTo(bookId1))
                .body("title", equalTo("Alladyna"))
                .body("author", equalTo("Adam Slodowa"))
                .body("publishingHouse", equalTo("Muza"))
                .body("pagesSum", equalTo(132))
                .body("yearOfPublished", equalTo(2014));



    }

}
