const express = require('express');
const router = express.Router();
const RecommendationController = require('../controllers/recommendation');

const recommendationController = new RecommendationController();

router.get('/:id/recommend', recommendationController.getRecommendations.bind(recommendationController));
router.post('/:id/recommend', recommendationController.addUserMovie.bind(recommendationController));

module.exports = router;