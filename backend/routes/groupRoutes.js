const express = require('express');
const router = express.Router();
const groupController = require('../controllers/groupController');

router.post('/', groupController.createGroup);
router.get('/users/:id', groupController.getUserGroups);

router.post('/:groupId/invite', groupController.inviteToGroup);

router.post('/accept/:userId/:notificationId', groupController.acceptInvite);
router.post('/decline/:userId/:notificationId', groupController.declineInvite);

router.get('/:groupId', groupController.getGroup);
router.delete('/:groupId/members/:userId', groupController.leaveGroup);

module.exports = router;