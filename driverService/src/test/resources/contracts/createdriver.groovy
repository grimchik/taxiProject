import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create Driver"
    request {
        method 'POST'
        url '/api/v1/drivers/'
        headers {
            contentType(applicationJson())
        }
        body(
                name: "John Doe",
                licenseNumber: "D1234567"
        )
    }
    response {
        status 201
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

