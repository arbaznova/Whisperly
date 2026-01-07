Multithreaded Chat Server (Java)

A from-scratch, protocol-driven, multithreaded chat system built using Java sockets, designed to deeply understand networking, concurrency, and security fundamentals without relying on frameworks or managed services.

This project intentionally avoids auto-configuration and high-level abstractions in favor of manual system design, explicit protocols, and low-level control over connections, threads, and data flow.



🚀 Project Overview
This application is a TCP-based client–server chat system that supports:
Multiple concurrent clients
Public and private messaging
Username validation and lifecycle management
Command-based protocol (/users, /quit, private messages)
Clean architectural separation (Server, ClientHandler, Registry, I/O abstractions)
TLS-ready design (encryption integration in progress)
The system currently runs locally and is architected to be extended to encrypted, multi-machine deployment.


🧠 Why This Project Exists
Most modern backend projects hide complexity behind frameworks, cloud services, and auto-configured networking stacks.
This project was built to:
->Understand how client–server systems actually work
->Learn socket programming and concurrency from first principles
->Design and evolve a custom application-layer protocol
->Reason about threading, blocking I/O, failure modes, and cleanup
->Prepare for manual TLS integration using keystores and truststores


🏗️ Architecture
multithreadedserver/
├── Main/
│   ├── Server.java            # Accepts connections, spawns client handlers
│   ├── Client.java            # Console-based client with async send/receive
│   ├── ClientHandler.java     # Per-client thread, protocol interpreter
│   └── ClientRegistry.java    # Thread-safe client management & routing
│
├── readersandwriters/
│   ├── SocketReader.java      # Abstraction over socket input
│   ├── SocketWriter.java      # Abstraction over socket output
│   ├── ConsoleReader.java     # Console input abstraction
│   └── ConsoleWriter.java     # Console output abstraction
│
└── security/ (planned)
    ├── keystore/
    └── truststore/



🧪 Running the Project (Local)
1. Start the Server
java Server
2. Start Multiple Clients
java Client

Each client runs in its own terminal and communicates over TCP.


🛠️ Technologies Used
Java SE
TCP Sockets (ServerSocket, Socket)
Multithreading
Blocking I/O (BufferedReader, BufferedWriter)
Concurrent Collections
Custom Protocol Design
JSSE (TLS concepts, upcoming)


📈 Future Roadmap
TLS-encrypted sockets (SSLServerSocket, SSLSocket)
Certificate-based authentication
End-to-end encryption for private messages
Deployment on a public server (multi-machine chat)
Connection limits and thread pool optimization
Logging, metrics, and observability
Message persistence
