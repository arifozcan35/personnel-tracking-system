/* 

package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.dto.EmailNotificationEventDto;
import com.personneltrackingsystem.dto.GatePassageEventDto;
import com.personneltrackingsystem.dto.WorkValidationEventDto;
import com.personneltrackingsystem.entity.Personel;
import com.personneltrackingsystem.entity.WorkingHours;
import com.personneltrackingsystem.repository.PersonelRepository;
import com.personneltrackingsystem.service.KafkaProducerService;
import com.personneltrackingsystem.validator.PersonelValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerServiceImpl {

    private final KafkaProducerService kafkaProducerService;
    private final JavaMailSender emailSender;
    private final PersonelRepository personelRepository;
    private final PersonelValidator personelValidator;
    
    public static final LocalTime WORK_START = LocalTime.of(9, 0);
    public static final LocalTime WORK_FINISH = LocalTime.of(18, 0);
    public static final Duration MAX_WORK_MISSING = Duration.ofMinutes(15);
    public static final double PENALTY_AMOUNT = 200.0;

    @KafkaListener(topics = "${app.kafka.topic.gate-passage}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeGatePassageEvent(GatePassageEventDto gatePassageEvent) {
        log.info("Received gate passage event: {}", gatePassageEvent);
        
        Personel personel = personelRepository.findById(gatePassageEvent.getPersonelId())
                .orElse(null);
        
        if (personel == null || personel.getWork() == null) {
            log.error("Personel or Work record not found for ID: {}", gatePassageEvent.getPersonelId());
            return;
        }
        
        // Use check-in and check-out information from the work entity
        WorkingHours work = personel.getWork();
        
        WorkValidationEventDto workValidationEvent = new WorkValidationEventDto();
        workValidationEvent.setPersonelId(personel.getPersonelId());
        workValidationEvent.setPersonelName(personel.getPersonelName());
        workValidationEvent.setPersonelEmail(personel.getEmail());
        workValidationEvent.setCheckInTime(work.getCheckInTime());
        workValidationEvent.setCheckOutTime(work.getCheckOutTime());
        workValidationEvent.setIsAdministrator(personel.getAdministrator());
        workValidationEvent.setSalary(personel.getSalary());
        
        Duration workDuration = personelValidator.calculateWorkTime(work.getCheckInTime(), work.getCheckOutTime());
        boolean isValid = personelValidator.isWorkValid(work.getCheckInTime(), work.getCheckOutTime());
        
        workValidationEvent.setIsWorkValid(isValid);
        workValidationEvent.setValidationTime(LocalDateTime.now());
        
        double penaltyAmount = 0.0;
        
        // If the work is invalid and the personel is not an administrator, calculate the salary deduction
        if (!isValid && !personel.getAdministrator()) {
            penaltyAmount = personelValidator.calculatePenalty(workDuration);
            workValidationEvent.setPenaltyAmount(penaltyAmount);
            
            // Update personel's salary by applying the deduction and save to database
            double currentSalary = personel.getSalary();
            double newSalary = currentSalary - penaltyAmount;
            
            // Set new salary
            personel.setSalary(newSalary);
            
            // Save personel with updated salary to database
            personelRepository.save(personel);
            
            log.info("Applied salary deduction of {} TL to personel {}. New salary: {} TL", 
                     penaltyAmount, personel.getPersonelName(), newSalary);
        } else {
            workValidationEvent.setPenaltyAmount(0.0);
        }
        
        work.setIsWorkValid(isValid);
        
        // Send to Kafka topic
        kafkaProducerService.sendWorkValidationEvent(workValidationEvent);
    }
    
    @KafkaListener(topics = "${app.kafka.topic.work-validation}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeWorkValidationEvent(WorkValidationEventDto workValidationEvent) {
        log.info("Received work validation event: {}", workValidationEvent);
        
        // Create email notification based on work validation
        EmailNotificationEventDto emailEvent = new EmailNotificationEventDto();
        emailEvent.setPersonelId(workValidationEvent.getPersonelId());
        emailEvent.setPersonelName(workValidationEvent.getPersonelName());
        emailEvent.setPersonelEmail(workValidationEvent.getPersonelEmail());
        emailEvent.setIsWorkValid(workValidationEvent.getIsWorkValid());
        emailEvent.setNotificationTime(LocalDateTime.now());
        
        String messageBody;
        
        if (workValidationEvent.getIsWorkValid()) {
            emailEvent.setSubject("About Working Hours Verification");
            messageBody = "Dear " + workValidationEvent.getPersonelName() + 
                    ",\n\nYour work hours for today are valid.\n" + 
                    "Check-in time: " + workValidationEvent.getCheckInTime() + "\n" +
                    "Check-out time: " + workValidationEvent.getCheckOutTime() + "\n\n" +
                    "Your current salary: " + workValidationEvent.getSalary() + " TL\n\n" +
                    "Thank you for your punctuality!\n\n" +
                    "Personnel Tracking System";
        } else {
            // Control if administrator
            if (workValidationEvent.getIsAdministrator() != null && workValidationEvent.getIsAdministrator()) {
                emailEvent.setSubject("About Working Hours Verification - Administrator");
                messageBody = "Dear " + workValidationEvent.getPersonelName() + 
                        ",\n\nYour work hours for today are invalid.\n" + 
                        "Check-in time: " + workValidationEvent.getCheckInTime() + "\n" +
                        "Check-out time: " + workValidationEvent.getCheckOutTime() + "\n\n" +
                        "Required working hours: " + WORK_START + " - " + WORK_FINISH + 
                        " with maximum " + MAX_WORK_MISSING.toMinutes() + " minutes allowance.\n\n" +
                        "As an administrator, you are exempt from salary deductions.\n" +
                        "Your current salary: " + workValidationEvent.getSalary() + " TL\n\n" +
                        "Personnel Tracking System";
            } else {
                Double penaltyAmount = workValidationEvent.getPenaltyAmount() != null ? 
                        workValidationEvent.getPenaltyAmount() : PENALTY_AMOUNT;
                        
                Double newSalary = workValidationEvent.getSalary() - penaltyAmount;
                
                emailEvent.setSubject("About Working Hours Verification - Salary Deduction");
                messageBody = "Dear " + workValidationEvent.getPersonelName() + 
                        ",\n\nYour work hours for today are invalid.\n" + 
                        "Check-in time: " + workValidationEvent.getCheckInTime() + "\n" +
                        "Check-out time: " + workValidationEvent.getCheckOutTime() + "\n\n" +
                        "Required working hours: " + WORK_START + " - " + WORK_FINISH + 
                        " with maximum " + MAX_WORK_MISSING.toMinutes() + " minutes allowance.\n\n" +
                        "Penalty amount: " + penaltyAmount + " TL\n" +
                        "Previous salary: " + workValidationEvent.getSalary() + " TL\n" +
                        "New salary: " + newSalary + " TL\n\n" +
                        "Personnel Tracking System";
            }
        }
        
        emailEvent.setMessage(messageBody);
        
        // Send email notification event
        kafkaProducerService.sendEmailNotificationEvent(emailEvent);
    }
    
    @KafkaListener(topics = "${app.kafka.topic.email-notification}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeEmailNotificationEvent(EmailNotificationEventDto emailEvent) {
        log.info("Received email notification event: {}", emailEvent);
        
        // Send actual email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailEvent.getPersonelEmail());
        message.setFrom("zcanarif@gmail.com");
        message.setSubject(emailEvent.getSubject());
        message.setText(emailEvent.getMessage());
        
        try {
            emailSender.send(message);
            log.info("Email sent successfully to: {}", emailEvent.getPersonelEmail());
        } catch (Exception e) {
            log.error("Failed to send email to: {}. Error: {}", emailEvent.getPersonelEmail(), e.getMessage());
        }
    }
} 

*/