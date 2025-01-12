package com.thacbao.codeSphere.exceptions.user;

public class PermissionException extends RuntimeException{
    public PermissionException(String message){
        super(message);
    }
}
