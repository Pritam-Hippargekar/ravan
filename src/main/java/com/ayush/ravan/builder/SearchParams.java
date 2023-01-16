package com.ayush.ravan.builder;

public class SearchParams {
    private String query;
    private int limit;
    private int offset;

    private SearchParams(Builder builder) {
        query = builder.query;
        limit = builder.limit;
        offset = builder.offset;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {

        private String query;
        private int limit;
        private int offset;

        private Builder() {
        }

        public Builder withQuery(String val) {
            query = val;
            return this;
        }

        public Builder withLimit(int val) {
            limit = val;
            return this;
        }

        public Builder withOffset(int val) {
            offset = val;
            return this;
        }

        public SearchParams build() {
            return new SearchParams(this);
        }

    }

    @Override
    public String toString() {
        return "SearchParams{" +
                "query='" + query + '\'' +
                ", limit=" + limit +
                ", offset=" + offset +
                '}';
    }
}
