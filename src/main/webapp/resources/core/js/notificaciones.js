window.cargarNotificaciones = async function () {
    try {
        const res = await fetch('/spring/api/notificaciones/no-leidas');
        if (!res.ok) return;

        const notificaciones = await res.json();
        const contenido = document.getElementById('notif-dropdown-list');
        contenido.innerHTML = '';

        if (!notificaciones || notificaciones.length === 0) {
            contenido.innerHTML = '<div class="p-2 text-muted">No tienes notificaciones nuevas</div>';
            document.getElementById('badge-notif').innerText = '0';
            return;
        }
        

        document.getElementById('badge-notif').innerText = notificaciones.length;

        notificaciones.forEach(n => {
            const item = document.createElement('div');

            const fecha = parseFecha(n.fechaFormato);
            const fechaBienEscrita = fecha.toLocaleString('es-AR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
            });


            item.className = 'dropdown-item d-flex align-items-start';
          item.innerHTML = `
    <div>
        <div class="small text-muted">${fechaBienEscrita}</div>
        <a href="${n.url}" class="notif-link" data-id="${n.id}">
            <span class="notif-text">${n.mensaje}</span>
        </a>
    </div>
`;

            contenido.appendChild(item);
        });

       document.querySelectorAll('.notif-link').forEach(a => {
    a.addEventListener('click', async (ev) => {
        ev.stopPropagation(); // evita que el click cierre el dropdown o dispare eventos raros
        const id = ev.currentTarget.dataset.id; // <<--- ESTO SOLUCIONA EL undefined
        fetch('/spring/api/notificaciones/' + id + '/marcar-leida', { method: 'POST' });
    });
});


    } catch (e) {
        console.error(e);
    }
};

function parseFecha(fechaStr) {
    return new Date(fechaStr.replace(/\.\d+$/, ''));
}

function actualizarBadgeNotificaciones() {
    fetch('/spring/api/notificaciones/contar')
        .then(r => r.json())
        .then(count => {

            const badge = document.getElementById('badge-notif');

            if (!badge) return;

            badge.textContent = count;

            if (count > 0) {
                badge.classList.remove("badge-notif-hidden");
                badge.classList.add("bg-danger");
            } else {
                badge.classList.add("badge-notif-hidden");
                badge.classList.remove("bg-danger");
            }
        })
        .catch(err => console.error("Error cargando contador:", err));
}


setInterval(actualizarBadgeNotificaciones, 20000);


document.addEventListener("DOMContentLoaded", actualizarBadgeNotificaciones);


