# Financial Trading AI Agentic Showcase

An end-to-end multi-agent framework demonstrating high-throughput data orchestration, distributed compute, and intelligent trading decision-making in Java.

---

## Key Frameworks & Technologies

| Technology               | Core Role & Architectural Purpose                                                                                                                       |
|--------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Spring AI**            | Serves as the foundation for Agentic AI in Enterprise Java applications, managing agent workflows, prompt handling, and LLM integrations.               |
| **Tanzu Data Flow**      | Orchestrates streaming and batch AI data pipelines for real-time market data ingestion and transformation.                                              |
| **Valkey**               | Operates as high-speed caching layer and provides cached advice/signals to quickly determine whether a stock sentiment is **Bullish** or **Bearish**.   |
| **VMware GemFire**       | Enables distributed in-memory computing, trade signal generation, and continuous queries for real-time stock price monitoring.                          |
| **Greenplum Database**   | Provides enterprise-scale analytics (built on PostgreSQL) for the Portfolio Agent to process and evaluate large volume asset datasets.                  |

---

## 🏗️ Architecture Overview

The system uses specialized agents operating across streaming data, in-memory caches, distributed processing engines, and analytical databases:

              +------------------------+
              |    Tanzu Data Flow     |
              | (AI Data Pipeline)     |
              +-----------+------------+
                          |
+----------------------+----------------------+
|                      |                      |
+------v-------+      +-------v------+      +--------v-------+
|    Valkey    |      |   GemFire    |      |   Greenplum    |
| (Caching &   |      | (Compute &   |      | (Portfolio     |
| Sentiment)   |      | Continuous)  |      | Analytics)     |
+------+-------+      +-------+------+      +--------+-------+
|                      |                      |
+----------------------+----------------------+
|
+-----------v------------+
|       Spring AI        |
|    (Agentic AI Core)   |
+------------------------+


![Architecture Overview](docs/img/architecture.png)

---

## 🤖 Agent Roles & Data Responsibilities

1. **Market Sentiment Agent (Valkey):** Leverages ultra-low latency caching to assess current market sentiment and determine if target equities display bullish or bearish behavior.
2. **Trade Monitoring Agent (GemFire):** Utilizes continuous queries and distributed compute to listen for live price thresholds and automatically generate actionable trade execution advice.
3. **Portfolio Analytics Agent (Greenplum):** Queries massive multi-structured datasets in parallel to deliver deep portfolio risk analysis, historical performance tracking, and asset allocation insights.

---

## 🛠️ Prerequisites & Setup

1. Java 17 or higher
2. Spring Boot 3.x with Spring AI dependencies
3. Running instances/containers of Valkey, GemFire, and Greenplum

```bash
# Clone the repository
git clone [https://github.com/ggreen/financial-trading-ai-agentic-showcase.git](https://github.com/ggreen/financial-trading-ai-agentic-showcase.git)
cd financial-trading-ai-agentic-showcase

# Build the project using Maven
./gradlew clean build
```

📺 Related Resources

- [Webinar Demo: Watch the AI Agentic Showcase on YouTube](https://www.youtube.com/watch?v=opOKT0b724U)