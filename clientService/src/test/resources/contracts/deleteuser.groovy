
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete User"
    request {
        method 'DELETE'
        url '/api/v1/users/1'
    }
    response {
        status 204
    }
}

