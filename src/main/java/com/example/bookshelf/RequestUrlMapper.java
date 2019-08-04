package com.example.bookshelf;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response;
import fi.iki.elonen.NanoHTTPD.IHTTPSession;

import static fi.iki.elonen.NanoHTTPD.Method.GET;
import static fi.iki.elonen.NanoHTTPD.Method.POST;
import static fi.iki.elonen.NanoHTTPD.Response.Status.NOT_FOUND;



public class RequestUrlMapper {

    private final static  String ADD_BOOK_URL = "/book/add";
    private final static String GET_BOOK_URL = "/book/get";
    private final static String GET_ALL_BOOK_URL = "/book/getAll";

    public Response delegateRequest(IHTTPSession session){

        if (GET.equals(session.getMethod()) && GET_BOOK_URL.equals(session.getUri())){
            return null;
        }
        else if (GET.equals(session.getMethod()) && GET_ALL_BOOK_URL.equals(session.getUri())){
            return null;
        }
        else if (POST.equals(session.getMethod()) && ADD_BOOK_URL.equals(session.getUri())){
            return null;
        }
        return NanoHTTPD.newFixedLengthResponse(NOT_FOUND, "text/plain", "Not Found");
    }

}
