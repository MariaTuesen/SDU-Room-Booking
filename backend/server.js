const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { Database } = require('./Database');
const { v4: uuidv4 } = require('uuid');
const path = require('path');
const multer = require('multer');
const fs = require('fs');

const { readFriendsFile, writeFriendsFile, uniq } = require('./models/FriendsStore');
const { readBookingsFile, writeBookingsFile, hasConflict } = require('./models/BookingsStore');
const { readNotificationsFile, writeNotificationsFile, removeExpiredNotifications } = require('./models/NotificationsStore');
const { readGroupsFile, writeGroupsFile } = require('./models/GroupsStore');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const uploadsDir = path.join(__dirname, 'data', 'uploads');
if (!fs.existsSync(uploadsDir)) {
  fs.mkdirSync(uploadsDir, { recursive: true });
}
app.use('/uploads', express.static(uploadsDir));

const usersDB = Database.getInstance('users');

const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadsDir),
  filename: (req, file, cb) => {
    const userId = req.params.id;

    const mime = (file.mimetype || '').toLowerCase();
    let ext = '';
    if (mime === 'image/png') ext = '.png';
    else if (mime === 'image/jpeg' || mime === 'image/jpg') ext = '.jpg';
    else if (mime === 'image/webp') ext = '.webp';
    else {
      ext = path.extname(file.originalname).toLowerCase() || '.png';
    }

    cb(null, `${userId}${ext}`);
  }
});

const fileFilter = (req, file, cb) => {
  if ((file.mimetype || '').startsWith('image/')) cb(null, true);
  else cb(new Error('Only image files are allowed'), false);
};

const upload = multer({ storage, fileFilter });

function persistUserProfilePicture(userId, profilePath) {
  if (typeof usersDB.update === 'function') {
    usersDB.update(userId, { profile_picture: profilePath });
    return;
  }

  const usersFilePath = path.join(__dirname, 'data', 'users.json');

  if (!fs.existsSync(usersFilePath)) {
    throw new Error(`No users file found at ${usersFilePath}.`);
  }

  const raw = fs.readFileSync(usersFilePath, 'utf8');
  const data = JSON.parse(raw);

  const usersArray = Array.isArray(data) ? data : data.users;

  if (!Array.isArray(usersArray)) {
    throw new Error(`Unexpected users JSON shape in ${usersFilePath}`);
  }

  const idx = usersArray.findIndex(u => u.id === userId);
  if (idx === -1) throw new Error('User not found in users file');

  usersArray[idx].profile_picture = profilePath;

  fs.writeFileSync(usersFilePath, JSON.stringify(data, null, 2), 'utf8');
}

function removeGroupInviteNotifications(groupId) {
  const notifications = readNotificationsFile();

  const filtered = notifications.filter(
    n => !(n.type === 'group_invite' && n.groupId === groupId)
  );

  writeNotificationsFile(filtered);
}

function removeGroupInviteNotificationById(notificationId) {
  const notifications = readNotificationsFile();
  const filtered = notifications.filter(n => n.id !== notificationId);
  writeNotificationsFile(filtered);
}

function normalizeGroup(group) {
  if (Array.isArray(group.participants)) {
    return {
      ...group,
      participants: group.participants.map(p => ({
        userId: p.userId,
        status: p.status || 'pending'
      }))
    };
  }

  const memberIds = Array.isArray(group.memberIds) ? group.memberIds : [];

  return {
    id: group.id,
    name: group.name,
    participants: memberIds.map(userId => ({
      userId,
      status: 'accepted'
    }))
  };
}

function getNormalizedGroupsData() {
  const groupsData = readGroupsFile();
  groupsData.groups = (groupsData.groups || []).map(normalizeGroup);
  return groupsData;
}

app.post('/auth/signup', (req, res) => {
  try {
    const { fullName, email, password } = req.body;

    if (!fullName || !email || !password) {
      return res.status(400).json({ message: 'Missing fields' });
    }

    if (!email.endsWith('@student.sdu.dk')) {
      return res.status(400).json({
        message: 'Only SDU students can create an account'
      });
    }

    if (usersDB.select({ email }).length > 0) {
      return res.status(400).json({ message: 'User already exists' });
    }

    const newUser = {
      id: uuidv4(),
      fullName,
      email,
      password,
      profile_picture: null
    };

    usersDB.insert(newUser);

    return res.status(201).json({
      message: 'User created successfully',
      user: newUser
    });
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Server error' });
  }
});

app.post('/auth/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) {
    return res.status(400).json({ message: 'Missing credentials' });
  }

  const user = usersDB.select({ email, password })[0];
  if (!user) {
    return res.status(401).json({ message: 'Invalid credentials' });
  }

  return res.json(user);
});

app.get('/users', (req, res) => {
  try {
    const users = usersDB.select({}) || [];

    const safeUsers = users.map(u => ({
      id: u.id,
      fullName: u.fullName,
      email: u.email,
      profile_picture: u.profile_picture ?? null
    }));

    return res.json(safeUsers);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to fetch users' });
  }
});

app.post('/users/:id/profile-picture', upload.single('file'), (req, res) => {
  try {
    const { id } = req.params;

    const user = usersDB.select({ id })[0];
    if (!user) return res.status(404).json({ message: 'User not found' });

    if (!req.file) return res.status(400).json({ message: 'No file uploaded' });

    const profilePath = `/uploads/${req.file.filename}`;
    user.profile_picture = profilePath;

    persistUserProfilePicture(id, profilePath);

    return res.json(user);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: err.message || 'Upload failed' });
  }
});

app.get('/users/:id/friends', async (req, res) => {
  try {
    const userId = req.params.id;

    const friendsData = await readFriendsFile();
    if (!friendsData.byUserId) friendsData.byUserId = {};

    const friendIds = friendsData.byUserId[userId] || [];

    const users = usersDB.select({}) || [];
    const safeUsers = users.map(u => ({
      id: u.id,
      fullName: u.fullName,
      email: u.email,
      profile_picture: u.profile_picture ?? null
    }));

    const friendUsers = safeUsers.filter(u => friendIds.includes(u.id));
    return res.json(friendUsers);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to get friends' });
  }
});

app.post('/users/:id/friends/:friendId', async (req, res) => {
  try {
    const userId = req.params.id;
    const friendId = req.params.friendId;

    const friendsData = await readFriendsFile();
    if (!friendsData.byUserId) friendsData.byUserId = {};

    const current = friendsData.byUserId[userId] || [];
    friendsData.byUserId[userId] = uniq([...current, friendId]);

    await writeFriendsFile(friendsData);
    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to add friend' });
  }
});

app.delete('/users/:id/friends/:friendId', async (req, res) => {
  try {
    const userId = req.params.id;
    const friendId = req.params.friendId;

    const friendsData = await readFriendsFile();
    if (!friendsData.byUserId) friendsData.byUserId = {};

    const current = friendsData.byUserId[userId] || [];
    friendsData.byUserId[userId] = current.filter(fid => fid !== friendId);

    await writeFriendsFile(friendsData);
    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to remove friend' });
  }
});

app.post('/groups', (req, res) => {
  try {
    const { name, creatorId, invitedUserIds } = req.body;

    if (!name || !creatorId) {
      return res.status(400).json({ message: 'Missing group name or creatorId' });
    }

    const users = usersDB.select({}) || [];
    const creator = users.find(u => u.id === creatorId);

    if (!creator) {
      return res.status(404).json({ message: 'Creator not found' });
    }

    const groupsData = getNormalizedGroupsData();
    const notifications = readNotificationsFile();

    const uniqueInvites = Array.isArray(invitedUserIds)
      ? [...new Set(invitedUserIds)].filter(id => id !== creatorId)
      : [];

    const newGroup = {
      id: uuidv4(),
      name: name.trim(),
      participants: [
        { userId: creatorId, status: 'accepted' },
        ...uniqueInvites.map(id => ({
          userId: id,
          status: 'pending'
        }))
      ]
    };

    groupsData.groups.push(newGroup);
    writeGroupsFile(groupsData);

    for (const invitedUserId of uniqueInvites) {
      notifications.push({
        id: uuidv4(),
        userId: invitedUserId,
        title: 'Group invite',
        message: `${creator.fullName} invited you to join "${newGroup.name}"`,
        createdAt: new Date().toISOString(),
        read: false,
        type: 'group_invite',
        groupId: newGroup.id,
        invitedByUserId: creatorId
      });
    }

    writeNotificationsFile(notifications);

    return res.status(201).json(newGroup);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to create group' });
  }
});

app.get('/users/:id/groups', (req, res) => {
  try {
    const userId = req.params.id;
    const groupsData = getNormalizedGroupsData();

    const userGroups = groupsData.groups.filter(group =>
      (group.participants || []).some(
        p => p.userId === userId && p.status === 'accepted'
      )
    );

    writeGroupsFile(groupsData);
    return res.json(userGroups);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to fetch groups' });
  }
});

app.post('/groups/:groupId/invite', (req, res) => {
  try {
    const { groupId } = req.params;
    const { invitedUserId, invitedByUserId } = req.body;

    if (!invitedUserId || !invitedByUserId) {
      return res.status(400).json({ message: 'Missing invitedUserId or invitedByUserId' });
    }

    const groupsData = getNormalizedGroupsData();
    const notifications = readNotificationsFile();
    const users = usersDB.select({}) || [];

    const group = groupsData.groups.find(g => g.id === groupId);
    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    if (!Array.isArray(group.participants)) {
      group.participants = [];
    }

    const inviterIsAccepted = group.participants.some(
      p => p.userId === invitedByUserId && p.status === 'accepted'
    );

    if (!inviterIsAccepted) {
      return res.status(403).json({ message: 'Only accepted group members can invite others' });
    }

    const existingParticipant = group.participants.find(p => p.userId === invitedUserId);
    if (existingParticipant && existingParticipant.status !== 'declined') {
      return res.status(400).json({ message: 'User is already in the group or already invited' });
    }

    const inviter = users.find(u => u.id === invitedByUserId);

    if (existingParticipant && existingParticipant.status === 'declined') {
      existingParticipant.status = 'pending';
    } else {
      group.participants.push({
        userId: invitedUserId,
        status: 'pending'
      });
    }

    notifications.push({
      id: uuidv4(),
      userId: invitedUserId,
      title: 'Group invite',
      message: `${inviter?.fullName || 'Someone'} invited you to join "${group.name}"`,
      createdAt: new Date().toISOString(),
      read: false,
      type: 'group_invite',
      groupId: group.id,
      invitedByUserId
    });

    writeGroupsFile(groupsData);
    writeNotificationsFile(notifications);

    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to invite user to group' });
  }
});

app.post('/users/:id/notifications/:notificationId/accept-group-invite', (req, res) => {
  try {
    const { id: userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const groupsData = getNormalizedGroupsData();

    const notificationIndex = notifications.findIndex(
      n =>
        n.id === notificationId &&
        n.userId === userId &&
        n.type === 'group_invite'
    );

    if (notificationIndex === -1) {
      return res.status(404).json({ message: 'Group invite notification not found' });
    }

    const notification = notifications[notificationIndex];
    const group = groupsData.groups.find(g => g.id === notification.groupId);

    if (!group) {
      notifications.splice(notificationIndex, 1);
      writeNotificationsFile(notifications);
      return res.status(404).json({ message: 'This group no longer exists' });
    }

    if (!Array.isArray(group.participants)) {
      group.participants = [];
    }

    const participant = group.participants.find(p => p.userId === userId);

    if (participant) {
      participant.status = 'accepted';
    } else {
      group.participants.push({
        userId,
        status: 'accepted'
      });
    }

    notifications.splice(notificationIndex, 1);

    writeGroupsFile(groupsData);
    writeNotificationsFile(notifications);

    return res.json(group);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to accept group invite' });
  }
});

app.post('/notifications/:userId/:notificationId/decline', (req, res) => {
  try {
    const { userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const groupsData = getNormalizedGroupsData();

    const notificationIndex = notifications.findIndex(
      n =>
        n.id === notificationId &&
        n.userId === userId &&
        n.type === 'group_invite'
    );

    if (notificationIndex === -1) {
      return res.status(404).json({ message: 'Group invite notification not found' });
    }

    const notification = notifications[notificationIndex];
    const group = groupsData.groups.find(g => g.id === notification.groupId);

    if (group) {
      if (!Array.isArray(group.participants)) {
        group.participants = [];
      }

      const participant = group.participants.find(p => p.userId === userId);

      if (participant) {
        participant.status = 'declined';
      } else {
        group.participants.push({
          userId,
          status: 'declined'
        });
      }

      writeGroupsFile(groupsData);
    }

    notifications.splice(notificationIndex, 1);
    writeNotificationsFile(notifications);

    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to decline group invite' });
  }
});

app.get('/groups/:groupId', (req, res) => {
  try {
    const { groupId } = req.params;
    const groupsData = getNormalizedGroupsData();

    const group = groupsData.groups.find(g => g.id === groupId);
    if (!group) {
      return res.status(404).json({ message: 'Group not found' });
    }

    writeGroupsFile(groupsData);
    return res.json(group);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to fetch group' });
  }
});

app.delete('/groups/:groupId/members/:userId', (req, res) => {
  try {
    const { groupId, userId } = req.params;
    const groupsData = getNormalizedGroupsData();

    const groupIndex = groupsData.groups.findIndex(g => g.id === groupId);
    if (groupIndex === -1) {
      return res.status(404).json({ message: 'Group not found' });
    }

    const group = groupsData.groups[groupIndex];
    group.participants = (group.participants || []).filter(p => p.userId !== userId);

    const acceptedCount = group.participants.filter(p => p.status === 'accepted').length;

    if (acceptedCount === 0) {
      groupsData.groups.splice(groupIndex, 1);
      writeGroupsFile(groupsData);
      removeGroupInviteNotifications(groupId);
      return res.sendStatus(204);
    }

    writeGroupsFile(groupsData);
    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to leave group' });
  }
});

app.delete('/users/:id', async (req, res) => {
  try {
    const { id } = req.params;

    const user = usersDB.select({ id })[0];
    if (!user) return res.status(404).json({ message: 'User not found' });

    if (user.profile_picture) {
      const filename = user.profile_picture.replace('/uploads/', '');
      const filePath = path.join(uploadsDir, filename);
      if (fs.existsSync(filePath)) fs.unlinkSync(filePath);
    }

    usersDB.delete(id);

    const friendsData = await readFriendsFile();
    if (!friendsData.byUserId) friendsData.byUserId = {};

    delete friendsData.byUserId[id];

    for (const uid of Object.keys(friendsData.byUserId)) {
      friendsData.byUserId[uid] = (friendsData.byUserId[uid] || []).filter(fid => fid !== id);
    }

    await writeFriendsFile(friendsData);

    const groupsData = getNormalizedGroupsData();
    const deletedGroupIds = [];

    groupsData.groups = groupsData.groups
      .map(group => ({
        ...group,
        participants: (group.participants || []).filter(p => p.userId !== id)
      }))
      .filter(group => {
        const keepGroup = group.participants.some(p => p.status === 'accepted');
        if (!keepGroup) {
          deletedGroupIds.push(group.id);
        }
        return keepGroup;
      });

    writeGroupsFile(groupsData);

    if (deletedGroupIds.length > 0) {
      const notifications = readNotificationsFile();
      const filteredNotifications = notifications.filter(
        n => !(n.type === 'group_invite' && deletedGroupIds.includes(n.groupId))
      );
      writeNotificationsFile(filteredNotifications);
    }

    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: err.message || 'Failed to delete user' });
  }
});

app.get('/rooms', (req, res) => {
  try {
    const roomsFilePath = path.join(__dirname, 'data', 'rooms.json');

    if (!fs.existsSync(roomsFilePath)) {
      return res.status(404).json({ message: 'rooms.json not found' });
    }

    const raw = fs.readFileSync(roomsFilePath, 'utf8');
    const rooms = JSON.parse(raw);

    const normalized = (rooms || []).map(r => ({
      ...r,
      id: r.id || '',
      building: String(r.building),
    }));

    return res.json(normalized);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to load rooms' });
  }
});

app.get('/bookings', (req, res) => {
  try {
    const { date, roomId } = req.query;
    const bookings = readBookingsFile();

    const filtered = bookings.filter(b => {
      if (date && b.date !== date) return false;
      if (roomId && String(b.roomId) !== String(roomId)) return false;
      return true;
    });

    return res.json(filtered);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to fetch bookings' });
  }
});

app.post('/bookings', (req, res) => {
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
    const allUsers = usersDB.select({}) || [];
    const creator = allUsers.find(u => u.id === creatorId);

    const notifications = readNotificationsFile();

    for (const invitedUserId of uniqueUserIds) {
      if (invitedUserId === creatorId) continue;

      const expiresAt = `${date} ${endTime}`;

      notifications.push({
        id: uuidv4(),
        userId: invitedUserId,
        title: 'New booking',
        message: `${creator?.fullName || 'Someone'} added you to a booking on ${date} from ${startTime} to ${endTime}`,
        createdAt: new Date().toISOString(),
        expiresAt,
        read: false,
        type: 'booking_invite',
        bookingId: newBooking.id
      });
    }

    writeNotificationsFile(notifications);

    return res.status(201).json(newBooking);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to create booking' });
  }
});

app.post('/bookings/:id', (req, res) =>
{
  try
   {
    const { id } = req.params;
    const { userIds } = req.body;

    if (!Array.isArray(userIds))
     {
      return res.status(400).json({ message: 'userIds must be an array' });
    }

    const bookings = readBookingsFile();
    const index = bookings.findIndex(b => b.id === id);

    if (index === -1)
    {
      return res.status(404).json({ message: 'Booking not found' });
    }
    bookings[index].userIds = [...new Set(userIds)];

    writeBookingsFile(bookings);

    console.log(`Booking ${id} got updated with new members:`, bookings[index].userIds);

    return res.json(bookings[index]);
  } catch (err)
   {
    console.error('Error with updating booking:', err);
    return res.status(500).json({ message: 'Failed to update booking' });
  }
});

app.delete('/bookings/:id', (req, res) =>
{
  try
   {
    const { id } = req.params;
    let bookings = readBookingsFile();

    const originalLength = bookings.length;
    bookings = bookings.filter(b => b.id !== id);

    if (bookings.length === originalLength)
     {
      return res.status(404).json({ message: 'Booking not found' });
    }

    writeBookingsFile(bookings);
    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to delete booking' });
  }
});

app.get('/users/:id/notifications', (req, res) => {
  try {
    const userId = req.params.id;
    console.log('Fetching notifications for user:', userId);

    const notifications = removeExpiredNotifications();

    const userNotifications = notifications
      .filter(n => n.userId === userId)
      .sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));

    return res.json(userNotifications);
  } catch (err) {
    console.error('Failed to fetch notifications:', err);
    return res.status(500).json({ message: 'Failed to fetch notifications' });
  }
});

app.post('/users/:id/notifications/:notificationId/read', (req, res) => {
  try {
    const { id, notificationId } = req.params;
    const notifications = readNotificationsFile();

    const index = notifications.findIndex(
      n => n.id === notificationId && n.userId === id
    );

    if (index === -1) {
      return res.status(404).json({ message: 'Notification not found' });
    }

    notifications[index].read = true;
    writeNotificationsFile(notifications);

    return res.sendStatus(204);
  } catch (err) {
    console.error(err);
    return res.status(500).json({ message: 'Failed to mark notification as read' });
  }
});

app.listen(3000, '0.0.0.0', () => {
  console.log('Server running on http://localhost:3000');
});