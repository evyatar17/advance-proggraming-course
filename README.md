# 🎬 TMM4 – Movie Streaming Web Server

## Overview
**TMM4** is a full-stack movie streaming web application designed to provide a smooth, modern viewing experience.  
The platform allows users to register, log in, browse and watch movies, receive personalized recommendations, and — for authorized users — manage content through an admin dashboard.

---

## 🌐 Features
- **User Authentication** – Registration and login system with secure authorization.  
- **Movie Streaming** – Watch movies directly through the web interface.  
- **Home Page** – Clean and responsive homepage displaying featured content.  
- **Admin Panel** – Manage users, movies, and platform content (accessible only to admins).  
- **Recommendation System** – All users receive movie suggestions based on preferences, viewing history, or popularity.  
- **Navigation Bar** – Easy access to all main pages (Home, Movies, Profile, Admin, etc.).  

---

## 🧩 Tech Stack
| Layer | Technology |
|-------|-------------|
| **Frontend** | React.js, HTML5, CSS3, JavaScript |
| **Backend** | Node.js, Express.js |
| **Database** | MongoDB (via Mongoose) |
| **DevOps** | Docker, Docker Compose |
| **Mobile** | Android Studio (companion Android app) |
| **Version Control** | Git & GitHub |

---

## ⚙️ Project Structure
```
TMM4/
├── client/              # React frontend
├── server/              # Node.js backend (Express)
├── androidAPP/          # Android Studio project
├── docker-compose.yml   # Docker setup for services
└── README.md            # Project documentation
```

---

## 🚀 Getting Started

### Prerequisites
- Node.js 18+
- MongoDB running locally or in Docker
- Docker Desktop (optional but recommended)

### Installation
```bash
# Clone the repository
git clone https://github.com/evyatar17/TMM4.git
cd TMM4

# Install dependencies
cd server && npm install
cd ../client && npm install
```

### Running Locally
```bash
# Option 1 – using Docker
docker-compose up --build

# Option 2 – manual start
cd server && npm start
cd ../client && npm start
```

---

## 🧠 Future Improvements
- Add JWT-based authentication across mobile and web.  
- Enhance recommendation algorithm using collaborative filtering or ML.  
- Add search and filtering by genre or rating.  
- Improve mobile app integration with backend API.  
- Add admin analytics dashboard.

---

## 🧑‍💻 Authors
- **Evyatar Amir**  
- **Team Members:** [add collaborators here]

---

## 📜 License
This project is released under the **MIT License**.
