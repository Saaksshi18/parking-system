# 🅿️ ParkEase — Online Parking Booking System

A full-stack, production-like **Online Parking Booking System** built with Java Spring Boot, MySQL, Thymeleaf, and Bootstrap 5. Designed as a resume-worthy project covering authentication, CRUD, business logic, QR codes, and admin analytics.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 21 + Spring Boot 3.5 |
| Database | MySQL 8.4 |
| Frontend | Thymeleaf + Bootstrap 5 |
| Security | Spring Security + BCrypt |
| QR Code | ZXing (Google) |
| Build Tool | Maven |

---

## ✅ Features

### Admin Panel
- 🔐 Secure admin login
- 🅿️ Add / Edit / Delete parking slots
- 🚗 Define slot type (2-wheeler / 4-wheeler)
- 🔵 Mark slots as reserved (permanent users) or unreserved
- 💰 Set price per hour per slot
- 📋 View all bookings across all users
- 👥 Manage user accounts (activate / suspend)
- 📊 Revenue analytics (today + all-time)

### User Panel
- 📝 Register and login
- 🔍 View available parking slots in real time
- 🎛️ Filter by vehicle type (2W / 4W)
- 📅 Book a slot with start time and duration
- 💰 Live cost preview before confirming
- 📱 QR code generated on booking confirmation
- ❌ Cancel active bookings
- 📋 View full booking history

### Core System Logic
- 🚫 Double-booking prevention (conflict detection)
- ⏰ Slots auto-marked as occupied during booking
- 🔄 Auto-release slots after booking ends (scheduled every 5 mins)
- ⚠️ Overstay penalty calculation (₹20/hr extra)
- 🔑 Role-based access (ADMIN vs USER)
- 🥇 Reserved slots for permanent/priority users

---

## 🛠️ Setup & Installation

### Prerequisites
- Java 21+
- Maven 3.9+
- MySQL 8.4+

### Step 1 — Clone the Repository
```bash
git clone https://github.com/YOUR_USERNAME/parking-system.git
cd parking-system
```

### Step 2 — Create MySQL Database
```sql
CREATE DATABASE parking_db;
CREATE USER 'parking_user'@'localhost' IDENTIFIED BY 'parking123';
GRANT ALL PRIVILEGES ON parking_db.* TO 'parking_user'@'localhost';
FLUSH PRIVILEGES;
```

### Step 3 — Configure application.properties
```bash
cp src/main/resources/application-example.properties src/main/resources/application.properties
```
Then edit `application.properties` with your MySQL credentials.

### Step 4 — Run the Application
```bash
mvn spring-boot:run
```

### Step 5 — Access
- URL: `http://localhost:8080`
- **Admin:** `admin@park.com` / `admin123`
- **Register as User:** `http://localhost:8080/auth/register`

> Tables are auto-created by Hibernate. 24 sample slots are seeded on first run.

---

## 📁 Project Structure

```
src/main/
├── java/com/parking/parkingsystem/
│   ├── config/
│   │   ├── SecurityConfig.java        # Spring Security configuration
│   │   └── DataInitializer.java       # Seeds admin user + sample slots
│   ├── controller/
│   │   ├── AuthController.java        # Login & registration
│   │   ├── DashboardController.java   # Role-based redirect
│   │   ├── AdminController.java       # All admin routes
│   │   └── UserController.java        # All user routes
│   ├── model/
│   │   ├── ParkingSlot.java
│   │   ├── User.java
│   │   └── Booking.java
│   ├── repository/
│   │   ├── ParkingSlotRepository.java
│   │   ├── UserRepository.java
│   │   └── BookingRepository.java
│   └── service/
│       ├── ParkingSlotService.java
│       ├── UserService.java
│       ├── BookingService.java        # Core logic + QR + scheduler
│       └── CustomUserDetailsService.java
└── resources/
    ├── application-example.properties # Template — copy to application.properties
    ├── templates/
    │   ├── auth/      login.html, register.html
    │   ├── admin/     dashboard, slots, bookings, users, revenue
    │   ├── user/      dashboard, slots, book, confirmation, bookings
    │   └── fragments/ navbar, head
    └── static/css/style.css
```

---

## 🎯 Interview Talking Points

- **Architecture:** MVC pattern — Controller → Service → Repository → Database
- **Security:** BCrypt password hashing, Spring Security filter chain, role-based access control
- **ORM:** Hibernate auto-creates tables from Java entities via JPA annotations (`@Entity`, `@Table`)
- **Business Logic:** Double-booking conflict detection using JPQL range queries
- **Scheduler:** `@Scheduled` auto-releases expired slots every 5 minutes
- **QR Generation:** ZXing encodes booking data into Base64 PNG served inline via Thymeleaf
- **No boilerplate:** Lombok `@Data` auto-generates getters/setters/toString

---

## 📄 License

MIT — free to use for learning and portfolio purposes.
