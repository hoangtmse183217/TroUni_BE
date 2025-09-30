# TroUni Development To-Do List

## Project Overview
TroUni - University Roommate Finding Platform
A comprehensive platform for students to find accommodation and roommates with role-based access control.

---

## 📋 Development Roadmap

### 🔐 **Phase 1: Core Authentication & User Management**
- ✅ **User Registration & Login System**
  - ✅ Complete email verification flow
  - ✅ Google OAuth integration
  - ✅ Password reset functionality
  - ✅ JWT token management and refresh
  - ✅ User profile management (CRUD operations)

- ✅ **Role-Based Access Control (RBAC)**
  - ✅ Implement UserRole enum (STUDENT, LANDLORD, MANAGER, ADMIN)
  - ✅ Create role-based security configurations
  - ✅ Implement endpoint-level authorization
  - ✅ Create role hierarchy validation

---

### 👤 **Phase 2: Guest User Features (Backend APIs)**
- [ ] **Room Search & Filter APIs**
  - [ ] Basic search API (location, price, area)
  - [ ] Filter API for room types
  - [ ] Search result pagination API
  - [ ] Location-based search API (district/ward only)

- [ ] **Room Listing APIs**
  - [ ] Public room listing endpoint
  - [ ] Room summary information API
  - [ ] Image serving endpoints
  - [ ] Authentication check middleware

---

### 🎓 **Phase 3: Student User Features (Backend APIs)**
- [ ] **Enhanced Room Viewing APIs**
  - [ ] Full room details API
  - [ ] Complete image gallery API
  - [ ] Landlord contact information API
  - [ ] Room analytics API

- [ ] **Bookmark System APIs**
  - [ ] Saved/unsaved room API
  - [ ] Bookmarked rooms list API
  - [ ] Bookmark management APIs
  - [ ] Bookmark notifications API

- [ ] **Roommate Post Management APIs**
  - [ ] Create roommate seeking post API
  - [ ] Edit/update roommate post API
  - [ ] Delete roommate post API
  - [ ] Roommate posts listing API

- [ ] **Report System APIs**
  - [ ] Report inappropriate content API
  - [ ] Report categories management
  - [ ] Report status tracking API
  - [ ] Report history API

- ✅ **User Profile Management APIs**
  - ✅ Personal information update API
  - ✅ Avatar upload API
  - ✅ Password change API (via forgot/reset password)
  - ✅ Account settings API

---

### 🏠 **Phase 4: Landlord User Features (Backend APIs)**
- [ ] **Room Management System APIs (CRUD)**
  - [ ] Create new room post API
  - [ ] Edit existing room post API
  - [ ] Delete room post API
  - [ ] Room status management API (Available, Rented, Hidden)
  - [ ] Bulk room operations API

- [ ] **Landlord Dashboard APIs**
  - [ ] Overview statistics API
  - [ ] Room performance metrics API
  - [ ] View count analytics API
  - [ ] Quick actions API

- [ ] **Subscription Package System APIs**
  - [ ] **Basic Package (Free)**
    - [ ] Standard room creation API
    - [ ] Limited features API
    - [ ] Basic dashboard access API

  - [ ] **Pro Package**
    - [ ] Payment integration API
    - [ ] 3-day priority display API
    - [ ] "Trusted Post" badge API
    - [ ] Detailed view statistics API
    - [ ] Advanced dashboard features API

  - [ ] **Elite Package**
    - [ ] All Pro features API
    - [ ] 7-day priority display API
    - [ ] "Top Landlord" badge API
    - [ ] Auto-refresh feature API
    - [ ] Priority customer support API
    - [ ] Advanced analytics API

- [ ] **Payment Integration APIs**
  - [ ] Payment gateway integration
  - [ ] Subscription management API
  - [ ] Invoice generation API
  - [ ] Payment history API

---

### 👨‍💼 **Phase 5: Manager Features (Backend APIs)**
- [ ] **Manager Dashboard APIs**
  - [ ] System overview statistics API
  - [ ] New posts counter API
  - [ ] New users counter API
  - [ ] Pending reports counter API
  - [ ] Recent activity feed API

- [ ] **Content Moderation APIs**
  - [ ] View all posts API
  - [ ] Post approval workflow API
  - [ ] Post rejection API
  - [ ] Edit any post content API
  - [ ] Bulk moderation actions API

- [ ] **Report Management APIs**
  - [ ] View all reported content API
  - [ ] Report investigation tools API
  - [ ] Decision-making interface API
  - [ ] Report resolution tracking API
  - [ ] Communication with reporters API

- [ ] **User Communication APIs**
  - [ ] Send notifications API
  - [ ] Warning system API
  - [ ] Ban/unban functionality API

---

### 🔧 **Phase 6: Admin Features (Backend APIs)**
- ✅ **User Management System APIs**
  - ✅ View all users API
  - ✅ Edit user information API
  - ✅ Role assignment/change API
  - ✅ Account suspension/activation API
  - ✅ User deletion API (soft & hard delete)

- [ ] **Advanced Dashboard APIs**
  - [ ] All Manager dashboard APIs
  - [ ] Revenue tracking API
  - [ ] System performance metrics API
  - [ ] User engagement analytics API
  - [ ] Financial reports API

- [ ] **System Configuration APIs**
  - [ ] Amenities management API
  - [ ] Package pricing configuration API
  - [ ] Feature toggles API
  - [ ] System settings API
  - [ ] Maintenance mode control API

---

### 🔄 **Phase 7: Additional Backend Features**
- [ ] **Messaging System APIs**
  - [ ] Real-time chat API
  - [ ] Chat room management API
  - [ ] Message history API
  - [ ] File sharing in chats API

- [ ] **Notification System APIs**
  - [ ] Email notifications API
  - [ ] In-app notifications API
  - [ ] Push notifications API
  - [ ] Notification preferences API

- [ ] **Review & Rating System APIs**
  - [ ] Post reviews API
  - [ ] Landlord ratings API
  - [ ] Review moderation API
  - [ ] Rating aggregation API

---

### 🧪 **Phase 8: Testing & Quality Assurance**
- [ ] **Unit Testing**
  - [ ] Service layer tests
  - [ ] Repository tests
  - [ ] Controller tests
  - [ ] Utility class tests

- [ ] **Integration Testing**
  - [ ] API endpoint testing
  - [ ] Database integration tests
  - [ ] Authentication flow tests
  - [ ] Payment integration tests

- [ ] **Security Testing**
  - [ ] JWT token validation tests
  - [ ] Role-based access testing
  - [ ] Input validation testing
  - [ ] SQL injection prevention tests

---

## 📊 **Current Implementation Status**

### 🔄 **In Progress**
- [ ] Room management system for landlords
- [ ] Search and filter APIs
- [ ] Booking and subscription system

---

## 🎯 **Priority Levels**

### **High Priority (Must Have)**
1. Complete room CRUD APIs for landlords
2. Basic search and filtering APIs
3. User role management APIs
4. Basic dashboard APIs for each role

### **Medium Priority (Should Have)**
1. Subscription packages APIs
2. Payment integration APIs
3. Advanced search features APIs
4. Messaging system APIs
5. Notification system APIs

### **Low Priority (Nice to Have)**
1. Advanced analytics APIs
2. Review system APIs
3. Advanced reporting features APIs
4. Mobile app integration APIs

---

## 📝 **Notes**
- This to-do list focuses on backend API development only
- Frontend tasks have been removed as this is a backend project
- Each phase can be developed incrementally
- Testing should be done continuously throughout development
- Consider API versioning for future updates
- Maintain API documentation throughout development

---

*Last Updated: [Current Date]*
*Version: 1.1 - Backend Focus*