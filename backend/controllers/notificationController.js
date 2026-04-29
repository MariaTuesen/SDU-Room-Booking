const {
  readNotificationsFile,
  writeNotificationsFile,
  removeExpiredNotifications
} = require('../models/NotificationsStore');

exports.getUserNotifications = (req, res) => {
  try {
    const { userId } = req.params;

    const notifications = removeExpiredNotifications();

    const userNotifications = notifications
      .filter(n => n.userId === userId)
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    res.json(userNotifications);
  } catch {
    res.status(500).json({ message: 'Failed to fetch notifications' });
  }
};

exports.markAsRead = (req, res) => {
  try {
    const { userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const n = notifications.find(
      n => n.id === notificationId && n.userId === userId
    );

    if (!n) return res.status(404).json({ message: 'Not found' });

    n.read = true;
    writeNotificationsFile(notifications);

    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to mark as read' });
  }
};

exports.deleteNotification = (req, res) => {
  try {
    const { userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const filtered = notifications.filter(
      n => !(n.id === notificationId && n.userId === userId)
    );

    writeNotificationsFile(filtered);
    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to delete' });
  }
};