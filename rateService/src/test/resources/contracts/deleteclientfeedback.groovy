import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete Client Feedback"
    request {
        method 'DELETE'
        url '/api/v1/client-feedbacks/1'
    }
    response {
        status 204
    }
}
