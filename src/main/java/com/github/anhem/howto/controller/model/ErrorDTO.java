package com.github.anhem.howto.controller.model;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;

@Value
@Builder
@Jacksonized
public class ErrorDTO {

    @NonNull
    ErrorCode errorCode;
    @NonNull
    String message;
    @Singular
    Map<String, List<String>> fieldErrors;
}
