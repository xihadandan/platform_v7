package com.wellsoft.pt.repository.exception;

import com.wellsoft.context.exception.WellException;

public class JcrException extends WellException {
    private static final long serialVersionUID = 1L;

    public JcrException() {
        super("jcr error");
    }
}
