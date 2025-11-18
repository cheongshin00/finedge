package com.finedge.finedgeapi.exception;

public class AccountNotFoundException extends RuntimeException{
    public AccountNotFoundException(String msg) {
        super(msg);
    }
}
