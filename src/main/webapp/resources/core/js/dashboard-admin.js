google.charts.load('current', { packages: ['corechart', 'table'] });
google.charts.setOnLoadCallback(drawDashboard);

function drawDashboard() {
     const dataFromServer = window.dashboardData;
    if (!dataFromServer) return;

    drawPieChart(dataFromServer);

    const usuarios = JSON.parse(dataFromServer.usuarios || "[]");
    drawTable(usuarios);
    /*const dataFromServer = window.dashboardData;
    if (!dataFromServer) return;

    drawPieChart(dataFromServer);
    drawTable(dataFromServer.usuarios);*/
}

function drawPieChart(dataFromServer) {
    const data = google.visualization.arrayToDataTable([
        ['Tipo', 'Cantidad'],
        ['Clientes', dataFromServer.totalClientes],
        ['Proveedores', dataFromServer.totalProveedores]
    ]);

    const options = {
        title: 'Distribución de Usuarios',
        pieHole: 0.4,
        colors: ['#3498db', '#2ecc71'],
        chartArea: { width: '90%', height: '80%' }
    };

    const chart = new google.visualization.PieChart(document.getElementById('grafico_usuarios'));
    chart.draw(data, options);
}

function drawTable(usuarios) {
    const data = new google.visualization.DataTable();
    data.addColumn('string', 'Nombre');
    data.addColumn('string', 'Apellido');
    data.addColumn('string', 'Rol');
    data.addColumn('string', 'Estado');

    usuarios.forEach(u => {
        data.addRow([u.nombre, u.apellido, u.rol, u.estado]);
    });

    const table = new google.visualization.Table(document.getElementById('tabla_usuarios'));
    table.draw(data, { showRowNumber: true, width: '100%', height: 'auto' });
}

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