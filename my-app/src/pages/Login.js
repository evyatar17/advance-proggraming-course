import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/Authenticate";

function Login() {
    const navigate = useNavigate();
    const { login } = useAuth(); 

    const [formData, setFormData] = useState({
        username: "",
        password: "",
    });
    const [errors, setErrors] = useState({});

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData((prevData) => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleLogin = async (e) => {
        e.preventDefault();
        setErrors({});

        try {
            const response = await fetch("http://localhost:3001/api/token", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(formData),
            });

            console.log("Login API Response Status:", response.status); // 🔍 Log status code

            if (!response.ok) {
                const errorData = await response.json();
                setErrors({ server: errorData.error || "Login failed" });
                return;
            }

            const result = await response.json();
            const meResponse = await fetch(`http://localhost:3001/api/users/${result.userId}`);
            const meData = await meResponse.json();

            login(meData, result.token);

            if (meData.role && (meData.role === "admin" || meData.role === "user")) {
                navigate(meData.role.toLowerCase() === "admin" ? "/admin" : "/mainScreen");
            } else {
                console.error("Invalid user role detected.");
            }
        } catch (error) {
            setErrors({ server: "Network error, please try again." });
        }
    };

    return (
        <div className="login-container">
            <h2>Login</h2>
            <form onSubmit={handleLogin}>
                <input type="text" name="username" value={formData.username} onChange={handleChange} required />
                <input type="password" name="password" value={formData.password} onChange={handleChange} required />
                {errors.server && <p className="error">{errors.server}</p>}
                <button type="submit">Login</button>
            </form>
        </div>
    );
}

export default Login;
