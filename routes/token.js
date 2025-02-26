const express = require('express');
const router = express.Router();
const tokensController = require('../controllers/tokens');

// POST /api/token
router.post('/', tokensController.authentication);

module.exports = router;
