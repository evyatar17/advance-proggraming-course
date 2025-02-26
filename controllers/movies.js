const Movie = require('../models/movie');
const Category = require('../models/category');
const User = require('../models/user');
const RecommendationClient = require('../services/recommendationClient');
const mongoose = require('mongoose');

// Get movies by category with special conditions
const getMoviesByCategory = async (req, res) => {
    try {
        // read header
        const userId = req.header('User-ID'); 
        if (!userId) {
            return res.status(400).json();
        }
        // find promoted categories
        const promotedCategories = await Category.find({ promoted: true });
        if (promotedCategories.length === 0) {
            return res.status(404).json();
        }

        const user = await User.findById(userId);
        if (!user) {
            return res.status(404).json();
        }
        const viewedMovieIds = user.viewedMovies;

        const results = [];

        for (const category of promotedCategories) {
            const movies = await Movie.aggregate([
                { 
                    $match: { 
                        categories: { $in: [category._id] }, 
                        _id: { $nin: viewedMovieIds }       
                    } 
                },
                { $sample: { size: 20 } } 
            ]);

            results.push({
                category: category.name,
                movies: movies
            });
        }
        const lastMovies = await Movie.find({ _id: { $in: user.viewedMovies.slice(-20) } }); // 20 last watched movies
     return res.status(200).json({results,lastMovies});

    } catch (error) {
        res.status(500).json();
    }
};

const addMovie = async (req, res) => {
    const { title, description, categories, year, director, image, streamingUrl } = req.body;

    // Validation: Ensure all required fields are provided
    if (!title || typeof title !== 'string') {
        return res.status(400).json({ error: 'Invalid input: Title is required and must be a string' });
    }
    if (!streamingUrl || typeof streamingUrl !== 'string') {
        return res.status(400).json({ error: 'Invalid input: streamingUrl is required and must be a string' });
    }
    if (!categories || !Array.isArray(categories) || categories.length === 0) {
        return res.status(400).json({ error: 'Invalid input: At least one category is required (array of IDs)' });
    }

    try {

        //Convert category IDs to ObjectIds (Ensure they are valid)
        const categoryIds = [...new Set( // Remove duplicates
            categories
            .filter(cat => mongoose.Types.ObjectId.isValid(cat)) // Ensure IDs are valid
            .map(cat => mongoose.Types.ObjectId(cat)) // Convert to ObjectId
        )];

        //Ensure at least one valid category remains
        if (categoryIds.length === 0) {
            return res.status(400).json({ error: 'Invalid input: No valid category IDs provided.' });
        }
        // Auto-generate unique ID based on the highest existing ID
        const lastMovie = await Movie.findOne().sort({ ID: -1 });
        const newID = lastMovie ? lastMovie.ID + 1 : 1;

        const newMovie = new Movie({
            ID: newID,  // Unique ID
            title,
            description: description || '',
            categories: categoryIds,  
            year: year || new Date().getFullYear(), 
            director: director || 'Unknown',
            image: image || '',
            streamingUrl // Now required
        });

        await newMovie.save();
        res.status(201).json(newMovie);
    } catch (error) {
        console.error("Error adding movie:", error);
        res.status(400).json({ error: error.message });
    }
};


// Get movie by ID
const getMovieById = async (req, res) => {
    try {
        const movie = await Movie.findById(req.params.id);
        if (!movie) {
            return res.status(404).json({ error: 'Movie not found' });
        }
        res.location($`{req.originalUrl}`);
        res.status(200).json(movie);
    } catch (error) {
        res.status(500).json({ error: 'Failed to fetch the movie' });
    }
};

// Update movie by ID
const updateMovieById = async (req, res) => {
    try {
        const movie = await Movie.findByIdAndUpdate(req.params.id, req.body, {
            new: true,
            runValidators: true
        });
        if (!movie) {
            return res.status(404).json();
        }
        res.status(200).json({ message: 'Movie updated successfully', movie });
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};

const deleteMovieById = async (req, res) => {

    try {
        const movieId = req.params.id;

        // Validate and cast movieId to ObjectId
        if (!mongoose.Types.ObjectId.isValid(movieId)) {
            return res.status(400).json({ error: 'Invalid movie ID' });
        }

        const movie = await Movie.findByIdAndDelete(movieId);
        if (!movie) {
            return res.status(404).json({ error: 'Movie not found' });
        }

        // Find all affected users
        const affectedUsers = await User.find({ viewedMovies: req.params.id });

        // Update all users' watch history in MongoDB
        await User.updateMany(
            { viewedMovies: req.params.id },
            { $pull: { viewedMovies: req.params.id } }
        );

        // Notify the C++ server for each affected user
        for (const user of affectedUsers) {
            try {
                // Create a new RecommendationClient instance
                const recommendationClient = new RecommendationClient();
                await recommendationClient.connect(); // Open a new connection
                const command = `DELETE ${user.ID} ${movie.ID}`;
                await recommendationClient.sendCommand(command);
                recommendationClient.close(); // Close the connection
            } catch (err) {
                console.error(`Error notifying C++ server for user ${user.ID}:`, err);
            }
        }

        res.status(200).json({
            message: 'Movie deleted successfully and users updated',
            affectedUsers: affectedUsers.map((user) => user._id),
        });
    } catch (error) {
        console.error('Error deleting movie:', error);
        res.status(500).json({ error: 'Failed to delete the movie' });
    }
};




module.exports = {
    getMoviesByCategory,
    addMovie,
    getMovieById,
    updateMovieById,
    deleteMovieById
};
