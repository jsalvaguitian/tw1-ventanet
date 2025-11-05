document.addEventListener("DOMContentLoaded", function() {
    const inputFoto = document.querySelector('input[name="foto"]');
    const form = inputFoto.closest("form"); //Busca el form mas arriba que tiene input[name=foto], para no tener ids
    const imgPerfil = document.querySelector(".foto-perfil");

    inputFoto.addEventListener("change", function() {
        if (inputFoto.files && inputFoto.files[0]) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imgPerfil.src = e.target.result; //e.target === reader
            };
            // Cargo la foto con API filereader y agarro solo la primera que el usuario sube
            reader.readAsDataURL(inputFoto.files[0]);
            // leo la foto en bytes
            const formData = new FormData(form);

            fetch(form.action, {
                method: "POST",
                body: formData
            })
            .then(response => {
                if (!response.ok) throw new Error("Error al subir la imagen");
                return response.text();
            })
            .then(() => {
                console.log("Foto actualizada correctamente");
            })
            .catch(err => {
                console.error(err);
                alert("Ocurrió un error al cambiar la foto");
            });
        }
    });
     const abrirModal = document.getElementById('modal-abrir');
    const cerrarModal = document.getElementById('modal-cerrar');
    const modalContainer = document.getElementById('modal-container')
abrirModal.addEventListener('click', () => {
    modalContainer.classList.add('show');
  });

cerrarModal.addEventListener('click', () => {
    modalContainer.classList.remove('show');
  });

});
   

