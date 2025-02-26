const User = require('../models/user'); 

// GET /api/users/:id - returning the user object using the users id as reference
const getUserById = async (req, res, next) => {
  try {
    const user = await User.findById(req.params.id).select('-password'); 
    
    if (!user) {
      return res.status(404).json( );
    }

    res.json(user);
  } catch (err) {
    if (err.kind === 'ObjectId') {
      return res.status(400).json( );
    }
    next(err);
  }
};

// create new user
const createUser = async (req, res, next) => {
  console.log("Received registration data:", req.body);  

  const { FirstName, LastName, username, password, profileImage } = req.body;
  // Validation
  if (!username || typeof username !== 'string' || !password || typeof password !== 'string') {
      return res.status(400).json({ error: 'Invalid input: username and password are required and must be strings' });
  }

  //checking if username is taken
  const existingUser = await User.findOne({ username });
  if (existingUser) return res.status(400).json({ error: 'Username already taken' });
  
  const lastUser = await User.findOne().sort({ ID: -1 }); // Find the user with the highest ID
  const newID = lastUser ? lastUser.ID + 1 : 1; // Increment ID or start from 1

  try {
    const newUser = new User({
      ID: newID,
      FirstName,  
      LastName,   
      username,
      password,
      image: profileImage || "" 
  });

    await newUser.save();
    res.status(201).json(newUser);
  } catch (err) {
    console.error("Error creating user:", err);
    if (err.name === 'ValidationError') {
      return res.status(400).json( );
    }
    next(err);
  }
};
module.exports = {
  getUserById,
  createUser
  }