const { v4: uuidv4 } = require('uuid');
const { Database } = require('../Database');

const {
  readGroupsFile,
  writeGroupsFile
} = require('../models/GroupsStore');

const {
  readNotificationsFile,
  writeNotificationsFile
} = require('../models/NotificationsStore');

const usersDB = Database.getInstance('users');

function normalizeGroup(group) {
  if (Array.isArray(group.participants)) return group;

  return {
    ...group,
    participants: (group.memberIds || []).map(userId => ({
      userId,
      status: 'accepted'
    }))
  };
}

function getGroups() {
  const data = readGroupsFile();
  data.groups = (data.groups || []).map(normalizeGroup);
  return data;
}

exports.createGroup = (req, res) => {
  try {
    const { name, creatorId, invitedUserIds } = req.body;

    const users = usersDB.select({});
    const creator = users.find(u => u.id === creatorId);

    if (!creator) return res.status(404).json({ message: 'Creator not found' });

    const groupsData = getGroups();
    const notifications = readNotificationsFile();

    const newGroup = {
      id: uuidv4(),
      name,
      participants: [
        { userId: creatorId, status: 'accepted' },
        ...(invitedUserIds || []).map(id => ({
          userId: id,
          status: 'pending'
        }))
      ]
    };

    groupsData.groups.push(newGroup);
    writeGroupsFile(groupsData);

    for (const userId of invitedUserIds || []) {
      notifications.push({
        id: uuidv4(),
        userId,
        title: 'Group invite',
        message: `${creator.fullName} invited you to join "${name}"`,
        createdAt: new Date().toISOString(),
        read: false,
        type: 'group_invite',
        groupId: newGroup.id
      });
    }

    writeNotificationsFile(notifications);

    res.status(201).json(newGroup);
  } catch {
    res.status(500).json({ message: 'Failed to create group' });
  }
};

exports.getUserGroups = (req, res) => {
  try {
    const { id } = req.params;
    const groups = getGroups().groups;

    const result = groups.filter(g =>
      g.participants.some(p => p.userId === id && p.status === 'accepted')
    );

    res.json(result);
  } catch {
    res.status(500).json({ message: 'Failed to fetch groups' });
  }
};

exports.inviteToGroup = (req, res) => {
  try {
    const { groupId } = req.params;
    const { invitedUserId } = req.body;

    const groupsData = getGroups();
    const group = groupsData.groups.find(g => g.id === groupId);

    if (!group) return res.status(404).json({ message: 'Group not found' });

    group.participants.push({
      userId: invitedUserId,
      status: 'pending'
    });

    writeGroupsFile(groupsData);
    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to invite' });
  }
};

exports.acceptInvite = (req, res) => {
  try {
    const { userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const groupsData = getGroups();

    const notification = notifications.find(n => n.id === notificationId);

    const group = groupsData.groups.find(g => g.id === notification.groupId);

    const participant = group.participants.find(p => p.userId === userId);
    if (participant) participant.status = 'accepted';

    writeGroupsFile(groupsData);
    res.json(group);
  } catch {
    res.status(500).json({ message: 'Failed to accept invite' });
  }
};

exports.declineInvite = (req, res) => {
  try {
    const { userId, notificationId } = req.params;

    const notifications = readNotificationsFile();
    const groupsData = getGroups();

    const notification = notifications.find(n => n.id === notificationId);
    const group = groupsData.groups.find(g => g.id === notification.groupId);

    const participant = group.participants.find(p => p.userId === userId);
    if (participant) participant.status = 'declined';

    writeGroupsFile(groupsData);
    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to decline invite' });
  }
};

exports.getGroup = (req, res) => {
  const groups = getGroups().groups;
  const group = groups.find(g => g.id === req.params.groupId);

  if (!group) return res.status(404).json({ message: 'Not found' });

  res.json(group);
};

exports.leaveGroup = (req, res) => {
  try {
    const { groupId, userId } = req.params;

    const groupsData = getGroups();
    const group = groupsData.groups.find(g => g.id === groupId);

    group.participants = group.participants.filter(p => p.userId !== userId);

    writeGroupsFile(groupsData);
    res.sendStatus(204);
  } catch {
    res.status(500).json({ message: 'Failed to leave group' });
  }
};