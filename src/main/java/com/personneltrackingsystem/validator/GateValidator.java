package com.personneltrackingsystem.validator;

import com.personneltrackingsystem.dto.DtoGate;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.repository.GateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GateValidator {

    public final GateRepository gateRepository;

    public void checkIfGateAlreadyExists(DtoGate dtoGate) {
        if (dtoGate.getGateId() != null) {
            if (gateRepository.existsByGateId(dtoGate.getGateId())) {
                throw new ValidationException("Gate with this gate ID already exists!");
            }
        }

        String gateName = dtoGate.getGateName();
        if (gateName == null || gateName.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (gateRepository.existsByGateName(gateName)) {
            throw new ValidationException("Gate with this gate name already exists!");
        }

    }
}
