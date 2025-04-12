import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete Driver"
    request {
        method 'DELETE'
        url '/api/v1/drivers/1'
    }
    response {
        status 204
    }
}