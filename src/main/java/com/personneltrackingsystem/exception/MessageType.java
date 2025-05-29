package com.personneltrackingsystem.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    // Entity existence errors
    NO_RECORD_EXIST("1001","No record found!"),
    BUILDING_NOT_FOUND("1002", "Building not found!"),
    FLOOR_NOT_FOUND("1003", "Floor not found!"),
    UNIT_NOT_FOUND("1004", "Unit not found!"),
    GATE_NOT_FOUND("1005", "Gate not found!"),
    TURNSTILE_NOT_FOUND("1006", "Turnstile not found!"),
    PERSONNEL_NOT_FOUND("1007", "Personnel not found!"),
    PERSONNEL_TYPE_NOT_FOUND("1008", "Personnel type not found!"),
    WORKING_HOURS_NOT_FOUND("1009", "Working hours not found!"),
    PERMISSION_NOT_FOUND("1010", "Permission not found!"),
    ROLE_NOT_FOUND("1011", "Role not found!"),

    // Validation errors
    REQUIRED_FIELD_AVAILABLE("5000", "Required field cannot be left blank!"),
    BUILDING_NAME_REQUIRED("5001", "Building name is required!"),
    FLOOR_NAME_REQUIRED("5002", "Floor name is required!"),
    UNIT_NAME_REQUIRED("5003", "Unit name is required!"),
    GATE_NAME_REQUIRED("5004", "Gate name is required!"),
    TURNSTILE_NAME_REQUIRED("5005", "Turnstile name is required!"),
    PERSONNEL_NAME_REQUIRED("5006", "Personnel name is required!"),
    PERSONNEL_EMAIL_REQUIRED("5007", "Personnel email is required!"),
    WORKING_HOURS_REQUIRED("5008", "Working hours are required!"),
    PERMISSION_NAME_REQUIRED("5009", "Permission name is required!"),
    PERMISSION_RESOURCE_REQUIRED("5010", "Permission resource is required!"),
    PERMISSION_METHOD_REQUIRED("5011", "Permission method is required!"),
    PERMISSION_PATH_REQUIRED("5012", "Permission path pattern is required!"),

    // Uniqueness constraint errors
    BUILDING_NAME_ALREADY_EXISTS("6001", "Building with this name already exists!"),
    GATE_NAME_ALREADY_EXISTS("6002", "Gate with this name already exists!"),
    TURNSTILE_NAME_ALREADY_EXISTS("6003", "Turnstile with this name already exists!"),
    UNIT_NAME_ALREADY_EXISTS("6004", "Unit with this name already exists!"),
    PERSONNEL_EMAIL_ALREADY_EXISTS("6005", "Personnel with this email already exists!"),
    PERSONNEL_TYPE_NAME_ALREADY_EXISTS("6006", "Personnel type with this name already exists!"),
    PERMISSION_NAME_ALREADY_EXISTS("6007", "Permission with this name already exists!"),
    PERMISSION_ALREADY_ASSIGNED("6008", "Permission is already assigned to this role!"),

    // Business logic errors
    INVALID_TIME_RANGE("7001", "Check-in time must be before check-out time!"),
    PERSONNEL_NOT_AUTHORIZED("7002", "Personnel is not authorized for this gate!"),
    PERMISSION_NOT_ASSIGNED("7003", "Permission is not assigned to this role!"),
    INVALID_DATE_FORMAT("7004", "Invalid date format. Expected format: yyyy-MM-dd HH:mm:ss"),
    INVALID_EVENT_TYPE("7005", "Invalid event type!"),
    
    // General errors
    GENERAL_EXCEPTION("9999" , "A general error has occurred!");

    private String code;
    private String message;

    MessageType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
