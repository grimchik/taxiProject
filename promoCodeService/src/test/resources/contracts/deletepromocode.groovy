
import org.springframework.cloud.contract.spec.Contract;

Contract.make {
    description "Delete Promo Code"
    request {
        method 'DELETE'
        url '/api/v1/promocodes/1'
    }
    response {
        status 204
    }
}
