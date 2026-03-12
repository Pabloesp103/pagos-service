package com.payment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "pagos")
public class Pago {
    @Id
    private String id;
    private String ordenId;
    private Double monto;
    private String metodoPago; // TARJETA, TRANSFERENCIA, PAYPAL
    private String estado; // PROCESADO, REEMBOLSADO, FALLIDO
    private LocalDateTime fechaPago;
}
