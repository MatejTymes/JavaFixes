package javafixes.json5;

enum Token {
    objectStart,

    keyInObject,
    semicolon,
    valueInObject,
    commaInObject,

    arrayStart,

    valueInArray,
    commaInArray,
}
