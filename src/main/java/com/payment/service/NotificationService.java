package com.payment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:pabloesp2003@gmail.com}")
    private String fromEmail;

    @KafkaListener(topics = "payment_received_events", groupId = "notification-group")
    public void consumePaymentReceived(Map<String, Object> payload) {
        log.info("NotificationService: Received payment event for email: {}", payload);
        sendEmail(fromEmail, "Pago Procesado Correctamente", 
            "Tu pago para la orden " + payload.get("ordenId") + " ha sido procesado con éxito.");
    }

    @KafkaListener(topics = "order_status_changed_events", groupId = "notification-group")
    public void consumeOrderStatusChanged(Map<String, Object> payload) {
        log.info("NotificationService: Received order status event for email: {}", payload);
        String status = (String) payload.get("status");
        sendEmail(fromEmail, "Estado de Orden Actualizado: " + status, 
            "La orden " + payload.get("id") + " ha cambiado su estado a " + status);
    }

    @KafkaListener(topics = "inventory_update_events", groupId = "notification-group")
    public void consumeInventoryUpdate(Map<String, Object> payload) {
        log.info("NotificationService: Received inventory update event for email: {}", payload);
        sendEmail(fromEmail, "Inventario Actualizado", 
            "El stock del producto " + payload.get("nombre") + " ha sido actualizado a " + payload.get("stock"));
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            log.info("Email sent successfully to {}", to);
        } catch (Exception e) {
            log.error("Error sending email: {}", e.getMessage());
        }
    }
}
