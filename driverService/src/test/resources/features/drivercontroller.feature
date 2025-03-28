Feature: Driver Controller Component Tests

  Scenario: Successfully create a new driver
    Given the following driver details in JSON:
      """
      {
        "name": "John Doe",
        "username": "john_doe",
        "password": "pass123",
        "phone": "+375291234567"
      }
      """
    When I send a POST request to "/api/v1/drivers/"
    Then the response status should be 201
    And the response should contain a "id"

  Scenario: Try to create a driver with an existing username
    Given the following driver details in JSON:
    """
    {
      "name": "John Doe",
      "username": "john_doe",
      "password": "pass123",
      "phone": "+375291234567"
    }
    """
    And the driver is created
    When I send a POST request to "/api/v1/drivers/"
    Then the response status should be 409
    And the response should contain "detail": "Driver with the same username already exists"

  Scenario: Try to create a driver with an existing phone number
    Given the following driver details in JSON:
    """
    {
      "name": "John Doe",
      "username": "john_doe_unique",
      "password": "pass123",
      "phone": "+375291234567"
    }
    """
    And the driver is created
    When I send a POST request to "/api/v1/drivers/"
    Then the response status should be 409
    And the response should contain "detail": "Driver with the same phone already exists"

  Scenario: Update driver profile successfully (PUT)
    Given the following driver details in JSON:
    """
    {
      "name": "John Doe",
      "username": "john_doe",
      "password": "pass123",
      "phone": "+375291234567"
    }
    """
    And the driver is created
    When I send a PUT request to "/api/v1/drivers/{driverId}" with the following JSON:
    """
    {
      "name": "Johnny Doe",
      "username": "john_doe",
      "password": "pass1234",
      "phone": "+375291234567"
    }
    """
    Then the response status should be 200
    And the response should contain "name": "Johnny Doe"
    And the response should contain "phone": "+375291234567"

  Scenario: Updating password with the same value returns error
    Given the following driver details in JSON:
      """
      {
        "name": "John Doe",
        "username": "john_doe",
        "password": "pass1234",
        "phone": "+375291234567"
      }
      """
    And the driver is created
    When I send a PATCH request to "/api/v1/drivers/{driverId}" with the following JSON:
      """
      {
        "password": "pass1234"
      }
      """
    Then the response status should be 400
    And the response should contain "detail": "The new password cannot be the same as the old password."

  Scenario: Successfully delete a driver
    Given the following driver details in JSON:
      """
      {
        "name": "John Doe",
        "username": "john_doe",
        "password": "pass1234",
        "phone": "+375291234567"
      }
      """
    And the driver is created
    When I send a DELETE request to "/api/v1/drivers/{driverId}"
    Then the response status should be 204
    And the driver should be deleted

  Scenario: Driver not found by ID
    When I send a GET request to "/api/v1/drivers/{driverId}"
    Then the response status should be 400
    And the response should contain "detail": "Driver has already been deleted"

