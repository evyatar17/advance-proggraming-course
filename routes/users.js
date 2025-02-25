const express = require('express');
const router = express.Router();
const userController = require('../controllers/users');

// הגדרת הנתיבים למשתמשים
router.get('/:id', userController.getUserById);
router.post('/', userController.createUser);

module.exports = router;
