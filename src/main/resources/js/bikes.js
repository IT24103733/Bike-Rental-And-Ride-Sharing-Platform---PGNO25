$(document).ready(function () {
    loadBikes();

    // Event handlers
    $('#bikeList').on('click', '.rent-bike', function () {
        const bikeId = $(this).data('bike-id');
        rentBike(bikeId);
    });

    $('#bikeList').on('click', '.return-bike', function () {
        const bikeId = $(this).data('bike-id');
        returnBike(bikeId);
    });

    $('#addBikeForm').on('submit', function (e) {
        e.preventDefault();
        addNewBike();
    });
});

function loadBikes() {
    $.ajax({
        url: '/api/bikes',
        method: 'GET',
        success: function (bikes) {
            displayBikes(bikes);
        },
        error: function (xhr) {
            showError('Failed to load bikes');
        }
    });
}

function rentBike(bikeId) {
    $.ajax({
        url: `/api/bikes/${bikeId}/rent`,
        method: 'POST',
        success: function (response) {
            showSuccess('Bike rented successfully');
            loadBikes();
        },
        error: function (xhr) {
            showError('Failed to rent bike');
        }
    });
}

function returnBike(bikeId) {
    $.ajax({
        url: `/api/bikes/${bikeId}/return`,
        method: 'POST',
        success: function (response) {
            showSuccess('Bike returned successfully');
            loadBikes();
        },
        error: function (xhr) {
            showError('Failed to return bike');
        }
    });
}

function addNewBike() {
    const bikeData = {
        model: $('#bikeModel').val(),
        condition: $('#bikeCondition').val(),
        location: $('#bikeLocation').val()
    };

    $.ajax({
        url: '/api/bikes',
        method: 'POST',
        contentType: 'application/json',
        data: JSON.stringify(bikeData),
        success: function (response) {
            showSuccess('Bike added successfully');
            loadBikes();
            $('#addBikeForm')[0].reset();
        },
        error: function (xhr) {
            showError('Failed to add bike');
        }
    });
}

function displayBikes(bikes) {
    const bikeList = $('#bikeList');
    bikeList.empty();

    bikes.forEach(bike => {
        const bikeCard = createBikeCard(bike);
        bikeList.append(bikeCard);
    });
}

function createBikeCard(bike) {
    return `
        <div class="bike-card">
            <h3>${bike.model}</h3>
            <div class="bike-details">
                <p>Condition: ${bike.condition}</p>
                <p>Location: ${bike.location}</p>
                <p>Status: ${bike.status}</p>
            </div>
            <div class="bike-actions">
                ${bike.status === 'AVAILABLE'
        ? `<button class="btn btn-primary rent-bike" data-bike-id="${bike.id}">Rent</button>`
        : `<button class="btn btn-success return-bike" data-bike-id="${bike.id}">Return</button>`
    }
            </div>
        </div>
    `;
}

function showSuccess(message) {
    const alert = $('<div class="alert alert-success"></div>').text(message);
    $('.container').prepend(alert);
    setTimeout(() => alert.remove(), 3000);
}

function showError(message) {
    const alert = $('<div class="alert alert-danger"></div>').text(message);
    $('.container').prepend(alert);
    setTimeout(() => alert.remove(), 3000);


}

