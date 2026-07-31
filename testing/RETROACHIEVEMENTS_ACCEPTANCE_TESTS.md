# RetroAchievements-acceptatietests

## Policy unit matrix

Test alle combinaties van:

- requested Off/Casual/Hardcore;
- Original/Enhanced;
- curated codes;
- user cheats;
- OC;
- rewind;
- state load;
- resume.

## RA-01 Off

- no auth required;
- no game set load;
- no submission;
- game works.

## RA-02 Casual Original

- game ID 9983;
- set loads;
- rich presence;
- state/rewind according upstream Casual.

## RA-03 Casual Enhanced

- analog active;
- True WS active;
- set loads from original EU identity;
- active enhancements shown;
- normal achievement event/submission.

## RA-04 Hardcore conflict

Requested Hardcore + Enhanced:

- game does not silently start Hardcore;
- dialog offers Original+restart or Casual Enhanced;
- no runtimecodes in Hardcore.

## RA-05 Hardcore Original

- clean session;
- cheats blocked;
- rewind blocked;
- load state blocked;
- OC 100;
- mode visible;
- Casual→Hardcore reset required.

## RA-06 User-Agent

- own product/version;
- Android/core clauses;
- no upstream impersonation;
- no control chars;
- server requests use same stable identity.

## RA-07 Credentials

Search logcat/evidence for:

```text
password
token
authorization
username where private
```

No secret.

## RA-08 Offline

- disconnect;
- achievement runtime continues where supported;
- queue behavior upstream-compatible;
- reconnect;
- no duplicate submission.

## RA-09 Save states

Casual RA progress serializes/restores. Hardcore load blocked.

## Normal unlock

Select a simple unearned achievement. Obtain by real gameplay. Record:

- pre status;
- in-game trigger;
- submission result;
- post status.

No memory modification, direct award endpoint or credential export.

## Account protection

Wanneer alle simpele achievements al unlocked zijn, do not intentionally corrupt progress. Use controlled testaccount or mark online unlock gate blocked while preserving local mock/service tests.
