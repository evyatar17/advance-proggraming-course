
const mongoose = require('mongoose');
const userSchema = new mongoose.Schema({ 
    // id that we use for recommendation system
    ID: {
        type: Number,
        required: true,
        unique: true,
        trim: true
    },
    username: {
        type: String,
        required: true,
        unique: true,
        trim: true
    },
    password: {
        type: String,
        required: true
    },
    FirstName: {
        type: String,
        required: true,
        trim: true
    },
    LastName: {
        type: String,
        required: true,
        trim: true
    },
    viewedMovies: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Movie'
    }],
    image: {
        type: String,
        default: ''  // empty string
    },
    role: {
         type: String, enum: ["user", "admin"],
          default: "user"
    }
});

module.exports = mongoose.model('User', userSchema);
