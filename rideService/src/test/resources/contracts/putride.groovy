
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Full Update Ride (PUT)"
    request {
        method 'PUT'
        url '/api/v1/rides/1'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 1,
                locations: [
                        [ address: "100 First Ave", latitude: "40.111111", longitude: "70.111111" ],
                        [ address: "200 Second Ave", latitude: "41.222222", longitude: "71.222222" ]
                ]
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                userId: 1,
                driverId: 10,
                carId: 20,
                locations: [
                        [ address: "100 First Ave", latitude: "40.111111", longitude: "70.111111" ],
                        [ address: "200 Second Ave", latitude: "41.222222", longitude: "71.222222" ]
                ],
                status: "UPDATED",
                price: 30.00
        )
    }
}

