import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Get Client Feedback by ID"
    request {
        method 'GET'
        url '/api/v1/client-feedbacks/1'
    }
    response {
        status 200
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
