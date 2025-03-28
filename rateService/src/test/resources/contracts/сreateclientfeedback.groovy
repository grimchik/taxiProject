import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Create Client Feedback"
    request {
        method 'POST'
        url '/api/v1/client-feedbacks/'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 5,
                comment: "Excellent ride",
                cleanInterior: true,
                safeDriving: true,
                niceMusic: false,
                userId: 1,
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
                rate: 5,
                comment: "Excellent ride",
                cleanInterior: true,
                safeDriving: true,
                niceMusic: false,
                userId: 1,
                rideId: 10
        )
    }
}