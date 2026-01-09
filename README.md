🔐 Secure Multithreaded Chat Server (Java, TLS, Zero Auto-Config)

A from-scratch, TLS-secured, multithreaded chat server built in Java using raw sockets, explicit concurrency control, manual PKI management, and a custom application-layer protocol — no frameworks, no Spring, no auto-configuration.

This project is intentionally designed to explore how real backend systems work under the hood, rather than abstracting everything away behind managed services or libraries.


🧠 Motivation
Most chat applications and backend services today rely heavily on:

Framework-managed networking

Auto-configured TLS

Cloud-managed security and scaling

This project takes the opposite approach.

The goal is to:

Understand networking, security, and concurrency at the JVM level

Manually design and enforce system boundaries

Build production-grade behavior from first principles

✨ Key Features

🔒 Transport Security (TLS)

Manual TLS integration using JSSE

Explicit use of:

SSLContext

SSLServerSocket / SSLSocket

Custom PKCS12 keystore & truststore

Certificate generation and trust configuration via keytool

TLS 1.3 enforced

Clear separation of identity (keystore) and trust (truststore)

No HTTPS wrappers. No Spring Security. Raw TLS over TCP.



🧵 Concurrency Model

Thread-pooled server architecture

Server owns concurrency via ExecutorService

Predictable memory usage and controlled scheduling

No unbounded thread creation



🚦 Connection Management

Hard connection limits enforced using Semaphore

Graceful rejection of excess clients

Clean connection lifecycle tracking

Safe resource release on disconnect



💬 Custom Chat Protocol

Lightweight, text-based application protocol

Explicit handshake:

Username validation

Server acknowledgment

Supported commands:

/help – command reference

/users – list online users

/quit – graceful disconnect

@username message – private messaging

message – public broadcast

👥 Client Registry & Routing

Thread-safe user registry via ConcurrentHashMap

Real-time user presence tracking

Private and broadcast message routing

Username validation with collision prevention



📊 Observability (Production-Oriented)

Built-in observability without external tools:

Structured logging

Component-aware logs

Clear lifecycle visibility

Metrics collection

Active connections

Total connections

Rejected connections

Messages processed

Private message usage

Error counts

Live metrics reporting

Periodic server self-reporting

Designed to support future export (e.g., Prometheus)



🔑 Security Model

Server authentication via X.509 certificate

Client validates server identity using truststore

No plaintext communication

TLS handshake occurs before protocol negotiation

Designed to support mutual TLS (mTLS) in future
