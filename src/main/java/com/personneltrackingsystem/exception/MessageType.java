package com.personneltrackingsystem.exception;

import lombok.Getter;

@Getter
public enum MessageType {

    // Entity existence errors
    NO_RECORD_EXIST("1001","no.record.found"),
    BUILDING_NOT_FOUND("1002", "building.not.found"),
    FLOOR_NOT_FOUND("1003", "floor.not.found"),
    UNIT_NOT_FOUND("1004", "unit.not.found"),
    GATE_NOT_FOUND("1005", "gate.not.found"),
    TURNSTILE_NOT_FOUND("1006", "turnstile.not.found"),
    PERSONNEL_NOT_FOUND("1007", "personnel.not.found"),
    PERSONNEL_TYPE_NOT_FOUND("1008", "personnel.type.not.found"),
    WORKING_HOURS_NOT_FOUND("1009", "working.hours.not.found"),
    PERMISSION_NOT_FOUND("1010", "permission.not.found"),
    ROLE_NOT_FOUND("1011", "role.not.found"),
    SALARY_NOT_FOUND("1012", "salary.not.found"),
    PERSONEL_TYPE_OR_SALARY_NOT_FOUND("1013", "personel.type.or.salary.not.found"),

    // Validation errors
    REQUIRED_FIELD_AVAILABLE("5000", "required.field.available"),
    BUILDING_NAME_REQUIRED("5001", "building.name.required"),
    FLOOR_NAME_REQUIRED("5002", "floor.name.required"),
    UNIT_NAME_REQUIRED("5003", "unit.name.required"),
    GATE_NAME_REQUIRED("5004", "gate.name.required"),
    TURNSTILE_NAME_REQUIRED("5005", "turnstile.name.required"),
    PERSONNEL_NAME_REQUIRED("5006", "personnel.name.required"),
    PERSONNEL_EMAIL_REQUIRED("5007", "personnel.email.required"),
    WORKING_HOURS_REQUIRED("5008", "working.hours.required"),
    PERMISSION_NAME_REQUIRED("5009", "permission.name.required"),
    PERMISSION_RESOURCE_REQUIRED("5010", "permission.resource.required"),
    PERMISSION_METHOD_REQUIRED("5011", "permission.method.required"),
    PERMISSION_PATH_REQUIRED("5012", "permission.path.required"),
    MONTH_REQUIRED("5013", "month.required"),

    // Uniqueness constraint errors
    BUILDING_NAME_ALREADY_EXISTS("6001", "building.name.already.exists"),
    GATE_NAME_ALREADY_EXISTS("6002", "gate.name.already.exists"),
    TURNSTILE_NAME_ALREADY_EXISTS("6003", "turnstile.name.already.exists"),
    UNIT_NAME_ALREADY_EXISTS("6004", "unit.name.already.exists"),
    PERSONNEL_EMAIL_ALREADY_EXISTS("6005", "personnel.email.already.exists"),
    PERSONNEL_TYPE_NAME_ALREADY_EXISTS("6006", "personnel.type.name.already.exists"),
    PERMISSION_NAME_ALREADY_EXISTS("6007", "permission.name.already.exists"),
    PERMISSION_ALREADY_ASSIGNED("6008", "permission.already.assigned"),

    // Business logic errors
    INVALID_TIME_RANGE("7001", "invalid.time.range"),
    PERSONNEL_NOT_AUTHORIZED("7002", "personnel.not.authorized"),
    PERMISSION_NOT_ASSIGNED("7003", "permission.not.assigned"),
    INVALID_DATE_FORMAT("7004", "invalid.date.format"),
    INVALID_TIME_FORMAT("7006", "invalid.time.format"),
    INVALID_EVENT_TYPE("7005", "invalid.event.type"),
    
    // Turnstile passage validation errors
    TURNSTILE_EXIT_REQUIRES_PRIOR_ENTRY("7007", "turnstile.exit.requires.prior.entry"),
    TURNSTILE_ENTRY_REQUIRES_PRIOR_EXIT("7008", "turnstile.entry.requires.prior.exit"),
    
    // General errors
    GENERAL_EXCEPTION("9999" , "general.exception");

    private String code;
    private String messageKey;

    MessageType(String code, String messageKey) {
        this.code = code;
        this.messageKey = messageKey;
    }
}
