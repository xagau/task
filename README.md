# Tasks Framework

The Tasks Framework is designed to handle task distribution and parallel processing using RabbitMQ. Tasks are published by a "Publisher" and consumed by multiple computers, enabling efficient task execution in parallel. Custom Java classes are serialized and downloaded into the tasks folder, ensuring seamless execution across different machines.

## Table of Contents
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## Features

- Distributed task processing
- Parallel execution on multiple machines
- Task serialization and deserialization
- Integration with RabbitMQ for task queue management

## Installation

To install and run the Tasks Framework, follow these steps:

1. **Clone the repository:**

   !!!sh
   git clone https://github.com/yourusername/tasks.git
   cd tasks
   !!!

2. **Build the project using Maven:**

   !!!sh
   mvn clean install
   !!!

3. **Run the application:**

   !!!sh
   java -jar target/tasks-1.0-SNAPSHOT.jar
   !!!

## Usage

After running the application, tasks can be published to the RabbitMQ queue by the Publisher. These tasks are then consumed and executed by multiple computers, with the results being serialized and stored in the tasks folder. This setup allows for efficient parallel task processing.

## Project Structure

- `playbook.phl`: Playbook configuration file.
- `pom.xml`: Maven project configuration file.
- `server.properties`: Server configuration properties.
- `src`: Source code directory.
  - `main`: Main source directory.
    - `java`: Java source code.
      - `BasicMQ.java`: Basic message queue operations.
      - `Chomper.java`: Task chomping logic.
      - `Consumer.java`: Task consumer implementation.
      - `ConsumeTask.java`: Task consumption logic.
      - `Globals.java`: Global variables and constants.
      - `Log.java`: Logging functionality.
      - `MoneyMQ.java`: Money-related message queue operations.
      - `Playbook.java`: Playbook execution logic.
      - `ProduceResults.java`: Produces results after task execution.
      - `Producer.java`: Task producer implementation.
      - `Result.java`: Result handling logic.
      - `Server.java`: Server configuration and startup.
      - `Task.java`: Task definition and logic.
      - `TaskLookup.java`: Task lookup functionality.
      - `Transaction.java`: Transaction handling logic.
      - `Worker.java`: Worker implementation for task execution.
    - `resources`: Resource files.
  - `test`: Test source directory.
- `task.iml`: IntelliJ IDEA project file.
- `tasks`: Directory containing serialized task classes.
  - `CollectDataTask.class`
  - `EchoVersionCodeTask.class`
  - `JSoupTask.class`
  - `RestartFrameworkTask.class`
  - `SoupTask.class`
  - `Example.class`

## Contributing

We welcome contributions to improve the Tasks Framework! To contribute, please follow these steps:

1. Fork the repository.
2. Create a new branch (!!!git checkout -b feature/YourFeature!!!).
3. Commit your changes (!!!git commit -m 'Add some feature'!!!).
4. Push to the branch (!!!git push origin feature/YourFeature!!!).
5. Open a pull request.

Please make sure your code adheres to our coding standards and includes relevant tests.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Contact

If you have any questions or suggestions, feel free to reach out to us. You can create an issue on this repository, and we will get back to you as soon as possible.

---

Happy task managing with the Tasks Framework!
