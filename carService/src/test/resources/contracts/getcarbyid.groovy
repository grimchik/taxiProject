import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Получение автомобиля по ID"
    request {
        method 'GET'
        url '/api/v1/cars/1'
    }
    response {
        status 200
        headers {
            contentType(applicationJson())
        }
        body(
                id: 1,
                brand: "Toyota",
                model: "Corolla",
                description: "Reliable and efficient.",
                color: "Red",
                category: "ECONOMY",
                number: "2020TO-3"
        )
    }
}
