
const mongoose = require('mongoose');
const movieSchema = new mongoose.Schema({
    ID: {
        type: Number,
        required: true,
        unique: true,
        trim: true
    },
    title: {
        type: String,
        required: true,
        trim: true
    },
    description: {
        type: String,
        trim: true,
        default: ''
    },
    categories: [{
        type: mongoose.Schema.Types.ObjectId,
        ref: 'Category',
        required: true,
    }],
    year: { 
        type: Number,
        trim: true,
        default: ''
    },
    director: {
        type: String,
        trim: true
    },
    image: {
        type: String,
        default: ''  
    },
    streamingUrl: {  
        type: String,
        required: true,
        trim: true,
        default: ''
    }
}, {
    timestamps: true
});

//index for searching movie name
movieSchema.index({
    title: 'text',
    description: 'text',
    director: 'text'
});

module.exports = mongoose.model('Movie', movieSchema);