import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete Ride"
    request {
        method 'DELETE'
        url '/api/v1/rides/1'
    }
    response {
        status 204
    }
}
