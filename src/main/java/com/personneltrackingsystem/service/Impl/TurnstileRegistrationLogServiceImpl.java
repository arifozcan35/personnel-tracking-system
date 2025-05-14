package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.DtoTurnstileRegistrationLog;
import com.personneltrackingsystem.entity.TurnstileRegistrationLog;
import com.personneltrackingsystem.exception.BaseException;
import com.personneltrackingsystem.exception.ErrorMessage;
import com.personneltrackingsystem.exception.MessageResolver;
import com.personneltrackingsystem.exception.MessageType;
import com.personneltrackingsystem.mapper.TurnstileRegistrationLogMapper;
import com.personneltrackingsystem.repository.TurnstileRegistrationLogRepository;
import com.personneltrackingsystem.service.TurnstileRegistrationLogService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TurnstileRegistrationLogServiceImpl implements TurnstileRegistrationLogService {

    private final TurnstileRegistrationLogRepository logRepository;

    private final TurnstileRegistrationLogMapper logMapper;

    private final MessageResolver messageResolver;

    @Override
    public List<DtoTurnstileRegistrationLog> getAllLogs(){

        List<TurnstileRegistrationLog> logList =  logRepository.findAll();

        return logMapper.turnstileRegistrationLogListToDtoTurnstileRegistrationLogList(logList);
    }

    @Override
    public Optional<DtoTurnstileRegistrationLog> getLogById(Long logId) {

        TurnstileRegistrationLog log = logRepository.findById(logId)
                .orElseThrow(() -> new EntityNotFoundException("Turnstile not found with id: " + logId));

        return Optional.ofNullable(logMapper.turnstileRegistrationLogToDtoTurnstileRegistrationLog(log));
    }

    @Override
    public DtoTurnstileRegistrationLog getOneLog(Long logId){
        Optional<TurnstileRegistrationLog> optLog =  logRepository.findById(logId);
        if(optLog.isEmpty()){
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }else{
            return logMapper.turnstileRegistrationLogToDtoTurnstileRegistrationLog(optLog.get());
        }
    }

  

    @Override
    @Transactional
    public void deleteOneLog(Long logId) {
        Optional<TurnstileRegistrationLog> optLog = logRepository.findById(logId);

        if(optLog.isPresent()){
            // update associated turnstile records
            logRepository.updateTurnstileTurnstileRegistrationLogReferences(optLog.get());

            // update associated personel records

            // delete turnstile
            logRepository.delete(optLog.get());
        }
        else{
            ErrorMessage errorMessage = new ErrorMessage(MessageType.NO_RECORD_EXIST, messageResolver.toString());
            throw new BaseException(errorMessage);
        }
    }
} 