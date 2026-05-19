<<<<<<< HEAD
# Cybersecurity Incident Reporting System

**Database Systems — Group Project**
## Table of Contents
1. [Purpose and Objectives](#1-purpose-and-objectives)
2. [Features and Functionalities](#2-features-and-functionalities)
3. [Tools, Technologies and Programming Languages](#3-tools-technologies-and-programming-languages)
4. [Data Structure and Design](#4-data-structure-and-design)
5. [Entity Relationship Diagram (ERD)](#5-entity-relationship-diagram-erd)
6. [Step-by-Step Explanation](#6-step-by-step-explanation)
7. [Setup and Installation](#7-setup-and-installation)
8. [Difficulties Faced with MongoDB on First Use](#8-difficulties-faced-with-mongodb-on-first-use)

## 1. Purpose and Objectives

### 1.1 Purpose
The motive behind the creation of the *Cybersecurity Incident Reporting System* is the development of a platform where clients can submit complaints related to cybersecurity issues and/or vulnerabilities that occur within their systems.

### 1.2 Objectives
- Integrate CRUD (Create, Read, Update, Delete) operations
- Record cybersecurity incidents with the help of a DBMS
- Demonstrate the integration of a database using MongoDB
- Display a statistical report of cybersecurity incidents
- Create a sample dataset of at least 1,000 records

---

## 2. Features and Functionalities

### 2.1 Dashboard Module
The entry point of the system. Provides access to all other modules:
- Report Incidents
- Manage Incidents
- Statistics and Trends
- System Settings

### 2.2 Incident Reporting Form
This is where new incidents are added. Fields include:
- Title
- Category
- Severity
- Priority
- Affected System
- Description
- Reporter details

All submitted data is stored in MongoDB.

### 2.3 View and Manage Incidents
All incidents are displayed in a table. Functionalities include:
- Search incidents by keyword
- Filter by status (Open, Investigating, Resolved)
- Update incident status
- Delete incidents
- Edit incident details

### 2.4 Statistics Module
Displays financial impact analysis, severity distribution, category distribution, and total incident counts.

### 2.5 Settings Module
Displays database information and system information.

---

## 3. Tools, Technologies and Programming Languages

**Tools**
- MongoDB Compass
- MongoDB Atlas
- Visual Studio Code
- Microsoft Word *(for documentation)*

**Programming Language**
- Java (Java 21, Swing for the GUI)

**Build Tool**
- Apache Maven

**Database Driver**
- `mongodb-driver-sync` v5.1.0

---

## 4. Data Structure and Design

- **Database Name:** `cyber_incident_db`
- **Collection Name:** `incidents`

### 4.1 Incident Document Structure
Each incident document stores fields such as title, category, severity, status, affected system, description, reporter details, and a timestamp.

---

## 5. Entity Relationship Diagram (ERD)

MongoDB is a NoSQL database and uses a single-collection structure for this project, so the ERD reduces to a single `incidents` entity. Conceptually, the system can also be visualized with the incident entity at the center of all CRUD interactions performed by the user.

---

## 6. Step-by-Step Explanation

**Step 1 — Start the System**
The dashboard screen is displayed with 3 KPI cards at the top, 4 module cards in the center, and an Exit Console button at the bottom.

**Step 2 — Report an Incident**
Click the *Report Incident* card to be redirected to the form screen.

**Step 3 — Submit Incident**
Fill out the form fields and press *Submit Incident*.

**Step 4 — Manage Incidents**
A sample of 1000 records has already been loaded into the DBMS. Pressing the *Manage Incidents* card displays all incidents in a table.
- **Search:** look up a specific complaint by keyword.
- **Edit:** reassign or modify any field via the edit dialog, then save.
- **Update Status:** select a new status from the dropdown and press *Update Status*.
- **Delete:** select the incident and press *Delete*.

**Step 5 — Statistics and Trends**
Click the *Statistics and Trends* card. The screen buffers briefly while data is aggregated, then the statistics are displayed.

**Step 6 — System Settings**
View database and environment information via the *System Settings* card.

**Step 7 — Exit Console**
Click *Exit Console* at the bottom of the dashboard. Confirm the warning prompt to close the system.


## 7. Setup and Installation

### Prerequisites
- Java JDK 21
- Apache Maven
- A MongoDB Atlas cluster (or a local MongoDB instance)

### Configure the database connection
Credentials are **not** hardcoded. The application reads them from a `config.properties` file at the project root.

1. Copy `config.properties.example` to `config.properties`.
2. Fill in your own MongoDB user details:

```properties
mongodb.uri=mongodb+srv://<username>:<password>@<cluster>.mongodb.net/
mongodb.database=cyber_incident_db
```

`config.properties` is listed in `.gitignore`, so your credentials stay on your machine and are never pushed to GitHub.

## 8. Difficulties Faced with MongoDB on First Use

MongoDB was not part of our coursework, and we had never used it before this project — we taught it to ourselves from scratch using documentation, articles, and trial and error. Because of that, even small steps in the setup process became time-consuming, and the lack of prior exposure made the learning curve genuinely challenging. The two issues below were the biggest hurdles we faced while connecting our Java application to a MongoDB Atlas cluster:

### 8.1 Three different "MongoDBs" to learn at once
We initially confused **MongoDB Atlas** (the cloud-hosted database), **MongoDB Compass** (the desktop GUI client), and the **MongoDB Java Driver** (the library that talks to the database from code). They all need to be installed/configured separately, and as beginners it was easy to assume that setting one up meant the others were ready. Figuring out how the three pieces fit together took a fair amount of self-study.
### 8.2 Atlas account vs. database user
The login we created to access the Atlas web console is **not** the same as the database user the application uses. We had to create a dedicated DB user under *Database Access* with its own username and password, and we lost time trying to use our Atlas account credentials in the connection string before realizing why authentication kept failing. Since no one had walked us through this distinction, it was something we had to discover ourselves.

>>>>>>> e2256f0af46cedfd196eabe57863ba0b71a05612
