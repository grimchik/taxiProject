import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create User"
    request {
        method 'POST'
        url '/api/v1/users'
        headers {
            contentType(applicationJson())
        }
        body(
                username: "johndoe",
                password: "secret",
                phone: "+375(29)123-45-67"
        )
    }
    response {
        status 201
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
