const fs = require('fs');
const path = require('path');

const notificationsFilePath = path.join(__dirname, '..', 'data', 'notifications.json');

function ensureNotificationsFile() {
  const dir = path.dirname(notificationsFilePath);

  if (!fs.existsSync(dir)) {
    fs.mkdirSync(dir, { recursive: true });
  }

  if (!fs.existsSync(notificationsFilePath)) {
    fs.writeFileSync(notificationsFilePath, JSON.stringify([], null, 2), 'utf8');
  }
}

function readNotificationsFile() {
  ensureNotificationsFile();
  const raw = fs.readFileSync(notificationsFilePath, 'utf8');
  return JSON.parse(raw);
}

function writeNotificationsFile(data) {
  fs.writeFileSync(notificationsFilePath, JSON.stringify(data, null, 2), 'utf8');
}

module.exports = {
  readNotificationsFile,
  writeNotificationsFile
};