$(document).on('input', '.cantidad', function () {
    const row = $(this).closest('tr');
    const precio = parseFloat(row.find('.precio-unitario').text());
    const cantidad = parseInt($(this).val()) || 0;
    const subtotal = precio * cantidad;
    row.find('.subtotal').text(subtotal.toFixed(2));

    // Recalcular el total de la tabla
    const tabla = row.closest('table');
    let total = 0;
    tabla.find('.subtotal').each(function () {
        total += parseFloat($(this).text()) || 0;
    });
    tabla.find('.total-cotizacion').text(total.toFixed(2));
});
window.abrirPopupMediosPago = abrirPopupMediosPago;

$(document).on('change', '.seleccionar-cotizacion', function () {
    const container = $(this).closest('.cotizacion-container');
    container.toggleClass('selected', this.checked);
});

// Antes de enviar el formulario, generar un JSON con todas las cotizaciones
$('#formCotizaciones').on('submit', function (e) {
    const cotizaciones = [];

    $('#cotizaciones-lista .cotizacion-container').each(function () {
        const cotizacionDiv = $(this);
        const cotizacionId = cotizacionDiv.data('id');
        const proveedorId = cotizacionDiv.data('proveedor-id');
        const clienteId = cotizacionDiv.data('cliente-id');
        const seleccionada = cotizacionDiv.find('.seleccionar-cotizacion').is(':checked');
        const medioPagoId = cotizacionDiv.find('.medio-pago-input').val();

        const items = [];
        cotizacionDiv.find('tbody tr').each(function () {
            const row = $(this);
            const idProducto = row.find('.producto-id').val();
            const cantidad = parseInt(row.find('.cantidad').val());

            items.push({
                idProducto,
                cantidad
            });
        });

        const montoTotal = parseFloat(cotizacionDiv.find('.total-cotizacion').text());

        cotizaciones.push({
            id: cotizacionId,
            proveedorId,
            clienteId,
            seleccionada,
            montoTotal,
            medioPagoId,
            items
        });
    });

    // Asignar el JSON al campo oculto
    $('#cotizacionesJson').val(JSON.stringify(cotizaciones));
});

// 1. Abre el popup con el listado de Medios de Pago
// Ahora recibe el elemento del botón (referencia 'this')
// Variable global para almacenar temporalmente la referencia del botón (para actualizar su texto)
let currentButton = null;

function abrirPopupMediosPago(buttonElement) {
    currentButton = buttonElement; // Guarda la referencia del botón

    const proveedorId = buttonElement.getAttribute('data-proveedor-id');

    if (!proveedorId) {
        Swal.fire('Error', 'No se pudieron obtener los IDs del proveedor.', 'error');
        return;
    }

    fetch(`/spring/proveedor/medios-pago/${proveedorId}`)
        .then(response => {
            if (!response.ok) throw new Error('Error al obtener los medios de pago.');
            return response.json();
        })
        .then(mediosPago => {
            if (mediosPago.length === 0) {
                Swal.fire('Info', 'El proveedor aún no tiene medios de pago asociados. Lo podrán definir cuando se pongan en contacto.', 'warning');
                return;
            }

            let checkboxesHTML = mediosPago.map(mp => {
                let cuotas = '';
                if (mp.tipo === 'CREDITO') {
                    cuotas = mp.cantidad_cuotas ? `(${mp.cantidad_cuotas} cuotas)` : '';
                }

                return `
                    <div class="form-check text-start mb-2">
                        <input class="form-check-input" type="radio" 
                               name="medioPago" 
                               id="mp-${mp.id}" 
                               value="${mp.id}" 
                               data-nombre="${mp.nombre}" required>
                        <label class="form-check-label" for="mp-${mp.id}">
                            ${mp.nombre} ${cuotas} 
                            ${mp.imagen ? `<img src="/spring/img/${mp.imagen}" alt="${mp.nombre}" style="width: 20px; margin-left: 10px;">` : ''}
                        </label>
                    </div>
                `;
            }).join('');

            Swal.fire({
                title: 'Seleccionar Método de Pago',
                html: `<form id="form-medios-pago">${checkboxesHTML}</form>`,
                focusConfirm: false,
                showCancelButton: true,
                confirmButtonText: 'Asignar',
                cancelButtonText: 'Cancelar',
                preConfirm: () => {
                    const selectedRadio = document.querySelector('input[name="medioPago"]:checked');
                    if (!selectedRadio) {
                        Swal.showValidationMessage('Debes seleccionar un método de pago.');
                        return false;
                    }
                    // Retornamos el ID y el Nombre del método de pago
                    return {
                        id: selectedRadio.value,
                        nombre: selectedRadio.getAttribute('data-nombre')
                    };
                }
            }).then((result) => {
                console.log("Resultado del popup MP:", result);
                if (result.isConfirmed) {
                    const { id, nombre } = result.value;

                    // 1. Encontrar el campo oculto específico de esta cotización
                    const inputField = document.getElementById(`medio-pago-id-${proveedorId}`);

                    // 2. Asignar el ID al campo oculto
                    if (inputField) {
                        inputField.value = id;
                    }

                    // 3. Actualizar la interfaz (etiqueta simple)
                    if (currentButton) {
                        // Buscar el contenedor padre para agregar la etiqueta
                        const cotizacionContainer = currentButton.closest('.cotizacion-container');
                        let mpDisplay = cotizacionContainer.querySelector('.medio-pago-display');

                        if (!mpDisplay) {
                            mpDisplay = document.createElement('span');
                            mpDisplay.className = 'medio-pago-display badge bg-info text-dark ms-3';
                            // Insertar la etiqueta junto al título del proveedor
                            currentButton.parentElement.querySelector('h4').insertAdjacentElement('afterend', mpDisplay);
                        }
                        mpDisplay.textContent = `MP: ${nombre}`;
                    }

                    //Swal.fire('Asignado', `Método de pago ${nombre} asignado a la cotización ${cotizacionId}.`, 'success');
                }
            });

        })
        .catch(error => {
            console.error("Error al obtener MP:", error);
            Swal.fire('Error', 'No se pudieron cargar los medios de pago.', 'error');
        });
}

// Función para verificar el estado de los checkboxes y actualizar el botón
function actualizarBotonConfirmar() {
    // Cuenta cuántos checkboxes con la clase '.seleccionar-cotizacion' están marcados
    const cotizacionesSeleccionadas = $('#cotizaciones-lista .seleccionar-cotizacion:checked').length;
    const btnConfirmar = $('#btnConfirmar');

    if (cotizacionesSeleccionadas > 0) {
        // Si hay al menos una seleccionada, habilitar el botón
        btnConfirmar.prop('disabled', false);
    } else {
        // Si no hay ninguna seleccionada, deshabilitar el botón
        btnConfirmar.prop('disabled', true);
    }
}

// ----------------------------------------------------
// 3. Inicialización y Event Listeners
// ----------------------------------------------------

// 1. Llamar a la función al cargar la página para establecer el estado inicial
$(document).ready(function () {
    actualizarBotonConfirmar();

    // 2. Adjuntar el evento 'change' a todos los checkboxes de selección
    // Se usa delegación (.on('change', 'selector')) para cotizaciones cargadas dinámicamente
    $('#cotizaciones-lista').on('change', '.seleccionar-cotizacion', function () {
        actualizarBotonConfirmar();
    });

});