function mostrarImagen() {
    const tipoVentana = document.getElementById("tipoVentana").value;
    const imagen = document.getElementById("imagenVentana");

    if (tipoVentana) {
        imagen.src = "/img/" + tipoVentana;
        imagen.style.display = "block";
    } else {
        imagen.style.display = "none";
    }
}

// medidas de 0.5 a 3.0 en saltos de 0.1
function llenarMedidas() {
    const ancho = document.getElementById("ancho");
    const alto = document.getElementById("alto");
    for (let i = 0.5; i <= 3.0; i += 0.1) {
      let opt1 = document.createElement("option");
      opt1.value = i.toFixed(1);
      opt1.textContent = i.toFixed(1) + " m";
      ancho.appendChild(opt1);

      let opt2 = document.createElement("option");
      opt2.value = i.toFixed(1);
      opt2.textContent = i.toFixed(1) + " m";
      alto.appendChild(opt2);
    }
  }

function enviarSolicitud(event) {
    event.preventDefault();
    alert("Tu solicitud ha sido enviada con éxito.");
    window.location.href = "/";
    return false;
  }

// llenar medidas al cargar
window.onload = llenarMedidas;