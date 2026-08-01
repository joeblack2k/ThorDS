# Commit and push policy

For each gate:

1. review full diff;
2. run tests/build;
3. run ROM/private/secret scans;
4. stage explicit paths;
5. inspect staged diff;
6. commit one intent;
7. push without force;
8. verify remote SHA;
9. update status/worklog.

Do not commit a generated patch before its source, manifest and verifier.
