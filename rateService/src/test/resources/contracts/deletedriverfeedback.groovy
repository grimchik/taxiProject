
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Delete Driver Feedback"
    request {
        method 'DELETE'
        url '/api/v1/driver-feedbacks/1'
    }
    response {
        status 204
    }
}
