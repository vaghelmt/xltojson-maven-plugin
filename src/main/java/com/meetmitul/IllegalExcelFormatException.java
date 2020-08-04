package com.meetmitul;

public class IllegalExcelFormatException extends RuntimeException {

    public IllegalExcelFormatException(String errorMessage){
        super(errorMessage);
    }
}
