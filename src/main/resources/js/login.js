// User credentials store
const users = {
    admin: {username: 'admin', password: 'admin123', isAdmin: true},
};

// Function to validate user credentials
function validateCredentials(username, password) {
    const user = users[username];
    return user && user.password === password;
}

// Function to handle user login
function handleLogin(event) {
    event.preventDefault();
    const username = document.getElementById('username').value;
    const password = document.getElementById('password').value;

    if (validateCredentials(username, password)) {
        const user = users[username];
        if (user.isAdmin) {
            redirectToAdminDashboard();
        } else {
            redirectToUserDashboard();
        }
    } else {
        showError('Invalid credentials');
    }
}

// Function to show error message
function showError(message) {
    const errorDiv = document.getElementById('error-message');
    if (errorDiv) {
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
    }
}

// Function to redirect to admin dashboard
function redirectToAdminDashboard() {
    window.location.href = '/admin/dashboard';
}

// Function to redirect to user dashboard
function redirectToUserDashboard() {
    window.location.href = '/user/dashboard';
}

// Add event listener for login form
document.addEventListener('DOMContentLoaded', function () {
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
});

