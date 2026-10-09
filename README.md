# Pact demo

This demo uses Crime Assess as the consumer and the Koala API as the provider.
Consumer tests call a Pact mock server; provider tests verify the resulting
contract against the real Spring MVC application.

## Local broker demo

1. Start the broker:

   ```sh
   docker compose up -d --wait
   ```

   Open <http://localhost:9292>. The broker initially only has a contract for an example app.

2. Generate and publish a contract:

   ```sh
   ./gradlew pactPublish -PpactVersion=demo-1
   ```

   This runs `pactConsumerTest` first and writes
   `build/pacts/crime-assess-team-koala-api.json`. Refresh the broker UI to see
   `crime-assess-team` and `koala-api`, then inspect the requests and responses:
   Monday, Saturday, and an invalid day returning HTTP 400. At this stage the
   contract has not yet been verified by the provider.

3. Verify the provider and publish the results:

   ```sh
   ./gradlew pactProviderTest -PpactVersion=demo-1
   ```

   The test retrieves the contract from the broker and runs each interaction
   through the real Koala controller and service using MockMvc. Refresh the UI
   to see the verification result.

4. Demonstrate a breaking change:

   Change Monday's location in `KoalaService`, then run:

   ```sh
   ./gradlew pactProviderTest -PpactVersion=demo-2
   ```

   Verification fails because the provider no longer returns the location the
   consumer expects. The failed result is published to the broker. Restore the
   implementation afterwards, using a new version for another demo run.

5. Stop the broker and discard its history:

   ```sh
   docker compose down
   ```
   
### Miscellaneous

The broker uses SQLite in a temporary in-memory mount. Stopping or restarting the broker 
clears its stored contracts and verification history; publish and verify again for each demo run.
Generated contract files in `build/pacts` remain on your machine.

The normal test run also includes consumer contract tests against Pact’s local mock server.
Only publishing and provider verification require the broker.
