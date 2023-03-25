package com.github.anhem.howto.model;

import com.github.anhem.howto.model.id.AccountId;
import com.github.anhem.howto.model.id.PostId;
import com.github.anhem.howto.model.id.ReplyId;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

import java.time.Instant;

@Value
@Builder
public class Reply {

    @NonNull
    ReplyId replyId;
    @NonNull
    PostId postId;
    @NonNull
    AccountId accountId;
    String body;
    @NonNull
    Instant created;
    @NonNull
    Instant lastUpdated;

}
