
document.addEventListener("DOMContentLoaded", () => {
    const filtros = [
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

    // Llamar búsqueda inicial al cargar
    buscarProductos();
});

let productosACotizar = [];

async function buscarProductos() {
    const tipoVentanaId = document.getElementById("tipoVentana").value || "";
    const anchoId = document.getElementById("ancho").value || "";
    const altoId = document.getElementById("alto").value || "";
    const materialDePerfilId = document.getElementById("material").value || "";
    const tipoDeVidrioId = document.getElementById("vidrio").value || "";
    const colorId = document.getElementById("color").value || "";
    const conPremarco = document.getElementById("premarco").checked;
    const conBarrotillos = document.getElementById("barrotillos").checked;

    const params = new URLSearchParams({
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

            let cardHTML = '<div class="row">';           

            contenedor.innerHTML = `
                <div class="col-12 text-center">
                    <p class="text-muted">No se encontraron productos</p>
                </div>`;
            return;
        }

        productos.forEach((p, index) => {

            const cotizado = isCotizado(p.id);
            const btnClass = cotizado ? 'btn-secondary' : 'btn-primary';
            const btnText = cotizado ? 'Quitar' : 'Cotizar';
            const checkClass = cotizado ? '' : 'd-none';

            console.log('imagen', p.imagenUrl);

            // const cardHTML = `
            //     <div class="col-md-6 mb-4">
            //         <div class="card shadow-sm">
            //             <div class="row g-0">
            //                 <div class="col-md-3">
            //                     <img th:src="@{${p.imagenUrl}}" class="img-fluid rounded-start" th:alt="${p.nombre}">
            //                 </div>
            //                 <div class="col-md-6">
            //                     <div class="card-body">
            //                         <h5 class="card-title">${p.nombre}</h5>
            //                         <p class="card-text">${p.descripcion || ''}</p>
            //                         <span class="badge bg-primary">${p.tipoProducto?.nombre || ''}</span>
            //                         <span class="badge bg-secondary">${p.marca?.nombre || ''}</span>
            //                     </div>
            //                 </div>
            //                 <div class="col-md-3 d-flex flex-column justify-content-center align-items-center">
                               
            //                     <button class="btn btn-sm btn-cotizar ${btnClass}" 
            //                                     data-producto-id="${p.id}" 
            //                                     onclick="cotizarProducto(this, ${p.id}, '${p.nombre}')">
            //                                 ${btnText}
            //                             </button>
                                        
            //                             <span class="cotizado-check ms-3 text-success ${checkClass}">
            //                                 <i class="fas fa-check-circle fa-lg"></i>
            //                             </span>
            //                 </div>
            //             </div>
            //         </div>
            //     </div>
            // `;
            let cardHTML = `
            <div class="col-md-5 mb-4 d-flex justify-content-center">
                    <div class="card producto-card h-100 shadow-sm" data-id="${p.id}">
                        <div class="row g-0">
                            <div class="col-md-5 d-flex align-items-center">
                                <img src="/spring${p.imagenUrl}" 
                                     class="img-fluid rounded-start mx-auto" 
                                     alt="${p.nombre}">
                            </div>
                            <div class="col-md-7">
                                <div class="card-body d-flex flex-column justify-content-between">
                                    <div>
                                        <h5 class="card-title">${p.nombre}</h5>
                                        <p class="card-text">${p.descripcion}</p>
                                    </div>
                                    <div class="mt-auto d-flex justify-content-between align-items-center">
                                        
                                        <button type="button" class="btn btn-sm btn-cotizar ${btnClass}" 
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
                        </div>
                    </div>
                </div>
            `;
            // Cierra la fila después de cada dos elementos (índices 1, 3, 5, etc.)
            if ((index + 1) % 2 === 0) {
                cardHTML += '</div><div class="row">';
            }
            contenedor.insertAdjacentHTML("beforeend", cardHTML);
        });

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
    const productoIndex = productosACotizar.findIndex(item => item.id === productoId);     

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
    // Aquí puedes llamar a una función para actualizar el contador de items en el menú.
    // actualizarContadorCotizacion(); 
}

async function enviarSolicitudOld(event) {
    event.preventDefault(); // evita el submit tradicional

    console.log('Enviando productos para cotización:', productosACotizar);
    if (productosACotizar.length === 0) {
        alert("Debe seleccionar al menos un producto para cotizar.");
        return false;
    }

    try {
        const response = await fetch("/spring/cotizacion/previsualizar", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(productosACotizar)
        });

        if (!response.ok) throw new Error("Error al enviar cotización");

        // Redirigir a la pantalla de detalle de cotización
        const cotizacion = await response.json();
        window.location.href = `/spring/cotizacion/detalle/${cotizacion.id}`;

    } catch (error) {
        console.error("Error al enviar la solicitud:", error);
        alert("Ocurrió un error al generar la cotización.");
    }
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
             // 🚀 CLAVE: Usamos la URL final del seguimiento
             window.location.href = "/spring/cotizacion/previsualizar";
             //window.location.href = response.url;
             return;
        }else{
            throw new Error("Error al enviar cotización");
        }

    } catch (error) {
        console.error("Error al enviar la solicitud:", error);
        alert("Ocurrió un error al generar la cotización.");
    }
}


