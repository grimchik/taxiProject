
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get Promo Code by ID"
    request {
        method 'GET'
        url '/api/v1/promocodes/1'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                percent: 20,
                activationDate: "2023-10-10",
                keyword: "SUMMER2023",
                expiryDate: "2023-12-31"
        )
    }
}

