
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Full Update Driver Feedback via PUT"
    request {
        method 'PUT'
        url '/api/v1/driver-feedbacks/1'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 3,
                comment: "Average ride",
                politePassenger: true,
                cleanPassenger: false,
                punctuality: false,
                driverId: 1,
                rideId: 10
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                rate: 3,
                comment: "Average ride",
                politePassenger: true,
                cleanPassenger: false,
                punctuality: false,
                driverId: 1,
                rideId: 10
        )
    }
}

