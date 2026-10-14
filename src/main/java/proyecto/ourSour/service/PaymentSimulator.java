package proyecto.ourSour.service;

import java.math.BigDecimal;
import java.util.UUID;

import org.springframework.stereotype.Component;

/**
 * Pasarela de pago SANDBOX: no se conecta a ningún banco ni guarda tarjetas.
 * Regla de prueba: las tarjetas que terminan en 0002 son rechazadas; el resto se aprueba.
 */
@Component
public class PaymentSimulator {
 
    public record PaymentResult(boolean approved, String reference) {
    }
 
    public PaymentResult charge(String cardNumber, BigDecimal amount) {
        String digits = cardNumber.replace(" ", "");
        boolean approved = !digits.endsWith("0002");
        String reference = approved ? "SBX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase() : null;
        return new PaymentResult(approved, reference);
    }
}

