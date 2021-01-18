# CPI Kafka Adapter ADK

## 📝 Table of Contents
- [Dependencies / Limitations](#limitations)
- [Usage](#getting_started)
- [Technology Stack](#tech_stack)
- [Contributing](../CONTRIBUTING.md)
- [Authors](#authors)
- [Acknowledgments](#acknowledgments)

## ⛓️ Dependencies / Limitations <a name = "limitations"></a>
This adapter is developed with [Apache Camel 2.17](https://cwiki.apache.org/confluence/display/CAMEL/Camel+2.17.0+Release) as of Jan 2021, CPI runtime is base on.  I will be using Kafka 1.0 as reference to support SASL Configuration support.

## 🏁 Getting Started <a name = "getting_started"></a>
---
## Configuration of iFlow
Once iFlow is imported you like to configure the size of Kafka Producer Payload:
 ![Specifying KB size](./images/iFlow%20Configure%20Parameter.png){ width=50% height=50% }

---
### Configuration of variaous Kafka parameters
![Producer Configurations](./images/Producer%20Config%20Parameters.png)

---
## Monitor Message Processing


To see results:
![iFlow Success Results](./images/iFlow%20Results_Logs.png)

Below Consumer output:
![Kafka Consumer output](images/Consumer%20output%20s.png)

## 🚀 Future Scope <a name = "future_scope"></a>
In the [Todo](TODO.md) section


## 🎈 Usage <a name="usage"></a>
Add notes about how to use the system.

## ⛏️ Built With <a name = "tech_stack"></a>
- [Confluent](https://confluent.io/) - Confluent Cloud
- [SAPCPI](https://expressjs.com/) - Orchestrating
- [Groovy](https://vuejs.org/) - GroovyScript for random geneator
- [Apache Camel-Components]() - Library runtimes
- [Maven](https://maven.apache.org/) Buiding/Packaging

## ✍️ Authors <a name = "authors"></a>
- [@jeremyyma](https://github.com/jeremyyma) - Idea & Initial work



## 🎉 Acknowledgments <a name = "acknowledgments"></a>
- Mayur Belur Mohan
- Inspiration
- References