package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.CategoryId;
import com.github.anhem.howto.model.id.PostId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Post {

    @NonNull
    PostId postId;
    @NonNull
    CategoryId categoryId;
    @NonNull
    AccountId accountId;
    @NonNull
    String title;
    String body;
    @NonNull
    Instant created;
    @NonNull
    Instant lastUpdated;

}
