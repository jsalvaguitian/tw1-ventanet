let descripcion, cantidad, btnEnviar;
const seleccionados = new Set(); // global para proveedores seleccionados

document.addEventListener("DOMContentLoaded", () => {
    descripcion = document.getElementById("descripcion");
    cantidad = document.getElementById("cantidad");
    btnEnviar = document.getElementById("btnEnviar");

    // Eventos de validación en tiempo real
    descripcion.addEventListener("input", validarFormulario);
    cantidad.addEventListener("input", validarFormulario);

    inicializarCarouselPorDefecto();
    listenerFiltroRubro();
    listenerFiltroRubroTodos();
    listenerMessages();
    listenerSubmitFormulario();
    validarFormulario(); // deshabilita el botón al iniciar
});

// Helper
function getCarouselEl() {
    return document.getElementById("carouselProveedores");
}

function inicializarCarouselPorDefecto() {
    const existingItems = Array.from(document.querySelectorAll(".proveedor-item"));
    if (existingItems.length === 0) return;

    const carouselInner = document.getElementById("proveedoresCarousel");
    carouselInner.innerHTML = "";

    const chunkSize = 8;
    for (let i = 0; i < existingItems.length; i += chunkSize) {
        const group = existingItems.slice(i, i + chunkSize);
        const slide = document.createElement("div");
        slide.className = `carousel-item ${i === 0 ? "active" : ""}`;
        const groupDiv = document.createElement("div");
        groupDiv.className = "d-flex justify-content-center flex-wrap gap-4";
        group.forEach(g => groupDiv.appendChild(g));
        slide.appendChild(groupDiv);
        carouselInner.appendChild(slide);
    }

    document.querySelectorAll(".proveedor-item").forEach(it => {
        if (it.dataset.id && seleccionados.has(it.dataset.id)) it.classList.add("selected");
    });

    reiniciarBootstrapCarousel();
    listenerProveedorItems();
}

function reiniciarBootstrapCarousel() {
    const carouselEl = getCarouselEl();
    if (!carouselEl) return;
    const inst = bootstrap.Carousel.getInstance(carouselEl);
    if (inst) inst.dispose();

    new bootstrap.Carousel(carouselEl, {
        interval: false,
        ride: false,
        pause: true,
        wrap: false
    });
}

function listenerFiltroRubroTodos() {
    const btnTodos = document.getElementById("btnTodos");
    const loader = document.getElementById("loader");
    const filterActive = true;
    if (!btnTodos) return;

    btnTodos.addEventListener("click", () => {
        loader.style.display = "block";
        fetch(`/spring/proveedor/filtrar/${filterActive}`)
            .then(res => res.ok ? res.json() : Promise.reject())
            .then(data => {
                loader.style.display = "none";
                renderizarCarousel(data);
            })
            .catch(() => {
                loader.style.display = "none";
                document.getElementById("proveedoresCarousel").innerHTML = `<p class="text-danger text-center">Error al cargar proveedores.</p>`;
            });
    });
}

function listenerFiltroRubro() {
    const loader = document.getElementById("loader");
    document.querySelectorAll(".filtro-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const rubro = btn.dataset.rubro;
            if (!rubro) return;

            loader.style.display = "block";
            fetch(`/spring/proveedores/filtrar/${encodeURIComponent(rubro)}`)
                .then(res => res.ok ? res.json() : Promise.reject())
                .then(data => {
                    loader.style.display = "none";
                    renderizarCarousel(data);
                })
                .catch(() => {
                    loader.style.display = "none";
                    document.getElementById("proveedoresCarousel").innerHTML = `<p class="text-danger text-center">Error al cargar proveedores.</p>`;
                });
        });
    });
}

function renderizarCarousel(data) {
    const carouselInner = document.getElementById("proveedoresCarousel");
    carouselInner.innerHTML = "";

    if (!data || data.length === 0) {
        carouselInner.innerHTML = `<div class="carousel-item active text-center"><p class="text-muted mt-3">No hay proveedores disponibles.</p></div>`;
        reiniciarBootstrapCarousel();
        return;
    }

    const chunkSize = 8;
    for (let i = 0; i < data.length; i += chunkSize) {
        const group = data.slice(i, i + chunkSize);
        const itemsHTML = group.map(p => {
            const selectedClass = seleccionados.has(String(p.id)) ? " selected" : "";
            return `<div class="proveedor-item text-center${selectedClass}" data-id="${p.id}" style="width:120px;">
                      <img src="/spring/${p.logoPath}" class="proveedor-logo" alt="Logo del proveedor">
                      <p class="mt-2 mb-0">${escapeHtml(p.razonSocial)}</p>
                   </div>`;
        }).join("");

        carouselInner.innerHTML += `
          <div class="carousel-item ${i === 0 ? "active" : ""}">
              <div class="d-flex justify-content-center flex-wrap gap-4">${itemsHTML}</div>
          </div>`;
    }

    reiniciarBootstrapCarousel();
    listenerProveedorItems();
}

function listenerProveedorItems() {
    document.querySelectorAll(".proveedor-item").forEach(item => {
        item.replaceWith(item.cloneNode(true));
    });

    document.querySelectorAll(".proveedor-item").forEach(item => {
        item.addEventListener("click", () => {
            const id = String(item.dataset.id);
            if (!id) return;

            if (seleccionados.has(id)) {
                seleccionados.delete(id);
                item.classList.remove("selected");
            } else {
                seleccionados.add(id);
                item.classList.add("selected");
            }

            validarFormulario(); // ✅ validamos cada vez que selecciona o deselecciona
        });
    });
}

function escapeHtml(str) {
    if (!str) return "";
    return String(str).replace(/[&<>"']/g, s => ({
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    }[s]));
}

/* ✅ VALIDACIÓN FINAL */
function validarFormulario() {
    if (!descripcion || !cantidad || !btnEnviar) return;

    const descripcionValida = descripcion.value.trim().length > 0;
    const cantidadValida = cantidad.value.trim().length > 0 && Number(cantidad.value) > 0;
    const proveedoresValidos = seleccionados.size > 0;

    btnEnviar.disabled = !(descripcionValida && cantidadValida && proveedoresValidos);
}

function listenerSubmitFormularioOld() {
    const form = document.getElementById("formPresupuesto");
    const btnEnviar = document.getElementById("btnEnviar");

    form.addEventListener("submit", async (e) => {
        e.preventDefault();

        // Construimos el DTO
        const licitacionDto = {
            montoTotal: 0, // o podés calcular algo si aplica
            fechaExpiracion: null, // opcional
            proveedoresIds: Array.from(seleccionados).map(Number),
            productoCustom: {
                descripcion: document.getElementById("descripcion").value,
                ancho: parseFloat(document.getElementById("ancho").value) || 0,
                alto: parseFloat(document.getElementById("alto").value) || 0,
                largo: parseFloat(document.getElementById("largo").value) || 0,
                espesor: parseFloat(document.getElementById("espesor").value) || 0,
                tipoMaterial: document.getElementById("tipoMaterial").value,
                color: document.getElementById("color").value,
                cantidad: parseInt(document.getElementById("cantidad").value),
            },
        };

        // Preparamos el FormData (JSON + archivo)
        const formData = new FormData();
        formData.append("licitacionJson", JSON.stringify(licitacionDto));

        const fileInput = document.getElementById("imagenFile");
        if (fileInput.files.length > 0) {
            formData.append("imagenFile", fileInput.files[0]);
        }

        btnEnviar.disabled = true;

        try {
            const response = await fetch("/spring/licitacion/confirmar", {
                method: "POST",
                body: formData,
            });

            if (!response.ok) throw new Error("Error en el envío del formulario");

            // Redirigimos o mostramos mensaje
            window.location.href = "/spring/dashboard-cliente";
        } catch (err) {
            Swal.fire('Error', "Hubo un error al enviar la solicitud de licitación.", 'error');
            
        } finally {
            btnEnviar.disabled = false;
        }
    });
}

function listenerSubmitFormulario() { 
    const form = document.getElementById("formPresupuesto");
    const btnEnviar = document.getElementById("btnEnviar");

    form.addEventListener("submit", (e) => {       
        if (seleccionados.size === 0) {
            e.preventDefault();
            Swal.fire("Atención", "Debe seleccionar al menos un proveedor.", "warning");
            return;
        }

        // Armar el DTO con los valores del formulario
        const licitacionDto = {
            montoTotal: 0,
            fechaExpiracion: null,
            proveedoresIds: Array.from(seleccionados).map(Number),
            productoCustom: {
                descripcion: document.getElementById("descripcion").value,
                ancho: parseFloat(document.getElementById("ancho").value) || 0,
                alto: parseFloat(document.getElementById("alto").value) || 0,
                largo: parseFloat(document.getElementById("largo").value) || 0,
                espesor: parseFloat(document.getElementById("espesor").value) || 0,
                tipoMaterial: document.getElementById("tipoMaterial").value,
                color: document.getElementById("color").value,
                cantidad: parseInt(document.getElementById("cantidad").value),
            },
        };

        // Convertir a JSON y asignarlo al input oculto
        const hiddenInput = document.getElementById("licitacionJson");
        hiddenInput.value = JSON.stringify(licitacionDto);

        btnEnviar.disabled = true;
    });
}


function listenerMessages(){
    const successMessage = document.getElementById("successMessage")?.value;
    const errorMessage = document.getElementById("errorMessage")?.value;

    if (successMessage) {
        Swal.fire({
            title: '✅ ¡Licitación creada!',
            html: `
                <div style="font-size: 1rem; color: #333;">
                    ${successMessage}<br>
                    <small style="color:#666;">Serás redirigido al panel de cliente en unos segundos...</small>
                </div>
            `,
            icon: 'success',
            background: '#f8f9fa',
            color: '#1a1a1a',
            showConfirmButton: false,
            timer: 5000,
            timerProgressBar: true,
            customClass: {
                popup: 'shadow-lg rounded-4 border border-light'
            },
            willClose: () => {
                window.location.href = '/spring/cliente/dashboard';
            }
        });
    }

    if (errorMessage) {
        Swal.fire({
            title: '⚠️ Error',
            text: errorMessage,
            icon: 'error',
            confirmButtonColor: '#0d6efd',
            background: '#fff5f5',
            color: '#1a1a1a',
            customClass: {
                popup: 'shadow-lg rounded-4 border border-danger'
            }
        });
    }
}