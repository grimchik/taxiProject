import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Full Update User via PUT"
    request {
        method 'PUT'
        url '/api/v1/users/1'
        headers {
            contentType(applicationJson())
        }
        body(
                username: "john_updated",
                password: "secretupdated",
                phone: "+375(29)111-22-33"
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                username: "john_updated",
                phone: "+375(29)111-22-33"
        )
    }
}
