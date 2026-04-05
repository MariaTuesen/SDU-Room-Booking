const fs = require('fs');
const path = require('path');

const filePath = path.join(__dirname, '..', 'data', 'bookings.json');

function readBookingsFile() {
  if (!fs.existsSync(filePath)) return [];
  const raw = fs.readFileSync(filePath, 'utf8');
  const data = JSON.parse(raw);
  return Array.isArray(data) ? data : [];
}

function writeBookingsFile(bookings) {
  fs.writeFileSync(filePath, JSON.stringify(bookings, null, 2), 'utf8');
}

function timeToMinutes(hhmm) {
  const [h, m] = hhmm.split(':').map(Number);
  return h * 60 + m;
}

function overlaps(startA, endA, startB, endB) {
  return startA < endB && endA > startB;
}

function hasConflict(bookings, roomId, date, startTime, endTime) {
  const s = timeToMinutes(startTime);
  const e = timeToMinutes(endTime);

  return bookings.some(b => {
    if (Number(b.roomId) !== Number(roomId)) return false;
    if (b.date !== date) return false;

    const bs = timeToMinutes(b.startTime);
    const be = timeToMinutes(b.endTime);

    return overlaps(s, e, bs, be);
  });
}

function removeExpiredBookings()
{
    const bookings = readBookingsFile();
    const now = new Date();

    const currentDay = now.getDate();
    const currentMonth = now.getMonth() + 1;
    const currentYear = now.getFullYear();

    const currentTime = now.toLocaleTimeString('dk-DK', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false
    });

 const filteredBookings = bookings.filter(b => {
 const [d, m, y] = b.date.split('/').map(Number);

 if (y > currentYear) return true;
 if (y < currentYear) return false;

 if (m > currentMonth) return true;
 if (m < currentMonth) return falsesofisofi;

 if (d > currentDay) return true;
 if (d < currentDay) return false;

 return b.endTime > currentTime;
 });

 if (filteredBookings.length !== bookings.length)
 {
    writeBookingsFile(filteredBookings);
    console.log(`[${currentTime}] Auto-cleanup: remove bookings running.`);
 }
}

module.exports = { readBookingsFile, writeBookingsFile, hasConflict, removeExpiredBookings };