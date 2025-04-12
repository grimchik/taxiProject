import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get All Promo Codes with pagination"
    request {
        method 'GET'
        urlPath('/api/v1/promocodes') {
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
                        percent: 20,
                        activationDate: "2023-10-10",
                        keyword: "SUMMER2023",
                        expiryDate: "2023-12-31"
                ],
                [
                        id: 2,
                        percent: 15,
                        activationDate: "2023-09-01",
                        keyword: "EARLYBIRD",
                        expiryDate: "2023-10-01"
                ]
        ])
    }
}
