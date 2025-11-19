document.addEventListener("DOMContentLoaded", function () {
    const stats = document.querySelectorAll(".stat");
    const rows = document.querySelectorAll("#tablaCotizaciones tbody tr");
    const searchInput = document.querySelector(".form-control[placeholder^='Buscar']");
    const dateFilter = document.getElementById("dateFilter");
    const applyFilterBtn = document.getElementById("applyFilter");

    let currentFilter = "TODOS"; // Filtro de estado actual
    let searchText = "";
    let dateRange = null;

    function obtenerRangoFechas(valor) {
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0);
        let inicio = null;
        let fin = new Date(hoy);

        switch (valor) {
            case "today":
                inicio = new Date(hoy);
                break;
            case "yesterday":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 1);
                fin = new Date(inicio);
                break;
            case "last7":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 7);
                break;
            case "last30":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 30);
                break;
            case "month":
                inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1);
                break;
            default:
                inicio = null; // Todos
        }
        return { inicio, fin };
    }

    function aplicarFiltros() {
        // Detectar qué tabla está visible (cotizaciones o custom)
        const tablaActiva =
            document.querySelector("#tablaCotizaciones")?.offsetParent !== null
                ? "#tablaCotizaciones"
                : "#tablaLicitaciones";

        const filas = document.querySelectorAll(`${tablaActiva} tbody tr`);
        filas.forEach(row => {
            // Buscar índice de columna de estado y fecha (depende de la tabla)
            const esCustom = tablaActiva === "#tablaLicitaciones";
            const idxEstado = esCustom ? 3 : 3;
            const idxFecha = esCustom ? 4 : 4;

            const estado = row.children[idxEstado].textContent.trim().toUpperCase();
            const textoFila = row.textContent.toLowerCase();
            const fechaStr = row.children[idxFecha].textContent.trim();

            const coincideEstado = currentFilter === "TODOS" || estado === currentFilter;
            const coincideBusqueda = textoFila.includes(searchText);
            let coincideFecha = true;

            if (dateRange && dateRange.inicio) {
                const fecha = new Date(fechaStr);
                fecha.setHours(0, 0, 0, 0);
                coincideFecha = fecha >= dateRange.inicio && fecha <= dateRange.fin;
            }

            // Mostrar/ocultar fila según todos los criterios
            row.style.display = coincideEstado && coincideBusqueda && coincideFecha ? "" : "none";
        });
    }

    applyFilterBtn.addEventListener("click", () => {
        const valor = dateFilter.value;
        dateRange = obtenerRangoFechas(valor);
        aplicarFiltros();
    });

    // Filtro de estado
    stats.forEach(stat => {
        stat.addEventListener("click", () => {
            stats.forEach(s => s.classList.remove("active"));
            stat.classList.add("active");
            currentFilter = stat.getAttribute("data-filter");
            aplicarFiltros();
        });
    });

    searchInput.addEventListener("input", (e) => {
        searchText = e.target.value.toLowerCase();
        aplicarFiltros();
    });

    document.addEventListener('click', function (e) {
        // Verifica si el clic ocurrió en el botón o dentro de su icono
        let target = e.target.closest('.btn-detalle-cotizacion');

        if (target) {
            const id = target.getAttribute('data-id');
            // Llama a la función local
            mostrarDetalleCotizacion(id);
        }
    });

    document.addEventListener('click', function (e) {
        // Verifica si el clic ocurrió en el botón o dentro de su icono
        let target = e.target.closest('.btn-detalle-licitacion');

        if (target) {
            const id = target.getAttribute('data-id');
            // Llama a la función local
            mostrarDetalleLicitacion(id);
        }
    });

});

window.mostrarDetalleCotizacion = mostrarDetalleCotizacion;
window.manejarAccionCotizacion = manejarAccionCotizacion;
window.manejarAccionLicitacion = manejarAccionLicitacion;
window.getEstadoHTML = getEstadoHTML;

async function mostrarDetalleCotizacion(id) {
    const url = '/spring/cotizacion/detalle/' + id;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(cotizacion => {
            const estadoHTML = getEstadoHTML(cotizacion.estado);
            let botonesAccion = '';

            if (cotizacion.estado === 'PENDIENTE') {
                botonesAccion = `
                    <div style="margin-top: 25px; text-align: center;">
                        <button class="btn btn-danger" 
                                onclick="manejarAccionCotizacion(${cotizacion.id}, 'RECHAZADO')">
                            ❌ Rechazar
                        </button>
                    </div>
                `;
            }

            
            let medioDePagoHTML = '';
            if (cotizacion.medioDePago) {
                const mp = cotizacion.medioDePago;
                let cuotas = '';
                if(mp.tipo === 'CREDITO') {
                    cuotas = mp.cantidad_cuotas ? `(${mp.cantidad_cuotas} cuotas)` : '';
                }
                
                const detalleMP = `${mp.nombre} ${cuotas}`;
                
                medioDePagoHTML = `
                    <div style="display: flex; align-items: center; gap: 10px;">
                        <p style="margin: 0; font-size: 14px; text-align: right;">
                            <strong>Medio de Pago:</strong> ${detalleMP}
                        </p>
                        ${mp.imagen ? `<img src="/spring/img/${mp.imagen}" alt="${mp.nombre}" style="width: 30px; height: auto; border-radius: 5px;">` : ''}
                    </div>
                `;
            }

        if (cotizacion.estado === 'APROBADA' || cotizacion.estado === 'COMPLETADA') {
            botonesAccion += `
                <div style="margin-top: 20px; text-align: center;">
                    <button id="btnDescargarPDF" class="btn btn-success">
                        <i class="bi bi-file-earmark-pdf"></i> Descargar PDF
                    </button>
                </div>
            `;
        }

        // --- Cálculo de totales con IVA ---
        const subtotal = cotizacion.montoTotal / 1.21;
        const iva = cotizacion.montoTotal - subtotal;

        // --- Encabezado y tabla de productos ---
        let htmlContent = `
            <div style="font-family: Arial, sans-serif; color:#333; text-align:left;">
                <div style="display: flex; justify-content: space-between; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 10px;">
                    <div style="width: 48%;">
                        <h4 style="margin: 0; color: #003366;">${cotizacion.proveedor?.razonSocial || 'Proveedor desconocido'}</h4>
                        <p style="margin: 2px 0;">${cotizacion.proveedor?.direccion || ''}</p>
                        <p style="margin: 2px 0;">Tel: ${cotizacion.proveedor?.telefono || '-'}</p>
                        <p style="margin: 2px 0;">Email: ${cotizacion.proveedor?.email || '-'}</p>
                        <p style="margin: 2px 0;">CUIT: ${cotizacion.proveedor?.cuit || '-'}</p>
                    </div>
                    <div style="width: 48%; text-align: right;">
                        <h5 style="margin: 0 0 5px;">Cliente</h5>
                        <p style="margin: 2px 0;"><strong>${cotizacion.cliente?.nombre || 'Cliente desconocido'}</strong></p>
                        <p style="margin: 2px 0;">${cotizacion.cliente?.telefono || '-'}</p>
                        <p style="margin: 2px 0;">Fecha: ${new Date(cotizacion.fechaCreacion).toLocaleDateString()}</p>
                    </div>
                </div>

                    <!-- Estado -->
                    <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 15px;">
                        <p style="margin: 0;"><strong>Estado:</strong> ${estadoHTML}</p>
                        ${medioDePagoHTML}
                    </div>

                <h5 style="margin-top: 20px; color:#003366;">Detalle de la Cotización</h5>
                <table style="width:100%; border-collapse: collapse; font-size: 14px; margin-top:5px;">
                    <thead>
                        <tr style="background-color: #f2f2f2;">
                            <th style="border: 1px solid #ccc; padding: 8px;">Producto</th>
                            <th style="border: 1px solid #ccc; padding: 8px;">Cantidad</th>
                            <th style="border: 1px solid #ccc; padding: 8px;">Precio Unitario</th>
                            <th style="border: 1px solid #ccc; padding: 8px;">% Descuento</th>
                            <th style="border: 1px solid #ccc; padding: 8px;">IVA</th>
                            <th style="border: 1px solid #ccc; padding: 8px;">Total</th>
                        </tr>
                    </thead>
                    <tbody>
        `;

        cotizacion.items?.forEach(item => {
            const totalItem = item.cantidad * item.precioUnitario;
            htmlContent += `
                <tr>
                    <td style="border: 1px solid #ccc; padding: 8px;">${item.producto?.nombre || '-'}</td>
                    <td style="border: 1px solid #ccc; padding: 8px;">${item.cantidad}</td>
                    <td style="border: 1px solid #ccc; padding: 8px;">$${item.precioUnitario.toFixed(2)}</td>
                    <td style="border: 1px solid #ccc; padding: 8px;">${item.descuento ? item.descuento + '%' : '0%'}</td>
                    <td style="border: 1px solid #ccc; padding: 8px;">21%</td>
                    <td style="border: 1px solid #ccc; padding: 8px;">$${totalItem.toFixed(2)}</td>
                </tr>
            `;
        });

        htmlContent += `
                    </tbody>
                </table>
                <div style="margin-top: 20px; text-align: right;">
                    <p><strong>Base imponible:</strong> $${subtotal.toFixed(2)}</p>
                    <p><strong>IVA (21%):</strong> $${iva.toFixed(2)}</p>
                    <p style="font-size:16px;"><strong>Total:</strong> $${cotizacion.montoTotal.toFixed(2)}</p>
                </div>

                ${botonesAccion}

                <p style="margin-top:20px; font-style:italic;">
                    Para los detalles del envío, por favor comunicarse con el proveedor mediante mensajería correspondiente.
                </p>
            </div>
        `;

        // --- Mostrar SweetAlert ---
        Swal.fire({
            title: `Cotización #${id}`,
            html: htmlContent,
            icon: 'info',
            width: '75%',
            showConfirmButton: true,
            confirmButtonText: 'Cerrar',
            didOpen: (popup) => {
                setTimeout(() => {
                    const btnPDF = popup.querySelector("#btnDescargarPDF");
                    if (btnPDF) {
                        btnPDF.addEventListener("click", async () => {
                            const { jsPDF } = window.jspdf;
                            const doc = new jsPDF();

                            // --- Cabecera ---
                            doc.setFontSize(10);
                            doc.text(`Proveedor: ${cotizacion.proveedor.razonSocial}`, 10, 10);
                            doc.text(`CUIT: ${cotizacion.proveedor.cuit || 'N/A'}`, 10, 15);
                            doc.text(`Dirección: ${cotizacion.proveedor.direccion || '-'}`, 10, 20);
                            doc.text(`Teléfono: ${cotizacion.proveedor.telefono || '-'}`, 10, 25);

                            // --- Cliente ---
                            if (cotizacion.cliente) {
                                doc.text(`Cliente: ${cotizacion.cliente.nombre}`, 10, 35);
                                doc.text(`Teléfono: ${cotizacion.cliente.telefono || '-'}`, 10, 40);
                                doc.text(`Fecha: ${new Date(cotizacion.fechaCreacion).toLocaleDateString()}`, 10, 45);
                            }

                            // --- Línea separatoria ---
                            doc.line(10, 50, 200, 50);

                            // --- Productos ---
                            let y = 55;
                            cotizacion.items?.forEach(item => {
                                const totalItem = item.cantidad * item.precioUnitario;
                                doc.text(`${item.producto?.nombre} - Cant: ${item.cantidad} x $${item.precioUnitario.toFixed(2)}`, 10, y);
                                y += 8;
                            });

                            // --- Mensaje de envío ---
                            y += 10;
                            doc.text("Para los detalles del envío, por favor comunicarse con el proveedor mediante mensajería correspondiente.", 10, y);

                            // --- Total ---
                            y += 15;
                            doc.setFontSize(12);
                            doc.text(`TOTAL: $${cotizacion.montoTotal.toFixed(2)}`, 150, y);

                            doc.save(`cotizacion_${cotizacion.id}.pdf`);
                        });
                    }
                }, 200);
            }
        });

    }).catch (error => {
        console.error("Error al cargar el detalle:", error);
        Swal.fire('Error', 'No se pudo cargar el detalle de la cotización: ' + error.message, 'error');
    });
}

function mostrarDetalleLicitacion(id) {
    const url = '/spring/licitacion/detalle/' + id;

    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
            return response.json();
        })
        .then(licitacion => {
            const estadoHTML = getEstadoHTML(licitacion.estado);
            let botonesAccion = '';

            if (licitacion.estado === 'PENDIENTE') {
                botonesAccion = `
                    <div style="margin-top: 20px; text-align: center;">                
                        <button class="btn btn-danger" onclick="manejarAccionLicitacion(${licitacion.id}, 'RECHAZADO')">Rechazar</button>
                    </div>
                `;
            }

            // Calcular IVA (21%)
            const iva = licitacion.montoTotal * 0.21;
            const base = licitacion.montoTotal - iva;
            const total = licitacion.montoTotal;

            const htmlContent = `
                <div style="font-family: Arial; text-align: left;">
                    <!-- Encabezado -->
                    <div style="display: flex; justify-content: space-between; border-bottom: 2px solid #ccc; padding-bottom: 10px; margin-bottom: 10px;">
                        <div style="width: 50%;">
                            <h4 style="margin-bottom: 5px;">${licitacion.proveedor.razonSocial}</h4>
                            <p style="margin: 0;">${licitacion.proveedor.direccion || ''}</p>
                            <p style="margin: 0;">Tel: ${licitacion.proveedor.telefono || '-'}</p>
                            <p style="margin: 0;">Email: ${licitacion.proveedor.email || '-'}</p>
                        </div>
                        <div style="width: 45%; text-align: right;">
                            <h5 style="margin-bottom: 5px;">Cliente</h5>
                            <p style="margin: 0;"><strong>${licitacion.cliente.nombre}</strong></p>
                            <p style="margin: 0;">${licitacion.cliente.direccion || ''}</p>
                            <p style="margin: 0;">${licitacion.cliente.email || '-'}</p>
                            <p style="margin: 0;">Fecha: ${new Date(licitacion.fechaCreacion).toLocaleDateString()}</p>
                        </div>
                    </div>

                    <p><strong>Estado:</strong> ${estadoHTML}</p>

                    <!-- Tabla -->
                    <h5 style="margin-top: 20px;">Detalle de la Cotización</h5>
                    <table style="width:100%; border-collapse: collapse; font-size: 14px;">
                        <thead>
                            <tr style="background-color: #f2f2f2;">
                                <th style="border: 1px solid #ccc; padding: 8px;">Descripción</th>
                                <th style="border: 1px solid #ccc; padding: 8px;">Cantidad</th>
                                <th style="border: 1px solid #ccc; padding: 8px;">Precio Unitario</th>
                                <th style="border: 1px solid #ccc; padding: 8px;">Color</th>
                                <th style="border: 1px solid #ccc; padding: 8px;">Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td style="border: 1px solid #ccc; padding: 8px;">${licitacion.productoCustom.descripcion}</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">${licitacion.productoCustom.cantidad}</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">$${licitacion.productoCustom.precio.toFixed(2)}</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">${licitacion.productoCustom.color || '-'}</td>
                                <td style="border: 1px solid #ccc; padding: 8px;">$${(licitacion.productoCustom.precio * licitacion.productoCustom.cantidad).toFixed(2)}</td>
                            </tr>
                        </tbody>
                    </table>

                    <!-- Totales -->
                    <div style="margin-top: 20px; text-align: right;">
                        <p><strong>Base imponible:</strong> $${base.toFixed(2)}</p>
                        <p><strong>IVA (21%):</strong> $${iva.toFixed(2)}</p>
                        <p><strong>Total:</strong> $${total.toFixed(2)}</p>
                    </div>

                    ${botonesAccion}
                </div>
            `;

            Swal.fire({
                title: `Cotización #${id}`,
                html: htmlContent,
                icon: 'info',
                width: '70%',
                showConfirmButton: true,
                confirmButtonText: 'Cerrar'
            });
        })
        .catch(error => {
            console.error("Error al cargar el detalle:", error);
            Swal.fire('Error', 'No se pudo cargar el detalle de la cotización: ' + error.message, 'error');
        });
}

function getEstadoHTML(estado) {
    let color = '';
    switch (estado) {
        case 'APROBADA': color = 'green'; break;
        case 'PENDIENTE': color = 'orange'; break;
        case 'RECHAZADO': color = 'red'; break;
        case 'COMPLETADA': color = 'blue'; break;
        default: color = 'gray';
    }
    return `<span style="color:${color}; font-weight:bold;">${estado}</span>`;
}


function getEstadoHTMLOld(estado) {
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
                    console.log('respuesta cambio estado', response);
                    if (!response.ok) {
                        Swal.fire('¡Érror!', `Cotización #${id} no se pudo cambiar el estado a ${accion} correctamente.`, 'error');
                        throw new Error(`Error al cambiar estado: ${response.status}`);
                    }
                    Swal.fire('¡Éxito!', `Cotización #${id} se cambió a ${accion} correctamente.`, 'success');
                    location.reload();
                    // return response.json(); // O simplemente response.text()
                })
                .then(data => {
                    Swal.fire('¡Éxito!', `Cotización #${id} ${accion} correctamente.`, 'success');
                    // 4. Recargar la tabla o el dashboard para reflejar el cambio
                    location.reload();
                })
                .catch(error => {
                    Swal.fire('Error', error.message, 'error');
                });
        }
    });
}

function manejarAccionLicitacion(id, accion) {
    // 1. Cierra el popup actual (SweetAlert2)
    Swal.close();

    // 2. Muestra una confirmación (opcional)
    Swal.fire({
        title: `¿Confirmar ${accion}?`,
        text: `¿Estás seguro de que deseas pasar a estado ${accion} la Cotización a medida #${id}?`,
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: `Sí, ${accion}`,
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {            
            fetch(`/spring/licitacion/${id}/cambiar-estado/${accion}`, {
                method: 'POST'
            })
                .then(response => {
                    console.log('respuesta cambio estado', response);
                    if (!response.ok) {
                        Swal.fire('¡Érror!', `Cotización a medida #${id} no se pudo cambiar el estado a ${accion} correctamente.`, 'error');
                        throw new Error(`Error al cambiar estado: ${response.status}`);
                    }
                    Swal.fire('¡Éxito!', `Cotización a medida #${id} se cambió a ${accion} correctamente.`, 'success');
                    location.reload();                    
                })
                .then(data => {
                    Swal.fire('¡Éxito!', `Cotización a medida #${id} ${accion} correctamente.`, 'success');
                    location.reload();
                })
                .catch(error => {
                    Swal.fire('Error', error.message, 'error');
                });
        }
    });
}

/* Listener global del PDF
document.addEventListener("click", function (event) {
  if (event.target && event.target.id === "btnDescargarPDF") {
    console.log("Generando PDF...");

    const cotizacionId = event.target.getAttribute("data-id");
    const url = `/spring/cotizacion/detalle/${cotizacionId}`;

    fetch(url)
      .then((res) => res.json())
      .then(async (cotizacion) => {
        const { jsPDF } = window.jspdf;
        const doc = new jsPDF();

        // 1. LOGO
        const logoPath = `/spring/${cotizacion.proveedor.logoPath}`;
        const img = new Image();
        img.crossOrigin = "anonymous";
        img.src = logoPath;

        img.onload = async function () {
          try {
            doc.addImage(img, "PNG", 10, 10, 40, 20);

            // 2. DATOS PROVEEDOR
            doc.setFontSize(10);
            const proveedorX = 50;
            const proveedorY = 15;
            doc.text("Dirección: Av. Siempre Viva 123", proveedorX, proveedorY);
            doc.text("Ubicación: Springfield", proveedorX, proveedorY + 5);
            doc.text("Razón Social: ACME S.A.", proveedorX, proveedorY + 10);
            doc.text("CUIT: 30-12345678-9", proveedorX, proveedorY + 15);
            doc.text("Sitio Web: www.acme.com", proveedorX, proveedorY + 20);
            doc.text("Teléfono: (011) 4444-5555", proveedorX, proveedorY + 25);

            doc.line(10, 45, 200, 45);

            // 3. CABECERA DEL PRESUPUESTO
            doc.setFontSize(12);
            doc.text("Presupuesto N°: 0001-00001234", 10, 55);
            doc.text("Fecha: 13/11/2025", 10, 61);

            doc.text("Cliente: Juan Pérez", 130, 55);
            doc.text("Teléfono: +54 9 11 5555-6666", 130, 61);

            doc.line(10, 67, 200, 67);

            // 4. TABLA DE PRODUCTOS
            doc.setFontSize(11);
            let startY = 75;
            doc.text("Producto", 20, startY);
            doc.text("Descripción", 60, startY);
            doc.text("Cantidad", 130, startY);
            doc.text("Total", 170, startY);
            startY += 5;

            const productos = [
              {
                img: "/img/prod1.png",
                nombre: "Silla",
                desc: "Silla ergonómica",
                cantidad: 2,
                total: 24000,
              },
              {
                img: "/img/prod2.png",
                nombre: "Escritorio",
                desc: "Madera maciza",
                cantidad: 1,
                total: 56000,
              },
            ];

            for (const p of productos) {
              try {
                const imgBlob = await fetch(p.img).then((r) => r.blob());
                const imgData = await new Promise((res) => {
                  const reader = new FileReader();
                  reader.onload = () => res(reader.result);
                  reader.readAsDataURL(imgBlob);
                });
                doc.addImage(imgData, "PNG", 15, startY, 15, 15);
              } catch (e) {
                console.warn("No se pudo cargar imagen del producto:", p.img);
              }

              doc.text(p.nombre, 40, startY + 10);
              doc.text(p.desc, 70, startY + 10);
              doc.text(String(p.cantidad), 135, startY + 10);
              doc.text(`$${p.total.toLocaleString()}`, 190, startY + 10, {
                align: "right",
              });

              startY += 20;
            }

            doc.line(10, startY, 200, startY);
            startY += 10;

            // 5. MONTO TOTAL
            doc.setFontSize(13);
            doc.text(`TOTAL: $${(80000).toLocaleString()}`, 200, startY, {
              align: "right",
            });

            // Guardar PDF
            doc.save("cotizacion.pdf");
          } catch (error) {
            console.error("Error al generar el PDF:", error);
            Swal.fire("Error", "No se pudo generar el PDF", "error");
          }
        };
      })
      .catch((error) => {
        console.error("Error al obtener datos de la cotización:", error);
        Swal.fire("Error", "No se pudo obtener la información", "error");
      });
  }
});*/

// Ver valores en USD
document.addEventListener("DOMContentLoaded", () => {
    const toggle = document.getElementById("toggleDolar");
    if (!toggle) return;

    let dolarVenta = null;

    toggle.addEventListener("change", async () => {
        const montos = document.querySelectorAll(".monto-cotizacion");

        if (toggle.checked) {
            if (!dolarVenta) {
                try {
                    const response = await fetch("https://dolarapi.com/v1/dolares/oficial");
                    const data = await response.json();
                    dolarVenta = data.venta;
                    console.log("Dólar oficial venta:", dolarVenta);
                } catch (error) {
                    console.error("Error al obtener el dólar:", error);
                    return;
                }
            }

            // Convertir a USD
            montos.forEach((celda) => {
                const ars = parseFloat(celda.dataset.precioArs);
                const usd = ars / dolarVenta;
                const valorFormateado =
                    Number.isInteger(usd) ? usd.toString() : usd.toFixed(2);

                celda.textContent = `$${valorFormateado} USD`;
            });
        } else {
            // Volver a ARS
            montos.forEach((celda) => {
                const ars = parseFloat(celda.dataset.precioArs);
                celda.textContent = `$${ars.toLocaleString("es-AR")} ARS`;
            });
        }
    });
});