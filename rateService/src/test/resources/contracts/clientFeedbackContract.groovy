import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should return client feedback by ID"

    request {
        method GET()
        url '/api/v1/client-feedbacks/1'
    }

    response {
        status 200
        body([
                id: 1,
                userId: 123,
                rideId: 456,
                rate: 5,
                comment: "Excellent service",
                cleanInterior: true,
                safeDriving: false,
                niceMusic: true
        ])
        headers {
            contentType(applicationJson())
        }
    }
}