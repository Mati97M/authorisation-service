package com.gridhub.http;

public record HttpResponse(int statusCode, String body, String resourcePath) {
}
