Feature: Ride Controller Component Tests

  Scenario: Successfully create a new ride
    Given the following ride details in JSON:
      """
      {
        "userId": 2,
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
    When I send a POST request to "/api/v1/rides/"
    Then the response status should be 201
    And the response should contain "id"

  Scenario: Try to create a ride with active ride for user
    Given the following ride details in JSON:
      """
      {
        "userId": 2,
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
    When I send a POST request to "/api/v1/rides/"
    Then the response status should be 409
    And the response should contain "detail": "User already has an active ride with status: REQUESTED"

  Scenario: Successfully update ride details (PUT)
    Given the following ride details in JSON:
      """
      {
        "userId": 2,
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
    And the ride is created
    When I send a PATCH request to "/api/v1/rides/{rideId}" with the following JSON:
      """
      {
        "userId": 2,
        "locations": [
          {
            "address": "Gomel, Belarus",
            "latitude": "52.4",
            "longitude": "30.9936"
          },
          {
            "address": "Brest, Belarus",
            "latitude": "52.1",
            "longitude": "23.7"
          }
        ]
      }
      """
    Then the response status should be 200
    And the response should contain "locations" with "address": "Gomel, Belarus"

  Scenario: Successfully delete a ride
    Given the following ride details in JSON:
      """
      {
        "userId": 2,
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
    And the ride is created
    When I send a DELETE request to "/api/v1/rides/{rideId}"
    Then the response status should be 204
    And the ride should be deleted

  Scenario: Ride not found by ID
    When I send a GET request to "/api/v1/rides/{rideId}"
    Then the response status should be 404
    And the response should contain "detail": "Ride not found"