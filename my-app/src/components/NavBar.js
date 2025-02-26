import { Link } from "react-router-dom";
import { useAuth } from "../contexts/Authenticate"; 

const Navbar = () => {
  const { user, logout } = useAuth();  
  return (
    <nav style={{ padding: "10px", borderBottom: "1px solid #ccc" }}>
      <Link to="/" style={{ marginRight: "15px" }}>Home</Link>
      {!user ? (
        <>
          <Link to="/login" style={{ marginRight: "15px" }}>Login</Link>
          <Link to="/register">Register</Link>
        </>
      ) : (
        <>
          <Link to="/mainScreen" style={{ marginRight: "15px" }}>Dashboard</Link>
          {user.role === "admin" && <Link to="/admin">Admin Panel</Link>}
          <button onClick={logout} style={{ marginLeft: "15px" }}>Logout</button>
        </>
      )}
    </nav>
  );
};

export default Navbar;
