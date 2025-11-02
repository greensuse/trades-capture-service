# Trades Capture Service

Implements the requirements: file/kafka input, canonical transform (mask account, uppercase security ID, normalize trade type), platform JSON, Kafka publish, in-memory store, security, performance considerations, tests, Swagger, Dockerfile.

## Run
```
# Start Kafka
cd ./run
docker-compose up -d
```
mvn clean package
java -jar target/trades-capture-service-1.0.0.jar
```
Configure Kafka in `application.yml` (localhost:9092 by default).

## Endpoints
- `POST /api/v1/upload/file` (multipart) CSV/JSON

Sample file located at '\src\test\resources\test.csv'

- `POST /api/v1/upload/json` (list of objects)

[
  {
    "accountNumber": "ACC123",
    "securityId": "SEC456",
    "tradeType": "BUY",
    "quantity": "100",
    "price": "10.5",
    "tradeDate": "2025-10-31"
  }
]

- `POST /api/v1/upload/publish-inbound` (list of objects)

  {
    "accountNumber": "ACC123",
    "securityId": "SEC456",
    "tradeType": "BUY",
    "quantity": "100",
    "price": "10.5",
    "tradeDate": "2025-10-31"
  }

CSV headers:
```
account_number,security_id,trade_type,quantity,price,trade_date
```
Swagger UI: `http://localhost:8080/swagger-ui/index.html`


