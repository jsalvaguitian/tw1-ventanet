document.addEventListener("DOMContentLoaded", function() {
    const stats = document.querySelectorAll(".stat");
    const rows = document.querySelectorAll("#tablaCotizaciones tbody tr");
    const searchInput = document.querySelector(".form-control[placeholder^='Buscar']");
    
    let currentFilter = "TODOS"; // Filtro de estado actual
    let searchText = "";         // Texto de búsqueda actual

    // Función que aplica ambos filtros
    function aplicarFiltros() {
        rows.forEach(row => {
            const estado = row.children[3].textContent.trim().toUpperCase();
            const textoFila = row.textContent.toLowerCase();

            const coincideEstado = currentFilter === "TODOS" || estado === currentFilter;
            const coincideBusqueda = textoFila.includes(searchText);

            // Mostrar/ocultar fila según ambos criterios
            row.style.display = (coincideEstado && coincideBusqueda) ? "" : "none";
        });
    }

    // Filtro de estado
    stats.forEach(stat => {
        stat.addEventListener("click", () => {
            stats.forEach(s => s.classList.remove("active"));
            stat.classList.add("active");
            currentFilter = stat.getAttribute("data-filter");
            aplicarFiltros();
        });
    });

    // Filtro de búsqueda (en tiempo real)
    searchInput.addEventListener("input", (e) => {
        searchText = e.target.value.toLowerCase();
        aplicarFiltros();
    });

    // Inicializar vista por defecto (mostrar todas)
    aplicarFiltros();

    // Asegúrate de incluir jQuery y SweetAlert2/Bootstrap Modal en tu HTML

});

/* sacado del .html */
function toggleSidebar() {
    document.getElementById("sidebar").classList.toggle("collapsed");
}

function mostrarDetalleCotizacion(id) {
    const url = '/spring/cotizacion/detalle/' + id;

    fetch(url)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }
            return response.json();
        })
        .then(cotizacion => {
            const estadoHTML = getEstadoHTML(cotizacion.estado);
            let botonesAccion = '';

            if (cotizacion.estado === 'PENDIENTE') {
                botonesAccion = `
            <div style="margin-top: 20px; text-align: center;">
                <button class="btn btn-success me-3" onclick="manejarAccionCotizacion(${cotizacion.id}, 'APROBADA')">Aceptar</button>
                <button class="btn btn-danger" onclick="manejarAccionCotizacion(${cotizacion.id}, 'RECHAZADO')">Rechazar</button>
            </div>
        `;
            }


            let htmlContent = `
        <p><strong>Cliente:</strong> ${cotizacion.cliente.nombre} ${cotizacion.cliente.apellido} </p>
        <p><strong>Estado:</strong> ${estadoHTML}</p> 
        <p><strong>Monto Total:</strong> $${cotizacion.montoTotal.toFixed(2)}</p>
        
        <h5 style="margin-top: 20px;">Items de la Cotización</h5>
        <table class="table table-bordered table-sm">
            <thead>
                <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>P. Unitario</th>
                    <th>Subtotal</th>
                </tr>
            </thead>
            <tbody>
    `;

            cotizacion.items.forEach(item => {
                const subtotal = item.cantidad * item.precioUnitario;
                htmlContent += `
            <tr>
                <td>${item.producto.nombre}</td>
                <td>${item.cantidad}</td>
                <td>$${item.precioUnitario.toFixed(2)}</td>
                <td>$${subtotal.toFixed(2)}</td>
            </tr>
        `;
            });

            htmlContent += `</tbody></table>`;
            htmlContent += botonesAccion;

            Swal.fire({
                title: `Detalle de Cotización #${id}`,
                html: htmlContent,
                icon: 'info',
                width: '80%',
                showConfirmButton: true,
                confirmButtonText: 'Cerrar'
            });
        })
        .catch(error => {
            console.error("Error al cargar el detalle:", error);
            Swal.fire('Error', 'No se pudo cargar el detalle de la cotización. Detalle: ' + error.message, 'error');
        });
}

function getEstadoHTML(estado) {
    let className = '';
    let bgColor = '';

    switch (estado) {
        case 'PENDIENTE':
            className = 'status-badge status-pendiente'; // Fondo amarillo claro
            break;
        case 'APROBADA':
            className = 'status-badge status-aprobada';   // Fondo verde
            break;
        case 'RECHAZADO':
            className = 'status-badge status-rechazado'; // Fondo rojo
            break;
        case 'COMPLETADA':
            className = 'status-badge status-completada'; // Fondo azul claro
            break;
        default:
            className = 'status-badge';
    }

    // Devolvemos el HTML con la clase aplicada
    return `<span class="${className}">${estado}</span>`;
}

function manejarAccionCotizacion(id, accion) {
    // 1. Cierra el popup actual (SweetAlert2)
    Swal.close();

    // 2. Muestra una confirmación (opcional)
    Swal.fire({
        title: `¿Confirmar ${accion}?`,
        text: `¿Estás seguro de que deseas ${accion} la cotización #${id}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: `Sí, ${accion}`,
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            // 3. Llama al endpoint de tu servidor (ej. PUT/POST)
            fetch(`/spring/cotizacion/${id}/cambiar-estado/${accion}`, {
                method: 'POST' // o 'PUT'
                // Puedes agregar headers si es necesario
            })
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`Error al cambiar estado: ${response.status}`);
                    }
                    return response.json(); // O simplemente response.text()
                })
                .then(data => {
                    Swal.fire('¡Éxito!', `Cotización #${id} ${accion} correctamente.`, 'success');
                    // 4. Recargar la tabla o el dashboard para reflejar el cambio
                    // location.reload(); 
                })
                .catch(error => {
                    Swal.fire('Error', error.message, 'error');
                });
        }
    });
}