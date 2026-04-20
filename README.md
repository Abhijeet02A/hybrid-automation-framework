# 🚀 Hybrid Automation Framework

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-4.16.1-green.svg)](https://www.selenium.dev/)
[![Cucumber](https://img.shields.io/badge/Cucumber-7.18.0-brightgreen.svg)](https://cucumber.io/)
[![JUnit](https://img.shields.io/badge/JUnit-5-red.svg)](https://junit.org/junit5/)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![Jenkins](https://img.shields.io/badge/Jenkins-CI%2FCD-blue.svg)](https://www.jenkins.io/)

## 🏗️ Architecture & Tech Stack

* **Language:** Java 17
* **Core Engines:** Selenium WebDriver (UI) & RestAssured (API)
* **Test Runner & BDD:** Cucumber-JVM integrated with JUnit
* **Dependency Injection:** PicoContainer (Guarantees thread-safety and isolates scenario states)
* **Infrastructure:** Docker Compose (Selenium Grid, Jenkins, SonarQube)
* **CI/CD:** Jenkins Multibranch Pipeline with GitHub Webhooks
* **Reporting:** Allure Reports

**A modern, scalable test automation framework** built with **Java 17**, **Cucumber**, **Selenium WebDriver**, **RestAssured**, **PicoContainer** (Dependency Injection), and **Allure Reporting**.

This framework supports **UI + API** testing in the same project with clean architecture, parallel execution and full CI/CD integration via Jenkins + SonarQube + Selenium Grid.

---

## ✨ Key Features

- **PicoContainer** for clean Dependency Injection and thread-safe scenario context
- **Non-static DriverFactory** with proper lifecycle management
- **Parallel test execution** (Chrome + Firefox)
- **Allure Reporting** with rich screenshots, logs, and flaky test detection
- **Dockerized Selenium Grid** support
- **Jenkins Multibranch Pipeline** ready with SonarQube integration
- **Clean separation** of Page Objects, Step Definitions, and Framework utilities

---

## 📋 Prerequisites

Before starting, install the following on your machine:

1. **Java JDK 17** — [Download from Eclipse Temurin](https://adoptium.net/)
2. **Apache Maven 3.9+**
3. **Git**
4. **Docker Desktop** (required for local Selenium Grid, Jenkins, SonarQube)
5. (Optional) **Ngrok** — for local GitHub webhook testing

Make sure `JAVA_HOME` and `MAVEN_HOME` are correctly set.

---

## 🚀 Quick Start (Local Development)

### 1. Clone the Repository
```bash
git clone https://github.com/Abhijeet02A/hybrid-automation-framework.git
cd hybrid-automation-framework
```
###  2. Start Infrastructure (Selenium Grid, Jenkins, SonarQube)
Bash
```
docker-compose up -d
```

Wait ~30 seconds, then verify Grid is running at: http://localhost:4444, Jenkins at: http://localhost:8080, SonarQube at http://localhost:9000 

###  3. Execute Tests Locally

```
mvn clean verify allure:serve "-Dbrowser=chrome" "-Dheadless=false"
```

### 🚢 4. CI/CD Infrastructure Setup (Jenkins & SonarQube)

This repository includes a Jenkinsfile designed for a Multibranch Pipeline. Follow these steps to configure your Dockerized Jenkins instance (http://localhost:8080).

---

### A. Required Jenkins Plugins

Navigate to:  
**Manage Jenkins → Plugins** and install:

- Allure Jenkins Plugin  
- SonarQube Scanner for Jenkins  
- GitHub Branch Source Plugin  
- Pipeline: Multibranch  

---

### B. Configuring Global Tools

Navigate to:  
**Manage Jenkins → Global Tool Configuration**

- **JDK**: Add JDK 17 *(Name it exactly `jdk17`)*  
- **Maven**: Add Maven 3.x *(Name it exactly `maven3`)*  
- **Allure**: Add Allure Commandline *(Name it exactly `allure`)*  

---

### C. Integrating SonarQube

**1. Generate Sonar Token**  
Log into SonarQube (http://localhost:9000, default `admin/admin`) →  
**My Account → Security → Generate Token**

**2. Add to Jenkins**  
In Jenkins, go to:  
**Manage Credentials → Add Secret Text**  
Paste the Sonar Token *(ID: `sonar-token`)*

**3. Configure Jenkins**  
Go to:  
**Manage Jenkins → System → SonarQube servers**

- Name: `sonarqube`  
- URL: `http://sonarqube:9000`  
- Credentials: `sonar-token`

**4. SonarQube Webhook**  
In SonarQube, go to:  
**Administration → Configuration → Webhooks**

Add a webhook pointing to Jenkins:  
`http://jenkins:8080/sonarqube-webhook/`  

*(This allows the `waitForQualityGate` step to work.)*


## D. Setting up the GitHub Webhook (Local Tunnel)

To allow GitHub to trigger your local Jenkins on a Pull Request:

1. Start Ngrok on your machine:

   bash
````
   ngrok http 8080
````

2. Go to your GitHub Repository → **Settings** → **Webhooks** → **Add webhook**.

3. Configure the webhook:

   * **Payload URL:** Paste your Ngrok URL followed by `/github-webhook/`
     *(e.g., [https://1234.ngrok.io/github-webhook/](https://1234.ngrok.io/github-webhook/))*
   * **Content Type:** `application/json`
   * **Events:** Select **Pull Requests** and **Pushes**

4. Click **Save**.

## E. Creating the Pipeline

1. In Jenkins, click **New Item**.

2. Name it `Hybrid-Framework-PR-Checker`.

3. Select **Multibranch Pipeline**.

4. Configure:

   * **Branch Sources:** Add GitHub

     * Provide your repository HTTPS URL
     * Add a GitHub Personal Access Token (configured in Jenkins Credentials as **"Username with Password"**)

   * **Build Configuration:**

     * Mode → **by Jenkinsfile**

5. Click **Save**.

**Jenkins will now automatically scan your repository, discover branches/PRs, and run the pipeline!**

## 🛠️ Common Commands Cheatsheet

**Run specific tags:**
mvn clean verify "-Dcucumber.filter.tags=@smoke"

**Run on Firefox:**
mvn clean verify "-Dbrowser=firefox"

**Run API tests only:**
mvn clean verify "-Dcucumber.filter.tags=@api"

**Teardown Docker:**
docker-compose down

