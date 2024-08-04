// Получение аргументов командной строки
const args = process.argv.slice(2);
const sourceLang = args[0];
const targetLang = args[1];
const inputText = args.slice(2).join(" "); // Составляем весь текст из оставшихся аргументов

if (!sourceLang || !targetLang || !inputText) {
    console.error('Missing arguments. Usage: node translate.js <sourceLang> <targetLang> <inputText>');
    process.exit(1);
}

console.log('Source Language:', sourceLang);
console.log('Target Language:', targetLang);
console.log('Input Text:', inputText);


const translatedText = `Привет, мир! Это моя первая программа`; а

console.log(JSON.stringify({ text: translatedText }));

