import { createContext, useContext, useState, useEffect } from "react";

// Create the Authentication Context
const AuthContext = createContext(null);

// Provide Authentication State and Functions
export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);

    // Load user from localStorage when the app starts
    useEffect(() => {
        const storedUser = localStorage.getItem("user");
        if (storedUser) {
            setUser(JSON.parse(storedUser));  
        }
    }, []);

    // Login Function: Stores user data and token
    const login = (userData, token) => {
        setUser({ ...userData, role: userData.role });  
        localStorage.setItem("user", JSON.stringify({ ...userData, role: userData.role }));
        localStorage.setItem("token", token);
    };

    // Logout Function: Clears user data and token
    const logout = () => {
        setUser(null);
        localStorage.removeItem("user");
        localStorage.removeItem("token");
    };

    return (
        <AuthContext.Provider value={{ user, login, logout }}>
            {children}
        </AuthContext.Provider>
    );
};

// Custom Hook to Use Authentication Context
export const useAuth = () => {
    return useContext(AuthContext);
};
