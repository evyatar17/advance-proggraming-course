import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/Authenticate';
import '../styles/AdminPanel.css';

const AdminPanel = () => {
  const [categories, setCategories] = useState([]);
  const [movies, setMovies] = useState([]);
  const [newCategoryName, setNewCategoryName] = useState('');
  const [newMovie, setNewMovie] = useState({ 
    title: '', 
    categories: '', 
    description: '', 
    year: '', 
    streamingUrl: '', 
    thumbnail: '', 
    director: '' 
  });
  const navigate = useNavigate();
  const { user } = useAuth();

  useEffect(() => {
    if (!user || user.role !== "admin") {
      navigate("/");  
    }
  }, [user, navigate]);

  // Function to handle API requests
  const fetchData = async (url, options = {}) => {
    const token = localStorage.getItem('token');
    const headers = {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };

    try {
      const response = await fetch(url, { ...options, headers });
      if (!response.ok) throw new Error(`Network response was not ok: ${response.status}`);

      const text = await response.text();
      return text ? JSON.parse(text) : null; // Handle empty response
    } catch (error) {
      console.error(`Error fetching data from ${url}:`, error);
    }
  };

  useEffect(() => {
    fetchCategories();
    fetchMovies();
  }, []);

  const fetchCategories = () => {
    fetchData("http://localhost:3001/api/categories").then(data => {
      console.log("Fetched categories:", data);
      setCategories(data || []);
    });
  };

  const fetchMovies = async () => {
    try {
        const categoriesResponse = await fetch(`http://localhost:3001/api/categories`);
        if (!categoriesResponse.ok) throw new Error("Failed to fetch categories");
        const categories = await categoriesResponse.json();

        let allMovies = [];
        for (const category of categories) {
            const moviesResponse = await fetch(`http://localhost:3001/api/movies?category=${category._id}`);
            if (moviesResponse.ok) {
                const movies = await moviesResponse.json();
                allMovies = [...allMovies, ...movies];  
            }
        }

        setMovies(allMovies);  
    } catch (error) {
        console.error("Error fetching movies:", error);
    }
};


  const handleAddCategory = () => {
    if (!newCategoryName.trim()) {
        console.error("Category name cannot be empty");
        return;
    }

    fetchData("http://localhost:3001/api/categories", {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: newCategoryName, promoted: false }),
    }).then(response => {
        if (!response) {
            console.error("Error: No response received when adding category.");
            return;
        }
        console.log("Category added successfully:", response);
        setCategories(prevCategories => [...prevCategories, response]); 
        setNewCategoryName('');
    }).catch(error => console.error("Error adding category:", error));
  };


  const handleEditCategory = (id) => {
    if (!id) {
        console.error("Error: Category ID is undefined when attempting to edit a category.");
        return;
    }

    const newName = prompt('Enter new category name:');
    if (newName) {
        fetchData(`http://localhost:3001/api/categories/${id}`, {
            method: 'PUT',
            body: JSON.stringify({ name: newName, promoted: false }),
        }).then(response => {
            if (!response) {
                console.error("Error: No response received when updating category.");
                return;
            }
            fetchCategories();  // Refresh categories after successful update
        }).catch(error => console.error("Error updating category:", error));
    }
};

  const handleDeleteCategory = (id) => {
    fetchData(`http://localhost:3001/api/categories/${id}`, { method: 'DELETE' })
      .then(() => fetchCategories());
  };

  const handleAddMovie = () => {
    if (!newMovie.title || !newMovie.categories || !newMovie.description || !newMovie.streamingUrl) {
        console.error("Missing required fields for new movie");
        return;
    }

    const categoryList = newMovie.categories.split(",").map(category => category.trim());
    if (categoryList.length === 0) {
        console.error("At least one category is required.");
        return;
    }

    const movieData = {
        title: newMovie.title,
        categories: categoryList,
        year: newMovie.year || "2024",
        description: newMovie.description,
        streamingUrl: newMovie.streamingUrl, 
        thumbnail: newMovie.thumbnail || "",
        director: newMovie.director || "Unknown", 
    };

    console.log("Sending movie data:", movieData);

    fetchData("http://localhost:3001/api/movies", {
        method: 'POST',
        body: JSON.stringify(movieData),
    })
    .then(response => {
        console.log("Movie added successfully:", response);
        setNewMovie({ title: '', categories: '', description: '', year: '', streamingUrl: '', thumbnail: '', director: '' });
        fetchMovies();
    })
    .catch(error => console.error("Error adding movie:", error));
};


  const handleEditMovie = (id) => {
    if (!id) {
      console.error("Error: Movie ID is undefined when attempting to edit a movie.");
      return;
    }

    const newTitle = prompt('Enter new movie title:');
    const newCategories = prompt('Enter new movie categories (comma-separated):');
    const newDescription = prompt('Enter new movie description:');

    if (newTitle && newCategories && newDescription) {
      fetchData(`http://localhost:3001/api/movies/${id}`, {
        method: 'PUT',
        body: JSON.stringify({ title: newTitle, categories: newCategories, description: newDescription }),
      }).then(() => fetchMovies())
      .catch(error => console.error("Error updating movie:", error));
    }
  };

  const handleDeleteMovie = (id) => {
    if (!id) {
      console.error("Error: Movie ID is undefined when attempting to delete a movie.");
      return;
    }

    fetchData(`http://localhost:3001/api/movies/${id}`, { method: 'DELETE' })
      .then(() => fetchMovies())
      .catch(error => console.error("Error deleting movie:", error));
  };

  return (
    <div className="admin-panel">
      <h1>Admin Panel</h1>
      {/* Back to Main Button */}
      <button className="btn btn-secondary" onClick={() => navigate('/mainScreen')}>
        ← Back to Main Screen
      </button>
      <div>
        <h2>Categories</h2>
        <input
          type="text"
          value={newCategoryName}
          onChange={(e) => setNewCategoryName(e.target.value)}
          placeholder="New category name"
        />
        <button onClick={handleAddCategory}>Add Category</button>
        <ul>
          {categories.map(category => (
            <li key={category.id || category._id}>
              {category.name}
              <button onClick={() => handleEditCategory(category.id || category._id)}>Edit</button>
              <button onClick={() => handleDeleteCategory(category.id || category._id)}>Delete</button>
            </li>
          ))}
        </ul>
      </div>
      <div>
      <h2>Movies</h2>
        <input
            type="text"
            value={newMovie.title}
            onChange={(e) => setNewMovie({ ...newMovie, title: e.target.value })}
            placeholder="Movie Title (required)"
        />
        <input
            type="text"
            value={newMovie.categories}
            onChange={(e) => setNewMovie({ ...newMovie, categories: e.target.value })}
            placeholder="Movie Categories (comma-separated)"
        />
        <input
            type="text"
            value={newMovie.description}
            onChange={(e) => setNewMovie({ ...newMovie, description: e.target.value })}
            placeholder="Movie Description"
        />
        <input
            type="number"
            value={newMovie.year}
            onChange={(e) => setNewMovie({ ...newMovie, year: e.target.value })}
            placeholder="Release Year"
        />
        <input
            type="text"
            value={newMovie.director}
            onChange={(e) => setNewMovie({ ...newMovie, director: e.target.value })}
            placeholder="Director"
        />
        <input
            type="text"
            value={newMovie.image}
            onChange={(e) => setNewMovie({ ...newMovie, image: e.target.value })}
            placeholder="Thumbnail URL"
        />
        <input
            type="text"
            value={newMovie.streamingUrl}
            onChange={(e) => setNewMovie({ ...newMovie, streamingUrl: e.target.value })}
            placeholder="Streaming URL (required)"
        />
        <button onClick={handleAddMovie}>Add Movie</button>
        <ul>
          {movies.map(movie => (
            <li key={movie.id || movie._id}>
              <strong>{movie.title}</strong> ({movie.year})  
              <br /> <em>Directed by: {movie.director}</em>
              <br /> Categories: {movie.categories?.join(", ")}
              <br /> <img src={movie.image} alt={movie.title} width="100px" />
              <br /> <a href={movie.streamingUrl} target="_blank" rel="noopener noreferrer">Watch Movie</a>
              <button onClick={() => handleEditMovie(movie.id || movie._id)}>Edit</button>
              <button onClick={() => handleDeleteMovie(movie.id || movie._id)}>Delete</button>
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default AdminPanel;