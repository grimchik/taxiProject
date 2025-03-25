import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Patch Update Client Feedback"
    request {
        method 'PATCH'
        url '/api/v1/client-feedbacks/1'
        headers {
            contentType(applicationJson())
        }
        body(
                rate: 4,
                comment: "Good ride",
                cleanInterior: false,
                safeDriving: true,
                niceMusic: true
        )
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                rate: 4,
                comment: "Good ride",
                cleanInterior: false,
                safeDriving: true,
                niceMusic: true,
                userId: 1,
                rideId: 10
        )
    }
}
