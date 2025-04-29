let mapa;
let marcador;

function dibujarMapa(lat, lon) {
    mapa = L.map('map').setView([lat, lon], 13); // Establecemos la vista inicial

    // Capa del mapa (usando OpenStreetMap)
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(mapa);

    // Añadir marcador en la ubicación
    marcador = L.marker([lat, lon]).addTo(mapa)
        .bindPopup("<b>Tu Ubicación</b>")
        .openPopup();
}

function getLocation() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(guardarCoordenadas, gestionarError);
    } else {
        alert("La geolocalización no es compatible con este navegador.");
    }
}

function guardarCoordenadas(position) {
    const latitud = position.coords.latitude;
    const longitud = position.coords.longitude;
    colocarPosicionEnFormulario(latitud, longitud);
    dibujarMapa(latitud, longitud);
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