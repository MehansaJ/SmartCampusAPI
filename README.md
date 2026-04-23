# 🏫 Smart Campus Sensor & Room Management API

A high-performance RESTful web service built with **Jakarta EE (JAX-RS)** and **Jersey**, running on an embedded **Grizzly HTTP server**. It manages Rooms, Sensors, and a historical log of Sensor Readings — with a focus on thread-safety and robust error handling.

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Jakarta EE (JAX-RS 3.1) |
| Implementation | Eclipse Jersey 3.1.3 |
| HTTP Server | Grizzly (Embedded) |
| JSON Processing | Jackson 2.15 |
| Build Tool | Apache Maven |
| Data Storage | In-Memory (`ConcurrentHashMap`) |

---

## 📂 Project Structure

- `com.smartcampus.resources`: JAX-RS Resource classes (Endpoints)
- `com.smartcampus.models`: POJOs (Room, Sensor, Reading)
- `com.smartcampus.exceptions`: Custom exceptions and ExceptionMappers (Part 5)
- `com.smartcampus.filters`: Logging and Security filters
- `com.smartcampus.data`: DataStore singleton and thread-safe storage

---

## 🚀 Getting Started

### Prerequisites

- **Java 17** or higher
- **Apache Maven** 3.8+

### Installation

**Step 1** — Clone the repository:

```bash
git clone https://github.com/MehansaJ/SmartCampusAPI.git
cd SmartCampusAPI
```

**Step 2** — Build the project:

```bash
mvn clean install
```

**Step 3** — Run the server:

```bash
mvn exec:java -Dexec.mainClass="com.smartcampus.Main"
```

Or simply run `Main.java` from the IDE.

The Grizzly server starts on **http://localhost:8080**. All API endpoints are prefixed with `/api/v1`.

```
=========================================================
SmartCampusAPI Embedded Grizzly Server has started!
=========================================================
👉 http://localhost:8080/api/v1/rooms
=========================================================
```

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/v1` | Discovery — shows API info and available links |
| `GET` | `/api/v1/rooms` | Get all rooms |
| `POST` | `/api/v1/rooms` | Create a new room |
| `DELETE` | `/api/v1/rooms/{roomId}` | Delete a room (must have no sensors) |
| `GET` | `/api/v1/sensors` | Get all sensors (optional `?type=` filter) |
| `POST` | `/api/v1/sensors?roomId={id}` | Create a sensor and link it to a room |
| `PUT` | `/api/v1/sensors/{id}` | Update a sensor's current value |
| `GET` | `/api/v1/sensors/{id}/readings` | Get all readings (Sub-resource via `SensorResource`) |
| `POST` | `/api/v1/sensors/{id}/readings` | Add a new reading (Sub-resource via `SensorResource`) |

---

💻 Sample cURL Commands (Test Suite)

You can use these commands to verify the core functionality of the API. Ensure the server is running on http://localhost:8080.

1. API Discovery (Task 1.2)

Purpose: Demonstrates HATEOAS and root-level entry point.

Bash



curl -X GET http://localhost:8080/api/v1

2. Register a New Room (Task 2.1)

Purpose: Initializes a room resource in the DataStore.

Bash



curl -X POST http://localhost:8080/api/v1/rooms \

  -H "Content-Type: application/json" \

  -d '{"id": "R-101", "name": "Main Lab", "capacity": 50}'

3. Register a Sensor to a Room (Task 3.1)

Purpose: Demonstrates dependency validation and relational linking.

Bash



curl -X POST "http://localhost:8080/api/v1/sensors?roomId=R-101" \

  -H "Content-Type: application/json" \

  -d '{"id": "S-01", "type": "Temperature", "status": "ACTIVE"}'

4. Submit a Sensor Reading (Task 4.1)

Purpose: Demonstrates the sub-resource locator pattern (/sensors/{id}/readings).

Bash



curl -X POST http://localhost:8080/api/v1/sensors/S-01/readings \

  -H "Content-Type: application/json" \

  -d '{"value": 24.5}'

5. Retrieve Historical Readings (Task 4.1)

Purpose: Fetches the collection of readings for a specific sensor.

Bash



curl -X GET http://localhost:8080/api/v1/sensors/S-01/readings


---

## ⚠️ Error Handling

The API uses custom `ExceptionMapper` classes to return clean, consistent JSON error responses instead of raw stack traces.

| Scenario | Exception | HTTP Status |
|---|---|---|
| Deleting a room that still has sensors | `RoomNotEmptyException` | `409 Conflict` |
| Creating a sensor with an invalid/missing room ID | `LinkedResourceNotFoundException` | `422 Unprocessable Entity` |
| Adding a reading to a sensor in MAINTENANCE mode | `SensorUnavailableException` | `403 Forbidden` |
| Any other unhandled error | `GlobalExceptionMapper` | `500 Internal Server Error` |

**Example error response:**
```json
{
  "error": "Conflict",
  "message": "Cannot delete room: Room 'R001' contains sensors."
}
```

---

## 📝 Report: Design Decisions & Justifications

### Task 1.1 — JAX-RS Resource Lifecycle and Thread Safety

In JAX-RS, resource classes follow one of two lifecycles. In **request-scoped** mode (the default), a brand new instance of the resource class is created for every single HTTP request. This means each request gets its own copy of the object, so there's no risk of two requests stepping on each other's data. In **singleton** mode, one single instance handles all requests — which is faster (no object creation overhead) but introduces the danger of shared state being modified by multiple threads at the same time.

In this project, the resource classes (`RoomResource`, `SensorResource`, `ReadingResource`) are request-scoped by default, meaning Jersey creates a new object for every request. However, all of them point to the same `DataStore` singleton. The `DataStore` class follows the Singleton pattern — there is only one instance in the whole application, created through a `synchronized getInstance()` method.

The thread-safety concern comes from the fact that multiple requests can hit the `DataStore` at the same time. To handle this, two thread-safe collections are used:

- **`ConcurrentHashMap`** — Used to store Rooms and Sensors. This map allows multiple threads to read and write at the same time without locking the entire map. It divides the data into segments and only locks the segment being written to, so reads are almost never blocked.

- **`CopyOnWriteArrayList`** — Used for the list of Sensor IDs inside each Room and the list of Readings inside each Sensor. Every time something is added to this list, it creates a brand new copy of the underlying array. This means readers never need a lock — they just read the current snapshot. It's ideal here because readings and sensor lists are read far more often than they are written to.

This combination ensures that the API can safely handle many simultaneous requests without data corruption or race conditions.

---

### Task 1.2 — Hypermedia (HATEOAS) vs Static Documentation

In this project, the `DiscoveryResource` at the root endpoint (`/api/v1`) returns a JSON response containing links to the available resources (`/api/v1/rooms`, `/api/v1/sensors`). This is a basic form of HATEOAS — Hypermedia as the Engine of Application State.

The key advantage of this approach over static documentation (like a PDF or a wiki page) is **decoupling**. With static documentation, if the URL for the rooms endpoint ever changes from `/api/v1/rooms` to `/api/v2/rooms`, every client that hardcoded that URL would break. Developers would need to manually read the updated documentation, find the new URL, and change their code.

With HATEOAS, the client only needs to know one URL — the root endpoint. From there, it reads the `_links` section of the response and follows the correct link dynamically. If the rooms URL changes tomorrow, the discovery endpoint returns the updated link automatically. The client code doesn't need to change at all because it was never hardcoded in the first place.

This is similar to how a person navigates a website — they don't memorize every URL, they click links on the homepage. HATEOAS brings that same flexibility to APIs. It makes the API self-describing and easier for new developers to explore without reading pages of documentation first.

---

### Task 2.1 — Full Objects vs Only IDs in Collection Responses

When returning a list of rooms from `GET /api/v1/rooms`, there are two approaches:

**Option A — Return full objects:** Each room comes with all its fields (id, name, capacity, sensorIds list). This is what this API currently does. The benefit is that the client gets everything it needs in one request. No follow-up calls are needed to get the details of each room.

**Option B — Return only IDs:** Each room would just be represented by its ID string. The client would then need to make a separate `GET /api/v1/rooms/{id}` request for each room to get its full details.

The trade-off comes down to **bandwidth vs processing**:

- Full objects use **more bandwidth** per response because the payload is bigger, especially when rooms have many sensor IDs. For mobile clients on slow connections, this can be a problem.
- Only IDs use **less bandwidth** initially, but the client ends up making N additional HTTP requests (one per room), which means more total processing, more latency, and more load on the server. This is known as the "N+1 problem."

For this project, returning full objects makes more sense because the dataset is relatively small (in-memory data, campus-scale). The extra bandwidth is negligible, and it avoids the overhead of multiple round trips. For a much larger system with thousands of rooms, a paginated approach or a summary view (returning partial objects) would be a better middle ground.

---

### Task 2.2 — Why DELETE is Idempotent

In HTTP, an idempotent operation is one where making the same request once produces the same end result as making it ten times. `DELETE` is considered idempotent because of this principle.

In this API, when `DELETE /api/v1/rooms/R001` is called:

- **First call:** The room exists, it gets removed from the `DataStore`, and the server responds with `204 No Content`.
- **Second call (same request again):** The room is already gone. The server responds with `404 Not Found`.

Even though the response codes are different (204 vs 404), the **end state of the server is the same** — Room R001 does not exist. That's what makes it idempotent. The server's state after the first call and after the second call is identical. No matter how many times the delete is repeated, the room stays deleted.

This is important for reliability. If a client sends a DELETE request and the network drops before it receives the response, it can safely retry the same request without worrying about causing unintended side effects (unlike POST, where repeating the request might create duplicate entries).

---

### Task 3.1 — What Happens When a Client Sends XML Instead of JSON

All resource classes in this API are annotated with `@Consumes(MediaType.APPLICATION_JSON)`. This tells Jersey that the endpoint only accepts JSON request bodies. If a client sends a request with `Content-Type: application/xml` (or any other format that isn't JSON), Jersey will automatically reject it with an **HTTP 415 Unsupported Media Type** response before the request even reaches the resource method.

This is part of the **content negotiation** mechanism built into JAX-RS. The framework checks the `Content-Type` header of the incoming request against the `@Consumes` annotation. If they don't match, it short-circuits the request and returns 415 immediately.

No custom code was needed to handle this — it's a built-in feature of the JAX-RS specification. This protects the API from receiving data in formats it can't parse and gives the client a clear signal about what format to use.

---

### Task 3.2 — QueryParams vs PathParams

In REST APIs, **PathParams** and **QueryParams** serve different purposes:

- **PathParams** (`/sensors/{id}`) are used to identify a specific resource. The ID is part of the URL path itself, and it points to one exact item. For example, `/sensors/S1` means "give me sensor S1." Without the ID, the URL is incomplete and doesn't point to anything meaningful.

- **QueryParams** (`/sensors?type=Temperature`) are used to filter, sort, or modify the results of a collection. They are optional — `/sensors` still works and returns everything. Adding `?type=Temperature` just narrows down the results.

In this project, `SensorResource` uses a `@QueryParam("type")` to let clients filter sensors by their type. This is better than using a PathParam for filtering because:

1. **QueryParams are optional.** The same endpoint works with or without the filter. With a PathParam, a separate endpoint or additional routing logic would be needed.
2. **They can be combined.** If more filters are added later (e.g., `?type=Temperature&status=ACTIVE`), they naturally chain together in the URL. Doing this with PathParams would create ugly, confusing URLs like `/sensors/type/Temperature/status/ACTIVE`.
3. **They match the semantics correctly.** A filter doesn't change *which* resource is being accessed — it changes *how much* of the collection is returned. That's exactly what query parameters are designed for.

---

### Task 4.1 — Benefits of the Sub-Resource Locator Pattern

The `ReadingResource` is mapped to the path `/sensors/{id}/readings`. This is the sub-resource pattern — Readings don't exist as a top-level resource. They are always accessed through the Sensor they belong to.

This pattern has several benefits for managing complexity:

1. **Clear ownership.** The URL itself makes it obvious that readings belong to a specific sensor. There's no confusion about which sensor a reading is associated with. The relationship is encoded directly in the path.

2. **Logical grouping.** Instead of having one massive `SensorResource` class handling sensors, readings, and possibly calibration data all in one place, the reading-related logic is separated into its own `ReadingResource` class. Each class has a single responsibility.

3. **Automatic context.** The sensor ID is captured by `@PathParam("id")` at the class level, so every method inside `ReadingResource` automatically knows which sensor it's working with. There's no need to pass the sensor ID in the request body or as a separate parameter.

4. **Scalability.** If more nested resources are needed in the future (e.g., `/sensors/{id}/alerts` or `/sensors/{id}/calibration`), each one can be its own class without cluttering the existing code.

5. **Cleaner URLs.** The resulting URLs (`/sensors/S1/readings`) read naturally and follow REST conventions. A client can intuitively guess the URL structure.

---

### Task 5.2 — Why HTTP 422 Is More Accurate Than 404 for Missing References

When creating a sensor via `POST /api/v1/sensors?roomId=R999`, if `R999` doesn't exist, this API returns **HTTP 422 (Unprocessable Entity)** instead of 404.

Here's why 422 is the better choice:

- **HTTP 404** means "the resource at this URL was not found." But the URL being accessed is `/api/v1/sensors` — and that endpoint absolutely exists. Returning 404 would be misleading because it suggests the sensors endpoint itself is broken or missing.

- **HTTP 422** means "the server understands the request and the syntax is correct, but it can't process it because of a semantic error in the data." That's exactly what's happening here — the JSON body and the URL are perfectly valid, but the `roomId` value inside the request refers to something that doesn't exist. The **content** is the problem, not the URL.

Using 404 would confuse client developers because they'd wonder if they typed the endpoint URL wrong. Using 422 makes it clear that the endpoint is fine, but the data they sent has a problem — specifically, a reference to a non-existent room. The `LinkedResourceNotFoundException` and its mapper handle this distinction cleanly by returning 422 with a descriptive error message.

---

### Task 5.4 — Cybersecurity Risks of Exposing Java Stack Traces

If an unhandled exception occurs and the server sends the raw Java stack trace back to the client, it reveals sensitive internal details that an attacker can exploit. This falls under two major security risks:

1. **Information Leakage:** A stack trace shows internal class names, package structure, method names, library versions, and sometimes even file paths on the server. For example, seeing `com.smartcampus.repository.DataStore.getRooms(DataStore.java:31)` tells an attacker exactly how the code is structured, what libraries are being used, and where to look for vulnerabilities.

2. **Fingerprinting:** Attackers can use the technology details in a stack trace (e.g., Jersey 3.1.3, Jackson 2.15.2, Grizzly server) to search for known vulnerabilities (CVEs) in those specific versions. If a library has a known exploit, the attacker now knows exactly which version to target.

To prevent this, the `GlobalExceptionMapper` in this project catches all unhandled exceptions and returns a generic `ErrorResponse` JSON object with a safe message like "An unexpected error occurred." The actual exception details are never sent to the client. This way, the API gives the client enough information to know something went wrong, without revealing anything about the internal architecture.

---

### Task 5.5 — Why Use JAX-RS Filters Instead of Manual Logging

The `LoggingFilter` class implements both `ContainerRequestFilter` and `ContainerResponseFilter`. It automatically logs the HTTP method, URI, and response status of every single request that passes through the API.

The alternative would be to manually add `LOGGER.info(...)` statements at the beginning and end of every resource method — in `RoomResource`, `SensorResource`, `ReadingResource`, and so on.

Using a filter is better for several reasons:

1. **DRY Principle (Don't Repeat Yourself).** Without a filter, the same logging code would be copy-pasted into every method. If the log format needs to change (e.g., adding a timestamp or the client's IP address), every single method would need to be updated individually. With a filter, the logic is written once and applied everywhere.

2. **Cross-Cutting Concern.** Logging is not part of the business logic of creating rooms or reading sensors — it applies to all endpoints equally. JAX-RS filters are specifically designed for these "cross-cutting concerns" that span the entire API. Other examples include authentication, CORS headers, and rate limiting.

3. **Separation of Concerns.** Resource classes should focus on their actual job — handling rooms, sensors, and readings. Mixing in logging, security checks, and other infrastructure code makes them harder to read and maintain. The filter keeps the resource code clean.

4. **Automatic Coverage.** If a new endpoint is added tomorrow, the filter catches it automatically. There's no risk of someone forgetting to add logging to a new method.

