

# EBU6304Group111-Coinue
## Team Members

|Name| BUPTID | QMULID | Github mail | Github username |
|---|---|---|---|---|
|Yujue Yan|2022213499|221170766|2667965387@qq.com|Boris-KFJOY|
|Siyue Zhu|2022213509|221170858|Susana2004@126.com|jucie-yue|
|Yubingjie Long|2022213536|221171121|jp2022213536@qmul.ac.uk|JADE-13|
|Yufei Ye|2022213513|221170892|jp2022213513@qmul.ac.uk|Siloopy|
|Wanyu Chen|2022213534|221171109|2022213534@bupt.cn|Feldzug|
|Ruijun Gao|2022213510|221170869|grj0319@bupt.edu.cn|junjun0319|

---

## Coinue - EBU6304 Software Engineering Project

This project is developed for the EBU6304 Software Engineering module.

Coinue is a financial management application designed to help users track their expenses, manage budgets, and set payment reminders effectively.

## System Requirements

- **Java**: JDK 21 or higher
- **Maven**: 3.8.0 or higher
- **Operating System**: Windows, macOS, or Linux with JavaFX support

## Project Architecture

This project follows the **Model-View-Controller (MVC)** architectural pattern as required by the software engineering course design:

### 📁 **Architecture Components**

#### **1. Model (Entity Layer)**
- **Location**: `src/main/java/com/coinue/model/`
- **Purpose**: Contains all business logic, data models, and data management services
- **Key Classes**:
  - `User.java` - User entity and authentication logic
  - `UserDataService.java` - Data persistence service
  - `UserDataExportService.java` - Data export functionality
  - `UserBillData.java` - Bill payment data model
  - `UserAnalysisData.java` - Financial analysis data model
  - `ExpenseRecord.java` - Expense record entity

#### **2. View (Boundary Layer)**
- **Location**: `src/main/resources/view/`
- **Purpose**: User interface definitions using FXML
- **Key Files**:
  - `MainPage.fxml` - Main dashboard interface
  - `UserPage.fxml` - User management interface
  - `AnalysisPage.fxml` - Financial analysis interface
  - `BillPaymentPage.fxml` - Bill payment interface
  - `Register.fxml` - User registration and login interface

#### **3. Controller (Control Layer)**
- **Location**: `src/main/java/com/coinue/controller/`
- **Purpose**: Handles user interactions and coordinates between Model and View
- **Key Classes**:
  - `UserPageController.java` - User management controller
  - `AnalysisPageController.java` - Financial analysis controller
  - `BillPaymentPageController.java` - Bill payment controller
  - `HomepageController.java` - Main dashboard controller

#### **4. Utility Layer**
- **Location**: `src/main/java/com/coinue/util/`
- **Purpose**: Common utilities and helper classes
- **Key Classes**:
  - `PageManager.java` - Navigation management
  - `UserDataManager.java` - User data management utilities

## Dependencies

The project uses the following main dependencies (defined in `pom.xml`):

### **Core Dependencies**
- **JavaFX 21.0.2**: GUI framework
  - `javafx-controls` - UI controls
  - `javafx-fxml` - FXML support
  - `javafx-graphics` - Graphics rendering
  - `javafx-swing` - Swing integration

### **Data Processing**
- **Jackson 2.15.2**: JSON serialization/deserialization
  - `jackson-databind` - Core data binding
  - `jackson-datatype-jsr310` - Java 8 time support
- **Gson 2.10.1**: Alternative JSON processing
- **Apache PDFBox 2.0.27**: PDF generation support

### **Testing Dependencies**
- **JUnit Jupiter 5.10.1**: Unit testing framework
- **TestFX 4.0.17**: JavaFX application testing
- **Mockito 4.8.0**: Mocking framework

## Installation and Execution

### **Quick Start**
```bash
# Clone the repository
git clone https://github.com/Boris-KFJOY/EBU6304Group111-Coinue.git
cd EBU6304Group111-Coinue

# Run the application (skipping tests)
mvn javafx:run -DskipTests
```

### **Alternative Commands**
```bash
# Clean and compile
mvn clean compile -DskipTests

# Run with full Maven lifecycle
mvn clean javafx:run -DskipTests

# Package the application
mvn clean package -DskipTests
```

### **Important Notes**
- The `-DskipTests` flag is required to skip test execution during build
- The application will automatically log in with a test user (username: "Test", email: "1@q.com")
- User data is stored in the `data/users/` directory
- Exported data files are saved in the `data/exports/` directory

## Features

### **Core Functionality**
- 👤 **User Management**: Registration, login, password management
- 💳 **Bill Payment**: Credit card bill tracking and payment management
- 📊 **Financial Analysis**: Expense categorization and spending analysis
- 📈 **Data Visualization**: Charts and graphs for financial insights
- 📤 **Data Export**: CSV export of user financial data
- 🔒 **Data Security**: User-specific data isolation and secure storage

### **Technical Features**
- 🏗️ **MVC Architecture**: Clean separation of concerns
- 💾 **Data Persistence**: JSON-based data storage
- 🎨 **Modern UI**: Responsive JavaFX interface
- 🧪 **Test Coverage**: Comprehensive unit and integration tests
- 📱 **Cross-Platform**: Runs on Windows, macOS, and Linux


