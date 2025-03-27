Feature: Driver Feedback Management

  Scenario: Successfully create driver feedback
    Given the ride with ID 1 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 1
    }
    """
    When I send a POST request to "/api/v1/driver-feedbacks/"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Access to create driver feedback
    Given the ride with ID 1 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 12,
      "rideId": 1
    }
    """
    When I send a POST request to "/api/v1/driver-feedbacks/"
    Then the response status should be 400
    And the response should contain "detail": "Driver is not authorized to provide feedback for this ride."

  Scenario: Successfully get driver feedback by ID
    Given the ride with ID 2 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 2
    }
    """
    Given a driver feedback is created
    When I send a GET request to "/api/v1/driver-feedbacks/{feedbackId}"
    Then the response status should be 200
    And the response should contain "comment": "Great passenger!"

  Scenario: Successfully update driver feedback (PUT)
    Given the ride with ID 3 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 3
    }
    """
    Given a driver feedback is created
    When I send a PUT request to "/api/v1/driver-feedbacks/{feedbackId}" with the following JSON:
    """
    {
      "rate": 4,
      "comment": "Good passenger, but could improve on punctuality",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": false,
      "driverId": 123,
      "rideId": 3
    }
    """
    Then the response status should be 200
    And the response should contain "comment": "Good passenger, but could improve on punctuality"

  Scenario: Successfully update driver feedback (PATCH)
    Given the ride with ID 4 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 4
    }
    """
    Given a driver feedback is created
    When I send a PATCH request to "/api/v1/driver-feedbacks/{feedbackId}" with the following JSON:
    """
    {
      "rate": 3
    }
    """
    Then the response status should be 200
    And the response should contain "rate": 3

  Scenario: Successfully delete driver feedback
    Given the ride with ID 5 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 5
    }
    """
    Given a driver feedback is created
    When I send a DELETE request to "/api/v1/driver-feedbacks/{feedbackId}"
    Then the response status should be 204
    And the feedback should be deleted

  Scenario: Ride is not completed for driver feedback
    Given the ride with ID 1 is completed
    Given the following driver feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great passenger!",
      "politePassenger": true,
      "cleanPassenger": true,
      "punctuality": true,
      "driverId": 123,
      "rideId": 5
    }
    """
    When I send a POST request to "/api/v1/driver-feedbacks/"
    Then the response status should be 400
    And the response should contain "detail": "Can't create feedback. Ride is not completed."
