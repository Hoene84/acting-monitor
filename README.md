
# acting-monitor

## Development

### Start
Generate Sources `mvn install`\
Option A: run `mvn spring-boot:run` in "monitor-executor-script" and "monitor-app" \
Option B: use a spring starter of your IDE

### Debug
Launch the module directly from the IDE (tested in IntelliJ). It is much easier than attaching the maven process.

### Design desitions

#### Executor
* The executor is responsible to send results to the "publishTo" endpoint. Considering:
  * pro: Test execution can be a long-running process. Therefore, it shall be ansynchronious.
  * pro: The reduce of logic if centralized is minimal (avoids a loop)
  * cons: If the endpoint is not reachable, there should be a log event
    (this should be implemented in a robust logging concept anyway or taken over by a monitor monitor)

