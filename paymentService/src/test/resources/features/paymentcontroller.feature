Feature: Payment Controller API operations
  As a client
  I want to perform CRUD operations on payments
  So that I can manage payment transactions

  Scenario: Create a new payment with valid data
    Given a valid payment payload:
      """
      {
         "price": 100.50,
         "paymentType": "CARD",
         "cardNumber": "1234-5678-9012-3456",
         "rideId": 1,
         "userId": 42
      }
      """
    When I send a POST request to "/api/v1/payments/"
    Then the response status should be 201
    And the response should contain the created payment with an id

  Scenario: Fail to create a payment with invalid data
    Given a payment payload with invalid data:
      """
      {
         "price": -50,
         "paymentType": "BANK_TRANSFER"
      }
      """
    When I send a POST request to "/api/v1/payments/"
    Then the response status should be 400
    And the response should contain validation errors

  Scenario: Retrieve an existing payment
    Given an existing payment exists
    When I send a GET request to "/api/v1/payments/{id}"
    Then the response status should be 200
    And the response should contain a payment with the same id

  Scenario: Update an existing payment
    Given an existing payment exists
    And a payload to update the payment:
      """
      {
         "price": 200.00,
         "paymentType": "CASH",
         "rideId": 1,
         "userId": 42
      }
      """
    When I send a PUT request to "/api/v1/payments/{id}"
    Then the response status should be 200
    And the response should reflect the updated payment

  Scenario: Partially update an existing payment
    Given an existing payment exists
    And a payload to partially update the payment:
      """
      {
         "price": 150.75
      }
      """
    When I send a PATCH request to "/api/v1/payments/{id}"
    Then the response status should be 200
    And the response should reflect the updated payment with new price

  Scenario: Delete an existing payment
    Given an existing payment exists
    When I send a DELETE request to "/api/v1/payments/{id}"
    Then the response status should be 204

  Scenario: Retrieve a non-existing payment returns 404 status
    When I send a GET request to "/api/v1/payments/99999"
    Then the response status should be 404
