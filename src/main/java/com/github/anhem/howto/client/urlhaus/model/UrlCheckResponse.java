package com.github.anhem.howto.client.urlhaus.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
public class UrlCheckResponse {

    @NonNull
    String queryStatus;
    String id;
    String urlHausReference;
    String url;
    String urlStatus;
    String host;
    String threat;
}
