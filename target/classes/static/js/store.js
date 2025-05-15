$(document).on("click", ".remove", function(){
    let id = $(this).siblings('input[type="number"]').first().attr('id');
    remove(id);
});

$(document).on("click", ".add", function(){
    let id = $(this).siblings('input[type="number"]').first().attr('id');
    add(id);
});

function remove(id) {
    const input = document.getElementById(id);
    let valorActual = input.value;
    valorActual--;
    if(valorActual < 1) {
        valorActual = 1;
    }
    input.value = valorActual;
}

function add(id) {
    const input = document.getElementById(id);
    let valorActual = input.value;
    valorActual++;
    input.value = valorActual;
}

