import { BrowserRouter as Router, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./contexts/Authenticate";
import Home from "./pages/Home";
import Login from "./pages/Login";
import Register from "./pages/Register";
import MainScreen from "./pages/MainScreen";
import AdminPanel from "./pages/AdminPanel";
import Navbar from "./components/NavBar";
import MovieInfo from "./components/MovieInfo";
import MoviePlayer from "./components/MoviePlayer";
import 'bootstrap/dist/css/bootstrap.min.css';


function App() {
    const { user } = useAuth();

    return (
            <Router>
                <Navbar />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/mainScreen" element={<MainScreen />} />
                    <Route path="/admin" element={user?.role === "admin" ? <AdminPanel /> : <Navigate to="/login" />} />
                    <Route path="/movies/:id" element={<MovieInfo />} />  
                    <Route path="/player/:id" element={<MoviePlayer />} />
                    <Route path="/movies/:id/watch" element={<MoviePlayer />} />
                </Routes>
            </Router>
    );
}

export default App;
