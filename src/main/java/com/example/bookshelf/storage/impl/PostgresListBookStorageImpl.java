package com.example.bookshelf.storage.impl;

import com.example.bookshelf.storage.PostgresBookStorage;
import com.example.bookshelf.type.Book;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostgresListBookStorageImpl implements PostgresBookStorage {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/book_store";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASS = "postgres";

    private static List<Book> bookStorage = new ArrayList<Book>();
    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error " + e.getMessage());
        }
    }


    @Override
    public Book getBook(long id) throws  SQLException {
        Connection connection = DriverManager.getConnection(JDBC_URL, DATABASE_USER, DATABASE_PASS);
        Statement statement = connection.createStatement();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, title, author, \"publishingHouse\", \"pagesSum\", \"yearOfPublished\"\n" +
                "\tFROM public.books\n" +
                "\tWHERE id = ?; ");

        preparedStatement.setLong(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()){
            Book book = prepareBook(resultSet);

            return book;
        }
        closeConnection(connection, statement);
        return null;
    }



    @Override
    public List<Book> getAllBooks() throws  SQLException {

        Connection connection = DriverManager.getConnection(JDBC_URL, DATABASE_USER, DATABASE_PASS);
        Statement statement = connection.createStatement();

        ResultSet resultSet = statement.executeQuery(" SELECT * FROM public.books;");
        bookStorage.clear();
        while (resultSet.next()){
            Book book = prepareBook(resultSet);
            bookStorage.add(book);
        }
        closeConnection(connection, statement);
        return bookStorage;
    }

    @Override
    public long addBook(Book book) throws  SQLException {
        //Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(JDBC_URL, DATABASE_USER, DATABASE_PASS);
        //Statement statement = connection.createStatement();

        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO public.books(\n" +
                "\t title, author, \"publishingHouse\", \"pagesSum\", \"yearOfPublished\")\n" +
                "\tVALUES ( ?, ?, ?, ?, ?)" +
                "\tRETURNING id;");
        preparedStatement.setString(1, book.getTitle());
        preparedStatement.setString(2, book.getAuthor());
        preparedStatement.setString(3, book.getPublishingHouse());
        preparedStatement.setInt(4, book.getPagesSum());
        preparedStatement.setInt(5, book.getYearOfPublished());

        //preparedStatement.execute();
        ResultSet resultSet = preparedStatement.executeQuery();
        long id = 0;
        while (resultSet.next()){
            id = resultSet.getLong("id");
            System.out.println("id = " + id);
            return id;
        }
        closeConnection(connection, preparedStatement);
        return -1;
    }

    private Book prepareBook(ResultSet resultSet) throws SQLException {
        Book book = new Book();
        book.setId(resultSet.getLong("id"));
        book.setTitle(resultSet.getString("title"));
        book.setAuthor(resultSet.getString("author"));
        book.setPublishingHouse(resultSet.getString("publishingHouse"));
        book.setPagesSum(resultSet.getInt("pagesSum"));
        book.setYearOfPublished(resultSet.getInt("yearOfPublished"));
        return book;
    }

    private void closeConnection(Connection connection, Statement statement) throws SQLException {
        statement.close();
        connection.close();
    }
}
