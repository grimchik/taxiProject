import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get All Users"
    request {
        method 'GET'
        urlPath('/api/v1/users') {
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
                        username: "johndoe",
                        phone: "+375(29)123-45-67"
                ],
                [
                        id: 2,
                        username: "janedoe",
                        phone: "+375(29)765-43-21"
                ]
        ])
    }
}