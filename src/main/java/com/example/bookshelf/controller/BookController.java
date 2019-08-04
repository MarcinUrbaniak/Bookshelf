package com.example.bookshelf.controller;

import com.example.bookshelf.storage.BookStorage;
import com.example.bookshelf.storage.impl.StaticListBookStorageImpl;
import fi.iki.elonen.NanoHTTPD;

import static fi.iki.elonen.NanoHTTPD.*;

import javax.xml.ws.Response;

public class BookController {

    private BookStorage bookStorage = new StaticListBookStorageImpl();

    public Response serveGetBookRequest(IHTTPSession session){
        return null;
    }

    public Response serveGetBooksRequest(IHTTPSession session){
        return null;
    }

    public Response serveAddBookRequest(IHTTPSession session){
        return null;
    }

}
