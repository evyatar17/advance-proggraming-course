// src/routes/token.routes.js
const express = require('express');
const User = require('../models/user');
const jwt = require('jsonwebtoken');

// POST /api/token
const authentication = async (req, res) => {
    try {
        console.log("Request Body:", req.body); 

        const { username, password } = req.body;

        if (!username || typeof username !== 'string' || !password || typeof password !== 'string') {
            console.error("Invalid input received");
            return res.status(400).json({ error: "Invalid input: username and password are required and must be strings" });
        }
        
        console.log("🔍 Searching for user in database...");

        // חיפוש המשתמש במסד הנתונים
        const user = await User.findOne({ username });

        // בדיקה אם המשתמש קיים
        if (!user) {
            console.error("User not found:", username);
            return res.status(404).json({ error: "User not found" });
        }

        console.log("User found:", user.username);
        console.log("🔍 Checking password...");

        // בדיקת הסיסמה
        if (user.password !== password) {
            console.error("Incorrect password for user:", username);
            return res.status(401).json({ error: "Incorrect password" });
        }
        
        console.log("Password matched, generating token...");

        const token = jwt.sign(
            { userId: user._id, role: user.role }, 
            process.env.JWT_SECRET,  // Uses secret from .env
            { expiresIn: "1h" }
        );

        //returns user id and token
        console.log("Login successful. Sending response...");
        res.status(200).json({ userId: user._id, token });

    } catch (error) {
        console.error("Error during authentication:", error);
        res.status(500).json();
    }
};

// src/middleware/auth.middleware.js
const authMiddleware = (req, res, next) => {
    
    const userId = req.header('X-User-Id');
    
    if (!userId) {
        return res.status(401).json({
            error: 'Authentication required. Please provide X-User-Id header'
        });
    }

    req.userId = userId;
    next();
};

module.exports = {
authentication,
authMiddleware
};