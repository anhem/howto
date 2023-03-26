package com.github.anhem.howto.exception;

import com.github.anhem.howto.model.RoleName;
import com.github.anhem.howto.model.id.Id;

public class NotFoundException extends RuntimeException {

    private static final String NOT_FOUND_MESSAGE = "%s could not be found";

    public NotFoundException(Id<?> id) {
        super(String.format(NOT_FOUND_MESSAGE, id));
    }

    public NotFoundException(RoleName roleName) {
        super(String.format(NOT_FOUND_MESSAGE, roleName));
    }
}
