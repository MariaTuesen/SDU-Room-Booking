const { v4: uuidv4 } = require('uuid');
const {
  readBookingsFile,
  writeBookingsFile,
  hasConflict
} = require('../models/BookingsStore');

const {
  readNotificationsFile,
  writeNotificationsFile
} = require('../models/NotificationsStore');

const { Database } = require('../Database');

const usersDB = Database.getInstance('users');

exports.getBookings = (req, res) => {
  try {
    const { date, roomId } = req.query;
    const bookings = readBookingsFile();

    const filtered = bookings.filter(b => {
      if (date && b.date !== date) return false;
      if (roomId && String(b.roomId) !== String(roomId)) return false;
      return true;
    });

    res.json(filtered);
  } catch (err) {
    res.status(500).json({ message: 'Failed to fetch bookings' });
  }
};

exports.createBooking = (req, res) => {
  try {
    const { roomId, date, startTime, endTime, userIds } = req.body;

    if (roomId == null || !date || !startTime || !endTime) {
      return res.status(400).json({ message: 'Missing fields' });
    }

    const bookings = readBookingsFile();

    if (hasConflict(bookings, roomId, date, startTime, endTime)) {
      return res.status(409).json({ message: 'Room is already booked in that timeframe' });
    }

    const uniqueUserIds = Array.isArray(userIds) ? [...new Set(userIds)] : [];

    const newBooking = {
      id: uuidv4(),
      roomId: Number(roomId),
      date,
      startTime,
      endTime,
      userIds: uniqueUserIds,
      createdAt: new Date().toISOString()
    };

    bookings.push(newBooking);
    writeBookingsFile(bookings);

    const creatorId = uniqueUserIds[0];
    const users = usersDB.select({}) || [];
    const creator = users.find(u => u.id === creatorId);

    const notifications = readNotificationsFile();

    for (const invitedUserId of uniqueUserIds) {
      if (invitedUserId === creatorId) continue;

      notifications.push({
        id: uuidv4(),
        userId: invitedUserId,
        title: 'New booking',
        message: `${creator?.fullName || 'Someone'} added you to a booking on ${date} from ${startTime} to ${endTime}`,
        createdAt: new Date().toISOString(),
        expiresAt: `${date} ${endTime}`,
        read: false,
        type: 'booking_invite',
        bookingId: newBooking.id
      });
    }

    writeNotificationsFile(notifications);

    res.status(201).json(newBooking);
  } catch (err) {
    res.status(500).json({ message: 'Failed to create booking' });
  }
};

exports.updateBooking = (req, res) => {
  try {
    const { id } = req.params;
    const { userIds } = req.body;

    if (!Array.isArray(userIds)) {
      return res.status(400).json({ message: 'userIds must be an array' });
    }

    let bookings = readBookingsFile();
    const index = bookings.findIndex(b => b.id === id);

    if (index === -1) {
      return res.status(404).json({ message: 'Booking not found' });
    }

    if (userIds.length === 0) {
      bookings = bookings.filter(b => b.id !== id);
      writeBookingsFile(bookings);
      return res.sendStatus(204);
    }

    bookings[index].userIds = [...new Set(userIds)];
    writeBookingsFile(bookings);

    res.json(bookings[index]);
  } catch (err) {
    res.status(500).json({ message: 'Failed to update booking' });
  }
};

exports.deleteBooking = (req, res) => {
  try {
    const { id } = req.params;

    let bookings = readBookingsFile();
    const originalLength = bookings.length;

    bookings = bookings.filter(b => b.id !== id);

    if (bookings.length === originalLength) {
      return res.status(404).json({ message: 'Booking not found' });
    }

    writeBookingsFile(bookings);
    res.sendStatus(204);
  } catch (err) {
    res.status(500).json({ message: 'Failed to delete booking' });
  }
};