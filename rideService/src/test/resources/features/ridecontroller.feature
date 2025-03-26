Feature: Ride Controller API operations
  As a client
  I want to perform CRUD operations on rides
  So that I can manage ride information

  Scenario: Create a new ride with valid data
    Given a valid ride payload:
      """
      {
         "userId": 1,
         "locations": [
             {"address": "123 Main St", "latitude": "37.774929", "longitude": "-122.419416"},
             {"address": "456 Second St", "latitude": "37.775000", "longitude": "-122.417000"}
         ]
      }
      """
    When I send a POST request to "/api/v1/rides/"
    Then the response status should be 201
    And the response should contain the created ride with an id

  Scenario: Fail to create a ride with insufficient locations
    Given a valid ride payload:
      """
      {
         "userId": 1,
         "locations": [
             {"address": "Only One St", "latitude": "37.774929", "longitude": "-122.419416"}
         ]
      }
      """
    When I send a POST request to "/api/v1/rides/"
    Then the response status should be 400
    And the response should contain validation errors

  Scenario: Retrieve an existing ride
    Given an existing ride exists
    When I send a GET request to "/api/v1/rides/{id}"
    Then the response status should be 200
    And the response should contain a ride with the same id

  Scenario: Update an existing ride using PATCH
    Given an existing ride exists
    And a payload to update the ride:
      """
      {
         "status": "UPDATED"
      }
      """
    When I send a PATCH request to "/api/v1/rides/{id}"
    Then the response status should be 200
    And the response should reflect the updated ride

  Scenario: Delete an existing ride
    Given an existing ride exists
    When I send a DELETE request to "/api/v1/rides/{id}"
    Then the response status should be 204

  Scenario: Get all rides
    When I send a GET request to "/api/v1/rides/?page=0&size=5"
    Then the response status should be 200
    And the response should contain a list of rides

  Scenario: Get rides by user id
    When I send a GET request to "/api/v1/rides/user-rides/1?page=0&size=5"
    Then the response status should be 200
    And the response should contain rides for user id 1

  Scenario: Get rides by driver id
    When I send a GET request to "/api/v1/rides/driver-rides/2?page=0&size=5"
    Then the response status should be 200
    And the response should contain rides for driver id 2

  Scenario: Get available rides
    When I send a GET request to "/api/v1/rides/available-rides?page=0&size=5"
    Then the response status should be 200
    And the response should contain available rides

  Scenario: Get completed rides for a driver
    When I send a GET request to "/api/v1/rides/completed-rides/2?page=0&size=5"
    Then the response status should be 200
    And the response should contain completed rides for driver id 2

  Scenario: Get active ride for a driver
    When I send a GET request to "/api/v1/rides/active-ride/2"
    Then the response status should be 200
    And the response should contain an active ride for driver id 2

  Scenario: Apply ride with valid car and driver payload
    Given an existing ride exists
    And a valid car and driver payload:
      """
      {
         "carId": 5,
         "driverId": 2
      }
      """
    When I send a POST request to "/api/v1/rides/apply-ride/{id}"
    Then the response status should be 200
    And the response should reflect that the ride has been applied

  Scenario: Get completed rides within a period for a driver
    When I send a GET request to "/api/v1/rides/completed-rides-period/2?start=2023-10-01T00:00:00&end=2023-10-31T23:59:59&page=0&size=5"
    Then the response status should be 200
    And the response should contain completed rides within the period

  Scenario: Get total earnings for a driver between two dates
    When I send a GET request to "/api/v1/rides/earning/2?start=2023-10-01T00:00:00&end=2023-10-31T23:59:59"
    Then the response status should be 200
    And the response should contain the total earnings for driver id 2
