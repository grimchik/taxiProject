
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Create Driver Feedback"
    request {
        method 'POST'
        url '/api/v1/driver-feedbacks/'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 4,
                comment: "Great driver",
                politePassenger: true,
                cleanPassenger: true,
                punctuality: true,
                driverId: 1,
                rideId: 10
        )
    }
    response {
        status 201
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
