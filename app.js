
// src/server.js
const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

// import routers
const userRoutes = require('./routes/users');
const categoryRoutes = require('./routes/category');
const movieRoutes = require('./routes/movies');
const tokenRoutes = require('./routes/token');
const movieSearchRoutes = require('./routes/movieSearch');

// import recommendation client
const RecommendationClient = require('./services/recommendationClient');

// create app of express
const app = express();


// basic definitions 
// Disable ETag for the output will be like the example
app.disable('etag');

// basic definitions 
const PORT = process.env.PORT || 3001;
const MONGODB_URI = process.env.MONGODB_URI || 'mongodb://localhost:27017/movies_db';

app.use(cors());                
app.use(express.json());        
app.use(express.urlencoded({ extended: true }));  


// connecting to mongoDB
mongoose.connect(MONGODB_URI)
    .then(() => console.log('Connected to MongoDB'))
    .catch((error) => {
        console.error('MongoDB connection error:', error);
        process.exit(1);
    });


// initialize recommendation client
const SERVER_HOST = process.env.SERVER_HOST || 'cpp_server';
const SERVER_PORT = process.env.SERVER_PORT || 8080;
const syncClient = new RecommendationClient(SERVER_HOST, SERVER_PORT);
syncClient.connect().catch((err) => {
    console.error('Failed to connect to C++ server:', err.message);
});


// use routes
app.use('/api/users', userRoutes);
app.use('/api/categories', categoryRoutes);
app.use('/api/movies', movieRoutes);
app.use('/api/token', tokenRoutes);
app.use('/api/search', movieSearchRoutes);

// sync route to handle updates to C++ server
app.post('/api/sync', async (req, res) => {
    const { action, userId, movieId } = req.body;

    try {
        // Validate action
        if (!['POST', 'DELETE', 'PATCH', 'GET'].includes(action.toUpperCase())) {
            return res.status(400).json({ error: 'Invalid action' });
        }

        // Construct command string
        const command = `${action.toUpperCase()} ${userId} ${movieId}`;
        const response = await syncClient.sendCommand(command);

        res.status(200).json({ message: 'Synchronized with C++ server', serverResponse: response });
    } catch (err) {
        console.error('Error synchronizing with C++ server:', err.message);
        res.status(500).json({ error: 'Synchronization failed', message: err.message });
    }
});

app.get('/', (req, res) => {
    res.json({ });
});


app.use((req, res) => {
    res.status(404).json({ error: '"app.use" Not Found' });
});

app.use((err, req, res, next) => {
    console.error(err.stack);
    res.status(500).json({ 
        error: 'Internal server error',
        message: err.message
    });
});

// run sever
app.listen(PORT, () => {
    console.log(`Server is running on port ${PORT}`);
});

// close server
process.on('SIGTERM', () => {
    console.log('SIGTERM signal received: closing HTTP server');
    mongoose.connection.close(() => {
        console.log('MongoDB connection closed');
        process.exit(0);
    });
});