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
        //clear storage after each test
        bookshelfApp.requestUrlMapper.getBookController().getBookStorage().getAllBooks().clear();
        bookshelfApp.stop();

    }

    private long addBookAndGetId(String json){
        String responseText = with().body(json)
                .when().post("/book/add")
                .then().statusCode(200).body(startsWith("Book has been successfully added. id ="))
                .extract().body().asString();

        String idString = responseText.substring(responseText.indexOf("=")+1).trim();
        return Long.parseLong(idString);
    }

    @Test
    public void addMethod_correctBody_shouldReturnStatus200() throws Exception {
        with().body(BOOK_1).when().post("/book/add")
                .then().statusCode(200)
                .body(startsWith("Book has been successfully added. id ="));
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



    @Test
    public void getMethod_correctBookIdParam_shouldReturnStatus500(){
        long bookId1 = addBookAndGetId(BOOK_1);
        System.out.println("bookId1 = " + bookId1);
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

    @Test
    public void getMethod_noBookIdParameter_shouldReturnStatus400(){
        when().get("/book/get").then().statusCode(400).body(equalTo("Uncorrect request params"));
    }

    @Test
    public void getMethod_wrongTypeOfBookIdParameter_shouldReturnStatus400(){
        with().param("id", "abc")
                .when().get("/book/get")
                .then().statusCode(400)
                .body(equalTo("Request param 'bookid' have not be a number"));
    }
    @Test
    public void getMetchod_bookDoesNotExist_shouldReturnStatus404(){
        with().param("id", "444").when().get("book/get")
                .then().statusCode(404);
    }


    @Test
    public void getAllMethod_0Books_shouldReturnStatus200(){
        when().get("/book/getAll")
                .then().statusCode(200)
                .body("", hasSize(0));
    }

    @Test
    public void getAllMethod_1Book_shouldReturnStatus200(){
        long id = addBookAndGetId(BOOK_1);

        when().get("/book/getAll")
                .then().statusCode(200)
                .body("", hasSize(1))
                .body("id", hasItem(id))
                .body("title", hasItem("Alladyna"))
                .body("author", hasItem("Adam Slodowa"))
                .body("publishingHouse", hasItem("Muza"))
                .body("pagesSum", hasItem(132))
                .body("yearOfPublished", hasItem(2014));
    }

    @Test
    public void getAllMethod_2Books_shouldReturnStatus200(){
        long id1 = addBookAndGetId(BOOK_1);
        long id2 = addBookAndGetId(BOOK_2);

        when().get("/book/getAll")
                .then().statusCode(200)
                .body("", hasSize(2))
                .body("id", hasItems(id1, id2))
                .body("title", hasItems("Alladyna", "Katarynka"))
                .body("author", hasItems("Adam Slodowa", "Boleslaw Prus"))
                .body("publishingHouse", hasItems("Muza", "Nasza ksiegarnia"))
                .body("pagesSum", hasItems(132, 345))
                .body("yearOfPublished", hasItems(2014, 1995));
    }
}
