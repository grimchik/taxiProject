Feature: Client Feedback Management

  Scenario: Successfully create client feedback
    Given the ride with ID 1 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 1
    }
    """
    When I send a POST request to "/api/v1/client-feedbacks/"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Access to create feedback
    Given the ride with ID 1 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 12,
      "rideId": 1
    }
    """
    When I send a POST request to "/api/v1/client-feedbacks/"
    Then the response status should be 400
    And the response should contain "detail": "User is not authorized to provide feedback for this ride."

  Scenario: Successfully get client feedback by ID
    Given the ride with ID 2 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 2
    }
    """
    Given a client feedback is created
    When I send a GET request to "/api/v1/client-feedbacks/{feedbackId}"
    Then the response status should be 200
    And the response should contain "comment": "Great ride!"

  Scenario: Successfully update client feedback (PUT)
    Given the ride with ID 3 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 3
    }
    """
    Given a client feedback is created
    When I send a PUT request to "/api/v1/client-feedbacks/{feedbackId}" with the following JSON:
    """
    {
      "rate": 4,
      "comment": "Good ride, but could be better",
      "cleanInterior": true,
      "safeDriving": false,
      "niceMusic": true,
      "userId": 123,
      "rideId": 3
    }
    """
    Then the response status should be 200
    And the response should contain "comment": "Good ride, but could be better"

  Scenario: Successfully update client feedback (PATCH)
    Given the ride with ID 4 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 4
    }
    """
    Given a client feedback is created
    When I send a PATCH request to "/api/v1/client-feedbacks/{feedbackId}" with the following JSON:
    """
    {
      "rate": 3
    }
    """
    Then the response status should be 200
    And the response should contain "rate": 3

  Scenario: Successfully delete client feedback
    Given the ride with ID 5 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 5
    }
    """
    Given a client feedback is created
    When I send a DELETE request to "/api/v1/client-feedbacks/{feedbackId}"
    Then the response status should be 204
    And the feedback should be deleted

  Scenario: Ride is not completed
    Given the ride with ID 1 is completed
    Given the following client feedback details in JSON:
    """
    {
      "rate": 5,
      "comment": "Great ride!",
      "cleanInterior": true,
      "safeDriving": true,
      "niceMusic": false,
      "userId": 123,
      "rideId": 5
    }
    """
    When I send a POST request to "/api/v1/client-feedbacks/"
    Then the response status should be 400
    And the response should contain "detail": "Can't create feedback. Ride is not completed."
