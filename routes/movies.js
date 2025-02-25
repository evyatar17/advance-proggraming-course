const express = require('express');
const router = express.Router();
const movieController = require('../controllers/movies');
const RecommendationController = require('../controllers/recommendation');

const recommendationController = new RecommendationController();
// Setting the paths for movies
router.get('/',  movieController.getMoviesByCategory);
router.get('/:id',  movieController.getMovieById);
router.put('/:id',  movieController.updateMovieById);
router.delete('/:id',  movieController.deleteMovieById);
router.post('/', movieController.addMovie);
router.get('/:id/recommend', recommendationController.getRecommendations.bind(recommendationController));
router.post('/:id/recommend', recommendationController.addUserMovie.bind(recommendationController));

module.exports = router;
