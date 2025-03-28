import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Full Update Client Feedback"
    request {
        method 'PUT'
        url '/api/v1/client-feedbacks/1'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 3,
                comment: "Average ride",
                cleanInterior: true,
                safeDriving: false,
                niceMusic: false,
                userId: 1,
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
                cleanInterior: true,
                safeDriving: false,
                niceMusic: false,
                userId: 1,
                rideId: 10
        )
    }
}
