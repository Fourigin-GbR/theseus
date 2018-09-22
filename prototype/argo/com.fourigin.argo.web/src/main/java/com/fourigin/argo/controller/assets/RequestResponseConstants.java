package com.fourigin.argo.controller.assets;

public interface RequestResponseConstants
{
	String CONTENT_TYPE_JSON = "Content-Type=application/json";
    String CONTENT_TYPE_PNG = "image/png";
	String CONTENT_TYPE_URL_ENCODED = "Content-Type=application/x-www-form-urlencoded";
	String ACCEPT_JSON = "Accept=application/json";
	String ACCEPT_HTML = "Accept=text/html";

	String ETAG_RESPONSE_HEADER = "ETag";
	String IF_NONE_MATCH_REQUEST_HEADER = "If-None-Match";
}
