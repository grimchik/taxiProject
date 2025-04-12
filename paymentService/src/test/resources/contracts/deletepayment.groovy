import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete Payment"
    request {
        method 'DELETE'
        url '/api/v1/payments/1'
    }
    response {
        status 204
    }
}
