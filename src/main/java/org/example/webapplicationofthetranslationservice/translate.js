const { translate } = require('@vitalets/google-translate-api');

const sourceLang = process.argv[2];
const targetLang = process.argv[3];
const text = process.argv.slice(4).join(' ');

translate(text, { from: sourceLang, to: targetLang }).then(res => {
    console.log(JSON.stringify({text: res.text}));
}).catch(err => {
    console.log(JSON.stringify({error: err.message}));
    process.exit(1);
});