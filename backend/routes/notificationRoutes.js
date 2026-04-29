const express = require('express');
const router = express.Router();
const notificationController = require('../controllers/notificationController');

router.get('/:userId', notificationController.getUserNotifications);
router.post('/:userId/:notificationId/read', notificationController.markAsRead);
router.delete('/:userId/:notificationId', notificationController.deleteNotification);

module.exports = router;