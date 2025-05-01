let mapa;
let marcador;
const apiKey = 'pk.a921cf52c140515c913de88dd831f598';
window.onload = function() {
    inicializarMapa(10.394598592365298,-75.5571199005398);
    obtenerUbicacion();
};

function inicializarMapa(lat, lon) {
    mapa = L.map('map').setView([lat, lon], 16); // Establecemos la vista inicial

    // Capa del mapa (usando OpenStreetMap)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(mapa);

    // Añadir marcador en la ubicación
    marcador = L.marker([lat, lon]).addTo(mapa)
        .bindPopup("<b>Tu Ubicación</b>")
        .openPopup();
}

function cambiarUbicacionDeMapa(lat,lon) {
    mapa.setView([lat, lon], 16);
    marcador.setLatLng([lat, lon]);
}

function obtenerUbicacion() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(guardarCoordenadas, gestionarError);
    } else {
        alert("La geolocalización no es compatible con este navegador.");
    }
}

async function guardarCoordenadas(position) {
    const latitud = position.coords.latitude;
    const longitud = position.coords.longitude;
    colocarPosicionEnFormulario(latitud, longitud);
    cambiarUbicacionDeMapa(latitud, longitud);
    document.getElementById('address').value = await obtenerDireccion(latitud, longitud);
}

function colocarPosicionEnFormulario(lat, lon){
    const inputLatitud = document.getElementById('latitud');
    const inputLongitud = document.getElementById('longitud');
    inputLatitud.value = lat;
    inputLongitud.value = lon;
}

function gestionarError(error) {
    switch(error.code) {
        case error.PERMISSION_DENIED:
            alert("El usuario denegó la solicitud de geolocalización.");
            break;
        case error.POSITION_UNAVAILABLE:
            alert("La ubicación no está disponible.");
            break;
        case error.TIMEOUT:
            alert("La solicitud de geolocalización ha excedido el tiempo de espera.");
            break;
        default:
            alert("Ocurrió un error desconocido.");
            break;
    }
}

// Replace with your key

async function obtenerDireccion(lat, lon) {
    const url = `https://us1.locationiq.com/v1/reverse?key=${apiKey}&lat=${lat}&lon=${lon}&format=json`;
    try {
        const response = await fetch(url);
        const data = await response.json();
        return data.display_name || 'No se encontró dirección';
    } catch (error) {
        console.error('Error fetching address:', error);
        return 'Error retrieving address';
    }
}
