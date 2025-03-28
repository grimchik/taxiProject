
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Partial Update Ride (PATCH)"
    request {
        method 'PATCH'
        url '/api/v1/rides/1'
        headers {
            contentType(applicationJson())
        }
        body(
                userId: 1,
                locations: [
                        [ address: "789 Oak St", latitude: "47.000000", longitude: "92.000000" ],
                        [ address: "321 Pine St", latitude: "48.000000", longitude: "93.000000" ]
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
                        [ address: "789 Oak St", latitude: "47.000000", longitude: "92.000000" ],
                        [ address: "321 Pine St", latitude: "48.000000", longitude: "93.000000" ]
                ],
                status: "UPDATED",
                price: 25.50
        )
    }
}
