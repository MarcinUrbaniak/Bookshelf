package com.example.bookshelf.storage;

import com.example.bookshelf.type.Book;

import java.sql.SQLException;
import java.util.List;

public interface PostgresBookStorage {

    Book getBook(long id)  throws ClassNotFoundException, SQLException;

    List<Book> getAllBooks() throws ClassNotFoundException, SQLException;


    void addBook(Book book) throws ClassNotFoundException, SQLException;

}
