Feature: E2E Tests

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

  Scenario: Create feedback for a driver with ride id
    Given a valid feedback payload:
      """
      {
        "rate": 5,
        "comment": "Great ride!",
        "politePassenger": true,
        "cleanPassenger": true,
        "punctuality": true,
        "driverId": 1,
        "rideId": 1
      }
      """
    Given the feedback is created
    When I send a POST request to feedback "/api/v1/drivers/{driverId}/create-feedback"
    Then the response status should be 201
    And the response should contain the created feedback with an id


  Scenario: Assign a car to a driver
    Given the car is created:
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
    When I send a POST request to assign car "/api/v1/drivers/{driverId}/assign-car/1"
    Then the response status should be 200
    And the response should contain a "carId"

  Scenario: Apply a ride to a driver
    Given the ride is created:
      """
      {
        "userId" : 1,
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
    When I send a POST request to apply for a ride "/api/v1/drivers/{driverId}/apply-ride/1"
    Then the response status should be 200
    And the response should contain a "carId"

