package com.sample.http;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import groovy.json.JsonSlurper;

import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpRequest.BodyPublishers;
import static java.net.http.HttpResponse.BodyHandlers;

public class RestRequestHelper {

    enum RequestBodyType {
        BYTE_ARRAY,
        BYTE_ARRAY_OFFSET,
        BYTE_ARRAYS,
        FILE,
        INPUTSTREAM,
        STRING,
        STRING_WITH_CHARSET,
        OBJECT
    }

    enum ResponseBodyType {
        BYTE_ARRAY,
        BYTE_ARRAY_CONSUMER,
        DISCARD,
        FILE,
        FILE_WITH_OPTION,
        STRING,
        STRING_WITH_CHARSET,
        MAP,
        JSON,
        OBJECT
    }

    static HttpClient httpClient;
    static JsonSlurper jsonSlurper;

    static {
        CookieManager cookieManager = new CookieManager();

        httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();

        jsonSlurper = new JsonSlurper();
    }

    public RestRequestResponse post(String url, String[] headers, String requestPayload, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .POST(BodyPublishers.ofString(requestPayload))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public <T> RestRequestResponse post(String url, RequestBodyType requestBodyType, T requestBody, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
        switch (requestBodyType) {
            case STRING:
                httpRequestBuilder.POST(BodyPublishers.ofString(requestBody.toString()));
                break;
            case BYTE_ARRAY:
                httpRequestBuilder.POST(BodyPublishers.ofByteArray(requestBody.toString().getBytes()));
                break;
        }
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public <T> RestRequestResponse post(String url, RequestBodyType requestBodyType, T requestBody, Class clazz) throws Exception {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
        switch (requestBodyType) {
            case STRING:
                httpRequestBuilder.POST(BodyPublishers.ofString(requestBody.toString()));
                break;
            case BYTE_ARRAY:
                httpRequestBuilder.POST(BodyPublishers.ofByteArray(requestBody.toString().getBytes()));
                break;
        }
        HttpRequest httpRequest = httpRequestBuilder.build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(httpResponse, clazz);
    }

    public RestRequestResponse put(String url, String[] headers, String requestPayload, ResponseBodyType responseBodyType) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .PUT(BodyPublishers.ofString(requestPayload))
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public <T> RestRequestResponse put(String url, RequestBodyType requestBodyType, T requestBody, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
        switch (requestBodyType) {
            case STRING:
                httpRequestBuilder.PUT(BodyPublishers.ofString(requestBody.toString()));
                break;
            case BYTE_ARRAY:
                httpRequestBuilder.PUT(BodyPublishers.ofByteArray(requestBody.toString().getBytes()));
                break;
        }
        HttpRequest httpRequest = httpRequestBuilder.build();

        HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public <T> RestRequestResponse put(String url, RequestBodyType requestBodyType, T requestBody, Class clazz) throws Exception {
        HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder().uri(URI.create(url));
        switch (requestBodyType) {
            case STRING:
                httpRequestBuilder.PUT(BodyPublishers.ofString(requestBody.toString()));
                break;
            case BYTE_ARRAY:
                httpRequestBuilder.PUT(BodyPublishers.ofByteArray(requestBody.toString().getBytes()));
                break;
        }
        HttpRequest httpRequest = httpRequestBuilder.build();

        HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(httpResponse, clazz);
    }

    public RestRequestResponse get(String url, String[] headers, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .headers(headers)
                .GET()
                .build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public RestRequestResponse get(String url, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public RestRequestResponse get(String url, Class clazz) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
        HttpResponse<String> httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(httpResponse, clazz);
    }

    public RestRequestResponse delete(String url, ResponseBodyType... responseBodyType) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(responseBodyType, httpResponse);
    }

    public RestRequestResponse delete(String url, Class clazz) throws Exception {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(url)).DELETE().build();
        HttpResponse httpResponse = httpClient.send(httpRequest, BodyHandlers.ofString());
        return getCustomResponseBody(httpResponse, clazz);
    }

    public RestRequestResponse getCustomResponseBody(ResponseBodyType responseBodyType, HttpResponse httpResponse) throws Exception {
            switch (responseBodyType) {
                default:
                case STRING:
                    return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), httpResponse.body().toString());
                case MAP:
                    return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), jsonSlurper.parseText(httpResponse.body().toString()));
            }
    }

    public RestRequestResponse getCustomResponseBody(ResponseBodyType[] responseBodyType, HttpResponse httpResponse) throws Exception {
        if (responseBodyType.length > 0)
            switch (responseBodyType[0]) {
                default:
                case STRING:
                    return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), httpResponse.body().toString());
                case MAP:
                    return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), jsonSlurper.parseText(httpResponse.body().toString()));
            }
        else
            return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), httpResponse.body().toString());

    }

    public RestRequestResponse getCustomResponseBody(HttpResponse httpResponse, Class clazz) throws Exception {
        return new RestRequestResponse<>(httpResponse.statusCode(), httpResponse.headers().map(), parseResponseAsObject(httpResponse.body().toString(), clazz));
    }

    public JsonNode parseResponseAsJson(String response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(response);
    }

    public Object parseResponseAsObject(String response, Class clazz) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(response, clazz);
    }

}
