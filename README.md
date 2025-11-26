**Simple Chat / Messaging Simulator (SCD Project)**

- Project Description -

This is the Simple Chat / Messaging Simulator, an educational project demonstrating the robust application of five core Gang of Four (GoF) Design Patterns within a single, cohesive Java application. The purpose is to manage the lifecycle of messages—from creation and styling to routing and display—using decoupled components.

The patterns used are: Singleton, Factory Method, Builder, Decorator, and Observer.

**Design Patterns Summary**

- Factory Method: The "MessageFactory" creates two distinct message types ('TextMessage' and 'SystemMessage'), demonstrating encapsulation of object creation.

- Decorator: The "TimestampDecorator" dynamically adds the timestamp feature, proving that message functionality can be extended without modifying the base class.

- Observer: The "ChatEngine" (Subject) notifies the "ChatSimulator" (Observer) to handle real-time events, such as the automated "TeacherBot" messages.

- Singleton: The "ChatEngine" is implemented as a Singleton to ensure only one central instance manages message logs and routing across all parts of the application.

- Builder: The "ChatSessionBuilder" provides a clean, step-by-step process for configuring user details (username, theme) upon startup.

**How to Run the System**

1. Prerequisites: Ensure Java Development Kit (JDK) 8 or higher is installed.

2. Compile: Open your terminal in the 'src' directory and run:
"javac ChatSimulator.java"

3. Run: Execute the compiled class file:
"java ChatSimulator"

**Dependencies**

This project is built using standard Java libraries (JDK) and does not require any external frameworks, packages, or JAR files beyond the core installation.

**Folder Structure**

The code follows a minimal, standard structure matching the final GitHub repository:

/SCD_Project_Chat (Repository Root)
├── src/
│   └── ChatSimulator.java  <-- Main executable file
└── README.md

**Known Issues (Fixed and Unfixed)**

_Crucial Fixed Issue (Factory/Decorator Conflict)_: A conflict arose where the "TimestampDecorator" wrapped the RED "SystemMessage", making the coloring logic fail. This was resolved by implementing a dedicated "unwrapMessage()" utility that correctly identifies the base message type before applying the necessary RED color style.

_JTextPane Alignment_: Due to limitations in Java Swing, user messages are colored blue but do not align reliably to the far right side of the screen as they would in a real chat application. Color coding is used instead for clear sender differentiation.

_Simulated Network_: Message delivery from "TeacherBot" is simulated using fixed random delays and is not connected to a real network API.
