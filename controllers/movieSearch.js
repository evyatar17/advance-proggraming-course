const Movie = require('../models/movie');

// פונקציה שמטפלת בחיפוש סרטים לפי כותרת
const searchMovies = async (req, res) => {
    const query = req.params.query;

    // Validation
    if (!query || typeof query !== 'string') {
        return res.status(400).json({ error: 'Invalid query: must be a non-empty string' });
    }

    try {
        // the search is sensetive for capitals letters.
        const searchRegex = new RegExp(query);
        const movies = await Movie.find({
            $or: [
                { title: { $regex: searchRegex } }  
            ]
        });

        if (movies.length === 0) {
            return res.status(404).json();
        }

        res.status(200).json(movies);

    } catch (err) {
        res.status(500).json( );
    }
};
module.exports = { 
    searchMovies
    };