
document.addEventListener("DOMContentLoaded", () => {
    const filtros = [
        "tipoProducto",
        "tipoVentana",
        "ancho",
        "alto",
        "material",
        "vidrio",
        "color",
        "premarco",
        "barrotillos"
    ];

    // Asociar evento change a todos los filtros
    filtros.forEach(id => {
        const el = document.getElementById(id);
        if (el) {
            el.addEventListener("change", buscarProductos);
        }
    });

    listenerProvincia();
    listenerLocalidad();

    // Llamar búsqueda inicial al cargar
    buscarProductos();
});

let productosACotizar = [];



async function buscarProductos() {
    const tipoProductoId = document.getElementById("tipoProducto").value || "";
    const tipoVentanaId = document.getElementById("tipoVentana").value || "";
    const anchoId = document.getElementById("ancho").value || "";
    const altoId = document.getElementById("alto").value || "";
    const materialDePerfilId = document.getElementById("material").value || "";
    const tipoDeVidrioId = document.getElementById("vidrio").value || "";
    const colorId = document.getElementById("color").value || "";
    const conPremarco = document.getElementById("premarco").checked;
    const conBarrotillos = document.getElementById("barrotillos").checked;


    const params = new URLSearchParams({
        tipoProductoId,
        tipoVentanaId,
        anchoId,
        altoId,
        materialDePerfilId,
        tipoDeVidrioId,
        colorId,
        conPremarco,
        conBarrotillos
    });

    try {
        const response = await fetch(`/spring/producto/buscar?${params.toString()}`);
        const productos = await response.json();
        const contenedor = document.getElementById("contenedorProductos");
        contenedor.innerHTML = "";

        if (productos.length === 0) {
            contenedor.innerHTML = `
                <div class="col-12 text-center">
                    <p class="text-muted">No se encontraron productos</p>
                </div>`;
            return;
        }

        // 🔹 Contenedor flexible y responsivo
        const rowContainer = document.createElement("div");
        rowContainer.className = "row justify-content-center text-center";

        productos.forEach(p => {
            const cotizado = isCotizado(p.id);
            const btnClass = cotizado ? 'btn-secondary' : 'btn-primary';
            const btnText = cotizado ? 'Quitar' : 'Cotizar';
            const checkClass = cotizado ? '' : 'd-none';

            const cardHTML = `
                <div class="col-12 col-sm-6 col-md-4 d-flex justify-content-center mb-4">
                    <div class="card producto-card h-100 shadow-sm w-100" data-id="${p.id}">
                        <img src="/spring${p.imagenUrl}" 
                             class="card-img-top img-fluid rounded-top" 
                             alt="${p.nombre}" 
                             style="object-fit: cover; height: 200px;">
                        <div class="card-body d-flex flex-column justify-content-between">
                            <div>
                                <h5 class="card-title">${p.nombre}</h5>
                                <p class="card-text">${p.descripcion}</p>
                            </div>
                            <div class="mt-auto d-flex justify-content-between align-items-center gap-2">
                                <button type="button" 
                                        class="btn btn-sm ${btnClass}" 
                                        data-producto-id="${p.id}" 
                                        onclick="cotizarProducto(this, ${p.id}, '${p.nombre}')">
                                    ${btnText}
                                </button>
                                <span class="cotizado-check ms-3 text-success ${checkClass}">
                                    <i class="fas fa-check-circle fa-lg"></i>
                                </span>
                            </div>
                        </div>
                    </div>
                </div>`;
            rowContainer.insertAdjacentHTML("beforeend", cardHTML);
        });

        contenedor.appendChild(rowContainer);

    } catch (error) {
        console.error("Error al obtener productos:", error);
    }
}


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

function isCotizado(id) {
    return productosACotizar.some(item => item.id === id);
}


function cotizarProducto(button, productoId, nombreProducto) {
    const cardBody = button.closest('.card-body');
    const checkIcon = cardBody.querySelector('.cotizado-check');
    //const productoIndex = productosACotizar.findIndex(item => item.id === productoId);     
    const productoIndex = productosACotizar.indexOf(productoId);

    if (productoIndex !== -1) {
        productosACotizar.splice(productoIndex, 1);
        button.textContent = 'Cotizar';
        button.classList.remove('btn-secondary');
        button.classList.add('btn-primary');
        checkIcon.classList.add('d-none');

    } else {
        productosACotizar.push(productoId);
        button.textContent = 'Quitar';
        button.classList.remove('btn-primary');
        button.classList.add('btn-secondary');
        checkIcon.classList.remove('d-none');
    }

    console.log("Productos agregados:", productosACotizar);
}

async function enviarSolicitud(event) {
    event.preventDefault();

    if (productosACotizar.length === 0) {
        alert("Debe seleccionar al menos un producto para cotizar.");
        return;
    }

    try {
        const response = await fetch("/spring/cotizacion/datos-previsualizar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ productosIds: productosACotizar })
        });

        console.log('Response after sending cotization request:', response);
        if (response.ok) {
            const cotizaciones = await response.json();
            window.location.href = "/spring/cotizacion/previsualizar";
            return;
        } else {
            throw new Error("Error al enviar cotización");
        }

    } catch (error) {
        console.error("Error al enviar la solicitud:", error);
        alert("Ocurrió un error al generar la cotización.");
    }
}

function listenerProvincia() {
    const provinciaEl = document.getElementById('provincia');
    const localidadEl = document.getElementById('localidad');
    provinciaEl.addEventListener('change', function () {
        const provId = this.value;
        // limpiar opciones
        localidadEl.innerHTML = '<option value="">Seleccione...</option>';
        if (!provId) return;
        // Llamada al endpoint que retorna JSON
        fetch('/spring/presupuesto/provincia?provinciaId=' + encodeURIComponent(provId))
            .then(resp => {
                if (!resp.ok) throw new Error('Error en la respuesta');
                return resp.json();
            })
            .then(list => {
                list.forEach(it => {
                    const opt = document.createElement('option');
                    opt.value = it.id_localidad;
                    opt.textContent = it.nombre;
                    localidadEl.appendChild(opt);
                });
            })
            .catch(err => {
                console.error('No se pudieron cargar las localidades', err);
                showToast('No se pudieron cargar las localidades para la provincia seleccionada.');
            });
    });


}

function listenerLocalidad() {
    const localidadEl = document.getElementById('localidad');
    const partidoEl = document.getElementById('partido');

    localidadEl.addEventListener('change', function () {
        const localidadId = this.value;
        // limpiar opciones
        partidoEl.innerHTML = '<option value="">Seleccione...</option>';
        if (!localidadId) return;
        // Llamada al endpoint que retorna JSON
        fetch('/spring/presupuesto/localidad?localidadId=' + encodeURIComponent(localidadId))
            .then(resp => {
                if (!resp.ok) throw new Error('Error en la respuesta');
                return resp.json();
            })
            .then(list => {
                list.forEach(it => {
                    const opt = document.createElement('option');
                    opt.value = it.id_partido;
                    opt.textContent = it.nombre;
                    partidoEl.appendChild(opt);
                });
            })
            .catch(err => {
                console.error('No se pudieron cargar los partidos', err);
                showToast('No se pudieron cargar los partidos para la localidad seleccionada.');
            });

    });
}


