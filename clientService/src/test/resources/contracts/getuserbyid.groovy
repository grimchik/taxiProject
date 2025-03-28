import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get User by ID"
    request {
        method 'GET'
        url '/api/v1/users/1'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                username: "johndoe",
                phone: "+375(29)123-45-67"
        )
    }
}

