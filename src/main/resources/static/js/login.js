const $ = (id) => document.getElementById(id);

// Si ya hay una sesión guardada, ir directo al directorio.
if (sessionStorage.getItem('auth')) {
    window.location.replace('index.html');
}

function mostrarError(texto) {
    const el = $('login-error');
    el.textContent = texto;
    el.hidden = false;
}

// Codifica "usuario:clave" en Base64 respetando UTF-8 (tildes, ñ, etc.).
function basicToken(usuario, clave) {
    const bytes = new TextEncoder().encode(usuario + ':' + clave);
    let binario = '';
    bytes.forEach((b) => { binario += String.fromCharCode(b); });
    return btoa(binario);
}

$('form-login').addEventListener('submit', async (ev) => {
    ev.preventDefault();
    const usuario = $('usuario').value.trim();
    const clave = $('clave').value;
    if (!usuario || !clave) {
        mostrarError('Escribe tu usuario y tu contraseña.');
        return;
    }

    const token = basicToken(usuario, clave);
    const boton = $('btn-entrar');
    boton.disabled = true;
    $('login-error').hidden = true;

    try {
        const res = await fetch('/api/v1/auth/me', {
            headers: { 'Authorization': 'Basic ' + token }
        });
        if (res.ok) {
            sessionStorage.setItem('auth', token);
            window.location.replace('index.html');
            return;
        }
        mostrarError(res.status === 401
            ? 'Usuario o contraseña incorrectos.'
            : 'Error inesperado (' + res.status + ').');
    } catch (e) {
        mostrarError('No se pudo conectar con el servidor. Revisa que la aplicación esté en ejecución.');
    } finally {
        boton.disabled = false;
    }
});
