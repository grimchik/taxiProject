import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get All Drivers"
    request {
        method 'GET'
        urlPath('/api/v1/drivers') {
            queryParameters {
                parameter 'page': $(consumer(regex('[0-9]+')), producer(0))
                parameter 'size': $(consumer(regex('[0-9]+')), producer(5))
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
                        name: "John Doe",
                        licenseNumber: "D1234567"
                ]
        ])
    }
}