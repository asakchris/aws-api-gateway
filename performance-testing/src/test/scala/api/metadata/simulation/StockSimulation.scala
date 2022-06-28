package api.metadata.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._

class StockSimulation extends Simulation {
  val httpProtocol = http.baseUrl("https://0zljz3fga4.execute-api.us-east-1.amazonaws.com/DEV")

  val scn = scenario("Generate access token")
    .exec(
      http("Access Token")
        .post("/token")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("x-api-key", "doYzDgFoxL2iZK64MLedRaDGY0Kfgivb2mQLUAdu")
        .body(StringBody("username=christopher.kamaraj@gmail.com&password=SuttonFields"))
        .check(status.is(200))
    )

  setUp(scn.inject(atOnceUsers(20)).protocols(httpProtocol))
}
