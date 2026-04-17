package com.payment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "pagos")
public class Pago {
    @Id
    private String id;
    private String ordenId;
    private Double monto;
    private String metodoPago; // TARJETA, TRANSFERENCIA, PAYPAL
    private String estado; // PROCESADO, REEMBOLSADO, FALLIDO
    private LocalDateTime fechaPago;

    public Pago() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getOrdenId() { return ordenId; }
    public void setOrdenId(String ordenId) { this.ordenId = ordenId; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getMetodoPago() { return metodoPago; }
    public void setMetodoPago(String metodoPago) { this.metodoPago = metodoPago; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public LocalDateTime getFechaPago() { return fechaPago; }
    public void setFechaPago(LocalDateTime fechaPago) { this.fechaPago = fechaPago; }
}
