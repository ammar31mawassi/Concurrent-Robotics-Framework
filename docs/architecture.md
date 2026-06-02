<!-- bettergithub:generated-file -->
# Concurrent Robotics Framework Architecture

The framework separates message types, services, application objects, and tests. The docs describe how concurrency flow and sensor data fusion are intended to be reviewed.

## Reviewer Flow

1. Read the root README for the project purpose, setup, usage, and test signal.
2. Inspect the main source locations:
   - `src/main/java`
   - `src/test/java`
   - `pom.xml`
   - `docs`
3. Run or manually verify the project using the test plan.
4. Capture any new screenshot or terminal output under `docs/` so the README stays evidence-based.

## Boundaries

- This documentation pass does not alter runtime behavior.
- Secrets, API keys, and local machine paths should stay out of commits.
- Generated binaries and large local outputs should only be committed when they are intentional assignment artifacts.
