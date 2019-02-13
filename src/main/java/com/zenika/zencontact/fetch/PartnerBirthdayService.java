package com.zenika.zencontact.fetch;

import java.net.URL;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

public class PartnerBirthdayService {

    private static PartnerBirthdayService INSTANCE = new PartnerBirthdayService();

    public static PartnerBirthdayService getInstance() {
        return INSTANCE;
    }

    private static final String SERVICE_URL = "http://zenpartenaire.appspot.com/zenpartenaire";

    public String findBirthdate(String firstname, String lastname) {
        try {
            URLFetchService fetcher = URLFetchServiceFactory.getURLFetchService();
            URL url = new URL(SERVICE_URL);
            HTTPRequest postRequest = new HTTPRequest(url, HTTPMethod.POST, FetchOptions.Builder.withDeadline(30));
            String payload = firstname + " " + lastname;
            postRequest.setPayload(payload.getBytes());
            HTTPResponse response = fetcher.fetch(postRequest);
            if (response.getResponseCode() != 200) {
                return null;
            }
            byte[] content = response.getContent();
            String s = new String(content).trim();
            return s;
        } catch (Exception e) {
        }
        return null;
    }
}