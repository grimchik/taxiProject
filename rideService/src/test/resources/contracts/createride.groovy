import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create Ride"
    request {
        method 'POST'
        url '/api/v1/rides/'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 1,
                locations: [
                        [ address: "123 Main St", latitude: "45.123456", longitude: "90.123456" ],
                        [ address: "456 Elm St", latitude: "46.654321", longitude: "91.654321" ]
                ]
        )
    }
    response {
        status 201
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                userId: 1,
                driverId: 10,
                carId: 20,
                locations: [
                        [ address: "123 Main St", latitude: "45.123456", longitude: "90.123456" ],
                        [ address: "456 Elm St", latitude: "46.654321", longitude: "91.654321" ]
                ],
                status: "REQUESTED",
                price: 25.50
        )
    }
}
