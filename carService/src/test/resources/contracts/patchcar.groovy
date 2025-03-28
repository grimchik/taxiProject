import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Частичное обновление автомобиля (PATCH)"
    request {
        method 'PATCH'
        url '/api/v1/cars/1'
        headers {
            contentType(applicationJson())
        }
        body(
                number: "2021TY-4",
                category: "COMFORT"
        )
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
                category: "COMFORT",
                number: "2021TY-4"
        )
    }
}
