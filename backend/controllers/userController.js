const path = require('path');
const fs = require('fs');
const multer = require('multer');

const { Database } = require('../Database');
const {
  readFriendsFile,
  writeFriendsFile,
  uniq
} = require('../models/FriendsStore');

const usersDB = Database.getInstance('users');

const uploadsDir = path.join(__dirname, '..', 'data', 'uploads');

const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadsDir),
  filename: (req, file, cb) => {
    const userId = req.params.id;
    const ext = path.extname(file.originalname).toLowerCase() || '.png';
    cb(null, `${userId}${ext}`);
  }
});

const upload = multer({ storage });

exports.uploadProfilePicture = [
  upload.single('file'),
  (req, res) => {
    try {
      const { id } = req.params;
      const user = usersDB.select({ id })[0];

      if (!user) return res.status(404).json({ message: 'User not found' });
      if (!req.file) return res.status(400).json({ message: 'No file uploaded' });

      const profilePath = `/uploads/${req.file.filename}`;
      user.profile_picture = profilePath;

      usersDB.update(id, { profile_picture: profilePath });

      return res.json(user);
    } catch (err) {
      return res.status(500).json({ message: 'Upload failed' });
    }
  }
];

exports.getUsers = (req, res) => {
  try {
    const users = usersDB.select({}) || [];

    const safeUsers = users.map(u => ({
      id: u.id,
      fullName: u.fullName,
      email: u.email,
      profile_picture: u.profile_picture ?? null
    }));

    res.json(safeUsers);
  } catch {
    res.status(500).json({ message: 'Failed to fetch users' });
  }
};

exports.getFriends = async (req, res) => {
  try {
    const userId = req.params.id;

    const friendsData = await readFriendsFile();
    const friendIds = friendsData.byUserId?.[userId] || [];

    const users = usersDB.select({}) || [];

    const safeUsers = users.map(u => ({
      id: u.id,
      fullName: u.fullName,
      email: u.email,
      profile_picture: u.profile_picture ?? null
    }));

    res.json(safeUsers.filter(u => friendIds.includes(u.id)));
  } catch {
    res.status(500).json({ message: 'Failed to get friends' });
  }
};

exports.addFriend = async (req, res) => {
  try {
    const { id, friendId } = req.params;

    const data = await readFriendsFile();
    data.byUserId = data.byUserId || {};

    const current = data.byUserId[id] || [];
    data.byUserId[id] = uniq([...current, friendId]);

    await writeFriendsFile(data);

    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to add friend' });
  }
};

exports.removeFriend = async (req, res) => {
  try {
    const { id, friendId } = req.params;

    const data = await readFriendsFile();
    data.byUserId[id] =
      (data.byUserId[id] || []).filter(fid => fid !== friendId);

    await writeFriendsFile(data);

    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to remove friend' });
  }
};

exports.deleteUser = async (req, res) => {
  try {
    const { id } = req.params;

    const user = usersDB.select({ id })[0];
    if (!user) return res.status(404).json({ message: 'User not found' });

    usersDB.delete(id);

    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to delete user' });
  }
};