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

function parseExpiry(expiresAt) {
  if (!expiresAt) return null;

  const [datePart, timePart] = expiresAt.split(' ');
  if (!datePart || !timePart) return null;

  const [day, month, year] = datePart.split('/').map(Number);
  const [hour, minute] = timePart.split(':').map(Number);

  if (
    day == null || month == null || year == null ||
    hour == null || minute == null ||
    Number.isNaN(day) || Number.isNaN(month) || Number.isNaN(year) ||
    Number.isNaN(hour) || Number.isNaN(minute)
  ) {
    return null;
  }

  return new Date(year, month - 1, day, hour, minute, 0);
}

function removeExpiredNotifications() {
  const notifications = readNotificationsFile();
  const now = new Date();

  const filtered = notifications.filter(notification => {
    if (!notification.expiresAt) return true;

    const expiryDate = parseExpiry(notification.expiresAt);

    if (!expiryDate || Number.isNaN(expiryDate.getTime())) {
      return true;
    }

    return expiryDate > now;
  });

  if (filtered.length !== notifications.length) {
    writeNotificationsFile(filtered);
  }

  return filtered;
}

module.exports = {
  readNotificationsFile,
  writeNotificationsFile,
  removeExpiredNotifications
};