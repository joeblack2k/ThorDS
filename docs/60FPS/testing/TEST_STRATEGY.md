# Test strategy

Use four layers:

1. pure host tests;
2. core/native tests;
3. exact-profile integration tests;
4. physical Thor tests.

No physical claim may be replaced by a host test.

Every timing comparison uses:

- same private checkpoint;
- same input timeline;
- same renderer/features unless the variable under test;
- real elapsed time;
- at least three runs;
- documented warmup.
