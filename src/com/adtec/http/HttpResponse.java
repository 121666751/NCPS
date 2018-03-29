package com.adtec.http;

import org.apache.commons.httpclient.Header;
import java.io.UnsupportedEncodingException;

/* *
 *类名：HttpResponse
 *功能：Http返回对象的封装
 *详细：封装Http返回信息
 */
public class HttpResponse {

    /**
     * 返回中的Header信息
     */
    private Header[] responseHeaders;

    /**
     * String类型的result
     */
    private String   stringResult;

    
    public Header[] getResponseHeaders() {
        return responseHeaders;
    }

    public void setResponseHeaders(Header[] responseHeaders) {
        this.responseHeaders = responseHeaders;
    }

    public String getStringResult() throws UnsupportedEncodingException {
    	return stringResult;
    }

    public void setStringResult(String stringResult) {
        this.stringResult = stringResult;
    }
    public static void main(String[] args) {
		// TODO Auto-generated method stub
    	System.out.println("wee");

	}
}
