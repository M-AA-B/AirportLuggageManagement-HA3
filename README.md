# AirportLuggageManagement-HA3
Home Assignment 3 – Airport Luggage Management System

Topic: Parallel Simulation of AGV Charging and Task Execution

Team Members and Roles:
| Team Member  | Role / Responsibility                                                                                                                | Key Files                    |
| ------------ | ------------------------------------------------------------------------------------------------------------------------------------ | ---------------------------- |
| **Mohammed** | **Main Integration Lead** – integrates menu option 6, connects charging and task simulators, and manages overall program flow        | `Main.java`                  |
| **Junaid**   | **Charging Simulation Lead** – designs and implements discrete-event AGV charging logic with queue management and station assignment | `ChargingSim.java`           |
| **Ali**      | **Concurrency & Task Execution Lead** – builds multithreaded task manager for parallel task dispatch and execution across AGVs       | `ConcurrentTasksRunner.java` |

 Project Structure:

HomeAssignment3/

├─ src/com/bangs/luggage/

│  ├─ (All classes from Assignment 2)

│  ├─ ChargingSim.java

│  ├─ ConcurrentTasksRunner.java

│  └─ Main.java

│

└─ test/com/bangs/luggage/

   ├─ ChargingSimTest.java

   ├─ ConcurrentTasksRunnerTest.java

   ├─ MainIntegrationTest.java
   
   └─ (Existing JUnit tests reused from Assignment 2)

