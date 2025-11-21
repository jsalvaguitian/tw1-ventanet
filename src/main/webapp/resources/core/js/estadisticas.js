google.charts.load('current', { packages: ['corechart'] }); 
google.charts.setOnLoadCallback(() => {
  drawCotizacionesChart();
  drawPromedioGeneralChart();
  drawProductosMasCotizadosChart();
  drawProductosMasCotizadosDeTodosLosProveedoresChart();
});

function drawCotizacionesChart() {
    const dataFromServer = window.estadisticasData;
    if (!dataFromServer) return;

    const data = google.visualization.arrayToDataTable([
        ['Estado', 'Cantidad'],
        ['Aprobadas', dataFromServer.APROBADA || 0],
        ['Pendientes', dataFromServer.PENDIENTE || 0],
        ['Rechazadas', dataFromServer.RECHAZADO || 0],
        ['Completadas', dataFromServer.COMPLETADA || 0]
    ]);

    const options = {
        title: 'Estado de Cotizaciones',
        pieHole: 0.4,
        colors: ['#3498db', '#f1c40f', '#e74c3c', '#2ecc71'], 
        chartArea: { width: '90%', height: '80%' }
    };

    const chart = new google.visualization.PieChart(document.getElementById('grafico_cotizaciones'));
    chart.draw(data, options);
}

function drawPromedioGeneralChart() {
    const dataFromServer = window.promedioData;
    if (!dataFromServer) return;

    const data = google.visualization.arrayToDataTable([
        ['Proveedor', 'Cantidad'],
        ['Tú', dataFromServer.proveedor],
        ['Promedio General', dataFromServer.promedio]
    ]);

    const options = {
        title: 'Comparación de Cotizaciones Completadas',
        legend: { position: 'none' },
        colors: ['#3498db', '#2ecc71'],
        bar: { groupWidth: '50%' },
        chartArea: { width: '70%', height: '70%' }
    };

    const chart = new google.visualization.ColumnChart(document.getElementById('grafico_comparacion'));
    chart.draw(data, options);
}

function drawProductosMasCotizadosChart() {
  const dataFromServer = window.productosMasCotizadosData;
;
  if (!dataFromServer) return;

  const dataArray = [['Producto', 'Cotizaciones']];
  for (const [nombre, cantidad] of Object.entries(dataFromServer)) {
    dataArray.push([nombre, cantidad]);
  }

  const data = google.visualization.arrayToDataTable(dataArray);
  const options = {
    title: 'Productos más cotizados',
    pieHole: 0.4,
    chartArea: { width: '90%', height: '80%' }
  };

  const chart = new google.visualization.PieChart(document.getElementById('grafico_productos'));
  chart.draw(data, options);
}

function drawProductosMasCotizadosDeTodosLosProveedoresChart() {
    const dataFromServer = window.productosMasCotizadosDeTodosLosProveedoresData;
    if (!dataFromServer) return;

    const dataArray = [['Producto', 'Cantidad']];
    for (const [nombre, cantidad] of Object.entries(dataFromServer)) {
      dataArray.push([nombre, cantidad]);
    }

    const data = google.visualization.arrayToDataTable(dataArray);
    const options = {
      title: 'Productos más cotizados (proveedores)',
      legend: { position: 'none' },
      colors: ['#3498db'],
      bar: { groupWidth: '60%' },
      chartArea: { width: '75%', height: '70%' },
      hAxis: { title: 'Producto' },
      vAxis: { title: 'Cantidad de cotizaciones' }
    }
     const chart = new google.visualization.ColumnChart(
      document.getElementById('grafico_todos_productos_mas_cotizados')
    );
    chart.draw(data, options);
}
   

