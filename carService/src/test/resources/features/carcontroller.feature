Feature: Car Controller API operations
  As a client
  I want to perform CRUD operations on cars
  So that I can manage car information

  Scenario: Create a new car with valid data
    Given a valid car payload:
      """
      {
         "brand": "Toyota",
         "model": "Camry",
         "description": "Sedan car",
         "color": "Red",
         "category": "ECONOMY",
         "number": "1234AB-3"
      }
      """
    When I send a POST request to "/api/v1/cars/"
    Then the response status should be 201
    And the response should contain the created car with an id

  Scenario: Fail to create a car with invalid number format
    Given a car payload with invalid number format:
      """
      {
         "brand": "Toyota",
         "model": "Camry",
         "description": "Sedan car",
         "color": "Blue",
         "category": "ECONOMY",
         "number": "1234AB-8"
      }
      """
    When I send a POST request to "/api/v1/cars/"
    Then the response status should be 400
    And the response should contain validation errors

  Scenario: Retrieve an existing car
    Given an existing car exists
    When I send a GET request to "/api/v1/cars/{id}"
    Then the response status should be 200
    And the response should contain a car with the same id

  Scenario: Update an existing car
    Given an existing car exists
    And a payload to update the car:
      """
      {
         "brand": "Honda",
         "model": "Accord",
         "description": "Updated description",
         "color": "Black",
         "category": "COMFORT",
         "number": "5678CD-5"
      }
      """
    When I send a PUT request to "/api/v1/cars/{id}"
    Then the response status should be 200
    And the response should reflect the updated car

  Scenario: Partially update an existing car
    Given an existing car exists
    And a payload to partially update the car:
      """
      {
         "color": "Yellow"
      }
      """
    When I send a PATCH request to "/api/v1/cars/{id}"
    Then the response status should be 200
    And the response should reflect the updated color

  Scenario: Delete an existing car
    Given an existing car exists
    When I send a DELETE request to "/api/v1/cars/{id}"
    Then the response status should be 204

  Scenario: Creating duplicate car returns 409 status
    Given a valid car payload:
      """
      {
         "brand": "Toyota",
         "model": "Camry",
         "description": "Duplicate car",
         "color": "Blue",
         "category": "ECONOMY",
         "number": "1235AB-3"
      }
      """
    When I send a POST request to "/api/v1/cars/"
    Then the response status should be 201
    And the response should contain the created car with an id
    Given a valid car payload:
      """
      {
         "brand": "Toyota",
         "model": "Camry",
         "description": "Duplicate car",
         "color": "Blue",
         "category": "ECONOMY",
         "number": "1235AB-3"
      }
      """
    When I send a POST request to "/api/v1/cars/"
    Then the response status should be 409

  Scenario: Retrieve non existing car returns 404 status
    When I send a GET request to "/api/v1/cars/100"
    Then the response status should be 404
