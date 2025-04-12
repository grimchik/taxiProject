import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Patch Payment Update (UpdatePaymentDTO)"
    request {
        method 'PATCH'
        url '/api/v1/payments/1'
        headers {
            contentType(applicationJson())
        }
        body(
                paymentType: "CASH",
                price: 150.75
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                price: 150.75,
                paymentType: "CASH",
                paymentDate: "2023-10-01T12:00:00",
                cardNumber: null
        )
    }
}

