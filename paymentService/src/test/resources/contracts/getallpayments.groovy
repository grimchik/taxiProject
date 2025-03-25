import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get all Payments with pagination"
    request {
        method 'GET'
        urlPath '/api/v1/payments'
        {
            queryParameters
            {
                parameter 'page': '0'
                parameter 'size': '5'
            }
        }
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body([
                [
                        id: 1,
                        price: 100.50,
                        paymentType: "CARD",
                        paymentDate: "2023-10-01T12:00:00",
                        cardNumber: "1234-5678-1234-5678"
                ],
                [
                        id: 2,
                        price: 200.00,
                        paymentType: "CASH",
                        paymentDate: "2023-10-01T12:05:00",
                        cardNumber: null
                ]
        ])
    }
}