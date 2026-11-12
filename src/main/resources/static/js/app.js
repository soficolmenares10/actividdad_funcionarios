const API = '/api/v1/funcionarios';

// --- Autenticación (HTTP Basic) ---
const AUTH = sessionStorage.getItem('auth');
if (!AUTH) {
    window.location.replace('login.html');
}

function cerrarSesion() {
    sessionStorage.removeItem('auth');
    window.location.replace('login.html');
}
const $ = (id) => document.getElementById(id);
const form = $('form-funcionario');
const tbody = $('tabla-body');
const campos = ['nombre', 'cargo', 'departamento', 'telefono'];
const placeholders = {
    nombre: 'Escribe parte del nombre',
    departamento: 'Escribe el nombre completo del departamento',
    id: 'Escribe el ID del funcionario'
};
let modo = 'nombre';

function mostrarMensaje(texto, tipo) {
    const el = $('mensaje');
    el.textContent = texto;
    el.className = 'mensaje ' + tipo;
    el.hidden = false;
}

function ocultarMensaje() { $('mensaje').hidden = true; }

function actualizarContador(n) {
    $('contador').textContent = n === 1 ? '1 funcionario en pantalla' : n + ' funcionarios en pantalla';
}

async function peticion(url, opciones = {}) {
    let res;
    try {
        opciones.headers = { ...(opciones.headers || {}), 'Authorization': 'Basic ' + AUTH };
        res = await fetch(url, opciones);
    } catch (e) {
        throw new Error('No se pudo conectar con el servidor. Revisa que la aplicación esté en ejecución.');
    }
    if (res.status === 401) {
        cerrarSesion();
        throw new Error('Tu sesión no es válida. Inicia sesión de nuevo.');
    }
    if (res.status === 204) return { status: 204, data: null };
    let data = null;
    try { data = await res.json(); } catch (e) { /* sin cuerpo JSON */ }
    if (!res.ok) {
        if (res.status === 404) throw new Error('No existe un funcionario con ese ID.');
        if (res.status === 400) throw new Error('Datos inválidos: nombre, cargo y departamento son obligatorios.');
        throw new Error((data && (data.message || data.error)) || 'Error inesperado (' + res.status + ').');
    }
    return { status: res.status, data };
}

function iniciales(nombre) {
    return nombre.trim().split(/\s+/).slice(0, 2).map((p) => p[0].toUpperCase()).join('');
}

function celda(contenido, clase) {
    const td = document.createElement('td');
    if (clase) td.className = clase;
    if (contenido instanceof Node) td.appendChild(contenido); else td.textContent = contenido;
    return td;
}

function pintarTabla(lista) {
    tbody.replaceChildren();
    actualizarContador(lista.length);
    lista.forEach((f) => {
        const tr = document.createElement('tr');

        const persona = document.createElement('div');
        persona.className = 'persona';
        const av = document.createElement('span');
        av.className = 'avatar';
        av.textContent = iniciales(f.nombre);
        const nom = document.createElement('span');
        nom.textContent = f.nombre;
        persona.append(av, nom);

        const pildora = document.createElement('span');
        pildora.className = 'pildora';
        pildora.textContent = f.departamento;

        const acc = document.createElement('div');
        acc.className = 'fila-acciones';
        const bEd = document.createElement('button');
        bEd.className = 'fila-btn';
        bEd.textContent = 'Editar';
        bEd.addEventListener('click', () => cargarEnFormulario(f));
        const bDel = document.createElement('button');
        bDel.className = 'fila-btn borrar';
        bDel.textContent = 'Eliminar';
        bDel.addEventListener('click', () => eliminar(f));
        acc.append(bEd, bDel);

        tr.append(celda(f.id, 'id'), celda(persona), celda(f.cargo), celda(pildora), celda(f.telefono || '—'), celda(acc));
        tbody.appendChild(tr);
    });
}

// Devuelve true si hubo resultados.
async function listar(query = '', mensajeVacio = 'Aún no hay funcionarios registrados. Usa el formulario para agregar el primero.') {
    try {
        const { status, data } = await peticion(API + query);
        if (status === 204) {
            pintarTabla([]);
            mostrarMensaje(mensajeVacio, 'info');
            return false;
        }
        pintarTabla(data);
        return true;
    } catch (e) {
        pintarTabla([]);
        mostrarMensaje(e.message, 'error');
        return false;
    }
}

function cargarEnFormulario(f) {
    $('id').value = f.id;
    campos.forEach((c) => { $(c).value = f[c] ?? ''; });
    $('titulo-form').textContent = 'Editar funcionario #' + f.id;
    $('btn-guardar').textContent = 'Guardar cambios';
    $('nombre').focus();
}

function limpiarFormulario() {
    form.reset();
    $('id').value = '';
    $('titulo-form').textContent = 'Nuevo funcionario';
    $('btn-guardar').textContent = 'Guardar funcionario';
}

form.addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const cuerpo = {};
    campos.forEach((c) => { cuerpo[c] = $(c).value.trim(); });
    if (!cuerpo.nombre || !cuerpo.cargo || !cuerpo.departamento) {
        mostrarMensaje('Completa nombre, cargo y departamento para guardar.', 'error');
        return;
    }
    const id = $('id').value;
    const editando = id !== '';
    try {
        await peticion(editando ? `${API}/${id}` : API, {
            method: editando ? 'PUT' : 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(cuerpo)
        });
        limpiarFormulario();
        $('q').value = '';
        await listar();
        mostrarMensaje(editando ? 'Cambios guardados correctamente.' : 'Funcionario guardado correctamente.', 'exito');
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
});

function confirmar(nombre) {
    return new Promise((resolver) => {
        const d = $('dlg');
        $('dlg-texto').textContent = `Se eliminará a ${nombre} del directorio. Esta acción no se puede deshacer.`;
        d.returnValue = '';
        d.addEventListener('close', () => resolver(d.returnValue === 'si'), { once: true });
        d.showModal();
    });
}

async function eliminar(f) {
    if (!(await confirmar(f.nombre))) return;
    try {
        await peticion(`${API}/${f.id}`, { method: 'DELETE' });
        if ($('id').value === String(f.id)) limpiarFormulario();
        await listar();
        mostrarMensaje('Funcionario eliminado correctamente.', 'exito');
    } catch (e) {
        mostrarMensaje(e.message, 'error');
    }
}

document.querySelectorAll('.modo').forEach((b) => {
    b.addEventListener('click', () => {
        modo = b.dataset.modo;
        document.querySelectorAll('.modo').forEach((x) => x.classList.toggle('activo', x === b));
        $('q').placeholder = placeholders[modo];
        $('q').type = modo === 'id' ? 'number' : 'text';
        $('q').value = '';
        $('q').focus();
    });
});

$('form-busqueda').addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const texto = $('q').value.trim();
    if (!texto) { await listar(); return; }

    if (modo === 'id') {
        try {
            const { data } = await peticion(`${API}/${encodeURIComponent(texto)}`);
            pintarTabla([data]);
            mostrarMensaje('Funcionario encontrado.', 'exito');
        } catch (e) {
            pintarTabla([]);
            mostrarMensaje(e.message, 'error');
        }
        return;
    }
    const ok = modo === 'nombre'
        ? await listar('?nombre=' + encodeURIComponent(texto), `No hay funcionarios cuyo nombre contenga “${texto}”.`)
        : await listar('?departamento=' + encodeURIComponent(texto), `No hay funcionarios en el departamento “${texto}”. Escribe el nombre completo del área.`);
    if (ok) mostrarMensaje('Resultados para “' + texto + '”.', 'exito');
});

$('btn-todos').addEventListener('click', async () => {
    $('q').value = '';
    if (await listar()) mostrarMensaje('Mostrando el listado completo.', 'info');
});

$('btn-limpiar').addEventListener('click', () => { limpiarFormulario(); ocultarMensaje(); });
$('btn-salir').addEventListener('click', cerrarSesion);

if (AUTH) listar();
