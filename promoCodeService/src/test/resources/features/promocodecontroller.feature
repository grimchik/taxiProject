Feature: Promo Code Controller API operations
  As a client
  I want to perform CRUD operations on promo codes
  So that I can manage promo code information

  Scenario: Create a new promo code with valid data
    Given a valid promo code payload:
      """
      {
         "percent": 20,
         "activationDate": "2024-10-01",
         "expiryDate": "2025-12-31",
         "keyword": "PROMO20"
      }
      """
    When I send a POST request to "/api/v1/promocodes/"
    Then the response status should be 201
    And the response should contain the created promo code with an id

  Scenario: Fail to create a promo code with invalid percent
    Given a promo code payload with invalid percent:
      """
      {
         "percent": 150,
         "activationDate": "2024-10-01",
         "expiryDate": "2025-12-31",
         "keyword": "PROMO150"
      }
      """
    When I send a POST request to "/api/v1/promocodes/"
    Then the response status should be 400
    And the response should contain validation errors

  Scenario: Retrieve an existing promo code
    Given an existing promo code exists
    When I send a GET request to "/api/v1/promocodes/{id}"
    Then the response status should be 200
    And the response should contain a promo code with the same id

  Scenario: Update an existing promo code
    Given an existing promo code exists
    And a payload to update the promo code:
      """
      {
         "percent": 30,
         "activationDate": "2024-10-05",
         "expiryDate": "2025-01-01",
         "keyword": "PROMO30"
      }
      """
    When I send a PUT request to "/api/v1/promocodes/{id}"
    Then the response status should be 200
    And the response should reflect the updated promo code

  Scenario: Partially update an existing promo code
    Given an existing promo code exists
    And a payload to partially update the promo code:
      """
      {
         "keyword": "NEWPROMO"
      }
      """
    When I send a PATCH request to "/api/v1/promocodes/{id}"
    Then the response status should be 200
    And the response should reflect the updated promo code with new keyword

  Scenario: Delete an existing promo code
    Given an existing promo code exists
    When I send a DELETE request to "/api/v1/promocodes/{id}"
    Then the response status should be 204

  Scenario: Retrieve non existing promo code returns 404 status
    When I send a GET request to "/api/v1/promocodes/99999"
    Then the response status should be 404
