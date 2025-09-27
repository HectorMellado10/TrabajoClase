package com.example.android.api;

import com.example.android.model.Quote;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface QuoteApiService {
    @GET("random")
    Call<List<Quote>> getRandomQuote();
}
