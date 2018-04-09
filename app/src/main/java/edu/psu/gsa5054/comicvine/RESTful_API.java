package edu.psu.gsa5054.comicvine;

import okhttp3.OkHttpClient;

class RESTful_API {

    private String API_BASE_URL = "http://www.comicvine.com/api/<resource>/" +
            "?api_key=1b933662d46319e7bb1085f8a60f5a11519cc4f0&filter=<filter>:<value>&format=json";

    OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//    RESTful_API.Builder builder = new RESTful_API.Builder()
//            .baseUrl(API_BASE_URL).addConverterFactory(GsonConverterFactory.create());
}
