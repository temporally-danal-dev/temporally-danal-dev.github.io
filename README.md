# Wordle Battle

|        |                       | client(request)          | server(response)                           |
| ------ | --------------------- | ------------------------ | ------------------------------------------ |
| api    | POST mainUrl/matching |                          | body {me: Str, roomId: Str, opponent: Str} |
| socket | join                  | {nickname: Str}          |                                            |
|        | start                 |                          | {nickname: Str, answerLength: Int}         |
|        | submit                | {word:Str, nickname:Str} | {word:Str, matchStatus:Str, nickname: Str} |
|        | end                   |                          | {word:Str,nickname:Str}                    |
