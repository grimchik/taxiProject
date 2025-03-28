import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Put Payment Update (PaymentDTO)"
    request {
        method 'PUT'
        url '/api/v1/payments/1'
        headers {
            contentType(applicationJson())
        }
        body(
                price: 200.00,
                paymentType: "CASH"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                price: 200.00,
                paymentType: "CASH",
                paymentDate: "2023-10-01T12:05:00",
                cardNumber: null
        )
    }
}

