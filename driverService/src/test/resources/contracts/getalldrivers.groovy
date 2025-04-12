import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get All Drivers"
    request {
        method 'GET'
        urlPath '/api/v1/drivers/'
        queryParameters {
            parameter "page": "0"
            parameter "size": "5"
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
                        name: "John Doe",
                        licenseNumber: "D1234567"
                ]
        ])
    }
}
