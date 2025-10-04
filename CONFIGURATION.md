# 🔧 Configuration Guide

## 📋 Overview
TroUni Backend sử dụng Spring Profiles để quản lý cấu hình cho các môi trường khác nhau.

## 🏗️ File Structure
```
src/main/resources/
├── application.properties          # Cấu hình chung + active profile
├── application-local.properties    # Cấu hình cho local development
└── application-cloud.properties    # Cấu hình cho production deployment
```

## 🚀 Cách sử dụng

### 1. **Local Development (SQL Server)**
```bash
# Chạy với profile local (mặc định)
mvn spring-boot:run

# Hoặc chỉ định rõ ràng
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

**Cấu hình:**
- Database: SQL Server (localhost:1433)
- Show SQL: true
- DDL Auto: update

### 2. **Production Deployment (PostgreSQL)**
```bash
# Chạy với profile cloud
mvn spring-boot:run -Dspring-boot.run.profiles=cloud

# Hoặc set environment variable
export SPRING_PROFILES_ACTIVE=cloud
mvn spring-boot:run
```

**Cấu hình:**
- Database: PostgreSQL (Google Cloud SQL)
- Show SQL: false
- DDL Auto: update

## 🔄 Thay đổi Profile

### Cách 1: Sửa file application.properties
```properties
# Thay đổi dòng này
spring.profiles.active=local    # hoặc cloud
```

### Cách 2: Environment Variable
```bash
# Windows
set SPRING_PROFILES_ACTIVE=cloud

# Linux/Mac
export SPRING_PROFILES_ACTIVE=cloud
```

### Cách 3: JVM Arguments
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=cloud"
```

## 🔐 Environment Variables

### Local Development
```bash
DATA_SOURCE_PASSWORD=your_sql_server_password
JWT_SIGNER_KEY=your_jwt_secret_key
SPRING_MAIL_PASSWORD=your_gmail_app_password
```

### Production Deployment
```bash
SPRING_DATASOURCE_USERNAME=your_postgres_username
SPRING_DATASOURCE_PASSWORD=your_postgres_password
JWT_SIGNER_KEY=your_jwt_secret_key
SPRING_MAIL_PASSWORD=your_gmail_app_password
```

## ✅ Lợi ích

1. **Không cần comment/uncomment** code
2. **Tự động chọn cấu hình** dựa trên profile
3. **Dễ dàng deploy** với environment variables
4. **Tách biệt rõ ràng** giữa local và production
5. **An toàn hơn** - không commit sensitive data

## 🎯 Quick Commands

```bash
# Local development
mvn spring-boot:run

# Production simulation
mvn spring-boot:run -Dspring-boot.run.profiles=cloud

# Check active profile
echo $SPRING_PROFILES_ACTIVE
```
