# S4.02._fruitApi_MongoDB
This is a REST API developed with Spring Boot 3 to manage fruit orders for clients. The project demonstrates the use of MongoDB as a NoSQL persistence system, specifically focusing on embedded documents to store order items.🚀 

## Technologies
Java 21 (LTS)
Spring Boot 3.xSpring
Data MongoDB (NoSQL Persistence)
Spring Boot Validation (Data constraints)
Maven (Dependency management)

## Data Model
The application uses a document-oriented approach where each Order contains a list of OrderItem objects embedded within a single document in the orders collection.

Example MongoDB Document:
JSON{
  "_id": "65f1a...",
  "clientName": "John Doe",
  "deliveryDate": "2026-10-15",
  "items": [
    { "fruitName": "Apple", "quantityInKilos": 10 },
    { "fruitName": "Banana", "quantityInKilos": 5 }
  ]
}

## API 
EndpointsMethodEndpointDescriptionPOST/ordersCreate a new fruit order.
GET/ordersRetrieve all registered orders.
GET/orders/{id}Retrieve specific order details by ID.
PUT/orders/{id}Update an existing order.DELETE/orders/{id}Delete an order by ID.

## Business Logic & Constraints
To ensure data integrity, the following validation rules are applied:Delivery Date: Must be at least tomorrow (cannot be today or in the past).
Items: An order must contain at least one fruit.
Quantity: Fruit weight must be a positive integer ($> 0$).Errors: * Returns 400 Bad Request for validation failures.Returns 404 Not Found if the requested ID does not exist.

## Setup and Installation
Clone the repository:Bashgit clone https://github.com/your-username/fruit-order-api.git
cd fruit-order-api
Configure Database:Update src/main/resources/application.properties with your MongoDB URI:Propertiesspring.data.mongodb.uri=mongodb://localhost:27017/fruit_orders_db
Run the Application:Bashmvn spring-boot:run

## Testing the API
You can use Postman, Insomnia, or cURL.Sample POST Request:Bashcurl -X POST http://localhost:8080/orders \
-H "Content-Type: application/json" \
-d '{
    "clientName": "Sarah Connor",
    "deliveryDate": "2026-11-20",
    "items": [
        {"fruitName": "Orange", "quantityInKilos": 12}
    ]
}'
