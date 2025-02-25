const express = require('express');
const router = express.Router();
const movieSearchController = require('../controllers/movieSearch');

// Setting the path
router.get('/search/:query', movieSearchController.searchMovies);
module.exports = router;
