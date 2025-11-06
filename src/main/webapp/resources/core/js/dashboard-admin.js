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
