const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const { Database } = require('./Database');
const { v4: uuidv4 } = require('uuid');
const path = require('path');
const multer = require('multer');
const fs = require('fs');

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
      // fallback to original name extension
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
    usersDB.update({ id: userId }, { profile_picture: profilePath });
    return;
  }


  const usersFilePath = path.join(__dirname, 'data', 'users.json');

  if (!fs.existsSync(usersFilePath)) {

    throw new Error(
      `No users file found at ${usersFilePath}. Paste your Database.js and I’ll adapt persistence to your exact storage.`
    );
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

app.post('/auth/signup', (req, res) => {
  try {
    const { fullName, email, password } = req.body;

    if (!fullName || !email || !password) {
      return res.status(400).json({ message: 'Missing fields' });
    }

    if (!email.endsWith("@student.sdu.dk")) {
      console.log("Rejected signup: Not SDU email", email);
      return res.status(400).json({
        message: "Only SDU students can create an account"
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

    console.log("Signup successful:", newUser.email);
    return res.status(201).json({
      message: 'User created successfully',
      user: newUser
    });

  } catch (err) {
    console.error(err);
    res.status(500).json({ message: "Server error" });
  }
});

app.post('/auth/login', (req, res) => {
  const { email, password } = req.body;
  if (!email || !password) return res.status(400).json({ message: 'Missing credentials' });

  const user = usersDB.select({ email, password })[0];
  if (!user) return res.status(401).json({ message: 'Invalid credentials' });

  res.json(user);
});

app.listen(3000, '0.0.0.0', () => console.log('Server running on http://localhost:3000'));