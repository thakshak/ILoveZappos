package com.example.android.ilovezappos;

import java.util.List;

/**
 * Created by gthak on 31-01-2017.
 */

public class SearchResult {

    private String originalTerm;
    private String currentResultCount;
    private String totalResultCount;
    private String term;
    private List<Product> results;
    private String statusCode;

    public SearchResult(String originalTerm, String currentResultCount, String totalResultCount, String term, List<Product> results, String statusCode) {
        this.originalTerm = originalTerm;
        this.currentResultCount = currentResultCount;
        this.totalResultCount = totalResultCount;
        this.term = term;
        this.results = results;
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public List<Product> getResults() {
        return results;
    }

    public String getOriginalTerm() {
        return originalTerm;
    }
}
