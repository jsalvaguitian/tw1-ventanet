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