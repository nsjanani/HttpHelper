package com.sample.http;

import java.util.List;
import java.util.Map;

public class RestRequestResponse<T> {

    private int status;
    private Map<String, List<String>> responseHeaders;
    private T response;


    public RestRequestResponse(int status, Map<String, List<String>> responseHeaders, T response) {
        this.status = status;
        this.responseHeaders = responseHeaders;
        this.response = response;

    }

    public int getStatus(){
        return status;
    }

    public Map getResponseHeaders() { return responseHeaders; }

    public T getResponse(){
        return response;
    }

}
