import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Создание нового автомобиля"
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
                number: "2020TO-3"
        )
    }
    response {
        status 201
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                brand: "Toyota",
                model: "Corolla",
                description: "Reliable and efficient.",
                color: "Red",
                category: "SEDAN",
                number: "2020TO-3"
        )
    }
}
