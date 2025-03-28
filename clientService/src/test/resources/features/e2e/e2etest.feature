Feature: E2E Tests

  Scenario: Successfully create a new user
    Given the following user details in JSON:
      """
      {
        "username": "john_doe",
        "password": "pass123",
        "phone": "+375291234567"
      }
      """
    When I send a POST request to "/api/v1/users/"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Create a new promo code with valid data
    Given a valid promo code payload:
      """
      {
         "percent": 10,
         "activationDate": "2024-10-01",
         "expiryDate": "2025-12-31",
         "keyword": "TEST"
      }
      """
    And the promo code is created
    When I send a POST request to "/api/v1/users/create-promocode"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Successfully create a new ride
    Given the following ride details in JSON:
      """
      {
        "locations": [
          {
            "address": "Minsk, Belarus",
            "latitude": "53.9",
            "longitude": "27.5667"
          },
          {
            "address": "Brest, Belarus",
            "latitude": "52.1",
            "longitude": "23.7"
          }
        ]
      }
      """
    Given the ride is created
    When I send a POST request to ride "/api/v1/users/create-ride/{userId}"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Create a new car with valid data
    Given a valid car payload:
      """
      {
        "brand": "Toyota",
        "model": "Corolla",
        "description": "Norm",
        "color": "Blue",
        "category": "COMFORT",
        "number": "2111VB-5"
      }
      """
    Given the car is created
    When I send a POST request to car "/api/v1/users/create-car"
    Then the response status should be 201
    And the response should contain the created car with an id

  Scenario: Create feedback for a user with ride id
    Given a valid feedback payload:
      """
      {
        "rate": 5,
        "comment": "Great ride!",
        "cleanInterior": true,
        "safeDriving": true,
        "niceMusic": true,
        "userId": 1,
        "rideId": 1
      }
      """
    Given the feedback is created
    When I send a POST request to feedback "/api/v1/users/{userId}/create-feedback"
    Then the response status should be 201
    And the response should contain the created feedback with an id

  Scenario: Create a payment and confirm it
    Given a valid payment payload:
      """
      {
        "price": 11.0,
        "paymentType": "DEFAULT"
      }
      """
    When I send a PATCH request to "/api/v1/users/{userId}/confirmed-payment/{paymentId}"
      """
      {
        "price": 11.0,
        "paymentType": "CARD",
        "cardNumber": "1111-1111-1111-1111"
      }
      """
    Then the response status should be 200
    And the payment should be paid


