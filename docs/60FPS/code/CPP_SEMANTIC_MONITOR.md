# C++ — semantic monitor

Suggested new core files.

## Header

```cpp
#pragma once

#include <array>
#include <atomic>
#include <cstdint>

namespace melonDS
{

enum class Sm64dsSemanticEvent : std::uint32_t
{
    MainLoopSlot1 = 0,
    CadenceRender,
    LagCallback,
    StageBehavior,
    StageRender,
    EntryBehavior,
    EntryRender,
    EntryInit,
    VBlankHandler,
    Count,
};

struct Sm64dsSemanticSnapshot
{
    bool enabled = false;
    std::uint64_t generation = 0;
    std::array<std::uint64_t,
        static_cast<std::size_t>(Sm64dsSemanticEvent::Count)> counters {};
    std::uint32_t lastPc = 0;
};

class Sm64dsSemanticMonitor
{
public:
    void Configure(bool enabled) noexcept
    {
        Enabled.store(enabled, std::memory_order_release);
        if (!enabled)
            Reset();
    }

    [[nodiscard]] bool IsEnabled() const noexcept
    {
        return Enabled.load(std::memory_order_relaxed);
    }

    void Reset() noexcept
    {
        for (auto& counter : Counters)
            counter.store(0, std::memory_order_relaxed);
        LastPc.store(0, std::memory_order_relaxed);
        Generation.fetch_add(1, std::memory_order_relaxed);
    }

    inline void ObserveArm9Pc(std::uint32_t rawPc) noexcept
    {
        if (!IsEnabled())
            return;

        const std::uint32_t pc = rawPc & ~1u;
        LastPc.store(pc, std::memory_order_relaxed);

        switch (pc)
        {
        case 0x02019404: Increment(Sm64dsSemanticEvent::MainLoopSlot1); break;
        case 0x02019144: Increment(Sm64dsSemanticEvent::CadenceRender); break;
        case 0x02019100: Increment(Sm64dsSemanticEvent::LagCallback); break;
        case 0x0202BBBC: Increment(Sm64dsSemanticEvent::StageBehavior); break;
        case 0x0202B8A4: Increment(Sm64dsSemanticEvent::StageRender); break;
        case 0x0211A2B8: Increment(Sm64dsSemanticEvent::EntryBehavior); break;
        case 0x0211A26C: Increment(Sm64dsSemanticEvent::EntryRender); break;
        case 0x0211A410: Increment(Sm64dsSemanticEvent::EntryInit); break;
        case 0x0201A534: Increment(Sm64dsSemanticEvent::VBlankHandler); break;
        default: break;
        }
    }

    [[nodiscard]] Sm64dsSemanticSnapshot Snapshot() const noexcept
    {
        Sm64dsSemanticSnapshot result;
        result.enabled = IsEnabled();
        result.generation = Generation.load(std::memory_order_relaxed);
        result.lastPc = LastPc.load(std::memory_order_relaxed);
        for (std::size_t i = 0; i < result.counters.size(); ++i)
            result.counters[i] = Counters[i].load(std::memory_order_relaxed);
        return result;
    }

private:
    void Increment(Sm64dsSemanticEvent event) noexcept
    {
        Counters[static_cast<std::size_t>(event)]
            .fetch_add(1, std::memory_order_relaxed);
    }

    std::atomic<bool> Enabled {false};
    std::atomic<std::uint64_t> Generation {0};
    std::array<std::atomic<std::uint64_t>,
        static_cast<std::size_t>(Sm64dsSemanticEvent::Count)> Counters {};
    std::atomic<std::uint32_t> LastPc {0};
};

} // namespace melonDS
```

## Notes

- Verify all addresses before product use.
- Add an optional target table/config if current symbols differ.
- Counters are monotonic; one-second windows calculate deltas externally.
- Keep the hot path disabled outside exact validation sessions.
