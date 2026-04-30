const express = require('express');
const router = express.Router();
const userController = require('../controllers/userController');

router.get('/', userController.getUsers);
router.post('/:id/profile-picture', userController.uploadProfilePicture);

router.get('/:id/friends', userController.getFriends);
router.post('/:id/friends/:friendId', userController.addFriend);
router.delete('/:id/friends/:friendId', userController.removeFriend);

router.delete('/:id', userController.deleteUser);

module.exports = router;