package com.evalforge.exception;



public class ModelEndpointNotFoundException extends RuntimeException {

    public ModelEndpointNotFoundException(long id){ 

            super("Model endpoint not found with id: " + id);

    }

}