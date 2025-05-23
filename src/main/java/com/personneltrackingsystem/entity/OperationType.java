package com.personneltrackingsystem.entity;

public enum OperationType {
    
    IN("IN"),
    
    OUT("OUT");
    
    private final String value;
    
    OperationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
    
    
    public static OperationType fromValue(String value) {
        for (OperationType type : OperationType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid operation type: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
} 