document.getElementById("btnEnviarCotizacion").addEventListener("click", async () => {
            try {
                const respuesta = await fetch("/spring/cotizar/enviar", {
                    method: "POST"
                });

                const data = await respuesta.json();

                if (respuesta.ok) {
                    // Redirigir a la página de previsualización
                    window.location.href = "/spring/cotizacion/previsualizar";
                } else {
                    Swal.fire({
                        icon: "error",
                        title: "Error",
                        text: data.mensaje || "No se pudo generar la cotización."
                    });
                }
            } catch (error) {
                console.error(error);
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "Ocurrió un error al generar la cotización."
                });
            }
        });

        document.addEventListener("DOMContentLoaded", () => {
            const botones = document.querySelectorAll(".solicitar-cotizacion");

            const Toast = Swal.mixin({
                toast: true,
                position: "top-end",
                showConfirmButton: false,
                timer: 2500,
                timerProgressBar: true,
                didOpen: (toast) => {
                    toast.onmouseenter = Swal.stopTimer;
                    toast.onmouseleave = Swal.resumeTimer;
                }
            });

            botones.forEach(boton => {
                boton.addEventListener("click", async (e) => {
                    e.preventDefault();
                    const url = boton.getAttribute("href");
                    const respuesta = await fetch(url);
                    const data = await respuesta.json();

                    if (respuesta.status === 401) {
                            console.log('Data recibida del backend:', data);

                        if (data.tipo == "NO_LOGUEADO") {
                            mostrarModalPersonalizado({
                                id: "loginModal",
                                mensaje: "Debe iniciar sesión como cliente para cotizar productos",
                                textoBoton: "Ir a iniciar sesión",
                                linkBoton: "/spring/login",
                                tipoBoton: "btn-primary"
                            });
                        } else if (data.tipo == "PROVEEDOR") {
                            mostrarModalPersonalizado({
                                id: "soloClientesModal",
                                mensaje: "Solo los clientes pueden cotizar productos",
                                textoBoton: "Cerrar sesión",
                                linkBoton: "/spring/logout",
                                tipoBoton: "btn-danger"
                            });
                        }
                        return;
                    }

                    if (respuesta.ok) {
                        // Mostrar alerta elegante
                        Toast.fire({
                            icon: "success",
                            title: "Producto agregado al carrito"
                        });

                        // actualizar el carrito lateral
                        actualizarCarritoLateral(data.items);
                    } else {
                        Swal.fire({
                            icon: "error",
                            title: "Error",
                            text: "Hubo un problema al agregar el producto."
                        });
                    }
                })
            });



        });

        function actualizarCarritoLateral(items) {
            const contenedor = document.getElementById("carritoContenido");
            contenedor.innerHTML = "";

            if (items.length === 0) {
                contenedor.innerHTML = `<p class="text-muted">Aún no agregaste productos.</p>`;
                document.getElementById("btnEnviarCotizacion").disabled = true;
                return;
            }

            items.forEach(item => {
                contenedor.innerHTML += `
            <div class="d-flex align-items-center justify-content-between mb-2 border-bottom pb-2">
                <img src="${item.imagenUrl}" width="50" height="50" class="me-2 rounded">
                <div class="flex-grow-1">
                    <strong>${item.nombre}</strong><br>
                    <input type="number" min="1" value="${item.cantidad}" 
                        class="form-control form-control-sm w-50 mt-1" 
                        onchange="cambiarCantidad(${item.id}, this.value)">
                </div>
                <button class="btn btn-sm btn-danger" onclick="eliminarItem(${item.id})">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;
            });

            document.getElementById("btnEnviarCotizacion").disabled = false;
        }
/*
        function mostrarModalLogin() {
            const modalHtml = `
        <div class="modal fade" id="loginModal" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content text-center p-3">
                    <h5>Debe iniciar sesión para cotizar productos</h5>
                    <a href="/spring/login" class="btn btn-primary mt-3">Ir a iniciar sesión</a>
                </div>
            </div>
        </div>
    `;
            document.body.insertAdjacentHTML("beforeend", modalHtml);
            const modal = new bootstrap.Modal(document.getElementById("loginModal"));
            modal.show();
        }*/

        async function eliminarItem(idItem) {
            const respuesta = await fetch(`/spring/cotizar/eliminar/${idItem}`, { method: "DELETE" });

            if (respuesta.status === 401) {
                mostrarModalLogin();
                return;
            }

            const data = await respuesta.json();

            if (respuesta.ok) {
                /*Swal.fire({
                    icon: "success",
                    title: "Eliminado",
                    text: data.mensaje,
                    timer: 2000,
                    showConfirmButton: false
                });*/
                actualizarCarritoLateral(data.items);
            } /*else {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: data.mensaje || "No se pudo eliminar el producto."
                });
            }*/
        }

        function mostrarModalPersonalizado({ id, mensaje, textoBoton, linkBoton, tipoBoton }) {
            // Eliminar modal previo si existe
            const modalExistente = document.getElementById(id);
            if (modalExistente) modalExistente.remove();

            // Crear nuevo modal
            const modalHtml = `
        <div class="modal fade" id="${id}" tabindex="-1">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content text-center p-3">
                    <h5>${mensaje}</h5>
                </div>
            </div>
        </div>
    `;
            document.body.insertAdjacentHTML("beforeend", modalHtml);

            // Inicializar y mostrar
            const modal = new bootstrap.Modal(document.getElementById(id));
            modal.show();
        }



        async function cambiarCantidad(idItem, nuevaCantidad) {
            // Convertir a número entero
            nuevaCantidad = parseInt(nuevaCantidad);

            if (isNaN(nuevaCantidad) || nuevaCantidad < 1) {
                Swal.fire({
                    icon: "warning",
                    title: "Cantidad inválida",
                    text: "Debe ingresar una cantidad mayor o igual a 1."
                });
                return;
            }

            const respuesta = await fetch(`/spring/cotizar/cambiar-cantidad/${idItem}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ cantidad: nuevaCantidad })
            });

            if (respuesta.status === 401) {
                mostrarModalLogin();
                return;
            }

            const data = await respuesta.json();

            if (respuesta.ok) {
                actualizarCarritoLateral(data.items);
            } else {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: data.mensaje || "No se pudo actualizar la cantidad."
                });
            }
        }
