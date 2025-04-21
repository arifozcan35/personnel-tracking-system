package com.personneltrackingsystem.validator;

import com.personneltrackingsystem.dto.DtoUnit;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.exception.ValidationException;
import com.personneltrackingsystem.repository.UnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UnitValidator {

    private final UnitRepository unitRepository;

    public void checkIfUnitAlreadyExists(DtoUnit dtoUnit) {
        if (dtoUnit.getUnitId() != null) {
            if (unitRepository.existsByUnitId(dtoUnit.getUnitId())) {
                throw new ValidationException("Unit with this unit ID already exists!");
            }
        }

        String unitName = dtoUnit.getBirimIsim();
        if (unitName == null || unitName.isEmpty()) {
            throw new BaseException(new ErrorMessage(MessageType.REQUIRED_FIELD_AVAILABLE, null));
        }

        if (unitRepository.existsByUnitName(unitName)) {
            throw new ValidationException("Unit with this unit name already exists!");
        }

    }
}
