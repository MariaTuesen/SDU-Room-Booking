const fs = require('fs');
const path = require('path');

exports.getRooms = (req, res) => {
  try {
    const roomsFilePath = path.join(__dirname, '..', 'data', 'rooms.json');

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

    res.json(normalized);
  } catch (err) {
    res.status(500).json({ message: 'Failed to load rooms' });
  }
};