
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Patch Driver Feedback"
    request {
        method 'PATCH'
        url '/api/v1/driver-feedbacks/1'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 5,
                comment: "Excellent service",
                politePassenger: false,
                cleanPassenger: false,
                punctuality: true
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                rate: 5,
                comment: "Excellent service",
                politePassenger: false,
                cleanPassenger: false,
                punctuality: true,
                driverId: 1,
                rideId: 10
        )
    }
}

