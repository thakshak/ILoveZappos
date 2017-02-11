package com.example.android.ilovezappos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by gthak on 31-01-2017.
 */

public interface ZapposAPI {
    @GET("/Search")
    Call<SearchResult> getSearchResults(@Query("term") String term, @Query("key") String key);
}
