#ifndef MELONDS_ANDROID_ACTION_REPLAY_CODE_PARSER_H
#define MELONDS_ANDROID_ACTION_REPLAY_CODE_PARSER_H

#include <cstdint>
#include <sstream>
#include <string>
#include <utility>
#include <vector>

namespace MelonDSAndroid
{
inline bool parseActionReplayCode(const std::string& codeString, std::vector<uint32_t>& words)
{
    words.clear();

    std::istringstream codeStream(codeString);
    std::string sectionString;
    std::vector<uint32_t> parsedWords;

    while (codeStream >> sectionString)
    {
        if (sectionString.size() != 8)
            return false;

        uint32_t section = 0;
        for (char digit : sectionString)
        {
            uint32_t nibble;
            if (digit >= '0' && digit <= '9')
                nibble = static_cast<uint32_t>(digit - '0');
            else if (digit >= 'A' && digit <= 'F')
                nibble = static_cast<uint32_t>(digit - 'A' + 10);
            else if (digit >= 'a' && digit <= 'f')
                nibble = static_cast<uint32_t>(digit - 'a' + 10);
            else
                return false;

            section = (section << 4) | nibble;
        }
        parsedWords.push_back(section);
    }

    if (parsedWords.empty() || parsedWords.size() % 2 != 0)
        return false;

    words = std::move(parsedWords);
    return true;
}
}

#endif
