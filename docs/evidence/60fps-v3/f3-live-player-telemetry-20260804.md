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

F3 is not complete. The required scripted Yoshi backflip and the baseline
`A,A,B,B` pose cadence are not yet saved. The repeated trace is not a
backflip trace. F4 is not started.

The corner counter remains a loop-frequency indicator. It is not rendered-FPS
proof.
