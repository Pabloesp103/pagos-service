package com.payment.controller;

import com.payment.model.Pago;
import com.payment.repository.PagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/pagos")
public class PagoController {

    private static final Logger log = LoggerFactory.getLogger(PagoController.class);

    @Autowired
    private PagoRepository repository;

    @Autowired
    private org.springframework.kafka.core.KafkaTemplate<String, Object> kafkaTemplate;

    @PostMapping("/procesar")
    public Pago procesarPago(@RequestBody Pago pago, @RequestHeader(value = "X-Retry-Attempt", required = false) String isRetry) {
        log.info("Intentando procesar pago for the order ID: {}", pago.getOrdenId());
        try {
            pago.setEstado("PROCESADO");
            pago.setFechaPago(LocalDateTime.now());
            Pago pagoProcesado = repository.save(pago);
            log.info("Pago procesado con éxito. ID Transacción: {}", pagoProcesado.getId());
            return pagoProcesado;
        } catch (Exception e) {
            log.error("Error al procesar pago. Validando reenvío. Error: {}", e.getMessage());

            if ("true".equals(isRetry)) {
                log.warn("El reintento falló de nuevo. NO se re-enviará a Kafka.");
                throw new RuntimeException("Fallo persistente en procesar pago. Deteniendo ciclo.", e);
            }
            
            log.info("Enviando a reintento (Kafka)...");
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            payload.put("data", pago);
            payload.put("sendEmail", new java.util.HashMap<String, String>() {{
                put("status", "PENDING");
                put("message", "Pendiente de reintento");
            }});
            payload.put("updateRetryJobs", new java.util.HashMap<String, String>() {{
                put("status", "PENDING");
                put("message", "Pendiente de reintento");
            }});
            
            kafkaTemplate.send("payments_retry_jobs", payload);
            throw new RuntimeException("Error al procesar pago. Enviado a cola de reintentos.", e);
        }
    }

    @GetMapping("/{id}")
    public Pago getById(@PathVariable String id) {
        log.info("Consultando pago ID: {}", id);
        return repository.findById(id).orElse(null);
    }

    @GetMapping("/orden/{ordenId}")
    public Pago getByOrden(@PathVariable String ordenId) {
        log.info("Consultando pago de la orden ID: {}", ordenId);
        return repository.findByOrdenId(ordenId).orElse(null);
    }

    @PutMapping("/{id}/reembolso")
    public Pago reembolsar(@PathVariable String id) {
        log.info("Iniciando reembolso para el pago ID: {}", id);
        Pago pago = repository.findById(id).orElseThrow(() -> new RuntimeException("Pago no encontrado"));
        pago.setEstado("REEMBOLSADO");
        log.warn("Reembolso aplicado al pago ID: {}", id);
        return repository.save(pago);
    }
}
