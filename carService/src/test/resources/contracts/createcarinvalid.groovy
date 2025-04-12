import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Создание автомобиля с некорректными данными"
    request {
        method 'POST'
        url '/api/v1/cars/'
        headers {
            contentType(applicationJson())
        }
        body(
                brand: "Toyota",
                model: "Corolla",
                description: "Reliable and efficient.",
                color: "Red",
                category: "ECONOMY",
                number: "20TO-3"
        )
    }
    response {
        status 400
        headers {
            contentType(applicationJson())
        }
        body(
                error: "Validation Error",
                message: "Number must be in format DDDDLL-D"
        )
    }
}
