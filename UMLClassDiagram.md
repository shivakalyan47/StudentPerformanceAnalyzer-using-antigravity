# UML Class Diagram - Student Performance Analyzer

This document presents the structural Class Diagram of the **Student Performance Analyzer** project, illustrating how the Object-Oriented elements (Inheritance, Abstraction, Encapsulation, and Services) connect.

---

## 1. Graphical Mermaid Diagram
*If your Markdown reader supports Mermaid, the diagram below will render graphically.*

```mermaid
classDiagram
    class Person {
        <<abstract>>
        -String id
        -String name
        -String email
        +Person(id, name, email)
        +getId() String
        +getName() String
        +getEmail() String
        +setId(id) void
        +setName(name) void
        +setEmail(email) void
        +toString() String
    }

    class Student {
        -double attendance
        -double assignmentScore
        -Map~String, Double~ subjectMarks
        -double totalMarks
        -double averageMarks
        -String grade
        -String performanceCategory
        -String suggestions
        +Student(id, name, email)
        +setMark(subject, mark) void
        +getMark(subject) double
        +getAttendance() double
        +setAttendance(attendance) void
        +getAssignmentScore() double
        +setAssignmentScore(score) void
        +getTotalMarks() double
        +getAverageMarks() double
        +getGrade() String
        +getPerformanceCategory() String
        +getSuggestions() String
        +toString() String
    }

    class User {
        -String username
        -String password
        -String fullName
        -String role
        +User(username, password, fullName, role)
        +getUsername() String
        +getPassword() String
        +getFullName() String
        +getRole() String
    }

    class ValidationUtil {
        <<utility>>
        +validateNotEmpty(fieldName, value) void
        +validateId(id) void
        +validateEmail(email) void
        +validatePercentage(fieldName, valueStr) double
    }

    class AuthService {
        -Map~String, User~ users
        -User currentUser
        +AuthService()
        +registerUser(user) boolean
        +authenticate(username, password) boolean
        +logout() void
        +getCurrentUser() User
    }

    class StudentService {
        +SUBJECTS String[]
        -Map~String, Student~ students
        +StudentService()
        +registerStudent(student) boolean
        +updateStudentDetails(id, name, email) boolean
        +recordAcademicPerformance(id, marks, attendance, assignment) boolean
        +deleteStudent(id) boolean
        +getStudent(id) Student
        +getAllStudents() Collection~Student~
        +searchStudents(query) List~Student~
        +calculateMetrics(student) void
        +generateReportFile(id) String
    }

    Person <|-- Student : Inheritance
    AuthService o-- User : Manages
    StudentService o-- Student : Manages
    StudentPanel ..> ValidationUtil : Uses Exception Handling
    MarksPanel ..> ValidationUtil : Uses Exception Handling
    
    LoginFrame --> AuthService : Authenticates
    DashboardFrame --> AuthService : Session Check
    DashboardFrame --> StudentService : Analytics Query
```

---

## 2. Structured ASCII Diagram (Print & Offline Friendly)
*This formatted text diagram is highly clean and can be printed directly in reports.*

```text
 +=================================================================================+
 |                                   com.analyzer                                  |
 +=================================================================================+
 
                +---------------------------------------+
                |           <<abstract>>                |
                |              Person                   |
                +---------------------------------------+
                | - id : String                         |
                | - name : String                       |
                | - email : String                      |
                +---------------------------------------+
                | + Person(id, name, email)             |
                | + getters / setters()                 |
                | + toString() : String                 |
                +---------------------------------------+
                                    ^
                                    | (Inheritance)
                                    |
                +---------------------------------------+
                |               Student                 |
                +---------------------------------------+
                | - attendance : double                 |
                | - assignmentScore : double            |
                | - subjectMarks : Map<String, Double>  |
                | - totalMarks : double                 |
                | - averageMarks : double               |
                | - grade : String                      |
                | - performanceCategory : String        |
                | - suggestions : String                |
                +---------------------------------------+
                | + Student(id, name, email)            |
                | + setMark(subj, score) : void         |
                | + getMark(subj) : double              |
                | + getters / setters()                 |
                | + toString() : String                 |
                +---------------------------------------+

 +=================================================================================+
 |                                 Service Layer                                   |
 +=================================================================================+

      +---------------------------------+       +----------------------------------+
      |          AuthService            |       |          StudentService          |
      +---------------------------------+       +----------------------------------+
      | - users : Map<String, User>     |       | + SUBJECTS : String[]            |
      | - currentUser : User            |       | - students : Map<String, Student>|
      +---------------------------------+       +----------------------------------+
      | + AuthService()                 |       | + StudentService()               |
      | + authenticate(u, p) : boolean  |       | + registerStudent(s) : boolean   |
      | + registerUser(User) : boolean  |       | + recordPerformance() : boolean  |
      | + getCurrentUser() : User       |       | + searchStudents() : List        |
      | - loadUsers() / saveUsers()     |       | + calculateMetrics(s) : void     |
      +---------------------------------+       | + generateReportFile(id) : String|
                       |                        +----------------------------------+
                       | (Aggregates)                            |
                       v                                         v (Aggregates)
      +---------------------------------+       +----------------------------------+
      |             User                |       |             Student              |
      +---------------------------------+       +----------------------------------+
      | - username : String             |       | (Inherits base details           |
      | - password : String             |       |  and handles marks and           |
      | - fullName : String             |       |  advisory calculations)          |
      | - role : String                 |       +----------------------------------+
      +---------------------------------+
      | + User(u, p, name, r)           |
      | + getters / setters()           |
      +---------------------------------+

 +=================================================================================+
 |                               Utility & Helper                                  |
 +=================================================================================+

                        +-----------------------------------------+
                        |            ValidationUtil               |
                        +-----------------------------------------+
                        | - EMAIL_PATTERN : Pattern               |
                        +-----------------------------------------+
                        | + validateNotEmpty(field, val) : void   |
                        | + validateId(id) : void                 |
                        | + validateEmail(email) : void           |
                        | + validatePercentage(f, str) : double   |
                        +-----------------------------------------+
                                             |
                                             v (Throws nested exception)
                        +-----------------------------------------+
                        |           ValidationException           |
                        +-----------------------------------------+
                        | (Custom exception class representing    |
                        |  validation and format anomalies)       |
                        +-----------------------------------------+
```
