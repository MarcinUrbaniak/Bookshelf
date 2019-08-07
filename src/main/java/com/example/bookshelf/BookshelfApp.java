package com.example.bookshelf;

import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;

public class BookshelfApp extends NanoHTTPD {

    RequestUrlMapper requestUrlMapper = new RequestUrlMapper();

    public BookshelfApp(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("Server has been started");
    }

    public static void main(String[] args) {
        try {
            new BookshelfApp(8080);

        } catch (IOException e) {
            System.err.println("Server can't startet because of error: \n" + e);
        }
    }
    @Override
    public Response serve(IHTTPSession session){
        return requestUrlMapper.delegateRequest(session);
    }

}


