📍 Concurrent Robotics Framework — Java (Microservices + Sensors + SLAM)

This project implements a concurrent Java-based microservice framework that simulates the perception and mapping pipeline of an autonomous vacuum-mop robot. Multiple sensor services—Camera, LiDAR, GPS, and IMU—run in parallel and synchronize using threads, locks, callbacks, and Java 8 concurrency utilities. The system fuses sensor outputs to produce a simplified SLAM-like mapping process.

🛠 Core Features

Multithreaded sensor simulation

Camera: object detection (no coordinates)

LiDAR: 3D point cloud processing via multiple worker threads

IMU + GPS: pose tracking in a global reference frame

Data fusion pipeline

Combines outputs from all sensors

Maps landmarks into shared coordinate system

Updates recurring detections via weighted averaging

Custom microservice architecture

Node-based sensor system similar to ROS

Messaging + callbacks for inter-thread communication

Synchronized shared data structures

Concurrency principles

Thread pools / workers

Java concurrent collections

Minimal locking + performance focus

No unsupported exceptions or signature changes (strict spec compliance)

📚 Concepts & Technologies
Topic	Used
Java Threads	✔
Synchronization (synchronized, locks)	✔
Lambdas & Functional Interfaces	✔
Callbacks + Event-driven messaging	✔
Concurrent data structures	✔
Parallel workload simulation	✔
Sensor fusion & mapping logic	✔
📂 Project Structure (Expected)
src/
  ├── camera/
  ├── lidar/
  ├── imu/
  ├── gps/
  ├── fusion/
  └── framework/   # microservice + messaging logic

🚀 Running
javac -cp src -d bin $(find src -name "*.java")
java -cp bin Main


(Adjust based on your actual file layout)

🔍 Example Workflow

Start system clock + services

Each sensor publishes readings each tick

LiDAR processes data using multiple worker threads

Fusion module merges object IDs into global map

Re-detections update landmark estimates

📌 Key Rules From The Assignment

You must not change method signatures or add new exceptions.

Only add data members where allowed by documentation.

Synchronize only when necessary—performance matters.

Must run and compile on CS Lab UNIX machines.

🧪 Goals of This Project

Practice high-performance concurrency in Java

Coordinate independent services with shared state

Prototype robotics processing pipelines without ROS

Gain hands-on experience with SLAM-style data fusion