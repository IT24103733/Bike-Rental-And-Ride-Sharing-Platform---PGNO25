document.addEventListener('DOMContentLoaded', function () {
    loadBookings();
});

async function loadBookings() {
    try {
        const response = await fetch('/api/bookings');
        const bookings = await response.json();
        displayBookings(bookings);
    } catch (error) {
        showError('Failed to load bookings');
    }
}

function displayBookings(bookings) {
    const bookingsList = document.getElementById('bookings-list');
    if (!bookingsList) return;

    bookingsList.innerHTML = '';
    bookings.forEach(booking => {
        const bookingElement = createBookingElement(booking);
        bookingsList.appendChild(bookingElement);
    });
}

function createBookingElement(booking) {
    const div = document.createElement('div');
    div.className = 'bike-card';
    div.innerHTML = `
        <h3>Booking #${booking.id}</h3>
        <div class="bike-details">
            <p>Bike: ${booking.bikeName}</p>
            <p>Start Date: ${new Date(booking.startDate).toLocaleDateString()}</p>
            <p>End Date: ${new Date(booking.endDate).toLocaleDateString()}</p>
            <p>Status: ${booking.status}</p>
        </div>
        <div class="bike-actions">
            <button class="btn btn-primary" onclick="cancelBooking(${booking.id})">Cancel</button>
        </div>
    `;
    return div;
}

async function createBooking(bikeId, startDate, endDate) {
    try {
        const response = await fetch('/api/bookings', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({bikeId, startDate, endDate})
        });

        if (!response.ok) throw new Error('Booking failed');

        showSuccess('Booking created successfully');
        loadBookings();
    } catch (error) {
        showError('Failed to create booking');
    }
}

async function cancelBooking(bookingId) {
    try {
        const response = await fetch(`/api/bookings/${bookingId}/cancel`, {
            method: 'PUT'
        });

        if (!response.ok) throw new Error('Cancellation failed');

        showSuccess('Booking cancelled successfully');
        loadBookings();
    } catch (error) {
        showError('Failed to cancel booking');
    }
}

function showError(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-danger';
    alertDiv.textContent = message;
    document.body.insertBefore(alertDiv, document.body.firstChild);
    setTimeout(() => alertDiv.remove(), 3000);
}

function showSuccess(message) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-success';
    alertDiv.textContent = message;
    document.body.insertBefore(alertDiv, document.body.firstChild);
    setTimeout(() => alertDiv.remove(), 3000);
}