# 🚇 Smart Metro Ticketing System

A **console-based Java Object-Oriented Programming (OOP) project** that simulates the main operations of a metro ticketing system.

This project was developed for the **UECS1044 / UECS1144 Object-Oriented Application Development Group Assignment (June 2026)**. It demonstrates core OOP concepts such as inheritance, polymorphism, abstraction, interfaces, exception handling, collections, file handling, enums, packages, and sorting.

> **Repository:** https://github.com/yuanyuanx2/Smart-Metro-Ticketing-System.git

---

## 📌 Project Overview

The Smart Metro Ticketing System supports two main user roles:

- **Passenger**
- **Admin**

Passengers can register, log in, top up their balance, browse stations and routes, purchase and cancel tickets, make payments, view their tickets, and review travel history.

Admins can manage stations, trains, routes, users, reports, and JSON backups.

The application is completely **menu-driven and console-based**.

---

## ✨ Main Features

### 👤 Passenger Features

- Register a new passenger account
- Login using email and password
- View passenger profile
- Top up wallet balance
- View metro stations
- View metro routes
- Buy tickets
- Choose ticket type: `SINGLE`, `DAILY`, `MONTHLY`
- Pay using Cash or Card
- View purchased tickets
- Cancel active tickets
- View travel history:
    - Current month
    - Selected month
    - All-time history
- Loyalty discount reward
- Input validation and clear error messages

### 🛠️ Admin Features

- Login as Admin
- View Admin profile
- Manage stations
    - Add station
    - View stations
    - Search station
- Manage trains
    - Add train
    - View trains
- Manage routes
    - Create route
    - View routes
    - Find route
- Manage users
- Generate reports
- View Passenger Financial Summary
- Create and verify JSON backups
- Export reports to TXT
- Export reports to PDF

---

## 🎟️ Ticket Purchase Flow

The application follows a realistic purchase sequence:

```text
Select Route / Source / Destination
              ↓
       Select Ticket Type
              ↓
        Calculate Fare
              ↓
       Process Payment
              ↓
      Payment Successful?
        /             \
      No               Yes
      ↓                 ↓
 No Ticket        Create Ticket
 Created          Save Ticket
```

A ticket is created **only after successful payment**.

If payment fails or the passenger has insufficient balance, no ticket is created.

---

## 💰 Fare and Loyalty System

The system uses a fare calculator to determine ticket prices based on the selected route and ticket type.

A loyalty reward is also available:

- Every **RM100 of eligible accumulated spending** earns one reward.
- The reward gives **20% off the next successful ticket purchase**.
- A failed purchase does not consume the reward.
- Cancelled tickets do not contribute toward earning future loyalty credit.
- A loyalty reward already used is not restored if that discounted ticket is later cancelled.

---

## ❌ Ticket Cancellation and Refunds

Cancelling a ticket changes its status to `CANCELLED`.

The ticket remains stored for reporting and history purposes.

**The current project does not provide refunds for cancelled tickets.**

Therefore, a cancelled ticket is still included in financial spending/revenue calculations.

---

## 📊 Reporting Features

The Admin reporting module includes:

- System Summary
- Total Tickets Sold
- Gross Revenue
- Cancelled Ticket Count
- Ticket Type Statistics
- Route Popularity
- Fare Statistics
- Passenger Financial Summary
- Monthly Reports
- Quarterly Reports
- Yearly Reports
- TXT Report Export
- PDF Report Export

### Passenger Financial Summary

For every passenger, the report shows:

- Total Top Up
- Total Spending
- Current Balance
- Number of Tickets Bought

Under the current no-refund wallet model:

```text
Total Top Up = Current Balance + Total Ticket Spending
```

---

## 💾 Data Storage

### Primary Storage — TXT

The application uses TXT files as its primary persistence mechanism.

Files are stored in:

```text
src/main/resources/data/
```

Main data files:

```text
users.txt
stations.txt
trains.txt
routes.txt
tickets.txt
```

Data is loaded when the application starts and saved again during normal program shutdown.

### Bonus Storage — JSON Backup

The system can also create and verify JSON backups.

Runtime JSON files are generated under:

```text
src/main/resources/data/json/
```

This folder is intentionally ignored by Git because the JSON files are generated at runtime.

If live TXT data changes after an older JSON backup was created, JSON verification may correctly report that the backup is out of date. Create a fresh JSON backup and verify it again.

---

## 🧱 OOP Concepts Demonstrated

This project applies:

- Classes and Objects
- Encapsulation
- Inheritance
- Polymorphism
- Abstract Classes
- Interfaces
- Exception Handling
- `ArrayList`
- `HashMap`
- Enums
- Packages
- File Handling
- `Comparator`
- Service-oriented class separation

---

## 🗂️ Project Structure

```text
Smart-Metro-Ticketing-System/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── app/
│   │   │   │   └── Main.java
│   │   │   ├── enums/
│   │   │   ├── exception/
│   │   │   ├── fare/
│   │   │   ├── model/
│   │   │   ├── payment/
│   │   │   ├── report/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       └── data/
│   │           ├── users.txt
│   │           ├── stations.txt
│   │           ├── trains.txt
│   │           ├── routes.txt
│   │           └── tickets.txt
│   └── test/
│
├── .gitignore
├── pom.xml
└── README.md
```

### Important Packages

| Package | Purpose |
|---|---|
| `app` | Main application and console menus |
| `model` | Core domain classes such as User, Passenger, Admin, Station, Train, Route, and Ticket |
| `service` | Business logic and system operations |
| `payment` | Payment interface and payment implementations |
| `fare` | Fare calculation logic |
| `repository` | TXT/JSON file persistence |
| `report` | PDF report export |
| `enums` | User, ticket type, and ticket status enums |
| `exception` | Custom application exceptions |

---

# 🚀 Getting Started

This section is written for someone who may be new to GitHub and Git.

## 1. What Does "Clone" Mean?

**Cloning** means downloading a complete copy of this GitHub repository to your computer using Git.

Unlike downloading individual files manually, cloning also connects your local copy to the GitHub repository so you can later use commands such as `git pull` to receive newer changes.

---

## 2. Requirements

Before running the project, install:

### Java Development Kit

This project uses **Java 17**.

Check:

```bash
java -version
javac -version
```

### Apache Maven

Maven is used to compile the project, download dependencies, and create the runnable JAR.

Check:

```bash
mvn -version
```

### Git

Git is required if you want to clone the repository.

Check:

```bash
git --version
```

---

# 📥 Method A — Clone Using Git

## Step 1 — Choose a Folder

Open **Command Prompt**, **PowerShell**, or the terminal in your IDE.

Example:

```bash
cd C:\Users\YourName\IdeaProjects
```

## Step 2 — Clone the Repository

```bash
git clone https://github.com/yuanyuanx2/Smart-Metro-Ticketing-System.git
```

Git will create:

```text
Smart-Metro-Ticketing-System
```

## Step 3 — Enter the Project Folder

```bash
cd Smart-Metro-Ticketing-System
```

Check the current branch:

```bash
git branch --show-current
```

The normal final branch should be:

```text
main
```

## Step 4 — Check Repository Status

```bash
git status
```

A clean copy should show something similar to:

```text
On branch main
Your branch is up to date with 'origin/main'.

nothing to commit, working tree clean
```

---

# 📦 Method B — Download ZIP Without Git

If you only want to run or inspect the project and do not want to use Git:

1. Open the repository on GitHub.
2. Click the green **Code** button.
3. Choose **Download ZIP**.
4. Extract the ZIP file.
5. Open Command Prompt or PowerShell inside the extracted project folder.

> Downloading ZIP does **not** give you Git version history and you cannot use `git pull` unless you later clone the repository with Git.

---

# 🔨 Build the Project

From the **project root folder**, run:

```bash
mvn clean package
```

A successful build ends with:

```text
BUILD SUCCESS
```

Maven creates:

```text
target/Smart-Metro-Ticketing-System.jar
```

The JAR contains the external libraries required by the application, including the JSON and PDF-report dependencies.

> The `target/` folder is generated locally and is intentionally not stored in GitHub.

---

# ▶️ Run the Application

### Windows Command Prompt / PowerShell

```bash
java -jar target\Smart-Metro-Ticketing-System.jar
```

### macOS / Linux

```bash
java -jar target/Smart-Metro-Ticketing-System.jar
```

The main menu should appear:

```text
========================================
     SMART METRO TICKETING SYSTEM
========================================
1. Login
2. Register Passenger
0. Exit
========================================
```

## ⚠️ Run From the Project Root

Run the JAR while your terminal is inside:

```text
Smart-Metro-Ticketing-System/
```

The application reads and writes persistence files from:

```text
src/main/resources/data/
```

---

# 🖥️ Opening the Project in an IDE

The project uses Maven, so most Java IDEs can import it directly.

## IntelliJ IDEA

1. Open IntelliJ IDEA.
2. Select **Open**.
3. Select the `Smart-Metro-Ticketing-System` folder.
4. Allow IntelliJ to load `pom.xml`.
5. Wait for Maven synchronization.
6. Use Java 17 as the Project SDK.
7. Open `src/main/java/app/Main.java`.
8. Run `Main`.

## Eclipse

1. Choose **File → Import**.
2. Select **Existing Maven Projects**.
3. Select the cloned folder.
4. Make sure Java 17 is configured.
5. Import.

## Visual Studio Code

Install **Extension Pack for Java**, open the cloned folder, allow VS Code to import the Maven project, and confirm Java 17 is being used.

For the most consistent demonstration environment, build with Maven and run the generated JAR from Command Prompt or PowerShell.

---

# 🔄 Getting the Latest Version From GitHub

If you already cloned the project:

```bash
git switch main
git pull origin main
mvn clean package
```

---

# 🌿 Basic Git Workflow for Group Members

Avoid making experimental changes directly on `main`.

```bash
git switch main
git pull origin main
git switch -c feature/your-feature-name
```

After making changes:

```bash
git status
git add .
git commit -m "describe your change"
git push -u origin feature/your-feature-name
```

After the feature has been tested, it can be reviewed and merged into `main`.

---

# 🧠 Useful Git Commands for Beginners

| Command | Meaning |
|---|---|
| `git clone <url>` | Download the repository for the first time |
| `git status` | Show changed/untracked files |
| `git branch --show-current` | Show your current branch |
| `git switch main` | Move to the main branch |
| `git pull origin main` | Download the latest main branch changes |
| `git switch -c feature/name` | Create and switch to a new branch |
| `git add .` | Stage changes for commit |
| `git commit -m "message"` | Save a Git checkpoint locally |
| `git push` | Upload commits to GitHub |
| `git log --oneline` | View recent commit history |

---

# 🧪 Sample Data

The repository contains sample TXT data for:

- Users
- Stations
- Trains
- Routes
- Tickets

The system loads these files at startup and writes updated data back during normal shutdown.

---

# 🔐 Demo Accounts

The repository includes sample educational accounts in `users.txt`.

### Admin

```text
Email: admin@email.com
Password: admin123
```

### Passenger

```text
Email: ali@email.com
Password: pass123
```

These credentials are provided **only for demonstrating this academic project**.

> Passwords use the assignment's simple TXT-file persistence model. This is not intended to represent production authentication security.

---

# 📚 Technologies Used

- Java 17
- Maven
- Java Collections Framework
- TXT File Handling
- Jackson Databind — JSON backup
- Apache PDFBox — PDF report generation
- Git
- GitHub

---

# 📄 Generated Files

The following are generated locally and are intentionally excluded from Git where appropriate:

```text
target/
.idea/
*.iml
src/main/resources/data/json/
system_report_*.txt
system_report_*.pdf
```

Examples:

- `target/` appears after `mvn clean package`.
- JSON backups appear after using the Admin backup function.
- report files appear after exporting reports.

---

# 🛠️ Common Problems

## `java` is not recognized

Java is not installed or is not configured in your system `PATH`.

```bash
java -version
```

The project requires Java 17.

## `mvn` is not recognized

Apache Maven is not installed or its `bin` directory is not in your `PATH`.

```bash
mvn -version
```

## `Unable to access jarfile`

Run:

```bash
mvn clean package
```

and make sure you are in the project root.

## Data files cannot be found

Make sure the terminal is currently inside:

```text
Smart-Metro-Ticketing-System/
```

before running the JAR.

## JSON verification says `FAILED`

If the live TXT data changed after the previous JSON backup was created, the old JSON backup may no longer match the current system.

Use the Admin menu to create a fresh JSON backup and verify it again.

---

# ⚠️ Project Scope

This is an **academic console application**, not a production metro payment platform.

Therefore:

- TXT files are the primary data store.
- JSON is a bonus backup format.
- Password storage is simplified for academic demonstration.
- Ticket cancellation does not issue refunds.
- No real banking transaction is performed.
- Cash and card payment classes simulate payment processing.

---

# 🎓 Academic Purpose

This repository was created as part of a university Object-Oriented Application Development assignment.

The project focuses on:

- OOP class design
- inheritance and polymorphism
- interfaces and abstract classes
- exception handling
- collections
- file persistence
- service separation
- Git branching and collaboration
- console application development

---

## Repository

```text
https://github.com/yuanyuanx2/Smart-Metro-Ticketing-System.git
```

---

**Smart Metro Ticketing System — Java 17 / Maven / Console OOP Project**
