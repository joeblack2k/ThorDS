# C++ — aggregate telemetry window

Extend the existing `MelonInstance` sampler instead of adding a second logger.

```cpp
struct SemanticWindow
{
    u64 startNs = 0;
    melonDS::Sm64dsSemanticSnapshot start {};
    melonDS::Sm64dsSemanticSnapshot latest {};
};

void MelonInstance::sampleSm64dsSemanticWindow()
{
    if (nds == nullptr || !nds->Sm64dsMonitor.IsEnabled())
        return;

    const u64 now = PerfNowNs();
    const auto snapshot = nds->Sm64dsMonitor.Snapshot();

    std::lock_guard lock(sm64dsGameLoopTelemetryMutex);
    if (semanticWindow.startNs == 0
        || snapshot.generation != semanticWindow.start.generation)
    {
        semanticWindow.startNs = now;
        semanticWindow.start = snapshot;
        semanticWindow.latest = snapshot;
        return;
    }

    semanticWindow.latest = snapshot;
    if (now - semanticWindow.startNs < 1'000'000'000ULL)
        return;

    auto delta = [&](melonDS::Sm64dsSemanticEvent event) -> u64 {
        const auto index = static_cast<std::size_t>(event);
        return semanticWindow.latest.counters[index]
            - semanticWindow.start.counters[index];
    };

    sm64dsSemanticLatestJson =
        "{\"valid\":true"
        ",\"windowWallNs\":" + std::to_string(now - semanticWindow.startNs) +
        ",\"slot1\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::MainLoopSlot1)) +
        ",\"cadenceRender\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::CadenceRender)) +
        ",\"lagCallback\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::LagCallback)) +
        ",\"stageBehavior\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::StageBehavior)) +
        ",\"stageRender\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::StageRender)) +
        ",\"entryBehavior\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::EntryBehavior)) +
        ",\"entryRender\":" + std::to_string(delta(
            melonDS::Sm64dsSemanticEvent::EntryRender)) +
        "}";

    semanticWindow.startNs = now;
    semanticWindow.start = semanticWindow.latest;
}
```

Call immediately after `nds->RunFrame()` alongside the existing sampler.

Add identity/profile gating before enabling the core monitor. Do not let a
generic ROM activate SM64DS addresses.
