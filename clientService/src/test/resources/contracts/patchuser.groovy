import org.springframework.cloud.contract.spec.Contract;
Contract.make {
    description "User PATCH"
    request {
        method 'PATCH'
        url '/api/v1/users/1'
        headers {
            contentType(applicationJson())
        }
        body(
                password: "newsecret",
                phone: "+375(29)987-65-43",
                username: "johndoe"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                username: "johndoe",
                phone: "+375(29)987-65-43"
        )
    }
}

