const Category = require('../models/category');
const Movie = require('../models/movie');

// GET /api/categories - החזרת כל הקטגוריות
const getAllCategories = async (req, res) => {
    try {
        const categories = await Category.find();
        res.json(categories);
    } catch (error) {
        res.status(500).json({ error: error.message });
    }
};

// create new category
const createCategory = async (req, res) => {
    const { name } = req.body;
    // Validation
    if (!name || typeof name !== 'string') {
        return res.status(400).json({ error: 'Invalid input: name is required and must be a string' });
    }
    
    try {
        const category = new Category(req.body);
        await category.save();
        res.status(201).json(category).location(`${req.originalUrl}`);
    } catch (error) {
        res.status(400).json();
    }
};

// get category by id
const getCategoryById = async (req, res) => {
    try {
        const category = await Category.findById(req.params.id);
        if (!category) {
            return res.status(404).json();
        }
        res.json(category);
    } catch (error) {
        res.status(500).json();
    }
};

// update category no id updates
const updateCategoryById = async (req, res) => {
    try {
        const category = await Category.findByIdAndUpdate(req.params.id, req.body, { new: true, runValidators: true });
        if (!category) {
            return res.status(404).json();
        }
        res.json( category);
    } catch (error) {
        res.status(400).json({ error: error.message });
    }
};

// DELETE category
const deleteCategoryById = async (req, res) => {
    try {
        const category = await Category.findByIdAndDelete(req.params.id);
        if (!category) {
            return res.status(404).json();
        }

        // remove from movies
        await Movie.updateMany(
            { categories: req.params.id },          
            { $pull: { categories: req.params.id } } 
        );

        res.json({});
    } catch (error) {
        res.status(500).json();
    }
};
module.exports = {
    getAllCategories,
    createCategory,
    getCategoryById,
    updateCategoryById,
    deleteCategoryById
};
