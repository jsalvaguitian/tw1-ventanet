google.charts.load('current', { packages: ['corechart', 'table'] });
let chartsLoaded = false;
let domReady = false;

google.charts.setOnLoadCallback(() => {
    chartsLoaded = true;
    tryDrawDashboard();
});

document.addEventListener('DOMContentLoaded', () => {
    domReady = true;
    tryDrawDashboard();
});

function tryDrawDashboard() {
    if (chartsLoaded && domReady) {
        drawDashboard();
    }
}

function drawDashboard() {
    const dataFromServer = window.dashboardData;
    if (!dataFromServer) {
        console.error('No hay datos del servidor');
        return;
    }

    console.log('Iniciando dashboard con datos:', dataFromServer);

    // Verificar que los contenedores existan
    const containers = [
        'grafico_usuarios',
        'proveedores_rubro'
    ];

    containers.forEach(id => {
        const element = document.getElementById(id);
        if (!element) {
            console.error(`Contenedor ${id} no encontrado`);
        } else {
            console.log(`Contenedor ${id} encontrado`);
        }
    });

    // distribución usuarios
    try {
        drawPieChart(dataFromServer);
    } catch (e) {
        console.error('Error en drawPieChart:', e);
    }

    // proveedores por rubro
    try {
        const proveedoresData = JSON.parse(dataFromServer.proveedoresPorRubro || "[]");
        console.log('Datos proveedores parseados:', proveedoresData);
        if (proveedoresData.length > 0) {
            drawProveedoresPorRubro(proveedoresData);
        } else {
            console.warn('No hay datos de proveedores por rubro');
        }
    } catch (e) {
        console.error('Error al procesar proveedores por rubro:', e);
    }

    // tabla de usuarios
    if (document.getElementById('tabla_usuarios')) {
        try {
            const usuarios = JSON.parse(dataFromServer.usuarios || "[]");
            drawTable(usuarios);
        } catch (e) {
            console.error('Error en drawTable:', e);
        }
    }
}

function drawPieChart(dataFromServer) {
    const container = document.getElementById('grafico_usuarios');
    if (!container) {
        console.error('Contenedor grafico_usuarios no existe');
        return;
    }

    const data = google.visualization.arrayToDataTable([
        ['Tipo', 'Cantidad'],
        ['Clientes', dataFromServer.totalClientes],
        ['Proveedores', dataFromServer.totalProveedores]
    ]);

    const options = {
        title: 'Distribución de Usuarios',
        pieHole: 0.4,
        colors: ['#3498db', '#2ecc71'],
        chartArea: { width: '90%', height: '80%' },
        height: 400
    };

    const chart = new google.visualization.PieChart(container);
    chart.draw(data, options);
    console.log('Gráfico de usuarios dibujado correctamente');
}

function drawTable(usuarios) {
    const container = document.getElementById('tabla_usuarios');
    if (!container) {
        console.warn('Contenedor tabla_usuarios no existe');
        return;
    }

    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Nombre');
    data.addColumn('string', 'Apellido');
    data.addColumn('string', 'Rol');
    data.addColumn('string', 'Estado');

    usuarios.forEach(u => {
        data.addRow([u.nombre, u.apellido, u.rol, u.estado]);
    });

    const table = new google.visualization.Table(container);
    table.draw(data, { showRowNumber: true, width: '100%', height: 'auto' });
    console.log('Tabla de usuarios dibujada correctamente');
}

function drawProveedoresPorRubro(dataFromServer) {
    const container = document.getElementById('proveedores_rubro');
    if (!container) {
        console.error('Contenedor proveedores_rubro no existe');
        return;
    }

    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Rubro');
    data.addColumn('number', 'Cantidad');

    dataFromServer.forEach(r => {
        data.addRow([r.rubro, r.cantidad]);
    });

    const options = {
        title: 'Proveedores por Rubro',
        pieHole: 0.4,
        colors: ['#e74c3c', '#3498db', '#2ecc71', '#f39c12', '#9b59b6'],
        chartArea: { width: '90%', height: '80%' },
        height: 400
    };

    const chart = new google.visualization.PieChart(container);
    chart.draw(data, options);
    console.log('Gráfico de proveedores por rubro dibujado correctamente');
}

// Resto de tus funciones de filtros, búsqueda, etc.
// (las que ya tenías funcionando)

//export pdf luego usar dompdf
document.getElementById("btnExportarPDF").addEventListener("click", async () => {
    const { jsPDF } = window.jspdf;

    const elemento = document.getElementById("pdf-content");
    const canvas = await html2canvas(elemento, { scale: 2 });
    const imgData = canvas.toDataURL('image/png');

    const pdf = new jsPDF('p', 'mm', 'a4');
    const imgWidth = 190;
    const imgHeight = (canvas.height * imgWidth) / canvas.width;

    pdf.addImage(imgData, 'PNG', 10, 10, imgWidth, imgHeight);
    pdf.save('dashboard.pdf');
});

// Filtros
document.addEventListener("DOMContentLoaded", () => {

    const filas = document.querySelectorAll("#tablaUsuarios tbody tr");
    let filtroActual = "USUARIOS_TODOS";

    document.querySelectorAll(".stat").forEach(stat => {
        stat.addEventListener("click", () => {
            document.querySelectorAll(".stat").forEach(s => s.classList.remove("active"));
            stat.classList.add("active");

            filtroActual = stat.getAttribute("data-filter");
            aplicarFiltroAdmin();
        });
    });

    function aplicarFiltroAdmin() {

        filas.forEach(row => {

            const rol = row.children[5]?.textContent.trim().toUpperCase();       // columna 6
            const estado = row.children[6]?.textContent.trim().toUpperCase();    // columna 7

            let mostrar = true;

            switch (filtroActual) {
                case "USUARIOS_TODOS":
                    mostrar = true;
                    break;

                case "PROVEEDORES_TODOS":
                    mostrar = rol === "PROVEEDOR";
                    break;

                case "CLIENTES_TODOS":
                    mostrar = rol === "CLIENTE";
                    break;

                case "PENDIENTE":
                    mostrar = (rol === "PROVEEDOR" && estado === "PENDIENTE");
                    break;

                case "RECHAZADO":
                    mostrar = (rol === "PROVEEDOR" && estado === "RECHAZADO");
                    break;
            }

            row.style.display = mostrar ? "" : "none";
        });
    }

    aplicarFiltroAdmin();
});

// Filtros y searchbar
document.addEventListener("DOMContentLoaded", () => {
    const input = document.getElementById("buscarUsuario");
    const table = document.getElementById("tablaUsuarios");
    const rows = table.querySelectorAll("tbody tr");

    input.addEventListener("keyup", () => {
        const filter = input.value.toLowerCase();
        rows.forEach(row => {
            const cells = row.querySelectorAll("td");
            const match = Array.from(cells).some(cell =>
                cell.textContent.toLowerCase().includes(filter)
            );
            row.style.display = match ? "" : "none";
        });
    });

    // Activación de estadísticas
    document.querySelectorAll(".stat").forEach(stat => {
        stat.addEventListener("click", () => {
            document.querySelectorAll(".stat").forEach(s => s.classList.remove("stat-active"));
            stat.classList.add("stat-active");
        });
    });

    // Proveedores pendientes
    document.querySelectorAll(".fila-clickable").forEach(fila => {
        fila.addEventListener("click", () => {
            window.location.href = "/spring/admin/proveedores-pendientes";
        });
    });
});

// Ordenamiento
document.addEventListener("DOMContentLoaded", () => {
    const tabla = document.getElementById("tablaUsuarios");
    const headers = tabla.querySelectorAll("th.sortable");
    let sortState = {};

    headers.forEach(header => {
        header.addEventListener("click", () => {
            const colIndex = parseInt(header.getAttribute("data-col"));
            const tbody = tabla.querySelector("tbody");
            const rows = Array.from(tbody.querySelectorAll("tr"));

            const isAsc = sortState[colIndex] !== "asc";
            sortState[colIndex] = isAsc ? "asc" : "desc";

            headers.forEach(h => h.classList.remove("asc", "desc"));
            header.classList.add(isAsc ? "asc" : "desc");

            rows.sort((a, b) => {
                let valA = a.children[colIndex].textContent.trim();
                let valB = b.children[colIndex].textContent.trim();

                if (!isNaN(valA) && !isNaN(valB)) {
                    valA = parseFloat(valA);
                    valB = parseFloat(valB);
                }

                if (colIndex === 7) { 
                    valA = new Date(valA);
                    valB = new Date(valB);
                }

                return isAsc ? valA > valB ? 1 : -1 : valA < valB ? 1 : -1;
            });

            rows.forEach(r => tbody.appendChild(r));
        });
    });
});
/*
// top productos
function drawTopProductos(dataFromServer) {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Producto');
    data.addColumn('number', 'Cotizaciones');

    dataFromServer.forEach(p => {
        data.addRow([p.nombre, p.cantidadCotizada]);
    });

    const chart = new google.visualization.BarChart(document.getElementById('top_productos'));
    chart.draw(data, { title: 'Top 5 Productos Más Cotizados' });
}

// proveedores con mas productos
function drawTopProveedores(dataFromServer) {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Proveedor');
    data.addColumn('number', 'Productos');

    dataFromServer.forEach(p => {
        data.addRow([p.nombre, p.cantidad]);
    });

    const chart = new google.visualization.ColumnChart(document.getElementById('top_proveedores'));
    chart.draw(data, { title: 'Proveedores con Más Productos' });
}

// cotizaciones por estado
function drawCotizacionesEstado(dataFromServer) {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Estado');
    data.addColumn('number', 'Cantidad');

    Object.keys(dataFromServer).forEach(k => {
        data.addRow([k, dataFromServer[k]]);
    });

    const chart = new google.visualization.PieChart(document.getElementById('cotizaciones_estado'));
    chart.draw(data, { title: 'Estado de Cotizaciones', pieHole: 0.4 });
}*/