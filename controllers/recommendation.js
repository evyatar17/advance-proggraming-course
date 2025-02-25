const RecommendationClient = require('../services/recommendationClient');
const Movie = require('../models/movie');
const User = require('../models/user');
const mongoose = require('mongoose'); 

class RecommendationController {
    constructor() {
        this.client = new RecommendationClient();
    }

    async getRecommendations(req, res) {
        try {
            const userId = req.header('User-ID'); // Extract user ID from header
            const movieId = req.params.id; // Extract movie ID from URL

            // Validate input
            if (!userId || !movieId || !mongoose.Types.ObjectId.isValid(userId) || !mongoose.Types.ObjectId.isValid(movieId)) {
                return res.status(400).json({ error: 'Invalid input', message: 'userId and movieId must be valid ObjectIds' });
            }

            // Validate user and movie exist
            const user = await User.findById(userId).populate('viewedMovies');
            const movie = await Movie.findById(movieId);

            if (!user || !movie) {
                return res.status(404).json({ error: 'User or movie not found' });
            }

            // Connect to recommendation server
            await this.client.connect();
            
            // Get recommendations
            const response = await this.client.sendCommand(`GET ${user.ID} ${movie.ID}`);
            // Parse response and get movie details from MongoDB
            const movieIds = response.movies;

            const recommendations = await Movie.find({
                '_id': { $in: movieIds }
            });

            this.client.close();
            
            res.json(recommendations);

        } catch (error) {
            console.error('Recommendation error:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }

    async addUserMovie(req, res) {
        try {
            const userId = req.header('User-ID'); // Extract user ID from header
            const movieId = req.params.id; // Extract movie ID from URL

    
            // Validate input
            if (!userId || !movieId || !mongoose.Types.ObjectId.isValid(userId) || !mongoose.Types.ObjectId.isValid(movieId)) {
                return res.status(400).json({ error: 'Invalid input', message: 'userId and movieId must be valid ObjectIds' });
            }

            // Validate user and movie exist
            const user = await User.findById(userId).populate('viewedMovies');
            const movie = await Movie.findById(movieId);
    
            if (!user || !movie) {
                return res.status(404).json({ error: 'User or movie not found' });
            }

            // Check if the user already has movies in their list
            const hasMovies = user.viewedMovies && user.viewedMovies.length > 0;
            console.log('Has movies:', hasMovies);

            // Ensure `viewedMovies` is updated
            if (!user.viewedMovies.some((viewedMovie) => viewedMovie.equals(movie._id))) {
                user.viewedMovies.push(movie._id);
                await user.save();
                console.log('Updated user viewedMovies:', user.viewedMovies);
            }

            // Connect to recommendation server
            await this.client.connect();
            try {
                // Determine command based on whether the user already has movies
                const commandType = hasMovies ? 'PATCH' : 'POST';
                console.log(`Sending ${commandType} command to C++ server`);
                const response = await this.client.sendCommand(`${commandType} ${user.ID} ${movie.ID}`);
                
                // This is correct because response is now an object
                if (response.status === 200) {
                    res.status(201).json({ message: 'Movie added to recommendations' });
                } else {
                    res.status(response.status).json({ error: response.error || 'Failed to add movie' });
                }
            } finally {
                this.client.close();
            }
    
        } catch (error) {
            console.error('Add movie error:', error);
            res.status(500).json({ error: 'Internal server error' });
        }
    }
    
}
module.exports = RecommendationController;