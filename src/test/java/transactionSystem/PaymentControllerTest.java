package transactionSystem;

import org.junit.jupiter.api.Test;
import transactionSystem.models.Payment;

import java.sql.SQLException;

class PaymentControllerTest {
    @Test
    void getCreditModels() {
        PaymentController paymentController = new PaymentController();
        System.out.println(paymentController);
    }

    @Test
    void charge() throws SQLException {
        PaymentController paymentController = new PaymentController();
        //paymentController.charge(new User(), new Ticket());
    }

    @Test
    void refund() {
        PaymentController paymentController = new PaymentController();
        paymentController.refund(new Payment(2, 20, 1, 6));

    }

    @Test
    void removePayment() {
        PaymentController paymentController = new PaymentController();
        paymentController.removePayment(new Payment(10, 20, 1, 6));
    }

    @Test
    void findPayment() {
        PaymentController paymentController = new PaymentController();
        //System.out.println(paymentController.findPayment(new Ticket()).toString());
    }
}