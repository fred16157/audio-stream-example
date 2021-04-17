const express = require('express');
const app = express();
const fs = require('fs');
const path = require('path');

app.get('/', (req, res) => {
    console.log('New connection');
    res.set('content-type', 'audio/mp3');
    res.set('accept-ranges', 'bytes');

    let audio = fs.createReadStream(path.join(__dirname, './audio/sample.mp3'));

    audio.on('data', (data) => {
        res.write(data);
    });

    audio.on('end', () => {
        res.end();
    });
});

app.listen(3000, () => {
    console.log('Listening on port 3000');
});

