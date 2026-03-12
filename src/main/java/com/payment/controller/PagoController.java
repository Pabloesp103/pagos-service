package com.payment.controller;

import com.payment.model.Pago;
import com.payment.repository.PagoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/pagos")
public class PagoController {

    @Autowired
    private PagoRepository repository;

    @PostMapping("/procesar")
    public Pago procesarPago(@RequestBody Pago pago) {
        log.info("Procesando pago para la orden ID: {}", pago.getOrdenId());
        pago.setEstado("PROCESADO");
        pago.setFechaPago(LocalDateTime.now());
        Pago pagoProcesado = repository.save(pago);
        log.info("Pago procesado con éxito. ID Transacción: {}", pagoProcesado.getId());
        return pagoProcesado;
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
