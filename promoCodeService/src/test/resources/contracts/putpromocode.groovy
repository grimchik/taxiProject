import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Full Update Promo Code"
    request {
        method 'PUT'
        url '/api/v1/promocodes/1'
        headers {
            contentType(applicationJson())
        }
        body(
                percent: 30,
                activationDate: "2023-11-01",
                keyword: "WINTER2023",
                expiryDate: "2024-01-31"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                percent: 30,
                activationDate: "2023-11-01",
                keyword: "WINTER2023",
                expiryDate: "2024-01-31"
        )
    }
}

