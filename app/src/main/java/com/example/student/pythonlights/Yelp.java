package com.example.student.pythonlights;

/*
 Example code based on code from Nicholas Smith at http://imnes.blogspot.com/2011/01/how-to-use-yelp-v2-from-java-including.html
 For a more complete example (how to integrate with GSON, etc) see the blog post above.
 */

import android.content.Context;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Example for accessing the Yelp API.
 */
public class Yelp {

    OAuthService service;
    Token accessToken;

    String[] outdoor = {"park","hike","Farmers Market"};
    String[] indoor = {"theater","cafe","bar","restaurant"};

    public static Yelp getYelp(Context context) {
        return new Yelp(context.getString(R.string.consumer_key), context.getString(R.string.consumer_secret),
                context.getString(R.string.token), context.getString(R.string.token_secret));
    }

    /**
     * Setup the Yelp API OAuth credentials.
     *
     * OAuth credentials are available from the developer site, under Manage API access (version 2 API).
     *
     * @param consumerKey Consumer key
     * @param consumerSecret Consumer secret
     * @param token Token
     * @param tokenSecret Token secret
     */
    public Yelp(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(YelpApi2.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    /**
     * Search with term and location.
     *
     * @param term Search term
     * @param latitude Latitude
     * @param longitude Longitude
     * @return JSON string response
     */
    public String search(String term, double latitude, double longitude) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("ll", latitude + "," + longitude);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    /**
     * Search with term string location.
     *
     * @param term Search term
     * @return JSON string response
     */
    public String search(String term, String location) {
        OAuthRequest request = new OAuthRequest(Verb.GET, "http://api.yelp.com/v2/search");
        request.addQuerystringParameter("term", term);
        request.addQuerystringParameter("location", location);
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

    // CLI
    public static void main(String[] args) {
        // Update tokens here from Yelp developers site, Manage API access.
        String consumerKey = "0VJ2QVBQYJqEBqNZ_0g6Xg";
        String consumerSecret = "oI4ReNB4OsjHYCnczq4J9_3HLQ8";
        String token = "_DUpeCYJqwWUjlXijdFIpNKvpQjAA_CV";
        String tokenSecret = "ljnwwyEaVkZyyuaR5fCket9RWyw";

        Yelp yelp = new Yelp(consumerKey, consumerSecret, token, tokenSecret);
        String response = yelp.search("burritos", 30.361471, -87.164326);

        System.out.println(response);

        boolean rainy = true;
        // If weather is rainy or chance of precipitation
        if (rainy){
            for (int i = 0; i < yelp.indoor.length; i++){
                String options = yelp.search(yelp.indoor[i],30.361471, -87.164326);
            }
        }
        else{
            for (int i = 0; i < yelp.outdoor.length; i++){
                String options = yelp.search(yelp.outdoor[i],30.361471, -87.164326);
            }
        }
    }
}
