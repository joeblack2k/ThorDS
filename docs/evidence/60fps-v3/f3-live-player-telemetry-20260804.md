# F3 Live Player Telemetry

Status: PARTIAL

## Device flow

The autonomous AYN Thor flow reached the live game scene:

1. Title screen on the lower display.
2. Yoshi screen.
3. File A.
4. Gameplay on the upper display.

The device uses two touch displays. The lower display contains the title and
save flow. The upper display contains gameplay.

## Live dump

The `DUMP_SM64DS_GAME_LOOP` dump reported:

- `uniqueUpdates=61`
- `emulatorFrames=61`
- `cadenceValue=1`
- `playerPointer=35119672`
- `playerPositionValid=true`
- `playerBodyModelPointer=36260500`
- `playerAnimationValid=true`
- `playerAnimationPointer=36260580`
- `playerAnimationCurrFrameQ12=88064`
- `playerAnimationSpeedQ12=4096`
- `playerAnimationBaseFrame=21`
- `playerAnimationNextFrame=22`
- `playerAnimationAlphaQ12=2048`
- `playerAnimationBaseTransformHash=1637883349`
- `playerAnimationNextTransformHash=0`
- `playerAnimationOutputTransformHash=1637883349`
- `playerInterpolationExecutionCount=0`

This proves that the telemetry reaches a live player scene. It does not prove
pose interpolation.

The next transform hash is zero because no second pose sample is implemented.
The interpolation execution count is zero because the enhancement is disabled.

A repeated live trace also showed:

- `currFrameQ12` values with both zero and `0x800` fractions;
- changing full player transform-buffer hashes;
- output hash equal to the base hash on every sample;
- interpolation execution count equal to zero on every sample.

The hash covers the player transform buffer. The first root matrix alone was
not sufficient because it can remain constant while the body animates.

## Direct jump trace

The autonomous test used one direct `B` press with no crouch combination.
During the jump, the live dump reported:

- `playerPosY=485.580566`
- `playerSpeedY=13.091797`
- `playerStateStep=1`
- `playerAnimationCurrFrameQ12=98304`
- `playerAnimationBaseFrame=24`
- `playerAnimationNextFrame=25`
- `playerAnimationBaseTransformHash=858156675`
- `playerAnimationOutputTransformHash=858156675`

The later samples showed the player returning to `playerPosY=254.010986`.
This proves that a direct debug jump creates real player motion and reaches
the existing pose telemetry. It does not prove interpolation.

## Acceptance

F3 uses the autonomous direct-jump trace as its player-motion test. A
scripted Yoshi backflip and `A,A,B,B` cadence are outside the active acceptance
scope. F4 is not started.

## Pose payload control comparison

The developer-only pose profile was enabled through the exact EU ROM key. The
runtime hook word became `0xEAFFB961`, but the SM64DS game-loop telemetry then
reported `uniqueUpdates=0`, `counter=2589`, and `lastDelta=0` while emulator
frames continued. The player remained in the entry state.

The same session was relaunched with the pose profile disabled. The game-loop
telemetry then reported `uniqueUpdates=61`, `counter=420`, and `lastDelta=1`.
The disabled comparison is the control pass.

This is a payload regression. The developer pose profile remains disabled and
is not accepted for F4.

The hand-maintained pose code was removed from the runtime profile. The
developer-only profile entry remains metadata-only, with no curated runtime
code. A future pose payload must come from checked-in source and assembly,
the relocation-aware builder, and the independent verifier.

The corner counter remains a loop-frequency indicator. It is not rendered-FPS
proof.
