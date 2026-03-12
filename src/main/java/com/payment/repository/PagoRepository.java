package com.payment.repository;

import com.payment.model.Pago;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends MongoRepository<Pago, String> {
    Optional<Pago> findByOrdenId(String ordenId);
}
