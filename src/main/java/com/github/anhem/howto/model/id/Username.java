package com.github.anhem.howto.model.id;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Username implements Id<String> {

    @NonNull
    String value;

}
