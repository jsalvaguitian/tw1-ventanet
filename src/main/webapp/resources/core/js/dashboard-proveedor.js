// =============================
// DASHBOARD PROVEEDOR - Filtros replicados del cliente
// =============================

document.addEventListener('DOMContentLoaded', () => {
    console.log('[Proveedor] Init filtros');
    const stats = document.querySelectorAll('.stat');
    const searchInput = document.getElementById('searchInputProveedor') || document.querySelector(".form-control[placeholder^='Buscar']");
    const clienteSelect = document.getElementById('filterCliente');
    const montoMinInput = document.getElementById('filtroMonto');
    const montoMaxInput = document.getElementById('filtroMontoMax');
    const fechaCreacionDesdeInput = document.getElementById('filtroFechaCreacionDesde');
    const fechaCreacionHastaInput = document.getElementById('filtroFechaCreacionHasta');
    const fechaExpDesdeInput = document.getElementById('filtroFechaExpiracionDesde');
    const fechaExpHastaInput = document.getElementById('filtroFechaExpiracionHasta');
    const btnLimpiar = document.getElementById('btnLimpiarFiltros');
    const btnExport = document.getElementById('btnExportExcel');

    let currentFilter = 'TODOS';
    let searchText = '';
    let clienteFilterValue = '';
    let montoMinVal = null, montoMaxVal = null;
    let fechaCreacionDesde = null, fechaCreacionHasta = null, fechaExpDesde = null, fechaExpHasta = null;

    const normalizar = s => (s || '').trim().replace(/\s+/g, ' ').toLowerCase();
    const parseInputDate = v => { if (!v) return null; const m = v.match(/^(\d{4})-(\d{2})-(\d{2})$/); return m ? new Date(+m[1], +m[2] - 1, +m[3]) : null; };
    function parseFecha(txt) { if (!txt) return null; let b = txt.trim(); let mLat = b.match(/^(\d{2})\/(\d{2})\/(\d{4})$/); if (mLat) return new Date(+mLat[3], +mLat[2] - 1, +mLat[1]); let mIso = b.match(/^(\d{4})-(\d{2})-(\d{2})$/); if (mIso) return new Date(+mIso[1], +mIso[2] - 1, +mIso[3]); if (/^(\d{4})-(\d{2})-(\d{2})T\d{2}:\d{2}/.test(b) || /^(\d{4})-(\d{2})-(\d{2}) \d{2}:\d{2}/.test(b)) { if (b.includes(' ') && !b.includes('T')) b = b.replace(' ', 'T'); const d = new Date(b); if (!isNaN(d)) return new Date(d.getFullYear(), d.getMonth(), d.getDate()); } return null; }
    const getActiveTableSelector = () => document.querySelector('#tablaCotizaciones')?.offsetParent !== null ? '#tablaCotizaciones' : '#tablaLicitaciones';

    function aplicarFiltrosProveedor() {
        const tablaSel = getActiveTableSelector();
        const filas = document.querySelectorAll(`${tablaSel} tbody tr`);
        const esCustom = tablaSel === '#tablaLicitaciones';
        const idxCliente = esCustom ? 2 : 1;
        const idxMonto = esCustom ? 3 : 2;
        const idxEstado = esCustom ? 4 : 3;
        const idxCre = esCustom ? 5 : 4;
        const idxExp = esCustom ? 6 : 5;
        let cTot = 0, cPend = 0, cAprob = 0, cRech = 0, cComp = 0; const visibles = [];
        filas.forEach(r => {
            const estado = r.children[idxEstado]?.textContent.trim().toUpperCase();
            const clienteStr = normalizar(r.children[idxCliente]?.textContent);
            const textoFila = r.textContent.toLowerCase();
            const fechaCreStr = r.children[idxCre]?.textContent.trim();
            const fechaExpStr = r.children[idxExp]?.textContent.trim();
            const bBusqueda = searchText === '' || textoFila.includes(searchText);
            const bCliente = !clienteFilterValue || clienteStr === clienteFilterValue;
            let bMonto = true; if (montoMinVal !== null || montoMaxVal !== null) { const cell = r.children[idxMonto]; const raw = cell?.getAttribute('data-precio-ars'); const ars = raw ? parseFloat(raw) : NaN; const isUSD = document.getElementById('toggleDolar')?.checked; const tasa = window.dolarVentaGlobal || null; const minArs = (isUSD && tasa && montoMinVal !== null) ? montoMinVal * tasa : montoMinVal; const maxArs = (isUSD && tasa && montoMaxVal !== null) ? montoMaxVal * tasa : montoMaxVal; if (!isNaN(ars)) { if (minArs !== null && ars < minArs) bMonto = false; if (maxArs !== null && ars > maxArs) bMonto = false; } }
            let bCre = true; if (fechaCreacionDesde || fechaCreacionHasta) { const f = parseFecha(fechaCreStr); if (f) { if (fechaCreacionDesde && f < fechaCreacionDesde) bCre = false; if (fechaCreacionHasta && f > fechaCreacionHasta) bCre = false; } else bCre = false; }
            let bExp = true; if (fechaExpDesde || fechaExpHasta) { const f = parseFecha(fechaExpStr); if (f) { if (fechaExpDesde && f < fechaExpDesde) bExp = false; if (fechaExpHasta && f > fechaExpHasta) bExp = false; } else bExp = false; }
            const pasa = bBusqueda && bCliente && bMonto && bCre && bExp; if (pasa) { cTot++; switch (estado) { case 'PENDIENTE': cPend++; break; case 'APROBADA': cAprob++; break; case 'RECHAZADO': cRech++; break; case 'COMPLETADA': cComp++; break; } } const bEstado = currentFilter === 'TODOS' || estado === currentFilter; const visible = pasa && bEstado; r.style.display = visible ? '' : 'none'; if (visible) visibles.push(r);
        });
        (document.getElementById('totalEvents') || {}).textContent = cTot; (document.getElementById('pendingEvents') || {}).textContent = cPend; (document.getElementById('approvedEvents') || {}).textContent = cAprob; (document.getElementById('rejectedEvents') || {}).textContent = cRech; (document.getElementById('completedEvents') || {}).textContent = cComp;
        rebuildClienteOptions(visibles); rebuildMontosDatalist(visibles);
    }
    function populateClienteSelect() { if (!clienteSelect) return; const tablaSel = getActiveTableSelector(); const esCustom = tablaSel === '#tablaLicitaciones'; const idx = esCustom ? 2 : 1; const mapa = new Map(); document.querySelectorAll(`${tablaSel} tbody tr`).forEach(r => { const cell = r.children[idx]; if (!cell) return; const disp = cell.textContent.trim(); const key = normalizar(disp); if (key && !mapa.has(key)) mapa.set(key, disp); }); while (clienteSelect.options.length > 1) clienteSelect.remove(1);[...mapa.entries()].sort((a, b) => a[1].localeCompare(b[1], 'es')).forEach(([k, d]) => { const opt = document.createElement('option'); opt.value = k; opt.textContent = d; clienteSelect.appendChild(opt); }); }
    function rebuildClienteOptions(visibles) { if (!clienteSelect) return; const current = normalizar(clienteSelect.value); const mapa = new Map(); visibles.forEach(r => { const idT = r.closest('table')?.id; const idx = idT === 'tablaLicitaciones' ? 2 : 1; const cell = r.children[idx]; if (!cell) return; const disp = cell.textContent.trim(); const key = normalizar(disp); if (key && !mapa.has(key)) mapa.set(key, disp); }); while (clienteSelect.options.length > 1) clienteSelect.remove(1);[...mapa.entries()].sort((a, b) => a[1].localeCompare(b[1], 'es')).forEach(([k, d]) => { const opt = document.createElement('option'); opt.value = k; opt.textContent = d; clienteSelect.appendChild(opt); }); if (current && mapa.has(current)) clienteSelect.value = current; else { clienteSelect.value = ''; clienteFilterValue = ''; } }
    function rebuildMontosDatalist(visibles) { const dl = document.getElementById('listaMontos'); if (!dl) return; const montos = []; visibles.forEach(r => { const idT = r.closest('table')?.id; const idx = idT === 'tablaLicitaciones' ? 3 : 2; const raw = r.children[idx]?.getAttribute('data-precio-ars'); if (!raw) return; const n = parseFloat(raw); if (!isNaN(n)) montos.push(n); }); const unicos = [...new Set(montos)].sort((a, b) => a - b); dl.innerHTML = ''; const isUSD = document.getElementById('toggleDolar')?.checked; const tasa = window.dolarVentaGlobal || null; unicos.forEach(m => { const opt = document.createElement('option'); opt.value = (isUSD && tasa) ? (m / tasa).toFixed(2) : m; dl.appendChild(opt); }); if (unicos.length) { const minDisp = unicos[0], maxDisp = unicos[unicos.length - 1]; const suf = isUSD ? ' USD' : ' ARS'; const fmtMin = isUSD && tasa ? (minDisp / tasa).toFixed(2) : minDisp; const fmtMax = isUSD && tasa ? (maxDisp / tasa).toFixed(2) : maxDisp; if (montoMinInput && !montoMinInput.value) montoMinInput.placeholder = '≥ ' + fmtMin + suf; if (montoMaxInput && !montoMaxInput.value) montoMaxInput.placeholder = '≤ ' + fmtMax + suf; } else { if (montoMinInput && !montoMinInput.value) montoMinInput.placeholder = '≥'; if (montoMaxInput && !montoMaxInput.value) montoMaxInput.placeholder = '≤'; } }

    // Eventos
    stats.forEach(card => card.addEventListener('click', () => { stats.forEach(c => c.classList.remove('active')); card.classList.add('active'); currentFilter = card.getAttribute('data-filter'); aplicarFiltrosProveedor(); }));
    searchInput?.addEventListener('input', e => { searchText = e.target.value.toLowerCase(); aplicarFiltrosProveedor(); });
    populateClienteSelect();
    clienteSelect?.addEventListener('change', () => { clienteFilterValue = normalizar(clienteSelect.value); aplicarFiltrosProveedor(); });
    montoMinInput?.addEventListener('input', () => { const v = montoMinInput.value.trim(); montoMinVal = v === '' ? null : parseFloat(v); aplicarFiltrosProveedor(); });
    montoMaxInput?.addEventListener('input', () => { const v = montoMaxInput.value.trim(); montoMaxVal = v === '' ? null : parseFloat(v); aplicarFiltrosProveedor(); });
    [fechaCreacionDesdeInput, fechaCreacionHastaInput, fechaExpDesdeInput, fechaExpHastaInput].forEach(inp => inp?.addEventListener('change', () => { fechaCreacionDesde = parseInputDate(fechaCreacionDesdeInput?.value); fechaCreacionHasta = parseInputDate(fechaCreacionHastaInput?.value); fechaExpDesde = parseInputDate(fechaExpDesdeInput?.value); fechaExpHasta = parseInputDate(fechaExpHastaInput?.value); aplicarFiltrosProveedor(); }));
    btnLimpiar?.addEventListener('click', () => { clienteSelect && (clienteSelect.value = ''); clienteFilterValue = ''; montoMinInput && (montoMinInput.value = ''); montoMinVal = null; montoMaxInput && (montoMaxInput.value = ''); montoMaxVal = null; fechaCreacionDesdeInput && (fechaCreacionDesdeInput.value = ''); fechaCreacionDesde = null; fechaCreacionHastaInput && (fechaCreacionHastaInput.value = ''); fechaCreacionHasta = null; fechaExpDesdeInput && (fechaExpDesdeInput.value = ''); fechaExpDesde = null; fechaExpHastaInput && (fechaExpHastaInput.value = ''); fechaExpHasta = null; aplicarFiltrosProveedor(); });
    btnExport?.addEventListener('click', () => { const tablaAct = getActiveTableSelector(); const tabla = document.querySelector(tablaAct); if (!tabla) return; const visibles = Array.from(tabla.querySelectorAll('tbody tr')).filter(tr => tr.style.display !== 'none'); if (!visibles.length) { Swal.fire('Sin datos', 'No hay filas visibles para exportar.', 'info'); return; } const headers = Array.from(tabla.querySelectorAll('thead th')).map(th => th.textContent.trim()).filter(h => h.toLowerCase() !== 'acciones'); const rows = [headers]; visibles.forEach(tr => { const celdas = Array.from(tr.children).slice(0, headers.length); const vals = celdas.map(td => { let t = td.textContent.trim(); if (t.includes('"')) t = t.replace(/"/g, '""'); if (/[;\n,]/.test(t)) t = '"' + t + '"'; return t; }); rows.push(vals); }); const csv = '\uFEFF' + rows.map(r => r.join(';')).join('\n'); const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' }); const url = URL.createObjectURL(blob); const a = document.createElement('a'); const stamp = new Date().toISOString().slice(0, 19).replace(/[:T]/g, '-'); a.href = url; a.download = (tablaAct === '#tablaCotizaciones' ? 'cotizaciones_proveedor' : 'cotizaciones_custom_proveedor') + '_' + stamp + '.csv'; document.body.appendChild(a); a.click(); document.body.removeChild(a); URL.revokeObjectURL(url); });
    document.addEventListener('click', e => { const btn = e.target.closest('.btn-detalle-licitacion'); if (btn) { mostrarDetalleLicitacion(btn.getAttribute('data-id')); } });
    aplicarFiltrosProveedor();
    window.aplicarFiltrosProveedor = aplicarFiltrosProveedor;

    // Toggle USD (restaurado - antes se había eliminado al limpiar duplicados)
    const toggleDolar = document.getElementById('toggleDolar');
    if (toggleDolar) {
        let tasaUsd = null; // venta oficial
        toggleDolar.addEventListener('change', async () => {
            // Obtener la tasa sólo la primera vez que se activa
            if (toggleDolar.checked && !tasaUsd) {
                try {
                    const resp = await fetch('https://dolarapi.com/v1/dolares/oficial');
                    const data = await resp.json();
                    tasaUsd = data.venta || null;
                    if (tasaUsd) window.dolarVentaGlobal = tasaUsd;
                } catch (e) {
                    console.error('Error obteniendo dólar oficial', e);
                    Swal.fire('Error', 'No se pudo obtener la cotización del dólar.', 'error');
                    toggleDolar.checked = false; // revertir porque no se pudo convertir
                    return;
                }
            }
            const montos = document.querySelectorAll('.monto-cotizacion');
            montos.forEach(cell => {
                const ars = parseFloat(cell.dataset.precioArs);
                if (isNaN(ars)) return;
                if (toggleDolar.checked && tasaUsd) {
                    const usd = ars / tasaUsd;
                    cell.textContent = `$${usd.toFixed(2)} USD`;
                } else {
                    cell.textContent = `$${ars.toLocaleString('es-AR')} ARS`;
                }
            });
            // Recalcular placeholders y datalist según moneda
            aplicarFiltrosProveedor();
        });
    }
});

// =============================
// DETALLE / ACCIONES
// =============================

window.mostrarDetalleCotizacion = mostrarDetalleCotizacion;
window.mostrarDetalleLicitacion = mostrarDetalleLicitacion;
window.manejarAccionCotizacion = manejarAccionCotizacion;
window.manejarAccionLicitacion = manejarAccionLicitacion;
window.getEstadoHTML = getEstadoHTML;

function mostrarDetalleCotizacion(id) {
    const url = '/spring/cotizacion/detalle/' + id;
    // fetch(url).then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
    //     .then(cotizacion => {
    //         const estadoHTML = getEstadoHTML(cotizacion.estado);
    //         let botones = '';
    //         if (cotizacion.estado === 'PENDIENTE') {
    //             botones = `<div style=\"margin-top:20px;text-align:center;\">\n          <button class=\"btn btn-success me-3\" onclick=\"manejarAccionCotizacion(${cotizacion.id},'APROBADA')\">Aceptar</button>\n          <button class=\"btn btn-danger\" onclick=\"manejarAccionCotizacion(${cotizacion.id},'RECHAZADO')\">Rechazar</button>\n        </div>`;
    //         }
    //         let html = `<p><strong>Cliente:</strong> ${cotizacion.cliente.nombre} ${cotizacion.cliente.apellido}</p>\n      <p><strong>Estado:</strong> ${estadoHTML}</p>\n      <p><strong>Monto Total:</strong> $${cotizacion.montoTotal.toFixed(2)}</p>\n      <h5 style=\"margin-top:20px;\">Items de la Cotización</h5>\n      <table class=\"table table-bordered table-sm\"><thead><tr><th>Producto</th><th>Cantidad</th><th>P. Unitario</th><th>Subtotal</th></tr></thead><tbody>`;
    //         cotizacion.items.forEach(it => { const sub = it.cantidad * it.precioUnitario; html += `<tr><td>${it.producto.nombre}</td><td>${it.cantidad}</td><td>$${it.precioUnitario.toFixed(2)}</td><td>$${sub.toFixed(2)}</td></tr>`; });
    //         html += `</tbody></table>${botones}`;
    //         Swal.fire({ title: `Detalle de Cotización #${id}`, html: html, icon: 'info', width: '80%', confirmButtonText: 'Cerrar' });
    //     })
    //     .catch(e => { console.error('Error detalle cotización', e); Swal.fire('Error', 'No se pudo cargar el detalle: ' + e.message, 'error'); });
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
                     <button class=\"btn btn-success me-3\" onclick=\"manejarAccionCotizacion(${cotizacion.id},'APROBADA')\">Aceptar</button>\n
                        <button class="btn btn-danger" 
                                onclick="manejarAccionCotizacion(${cotizacion.id}, 'RECHAZADO')">
                            ❌ Rechazar
                        </button>
                    </div>
                `;
            }
            if (cotizacion.estado === 'APROBADA') {
                botonesAccion = `
                    <div style="margin-top: 25px; text-align: center;">
                     <button class=\"btn btn-success me-3\" onclick=\"manejarAccionCotizacion(${cotizacion.id},'COMPLETADA')\">Completar</button>\n                        
                    </div>
                `;
            }


            let medioDePagoHTML = '';
            if (cotizacion.medioDePago) {
                const mp = cotizacion.medioDePago;
                let cuotas = '';
                if (mp.tipo === 'CREDITO') {
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
                botonesAccion = `
                <div style="margin-top: 20px; text-align: center;">
                    <button id="btnDescargarPDF" class="btn btn-success">
                        <i class="bi bi-file-earmark-pdf"></i> Descargar PDF
                    </button>
                </div>
            `;
            }

            // Generar contenido HTML del detalle
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
                        <p style="margin: 2px 0;"><strong>${cotizacion.cliente?.nombre || 'Cliente desconocido'} ${cotizacion.cliente?.apellido || 'Cliente desconocido'}  </strong></p>
                        <p style="margin: 2px 0;">Tel: ${cotizacion.cliente?.telefono || '-'}</p>
                        <p style="margin: 2px 0;">Email: ${cotizacion.cliente?.email || '-'}</p>
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
                            <th style="border: 1px solid #ccc; padding: 8px;">Total</th>
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

            // Mostrar SweetAlert con el detalle
            Swal.fire({
                title: `Detalle de Cotización #${id}`,
                html: htmlContent,
                icon: 'info',
                width: '80%',
                showConfirmButton: true,
                confirmButtonText: 'Cerrar',
                didOpen: (popup) => {
                    setTimeout(() => {                        
                        const btnPDF = popup.querySelector("#btnDescargarPDF");                        
                        if (btnPDF) {
                            btnPDF.addEventListener("click", async () => {                                

                                const { jsPDF } = window.jspdf;
                                const doc = new jsPDF();
                                const pageWidth = doc.internal.pageSize.getWidth();

                                // CABECERA                       
                                // ---- Cargar logo del proveedor ----
                                let logoUrl = null;
                                let logoBase64 = null;

                                if (cotizacion.proveedor.origenLogo === "img") {
                                    logoUrl = `/spring/img/${cotizacion.proveedor.logoPath}`;
                                }
                                else if (cotizacion.proveedor.origenLogo === "uploads") {
                                    logoUrl = `/spring/uploads/${cotizacion.proveedor.logoPath}`;
                                }

                                if (logoUrl) {                                    
                                    try {
                                        logoBase64 = await loadImageAsBase64(logoUrl);
                                    } catch (err) {
                                        console.error("Error cargando logo:", err);
                                    }
                                }

                                // Insertar logo (columna izquierda)
                                if (logoBase64) {
                                    doc.addImage(logoBase64, "PNG", 10, 10, 40, 20);
                                }

                                doc.setFontSize(12);
                                doc.setFont("helvetica", "bold");
                                const columnaDerechaX = pageWidth - 80;

                                // Proveedor (ESQUINA SUPERIOR DERECHA)
                                const proveedorText = `Proveedor: ${cotizacion.proveedor.razonSocial}\nCUIT: ${cotizacion.proveedor.cuit || 'N/A'}\n${cotizacion.proveedor.sitioWeb || 'N/A'}`;

                                const proveedorX = pageWidth - doc.getTextWidth("Proveedor: " + cotizacion.proveedor.razonSocial) - 10;
                                doc.text(proveedorText, columnaDerechaX, 15);

                                // Cliente y Cotización en 2 columnas
                                doc.setFontSize(11);
                                doc.setFont("helvetica", "normal");

                                // Columna izquierda CLIENTE
                                let y = 35;
                                doc.text(`Cliente: ${cotizacion.cliente?.nombre || "N/A"}`, 10, y);
                                doc.text(`Teléfono: ${cotizacion.cliente?.telefono || "N/A"}`, 10, y + 7);

                                // Columna derecha COTIZACION + FECHA
                                const fechaActual = new Date().toLocaleString();
                                const datosDerecha =
                                    `Cotización #${cotizacion.id}\nFecha: ${fechaActual}`;

                                const derechaX = pageWidth - doc.getTextWidth("Cotización #" + cotizacion.id) - 20;
                                doc.text(datosDerecha, columnaDerechaX, y);

                                // Línea separadora CABECERA-CUERPO
                                doc.line(10, y + 15, pageWidth - 10, y + 15);

                                // CUERPO
                                y += 30;
                                doc.setFont("helvetica", "bold");
                                doc.setFontSize(13);
                                doc.text("Detalles de la cotización", 10, y);
                                y += 6;

                                doc.setFont("helvetica", "normal");
                                doc.setFontSize(11);

                                // Encabezado de tabla
                                doc.text("Producto", 10, y);
                                doc.text("Cant.", pageWidth / 2 - 10, y);
                                doc.text("Monto", pageWidth - 40, y);

                                y += 5;
                                doc.line(10, y, pageWidth - 10, y);
                                y += 7;

                                // Items
                                cotizacion.items.forEach(item => {
                                    const subtotal = item.cantidad * item.precioUnitario;

                                    doc.text(item.producto.nombre, 10, y);
                                    doc.text(String(item.cantidad), pageWidth / 2 - 10, y);
                                    doc.text(`$${subtotal.toFixed(2)}`, pageWidth - 40, y);
                                    y += 8;
                                });

                                // Línea separadora CUERPO-PIE
                                doc.line(10, y, pageWidth - 10, y);
                                y += 15;

                                // PIE
                                doc.setFont("helvetica", "bold");
                                doc.setFontSize(16);

                                const totalText = `TOTAL: $${cotizacion.montoTotal.toFixed(2)}`;
                                const totalX = pageWidth - doc.getTextWidth(totalText) - 10;

                                doc.text(totalText, totalX, y);

                                // Guardar PDF
                                doc.save(`cotizacion_${cotizacion.id}.pdf`);
                            });
                        }
                    }, 200);
                }
            });

        }).catch(error => {
            console.error("Error al cargar el detalle:", error);
            Swal.fire('Error', 'No se pudo cargar el detalle de la cotización: ' + error.message, 'error');
        });
}

function mostrarDetalleLicitacion(id) {
    const url = '/spring/licitacion/detalle/' + id;
    fetch(url).then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.json(); })
        .then(licitacion => {
            const estadoHTML = getEstadoHTML(licitacion.estado);
            let botones = '';
            if (licitacion.estado === 'PENDIENTE') {
                botones = `<div style=\"margin-top:20px;text-align:center;\">\n          <button class=\"btn btn-success me-3\" id=\"btnAprobar\">Aceptar</button>\n          <button class=\"btn btn-danger\" id=\"btnRechazar\">Rechazar</button>\n        </div>`;
            }
            const cantidad = licitacion.productoCustom.cantidad; let precioUnitario = licitacion.productoCustom.precio; let subtotal = cantidad * precioUnitario; let iva = subtotal * 0.21; let total = subtotal + iva; const editable = licitacion.estado === 'PENDIENTE';
            const html = `<div style=\"font-family:Arial;\">\n        <p><strong>Estado:</strong> ${estadoHTML}</p>\n        <table class=\"table table-bordered table-sm\"><thead><tr><th>Descripción</th><th>Cantidad</th><th>Precio Unitario</th><th>Color</th><th>Total</th></tr></thead><tbody>\n        <tr><td>${licitacion.productoCustom.descripcion}</td><td>${cantidad}</td><td><input type=\"number\" id=\"precioUnitarioInput\" ${editable ? '' : 'disabled'} value=\"${precioUnitario.toFixed(2)}\" step=\"0.01\" min=\"0\" style=\"width:100px;text-align:right\" /></td><td>${licitacion.productoCustom.color || '-'}</td><td id=\"totalItem\">$${subtotal.toFixed(2)}</td></tr>\n        </tbody></table>\n        <div style=\"text-align:right;\">\n          <p><strong>Base imponible:</strong> $<span id=\"base\">${subtotal.toFixed(2)}</span></p>\n          <p><strong>IVA (21%):</strong> $<span id=\"iva\">${iva.toFixed(2)}</span></p>\n          <p><strong>Total:</strong> $<span id=\"total\">${total.toFixed(2)}</span></p>\n        </div>${botones}</div>`;
            Swal.fire({
                title: `Cotización #${id}`, html: html, icon: 'info', width: '70%', confirmButtonText: 'Cerrar', didOpen: () => {
                    const precioInput = document.getElementById('precioUnitarioInput');
                    if (editable && precioInput) { precioInput.addEventListener('input', () => { const np = parseFloat(precioInput.value) || 0; const ns = np * cantidad; const niva = ns * 0.21; const nt = ns + niva; document.getElementById('totalItem').textContent = '$' + ns.toFixed(2); document.getElementById('base').textContent = ns.toFixed(2); document.getElementById('iva').textContent = niva.toFixed(2); document.getElementById('total').textContent = nt.toFixed(2); }); }
                    document.getElementById('btnAprobar')?.addEventListener('click', () => { const val = parseFloat(precioInput.value) || 0; if (val <= 0) { Swal.fire('Advertencia', 'Ingrese precio válido', 'warning'); return; } actualizarLicitacion(licitacion.id, 'APROBADA', val); });
                    document.getElementById('btnRechazar')?.addEventListener('click', () => actualizarLicitacion(licitacion.id, 'RECHAZADO', precioUnitario));
                }
            });
        })
        .catch(e => { console.error('Error detalle licitación', e); Swal.fire('Error', 'No se pudo cargar el detalle: ' + e.message, 'error'); });
}

function getEstadoHTML(estado) {
    let className = 'status-badge';
    switch (estado) {
        case 'PENDIENTE': className += ' status-pendiente'; break;
        case 'APROBADA': className += ' status-aprobada'; break;
        case 'RECHAZADO': className += ' status-rechazado'; break;
        case 'COMPLETADA': className += ' status-completada'; break;
    }
    return `<span class="${className}">${estado}</span>`;
}


function manejarAccionCotizacion(id, accion) { Swal.close(); Swal.fire({ title: `¿Confirmar pasar a estado ${accion}?`, text: `¿Estás seguro de pasar a estado ${accion} la cotización #${id}?`, icon: 'warning', showCancelButton: true, confirmButtonText: `Sí, ${accion}`, cancelButtonText: 'Cancelar' }).then(res => { if (res.isConfirmed) { fetch(`/spring/cotizacion/${id}/cambiar-estado/${accion}`, { method: 'POST' }).then(r => { if (!r.ok) throw new Error('Error estado ' + r.status); Swal.fire('Éxito', `Cotización #${id} -> ${accion}`, 'success').then(() => location.reload()); }).catch(e => Swal.fire('Error', e.message, 'error')); } }); }
function manejarAccionLicitacion(id, accion) { Swal.close(); Swal.fire({ title: `¿Confirmar pasar a estado ${accion}?`, text: `¿Pasar a estado ${accion} la Cotización a medida #${id}?`, icon: 'warning', showCancelButton: true, confirmButtonText: `Sí, ${accion}`, cancelButtonText: 'Cancelar' }).then(r => { if (r.isConfirmed) { fetch(`/spring/licitacion/${id}/cambiar-estado/${accion}`, { method: 'POST' }).then(resp => { if (!resp.ok) throw new Error('Error estado ' + resp.status); Swal.fire('Éxito', `Cotización a medida #${id} -> ${accion}`, 'success').then(() => location.reload()); }).catch(e => Swal.fire('Error', e.message, 'error')); } }); }
function actualizarLicitacion(id, nuevoEstado, nuevoPrecio) { fetch(`/spring/licitacion/${id}/cambiar-estado`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ nuevoEstado, precioUnitario: nuevoPrecio }) }).then(r => { if (!r.ok) throw new Error('HTTP ' + r.status); return r.text(); }).then(() => Swal.fire('Éxito', `Licitación #${id} -> ${nuevoEstado}`, 'success').then(() => location.reload())).catch(e => Swal.fire('Error', e.message, 'error')); }

// (Duplicate blocks removed for clarity.)