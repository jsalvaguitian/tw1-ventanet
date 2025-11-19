document.addEventListener("DOMContentLoaded", function () {
    const stats = document.querySelectorAll(".stat");
    const rows = document.querySelectorAll("#tablaCotizaciones tbody tr");
    const searchInput = document.querySelector(".form-control[placeholder^='Buscar']");
    const dateFilter = document.getElementById("dateFilter");
    // Botón opcional (puede no existir). El filtrado se aplica automáticamente al cambiar el select.
    const applyFilterBtn = document.getElementById("applyFilter");
    // Select proveedor (nuevo id unificado filterProveedor o id antiguo filtroProveedor)
    const proveedorSelect = document.getElementById('filterProveedor') || document.getElementById('filtroProveedor');

    let currentFilter = "TODOS"; // Filtro de estado actual
    let searchText = "";
    let dateRange = null;
    let proveedorFilterValue = ""; // Valor seleccionado de proveedor (normalizado)
    const montoMinInput = document.getElementById('filtroMonto');
    const montoMaxInput = document.getElementById('filtroMontoMax');
    let montoMinVal = null; // number | null
    let montoMaxVal = null; // number | null
    // Inputs fechas manuales
    const fechaCreacionDesdeInput = document.getElementById('filtroFechaCreacionDesde');
    const fechaCreacionHastaInput = document.getElementById('filtroFechaCreacionHasta');
    const fechaExpDesdeInput = document.getElementById('filtroFechaExpiracionDesde');
    const fechaExpHastaInput = document.getElementById('filtroFechaExpiracionHasta');
    let fechaCreacionDesde = null;
    let fechaCreacionHasta = null;
    let fechaExpDesde = null;
    let fechaExpHasta = null;

    // Normaliza nombre para comparación (trim + lower + colapsar espacios)
    function normalizarProveedor(nombre) {
        return (nombre || "").trim().replace(/\s+/g,' ').toLowerCase();
    }

    function populateProveedorSelect() {
        if (!proveedorSelect) return;
        // Recolectar valores únicos de ambas tablas
        const mapaDisplay = new Map(); // clave normalizada -> display original (primera aparición)
        const recolectar = (selectorTabla, idxCol) => {
            document.querySelectorAll(`${selectorTabla} tbody tr`).forEach(r => {
                const vRaw = r.children[idxCol]?.textContent; if (!vRaw) return;
                const key = normalizarProveedor(vRaw);
                if (key && !mapaDisplay.has(key)) mapaDisplay.set(key, vRaw.trim());
            });
        };
        recolectar('#tablaCotizaciones', 1); // Proveedor columna 1
        recolectar('#tablaLicitaciones', 2); // Proveedor columna 2 en licitaciones

        // Limpiar opciones existentes salvo placeholder
        while (proveedorSelect.options.length > 1) proveedorSelect.remove(1);
        // Ordenar por display usando locale es
        [...mapaDisplay.entries()].sort((a,b)=>a[1].localeCompare(b[1],'es')).forEach(([key, display]) => {
            const opt = document.createElement('option');
            opt.value = key; // guardamos normalizado
            opt.textContent = display; // mostramos formato original
            proveedorSelect.appendChild(opt);
        });
    }

    function endOfDay(d) {
        const dt = new Date(d);
        dt.setHours(23,59,59,999);
        return dt;
    }

    function obtenerRangoFechas(valor) {
        const hoy = new Date();
        hoy.setHours(0, 0, 0, 0); // normalizar inicio de hoy
        let inicio = null;
        let fin = endOfDay(hoy); // por defecto fin es hoy completo

        switch (valor) {
            case "today":
                inicio = new Date(hoy); // hoy 00:00
                fin = endOfDay(hoy);     // hoy 23:59:59
                break;
            case "yesterday":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 1); // ayer 00:00
                fin = endOfDay(inicio);               // ayer 23:59:59
                break;
            case "last7":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 7); // hace 7 días
                fin = endOfDay(hoy);                  // hasta hoy completo
                break;
            case "last30":
                inicio = new Date(hoy);
                inicio.setDate(inicio.getDate() - 30);
                fin = endOfDay(hoy);
                break;
            case "month":
                inicio = new Date(hoy.getFullYear(), hoy.getMonth(), 1); // primer día del mes
                fin = endOfDay(hoy);
                break;
            default:
                inicio = null; // sin filtro de fechas
                fin = null;
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
        // Contadores dinámicos (sin aplicar filtro de estado para distribución)
        let countTotal = 0;
        let countPendiente = 0;
        let countAprobada = 0;
        let countRechazado = 0;
        let countCompletada = 0;

    const visiblesFinal = []; // filas que pasan TODOS los filtros incluyendo estado (para narrowing)

    filas.forEach(row => {
            // Buscar índice de columna de estado y fecha (depende de la tabla)
            const esCustom = tablaActiva === "#tablaLicitaciones";
            const idxEstado = esCustom ? 3 : 3;
            // Fecha de CREACIÓN fija en índice 4 para ambas tablas
            const idxFechaCreacion = esCustom ? 4 : 4;
            // Índice proveedor (difiere entre tablas)
            const idxProveedor = esCustom ? 2 : 1;

            const estado = row.children[idxEstado].textContent.trim().toUpperCase();
            const textoFila = row.textContent.toLowerCase();
            const fechaStrCreacion = row.children[idxFechaCreacion]?.textContent.trim();
            // Índice expiración: misma posición (5) en ambas tablas
            const idxFechaExpiracion = esCustom ? 5 : 5;
            const fechaStrExp = row.children[idxFechaExpiracion]?.textContent.trim();
            const proveedorStr = normalizarProveedor(row.children[idxProveedor]?.textContent);

            // Primero evaluamos los filtros que NO son de estado para poder calcular distribución por estado.
            const coincideBusqueda = textoFila.includes(searchText);
            let coincideFecha = true;
            // Proveedor solo se filtra en tabla de cotizaciones
            let coincideProveedor = tablaActiva === '#tablaCotizaciones' ? (!proveedorFilterValue || proveedorStr === proveedorFilterValue) : true;
            // Filtro de monto solo aplica a tabla cotizaciones. Columna monto total index 2
            let coincideMonto = true;
            if (tablaActiva === '#tablaCotizaciones' && (montoMinVal !== null || montoMaxVal !== null)) {
                // Tomamos siempre el valor ARS original y adaptamos el umbral si está en modo USD
                const montoCell = row.children[2];
                const arsRaw = montoCell?.getAttribute('data-precio-ars');
                const numeroArs = arsRaw ? parseFloat(arsRaw) : NaN;
                const isUSD = document.getElementById('toggleDolar')?.checked;
                const tasa = window.dolarVentaGlobal || null;
                const umbralMinArs = (isUSD && tasa && montoMinVal !== null) ? montoMinVal * tasa : montoMinVal;
                const umbralMaxArs = (isUSD && tasa && montoMaxVal !== null) ? montoMaxVal * tasa : montoMaxVal;
                if (!isNaN(numeroArs)) {
                    if (umbralMinArs !== null && numeroArs < umbralMinArs) coincideMonto = false;
                    if (umbralMaxArs !== null && numeroArs > umbralMaxArs) coincideMonto = false;
                }
            }

            // Lógica de fechas de creación manual: si existen sobreescriben preset
            if (fechaCreacionDesde || fechaCreacionHasta) {
                const fecha = parseFecha(fechaStrCreacion);
                if (fecha) {
                    if (fechaCreacionDesde && fecha < fechaCreacionDesde) coincideFecha = false;
                    if (fechaCreacionHasta && fecha > fechaCreacionHasta) coincideFecha = false;
                } else { coincideFecha = false; }
            } else if (dateRange && dateRange.inicio) {
                const fecha = parseFecha(fechaStrCreacion);
                if (fecha) {
                    coincideFecha = fecha >= dateRange.inicio && (!dateRange.fin || fecha <= dateRange.fin);
                } else { coincideFecha = false; }
            }

            // Filtro de expiración independiente
            let coincideFechaExp = true;
            if (fechaExpDesde || fechaExpHasta) {
                const fExp = parseFecha(fechaStrExp);
                if (fExp) {
                    if (fechaExpDesde && fExp < fechaExpDesde) coincideFechaExp = false;
                    if (fechaExpHasta && fExp > fechaExpHasta) coincideFechaExp = false;
                } else { coincideFechaExp = false; }
            }

            const pasaNoEstado = (coincideBusqueda && coincideFecha && coincideFechaExp && coincideProveedor && coincideMonto);

            // Actualizar distribución si pasa filtros NO estado
            if (pasaNoEstado) {
                countTotal++;
                switch (estado) {
                    case 'PENDIENTE': countPendiente++; break;
                    case 'APROBADA': countAprobada++; break;
                    case 'RECHAZADO': countRechazado++; break;
                    case 'COMPLETADA': countCompletada++; break;
                }
            }

            // Estado se aplica solo para visibilidad final
            const coincideEstado = currentFilter === "TODOS" || estado === currentFilter;
            const visible = (pasaNoEstado && coincideEstado);
            row.style.display = visible ? "" : "none";
            if (visible) visiblesFinal.push(row);
        });

        // Actualizar indicadores (si existen)
        const elTotal = document.getElementById('totalEvents');
        const elPend = document.getElementById('pendingEvents');
        const elAprob = document.getElementById('approvedEvents');
        const elRech = document.getElementById('rejectedEvents');
        const elComp = document.getElementById('completedEvents');
        if (elTotal) elTotal.textContent = countTotal;
        if (elPend) elPend.textContent = countPendiente;
        if (elAprob) elAprob.textContent = countAprobada;
        if (elRech) elRech.textContent = countRechazado;
        if (elComp) elComp.textContent = countCompletada;

        // Narrowing dinámico de opciones (según subset final incluyendo estado activo)
        if (tablaActiva === '#tablaCotizaciones') {
            rebuildProveedorOptions(visiblesFinal);
            rebuildMontosDatalist(visiblesFinal);
        }
    }

    // Aplicar automáticamente al cambiar el select
    if (dateFilter) {
        dateFilter.addEventListener("change", () => {
            dateRange = obtenerRangoFechas(dateFilter.value);
            aplicarFiltros();
        });
        // Filtro inicial al cargar (si no es "all")
        dateRange = obtenerRangoFechas(dateFilter.value);
        aplicarFiltros();
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

    searchInput.addEventListener("input", (e) => {
        searchText = e.target.value.toLowerCase();
        aplicarFiltros();
    });

    if (proveedorSelect) {
        populateProveedorSelect();
        proveedorSelect.addEventListener('change', () => {
            proveedorFilterValue = normalizarProveedor(proveedorSelect.value);
            aplicarFiltros();
        });
    }

    // --- Populate datalist for montos únicos y placeholders rango ---
    function rebuildMontosDatalist(filasVisibles) {
        const datalist = document.getElementById('listaMontos');
        if (!datalist) return;
        const montos = [];
        filasVisibles.forEach(r => {
            const cell = r.querySelector('.monto-cotizacion');
            const raw = cell?.getAttribute('data-precio-ars');
            if (!raw) return;
            const num = parseFloat(raw);
            if (!isNaN(num)) montos.push(num);
        });
        const unicos = [...new Set(montos)].sort((a,b)=>a-b);
        datalist.innerHTML = '';
        unicos.forEach(m => {
            const opt = document.createElement('option');
            // Si estamos en USD mostrar valor convertido, pero el input numérico seguirá interpretando lo que se ve
            const isUSD = document.getElementById('toggleDolar')?.checked;
            const tasa = window.dolarVentaGlobal || null;
            opt.value = (isUSD && tasa) ? (m / tasa).toFixed(2) : m;
            datalist.appendChild(opt);
        });
        if (unicos.length) {
            const isUSD = document.getElementById('toggleDolar')?.checked;
            const tasa = window.dolarVentaGlobal || null;
            const minDisp = unicos[0];
            const maxDisp = unicos[unicos.length-1];
            const suf = isUSD ? ' USD' : ' ARS';
            const fmtMin = isUSD && tasa ? (minDisp / tasa).toFixed(2) : minDisp;
            const fmtMax = isUSD && tasa ? (maxDisp / tasa).toFixed(2) : maxDisp;
            if (montoMinInput && !montoMinInput.value) montoMinInput.placeholder = '≥ ' + fmtMin + suf;
            if (montoMaxInput && !montoMaxInput.value) montoMaxInput.placeholder = '≤ ' + fmtMax + suf;
        } else {
            if (montoMinInput && !montoMinInput.value) montoMinInput.placeholder = '≥';
            if (montoMaxInput && !montoMaxInput.value) montoMaxInput.placeholder = '≤';
        }
    }

    function rebuildProveedorOptions(filasVisibles) {
        if (!proveedorSelect) return;
        // Guardar selección actual normalizada
        const currentSelNorm = normalizarProveedor(proveedorSelect.value);
        const uniqueMap = new Map();
        filasVisibles.forEach(r => {
            const provCell = r.children[1];
            if (!provCell) return;
            const display = provCell.textContent.trim();
            const key = normalizarProveedor(display);
            if (key && !uniqueMap.has(key)) uniqueMap.set(key, display);
        });
        // Limpiar conservando la primera opción (Todos)
        while (proveedorSelect.options.length > 1) proveedorSelect.remove(1);
        [...uniqueMap.entries()].sort((a,b)=>a[1].localeCompare(b[1],'es')).forEach(([key, display]) => {
            const opt = document.createElement('option');
            opt.value = key;
            opt.textContent = display;
            proveedorSelect.appendChild(opt);
        });
        // Restaurar selección si todavía existe; sino reset a Todos
        if (currentSelNorm && uniqueMap.has(currentSelNorm)) {
            proveedorSelect.value = currentSelNorm;
        } else {
            proveedorSelect.value = '';
            proveedorFilterValue = '';
        }
    }

    // Listeners monto min/max
    if (montoMinInput) {
        montoMinInput.addEventListener('input', () => {
            const v = montoMinInput.value.trim();
            montoMinVal = v === '' ? null : parseFloat(v);
            aplicarFiltros();
        });
    }
    if (montoMaxInput) {
        montoMaxInput.addEventListener('input', () => {
            const v = montoMaxInput.value.trim();
            montoMaxVal = v === '' ? null : parseFloat(v);
            aplicarFiltros();
        });
    }

    // Listeners fechas manuales creación
    const parseInputDate = (val) => {
        if (!val) return null;
        // val formato yyyy-MM-dd -> crear fecha local sin desfase
        const m = val.match(/^(\d{4})-(\d{2})-(\d{2})$/);
        return m ? new Date(+m[1], +m[2]-1, +m[3]) : null;
    };

    function actualizarFechasYFiltrar() {
        fechaCreacionDesde = parseInputDate(fechaCreacionDesdeInput?.value);
        fechaCreacionHasta = parseInputDate(fechaCreacionHastaInput?.value);
        fechaExpDesde = parseInputDate(fechaExpDesdeInput?.value);
        fechaExpHasta = parseInputDate(fechaExpHastaInput?.value);
        aplicarFiltros();
    }

    [fechaCreacionDesdeInput, fechaCreacionHastaInput, fechaExpDesdeInput, fechaExpHastaInput].forEach(inp => {
        if (inp) inp.addEventListener('change', actualizarFechasYFiltrar);
    });

    // Botón limpiar filtros
    const btnLimpiar = document.getElementById('btnLimpiarFiltros');
    if (btnLimpiar) {
        btnLimpiar.addEventListener('click', () => {
            // Reset valores
            proveedorSelect && (proveedorSelect.value = '');
            proveedorFilterValue = '';
            montoMinInput && (montoMinInput.value = ''); montoMinVal = null;
            montoMaxInput && (montoMaxInput.value = ''); montoMaxVal = null;
            fechaCreacionDesdeInput && (fechaCreacionDesdeInput.value = ''); fechaCreacionDesde = null;
            fechaCreacionHastaInput && (fechaCreacionHastaInput.value = ''); fechaCreacionHasta = null;
            fechaExpDesdeInput && (fechaExpDesdeInput.value = ''); fechaExpDesde = null;
            fechaExpHastaInput && (fechaExpHastaInput.value = ''); fechaExpHasta = null;
            // Si hay preset, reseteamos a 'all'
            if (dateFilter) { dateFilter.value = 'all'; dateRange = obtenerRangoFechas('all'); }
            aplicarFiltros();
        });
    }

    // Exportar a CSV (compatible Excel)
    const btnExport = document.getElementById('btnExportExcel');
    if (btnExport) {
        btnExport.addEventListener('click', () => {
            // Detectar tabla activa igual que en aplicarFiltros
            const tablaActiva =
                document.querySelector('#tablaCotizaciones')?.offsetParent !== null
                    ? '#tablaCotizaciones'
                    : '#tablaLicitaciones';
            const tabla = document.querySelector(tablaActiva);
            if (!tabla) return;

            const filasVisibles = Array.from(tabla.querySelectorAll('tbody tr'))
                .filter(tr => tr.style.display !== 'none');
            if (!filasVisibles.length) {
                Swal.fire('Sin datos', 'No hay filas visibles para exportar.', 'info');
                return;
            }
            // Headers (excluir última columna Acciones)
            const headers = Array.from(tabla.querySelectorAll('thead th'))
                .map(th => th.textContent.trim())
                .filter(h => h.toLowerCase() !== 'acciones');

            const rowsCsv = [headers];
            filasVisibles.forEach(tr => {
                const celdas = Array.from(tr.children).slice(0, headers.length); // excluir acciones
                const vals = celdas.map(td => {
                    let txt = td.textContent.trim();
                    // Escapar comillas
                    if (txt.includes('"')) txt = txt.replace(/"/g,'""');
                    // Envolver si contiene separadores
                    if (/[;,\n]/.test(txt)) txt = '"' + txt + '"';
                    return txt;
                });
                rowsCsv.push(vals);
            });
            const sep = ';'; // Excel latino suele abrir bien con ;
            const csvContent = '\uFEFF' + rowsCsv.map(r => r.join(sep)).join('\n');
            const blob = new Blob([csvContent], {type: 'text/csv;charset=utf-8;'});
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            const fechaStamp = new Date().toISOString().slice(0,19).replace(/[:T]/g,'-');
            a.download = (tablaActiva === '#tablaCotizaciones' ? 'cotizaciones' : 'cotizaciones_custom') + '_' + fechaStamp + '.csv';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        });
    }

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

    try {
        const response = await fetch(url);
        if (!response.ok) throw new Error(`HTTP error! status: ${response.status}`);
        const cotizacion = await response.json();
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

                <p><strong>Estado:</strong> ${estadoHTML}</p>

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

    } catch (error) {
        console.error("Error al cargar el detalle:", error);
        Swal.fire('Error', 'No se pudo cargar el detalle de la cotización: ' + error.message, 'error');
    }
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
                if (isNaN(ars) || !dolarVenta) return;
                const usd = ars / dolarVenta;
                const valorFormateado = Number.isInteger(usd) ? usd.toString() : usd.toFixed(2);
                celda.textContent = `$${valorFormateado} USD`;
            });
            window.dolarVentaGlobal = dolarVenta;
        } else {
            // Volver a ARS
            montos.forEach((celda) => {
                const ars = parseFloat(celda.dataset.precioArs);
                celda.textContent = `$${ars.toLocaleString("es-AR")} ARS`;
            });
        }
        // Reaplicar filtros con interpretación adaptada (min/max como USD cuando toggle activo)
        aplicarFiltros();
    });
});

// --- Parsing robusto de fecha (solo día) ---
// Acepta formatos: AAAA-MM-DD (local), AAAA-MM-DDTHH:mm[:ss], AAAA-MM-DD HH:mm[:ss], dd/MM/yyyy
// IMPORTANTE: No usar new Date("YYYY-MM-DD") porque interpreta UTC y resta horas cambiando el día en zonas GMT-3.
function parseFecha(txt) {
    if (!txt) return null;
    let base = txt.trim();

    // Formato latino dd/MM/yyyy
    let mLat = base.match(/^(\d{2})\/(\d{2})\/(\d{4})$/);
    if (mLat) return new Date(+mLat[3], +mLat[2]-1, +mLat[1]);

    // ISO sólo fecha AAAA-MM-DD (crear fecha local sin desfase)
    let mIsoOnly = base.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (mIsoOnly) return new Date(+mIsoOnly[1], +mIsoOnly[2]-1, +mIsoOnly[3]);

    // ISO con hora -> normalizar quitando tiempo (se interpreta en local)
    if (/^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}/.test(base) || /^\d{4}-\d{2}-\d{2} \d{2}:\d{2}/.test(base)) {
        if (base.includes(' ') && !base.includes('T')) base = base.replace(' ', 'T');
        const d = new Date(base);
        if (!isNaN(d)) return new Date(d.getFullYear(), d.getMonth(), d.getDate());
    }
    return null;
}