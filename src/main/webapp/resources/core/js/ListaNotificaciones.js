document.addEventListener("DOMContentLoaded", cargarTodasLasNotificaciones);

async function cargarTodasLasNotificaciones() {
    

    const res = await fetch('/spring/api/notificaciones/todas-las-notificaciones');
    

    if (!res.ok) return;

    const notificaciones = await res.json();
    

    const contenido = document.getElementById('notif-list');
    

    contenido.innerHTML = '';

    if (!notificaciones || notificaciones.length === 0) {
        contenido.innerHTML = '<div class="p-2 text-muted">No tienes notificaciones</div>';
        return;
    }

    

    notificaciones.forEach(n => {
        

        const item = document.createElement('div');
        item.className = 'list-group-item';

        const fecha = new Date(n.fechaFormato.replace(/\.\d+$/, ''));
        const fechaBien = fecha.toLocaleString('es-AR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });

        item.innerHTML = `
            <div>
                <div class="small text-muted">${fechaBien}</div>
                <a href="${n.url}" class="notif-link" data-id="${n.id}">
                    ${n.mensaje}
                </a>
            </div>
        `;

        contenido.appendChild(item);

        
    });
}
