import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Full Update Driver via PUT"
    request {
        method 'PUT'
        url '/api/v1/drivers/1'
        headers {
            contentType(applicationJson())
        }
        body(
                name: "Johnathan Doe",
                licenseNumber: "D1234567"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                name: "Johnathan Doe",
                licenseNumber: "D1234567"
        )
    }
}
