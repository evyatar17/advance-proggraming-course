const express = require('express');
const mongoose = require('mongoose');
const bodyParser = require('body-parser');
const cors = require('cors');

const app = express();
const PORT = process.env.PORT || 3000;

// Middleware++
app.use(cors());
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

// Connect to MongoDB
mongoose.connect('mongodb://localhost:27017/moviedb', {
    useNewUrlParser: true,
    useUnifiedTopology: true
})
.then(() => console.log('Connected to MongoDB'))
.catch(err => console.error('MongoDB connection error:', err));

// Define schemas
const UserSchema = new mongoose.Schema({
    email: { type: String, required: true, unique: true },
    firstName: { type: String, required: true },
    lastName: { type: String, required: true },
    username: { type: String, required: true, unique: true },
    password: { type: String, required: true }
});


const categorySchema = new mongoose.Schema({
    name: { type: String, required: true, unique: true },
    description: { type: String }
});

const movieSchema = new mongoose.Schema({
    title: { type: String, required: true },
    director: { type: String },
    releaseYear: { type: Number },
    description: { type: String },
    category: { type: mongoose.Schema.Types.ObjectId, ref: 'Category' },
    rating: { type: Number, min: 0, max: 10 },
    posterUrl: { type: String }
});

// Create models
const User = mongoose.model("User", UserSchema);
const Category = mongoose.model('Category', categorySchema);
const Movie = mongoose.model('Movie', movieSchema);

//const router = express.Router();


app.post('/api/users/register', async (req, res) => {
    try {
        const { email, firstName, lastName, username, password } = req.body;

        if (!email || !firstName || !lastName || !username || !password) {
            return res.status(400).json({ error: "All fields are required" });
        }

        // Check if user already exists
        const existingUser = await User.findOne({ email });
        if (existingUser) {
            return res.status(400).json({ error: "User already exists" });
        }

        // Create new user with unique _id
        const newUser = new User({ _id: new mongoose.Types.ObjectId(), email, firstName, lastName, username, password });
        await newUser.save();

        //res.status(201).json({ message: "User registered successfully", user: newUser });
        res.status(201).json(newUser).location(`${req.originalUrl}`);

    } catch (error) {
        console.error("Signup Error:", error);
        if (res.headersSent) return;
        res.status(500).json({ error: "Error registering user", details: error.message });
    }
});

// ✅ Login Route
app.post('/api/users/login', async (req, res) => {
    try {
        const { username, password } = req.body;

        // Check if user exists
        const user = await User.findOne({ username });
        if (!user) {
            return res.status(400).json({ error: "Invalid credentials" });
        }

        // Compare passwords (no hashing, assuming plaintext)
        if (password !== user.password) {
            return res.status(400).json({ error: "Invalid credentials" });
        }

        res.json({ message: "Login successful", user });

    } catch (error) {
        if (res.headersSent) return;
        res.status(500).json({ error: "Error logging in" });
    }
});

// Category Routes
app.get('/api/categories', async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.get('/api/categories/:id', async (req, res) => {
    try {
        const category = await Category.findById(req.params.id);
        if (!category) return res.status(404).json({ error: 'Category not found' });
        res.json(category);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/categories', async (req, res) => {
    try {
        const category = new Category(req.body);
        await category.save();
        res.status(201).json(category);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

app.put('/api/categories/:id', async (req, res) => {
    try {
        const category = await Category.findByIdAndUpdate(
            req.params.id, 
            req.body,
            { new: true, runValidators: true }
        );
        if (!category) return res.status(404).json({ error: 'Category not found' });
        res.json(category);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

app.delete('/api/categories/:id', async (req, res) => {
    try {
        // Check if any movies are using this category
        const movieCount = await Movie.countDocuments({ category: req.params.id });
        if (movieCount > 0) {
            return res.status(400).json({ 
                error: 'Cannot delete category with associated movies' 
            });
        }
        
        const category = await Category.findByIdAndDelete(req.params.id);
        if (!category) return res.status(404).json({ error: 'Category not found' });
        res.json({ message: 'Category deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Movie Routes
app.get('/api/movies', async (req, res) => {
    try {
        const movies = await Movie.find().populate('category');
        res.json(movies);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.get('/api/movies/:id', async (req, res) => {
    try {
        const movie = await Movie.findById(req.params.id).populate('category');
        if (!movie) return res.status(404).json({ error: 'Movie not found' });
        res.json(movie);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

app.post('/api/movies', async (req, res) => {
    try {
        const movie = new Movie(req.body);
        await movie.save();
        res.status(201).json(movie);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

app.put('/api/movies/:id', async (req, res) => {
    try {
        const movie = await Movie.findByIdAndUpdate(
            req.params.id, 
            req.body,
            { new: true, runValidators: true }
        );
        if (!movie) return res.status(404).json({ error: 'Movie not found' });
        res.json(movie);
    } catch (err) {
        res.status(400).json({ error: err.message });
    }
});

app.delete('/api/movies/:id', async (req, res) => {
    try {
        const movie = await Movie.findByIdAndDelete(req.params.id);
        if (!movie) return res.status(404).json({ error: 'Movie not found' });
        res.json({ message: 'Movie deleted successfully' });
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Start server
app.listen(PORT, () => {
    console.log(`Server running on port ${PORT}`);
});