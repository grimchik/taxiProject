import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get Driver by ID"
    request {
        method 'GET'
        url '/api/v1/drivers/1'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                name: "John Doe",
                licenseNumber: "D1234567"
        )
    }
}
