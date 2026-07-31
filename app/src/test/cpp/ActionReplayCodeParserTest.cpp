#include <cassert>
#include <cstdint>
#include <string>
#include <vector>

#include "ActionReplayCodeParser.h"

int main()
{
    const std::string newlineComposedCode =
        "5200D03C 00001555\n"
        "0200D03C 00001C72\n"
        "D0000000 00000000\n"
        "5200F64C 00001555\n"
        "0200F64C 00001C72\n"
        "D0000000 00000000\n"
        "52015774 00001555\n"
        "02015774 00001C72\n"
        "D0000000 00000000\n"
        "520C025C 00001555\n"
        "020C025C 00001C72\n"
        "D0000000 00000000\n";
    const std::vector<uint32_t> expectedWords = {
        0x5200D03C, 0x00001555,
        0x0200D03C, 0x00001C72,
        0xD0000000, 0x00000000,
        0x5200F64C, 0x00001555,
        0x0200F64C, 0x00001C72,
        0xD0000000, 0x00000000,
        0x52015774, 0x00001555,
        0x02015774, 0x00001C72,
        0xD0000000, 0x00000000,
        0x520C025C, 0x00001555,
        0x020C025C, 0x00001C72,
        0xD0000000, 0x00000000,
    };

    std::vector<uint32_t> words;
    assert(MelonDSAndroid::parseActionReplayCode(newlineComposedCode, words));
    assert(words.size() == 24);
    assert(words == expectedWords);

    assert(!MelonDSAndroid::parseActionReplayCode("", words));
    assert(words.empty());
    assert(!MelonDSAndroid::parseActionReplayCode("5200D03C NOTHEX00", words));
    assert(words.empty());
    assert(!MelonDSAndroid::parseActionReplayCode("5200D03C", words));
    assert(words.empty());
}
