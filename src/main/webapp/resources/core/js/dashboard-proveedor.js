

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
