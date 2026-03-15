
# acting-monitor

## Architecture

### Orchestrator
#### Instance
- types: Instance gets only tested by tests supporting one of the defined types. Types contains always the component id.


## Development

### Start
Generate Sources `mvn install`\
Option A: run `mvn spring-boot:run` in "monitor-executor-script" and "monitor-app" \
Option B: use a spring starter of your IDE

### Debug
Launch the module directly from the IDE (tested in IntelliJ). It is much easier than attaching the maven process.

### Design desitions

#### Orchestrator
* It's not obvious where to define the available Executors
  1. As an orchestrator global list.
     * pro: one place for all existing orchestrators
     * pro: less repeating for widely used executors
     * cons: a lot of traffic for instances calling an executor providing no test for them (implicit knowledge)
  1. As an orchestrator global list and as an attribute on Component/Instance.
     * pro: pros of i. without the cons
     * cons: used executors not at one place
     * cons: double declaration of "suitable for" (on TestRef and Instance). Allows contradictory definitions. 

#### Executor
* The executor is responsible to send results to the "publishTo" endpoint. Considering:
  * pro: Test execution can be a long-running process. Therefore, it shall be ansynchronious.
  * pro: The reduce of logic if centralized is minimal (avoids a loop)
  * cons: If the endpoint is not reachable, there should be a log event
    (this should be implemented in a robust logging concept anyway or taken over by a monitor monitor)
    

#### publishTo
* It's not obvious where to define the actor endpoints notified for a test. At the moment implemented in:
    * TestRef: For test specific actors like a restart script
    * Instance: For general monitor overviews like a UI or an elastic connector