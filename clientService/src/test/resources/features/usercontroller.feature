Feature: User Controller Component Tests

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

  Scenario: Try to create a user with an existing username
    Given the following user details in JSON:
      """
      {
        "username": "john_doe",
        "password": "pass123",
        "phone": "+375291234567"
      }
      """
    And the user is created
    When I send a POST request to "/api/v1/users/"
    Then the response status should be 409
    And the response should contain "detail": "User with the same username already exists"

  Scenario: Try to create a user with an existing phone number
    Given the following user details in JSON:
      """
      {
        "username": "john_doe_unique",
        "password": "pass123",
        "phone": "+375291234567"
      }
      """
    And the user is created
    When I send a POST request to "/api/v1/users/"
    Then the response status should be 409
    And the response should contain "detail": "User with the same phone already exists"

  Scenario: Update user profile successfully (PUT)
    Given the following user details in JSON:
      """
      {
        "username": "john_doe",
        "password": "pass123",
        "phone": "+375291234567"
      }
      """
    And the user is created
    When I send a PUT request to "/api/v1/users/{userId}" with the following JSON:
      """
      {
        "username": "johnny_doe",
        "password": "pass1234",
        "phone": "+375291234567"
      }
      """
    Then the response status should be 200
    And the response should contain "username": "johnny_doe"
    And the response should contain "phone": "+375291234567"

  Scenario: Updating password with the same value returns error
    Given the following user details in JSON:
      """
      {
        "username": "john_doe",
        "password": "pass1234",
        "phone": "+375291234567"
      }
      """
    And the user is created
    When I send a PATCH request to "/api/v1/users/{userId}" with the following JSON:
      """
      {
        "password": "pass1234"
      }
      """
    Then the response status should be 400
    And the response should contain "detail": "The new password cannot be the same as the old password."

  Scenario: Successfully delete a user
    Given the following user details in JSON:
      """
      {
        "username": "john_doe",
        "password": "pass1234",
        "phone": "+375291234567"
      }
      """
    And the user is created
    When I send a DELETE request to "/api/v1/users/{userId}"
    Then the response status should be 204
    And the user should be deleted

