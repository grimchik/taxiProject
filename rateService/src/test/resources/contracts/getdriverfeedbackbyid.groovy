
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Get Driver Feedback by ID"
    request {
        method 'GET'
        url '/api/v1/driver-feedbacks/1'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                rate: 4,
                comment: "Great driver",
                politePassenger: true,
                cleanPassenger: true,
                punctuality: true,
                driverId: 1,
                rideId: 10
        )
    }
}

