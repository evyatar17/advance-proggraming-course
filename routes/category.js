const express = require('express');
const router = express.Router();
const categoryController = require('../controllers/category');

// Setting the paths for categories
router.get('/', categoryController.getAllCategories);
router.post('/', categoryController.createCategory);
router.get('/:id', categoryController.getCategoryById);
router.patch('/:id', categoryController.updateCategoryById);
router.delete('/:id', categoryController.deleteCategoryById);

module.exports = router;
