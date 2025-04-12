
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create Promo Code"
    request {
        method 'POST'
        url '/api/v1/promocodes/'
        headers {
            contentType(applicationJson())
        }
        body(
                percent: 20,
                activationDate: "2023-10-10",
                keyword: "SUMMER2023",
                expiryDate: "2023-12-31"
        )
    }
    response {
        status 201
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

