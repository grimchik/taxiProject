
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create Payment"
    request {
        method 'POST'
        url '/api/v1/payments/'
        headers {
            contentType(applicationJson())
        }
        body(
                price: 100.50,
                paymentType: "CARD",
                cardNumber: "1234-5678-1234-5678"
        )
    }
    response {
        status 201
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                price: 100.50,
                paymentType: "CARD",
                paymentDate: "2023-10-01T12:00:00",
                cardNumber: "1234-5678-1234-5678"
        )
    }
}

