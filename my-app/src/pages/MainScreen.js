import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../contexts/Authenticate';
import 'bootstrap/dist/css/bootstrap.min.css';
import '../styles/MainScreen.css';

import CategoryList from '../components/CategoryList';
import MovieList from '../components/Movielist';
import MovieInfo from '../components/MovieInfo';
import RandomMovie from '../components/RandomMovie';
import TopMenu from '../components/TopMenu';


const MainScreen = () => {
  const [movies, setMovies] = useState([]);
  const [categories, setCategories] = useState([]);
  const [watchedMovies, setWatchedMovies] = useState([]);
  const [recommendedMovies, setRecommendedMovies] = useState([]);
  const [theme, setTheme] = useState(localStorage.getItem('theme') || 'light');
  const [searchResults, setSearchResults] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const navigate = useNavigate();
  const { user, logout } = useAuth();

 

  useEffect(() => {
    console.log("MainScreen - User:", user);
    console.log("MainScreen - Is Authenticated:", !!user);
    console.log("MainScreen - Is Admin:", user?.role === "admin");


    fetchMoviesAndCategories();

    if (user) {
        fetchWatchedMovies(user._id); 
      }
  }, [user]);  

  // Fetch all movies & categories
  const fetchMoviesAndCategories = async () => {
    try {
        //  Fetch all categories
        const categoriesResponse = await fetch("http://localhost:3001/api/categories");
        if (!categoriesResponse.ok) throw new Error("Failed to fetch categories");

        const categoriesData = await categoriesResponse.json();
        setCategories(categoriesData);

        let allMovies = [];
        let categorizedMovies = {};

        // Loop through each category and fetch its movies
        for (const category of categoriesData) {
            const moviesResponse = await fetch(`http://localhost:3001/api/movies?category=${category._id}`);
            if (!moviesResponse.ok) continue;  // Skip if request fails

            const movies = await moviesResponse.json();
            categorizedMovies[category.name] = movies;
            allMovies = [...allMovies, ...movies];  // Combine all movies
        }

        setMovies(allMovies); 

    } catch (error) {
        console.error("Error fetching movies and categories:", error);
    }
};


  // Fetch watched movies for the current user
  const fetchWatchedMovies = async (userId) => {
    try {
        const token = localStorage.getItem("token");
        const response = await fetch(`http://localhost:3001/api/users/${userId}`, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });      
        if (!response.ok) {
            throw new Error('Failed to fetch user data');
        }

        const userData = await response.json();
        setWatchedMovies(userData.viewedMovies || []);  // Use viewedMovies

        // If user has watched movies, fetch recommendations
        if (userData.viewedMovies.length > 0) {
            const lastWatchedMovieId = userData.viewedMovies[userData.viewedMovies.length - 1];
            fetchRecommendedMovies(lastWatchedMovieId);
        }
    } catch (err) {
        console.error("Error fetching watched movies:", err);
    }
};

  const fetchRecommendedMovies = async (baseMovieId) => {
    try {
        const token = localStorage.getItem('token');
        const userId = localStorage.getItem('userId');
        if (!userId) {
            throw new Error("User ID is not available");
        }
        const url = `http://localhost:3001/api/movies/${baseMovieId}/recommend?userId=${userId}`;
        const response = await fetch(url, {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            }
        });
        const data = await response.json();
        
        if (!response.ok || !data.recommendedMovies) {
            console.warn("No recommended movies received:", data.message || response.statusText);
            setRecommendedMovies([]);
        } else {
            setRecommendedMovies(data.recommendedMovies || []);
        }
    } catch (error) {
        console.error("Error fetching recommended movies:", error);
        setRecommendedMovies([]);  // Prevent app crash
    }
  };

  // Theme toggle
  const toggleTheme = () => {
    const newTheme = theme === 'light' ? 'dark' : 'light';
    setTheme(newTheme);
    localStorage.setItem('theme', newTheme);
  };

  // Logout
  const handleLogout = () => {
    logout();  
    navigate('/login');  
};

  // Show movie info
  const handleMovieClick = (movieId) => {
    navigate(`/movies/${movieId}`);
  };

  // Category filter
  const handleCategoryClick = (categoryName) => {
    setSearchQuery(categoryName);
    setSearchResults(movies.filter(movie => movie.genre === categoryName));
} ;

  // Search
  const handleSearch = async (query) => {
    if (!query.trim()) {
        setSearchResults([]);
        setSearchQuery('');
        return;
    }

    setSearchQuery(query);
    try {
        const encodedQuery = encodeURIComponent(query);
        const response = await fetch(`http://localhost:3001/api/movies/search/${encodedQuery}`);

        if (!response.ok) {
            throw new Error("Failed to fetch search results");
        }
        
        const data = await response.json();
        setSearchResults(data);  // Store results in state
    } catch (error) {
        console.error("Error fetching search results:", error);
        setSearchResults([]);
    }
};


  const resetSearch = () => {
    setSearchQuery('');
    setSearchResults([]); 
  };


  return (
    <div className={`main-screen ${theme}`}>
      <TopMenu
        toggleTheme={toggleTheme}
        theme={theme}
        handleLogout={handleLogout}
        user={user}
        navigate={navigate}
        onSearch={handleSearch}
      />
      <div className="container-fluid">
        {searchQuery ? (
          searchResults.length > 0 ? (
            <div className="category-section">
              <div className="category-row">
                <h2 className="category-title">Search Results</h2>
                <MovieList movies={searchResults} handleMovieClick={handleMovieClick} />
              </div>
              <button className="btn btn-secondary" onClick={() => resetSearch()}>
                Back to Main Page
              </button>
            </div>
          ) : (
            <div className="alert alert-warning">
              No results found. Try searching for something else.
              <button className="btn btn-secondary" onClick={resetSearch}>
                Back to Main Page
              </button>
            </div>
          )
        ) : (
          <>
            <div className="hero-section">
              <RandomMovie movies={movies} />
            </div>

            <div className="category-section">
              <CategoryList
                categories={categories}
                movies={movies}
                handleCategoryClick={handleCategoryClick}
              />

              {watchedMovies.length > 0 && (
                <div className="category-row">
                  <h2 className="category-title">Your Watched Movies</h2>
                  <MovieList
                    movies={watchedMovies}
                    handleMovieClick={handleMovieClick}
                  />
                </div>
              )}

              {recommendedMovies.length > 0 && (
                <div className="category-row">
                  <h2 className="category-title">Recommended For You</h2>
                  <MovieList
                    movies={recommendedMovies}
                    handleMovieClick={handleMovieClick}
                  />
                </div>
              )}
              
              <div className="category-row">
                <h2 className="category-title">All Movies</h2>
                <MovieList
                  movies={movies}
                  handleMovieClick={handleMovieClick}
                />
              </div>
            </div>
          </>
        )}
      </div>
    </div>
  );
};

export default MainScreen;