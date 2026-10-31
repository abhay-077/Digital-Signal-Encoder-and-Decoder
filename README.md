
# Digital Signal Encoder and Decoder

## Project Team

This project was developed by the following students:

| S. No. | Student Name | Enrollment Number |
|---|---|---|
| 1 | Abhay Verma | 2024BITE077 |
| 2 | Pawan Bhat | 2024BITE001 |
| 3 | Vinay Kumar | 2024BITE079 |

## Overview

This project is developed for **ITT 036: Programming Assignment 1 — Autumn 2026**.

It implements digital signal generation, encoding, decoding, and analog-to-digital conversion using Java. A JavaFX graphical interface allows users to interact with the implemented techniques and visualize encoded signals.

## Features

### 1. Digital Data Generation
- Generate random binary sequences.
- Insert four or eight consecutive zeros into a binary sequence.
- Find the longest palindromic substring in a binary sequence.

### 2. Line Coding Techniques
- **NRZ-L:** Non-Return-to-Zero-Level
- **NRZ-I:** Non-Return-to-Zero-Invert
- **Manchester**
- **Differential Manchester**
- **AMI:** Alternate Mark Inversion
- **RZ:** Return-to-Zero

### 3. Scrambling Techniques
- **B8ZS:** Bipolar with 8-Zero Substitution
- **HDB3:** High-Density Bipolar 3-Zero

### 4. Analog-to-Digital Conversion
- Pulse Code Modulation (PCM).
- Delta Modulation (DM).
- Encoding and decoding of analog sample sequences.

### 5. Graphical Interface
- JavaFX-based graphical user interface.
- Visualization of encoded digital signal waveforms.
- Selection of line-coding schemes and analog modulation modes.
- Display of encoding and decoding results.

### 6. Automated Testing
- JUnit 5 tests for line-coding strategies.
- Tests for B8ZS and HDB3 scrambling.
- Tests for PCM and Delta Modulation.
- Tests for palindrome detection.
- Input validation and edge-case testing.

## Technologies Used

- **Programming Language:** Java 21
- **GUI Framework:** JavaFX 21
- **Build Tool:** Apache Maven
- **Testing Framework:** JUnit 5
- **Design Pattern:** Strategy Pattern

## Project Structure

```text
Digital-Signal-Encoder-and-Decoder/
├── src/
│   ├── main/
│   │   ├── java/com/digitalsignal/
│   │   │   ├── Main.java
│   │   │   ├── analog/
│   │   │   │   └── AnalogCodec.java
│   │   │   ├── core/
│   │   │   │   └── SignalCodec.java
│   │   │   ├── generator/
│   │   │   │   └── Palindrome.java
│   │   │   ├── linecoding/
│   │   │   │   ├── LineCodingStrategy.java
│   │   │   │   ├── LineCodingRegistry.java
│   │   │   │   ├── AmiStrategy.java
│   │   │   │   ├── DifferentialManchesterStrategy.java
│   │   │   │   ├── ManchesterStrategy.java
│   │   │   │   ├── NrzIStrategy.java
│   │   │   │   ├── NrzLStrategy.java
│   │   │   │   └── RzStrategy.java
│   │   │   └── scrambling/
│   │   │       ├── B8zsStrategy.java
│   │   │       └── Hdb3Strategy.java
│   │   └── resources/
│   └── test/java/com/digitalsignal/
│       ├── analog/AnalogCodecTest.java
│       ├── core/SignalCodecTest.java
│       ├── generator/PalindromeTest.java
│       ├── linecoding/
│       └── scrambling/
├── .gitignore
├── pom.xml
└── README.md
```

## Requirements

- Java Development Kit (JDK) 21 or compatible version.
- Apache Maven.
- JavaFX-compatible graphical environment.

## How to Run

Open a terminal in the project directory containing `pom.xml`.

### Build the Project

```bash
mvn clean package
```

### Run Automated Tests

```bash
mvn test
```

### Launch the Application

```bash
mvn javafx:run
```

## Test Results

The latest reported automated test run completed successfully:

- **Total tests:** 56
- **Failures:** 0
- **Errors:** 0
- **Skipped:** 0

Tests cover digital encoding and decoding, analog conversion, palindrome detection, scrambling, input validation, and edge cases.

## Design Approach

The project uses the **Strategy Pattern** for line-coding operations. Each coding scheme is implemented in an independent strategy class.

A central registry manages the available line-coding strategies, while the `SignalCodec` facade provides a common interface for encoding and decoding operations.

This design separates individual algorithms from their selection and makes the project easier to maintain and extend.

## Limitations

- The application decodes signals using their numerical encoded values rather than analysing waveform images.
- Image-based waveform decoding is not included.
- The implemented analog modulation techniques are PCM and Delta Modulation.

## Assignment Information

- **Course:** ITT 036
- **Assignment:** Programming Assignment 1
- **Semester:** Autumn 2026
- **Objective:** Implement a digital signal encoder and decoder.

## Acknowledgment

This project was developed for academic purposes. Any external resources and AI assistance used during development should be acknowledged according to the course requirements.
