import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Partial Update Driver via PATCH"
    request {
        method 'PATCH'
        url '/api/v1/drivers/1'
        headers {
            contentType(applicationJson())
        }
        body(
                name: "John D."
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                name: "John D.",
                licenseNumber: "D1234567"
        )
    }
}
