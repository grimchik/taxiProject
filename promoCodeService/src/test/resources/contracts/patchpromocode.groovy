import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Partial Update Promo Code"
    request {
        method 'PATCH'
        url '/api/v1/promocodes/1'
        headers {
            contentType(applicationJson())
        }
        body(
                percent: 25,
                activationDate: "2023-10-15",
                expiryDate: "2023-12-31",
                keyword: "FALL2023"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                percent: 25,
                activationDate: "2023-10-15",
                keyword: "FALL2023",
                expiryDate: "2023-12-31"
        )
    }
}

