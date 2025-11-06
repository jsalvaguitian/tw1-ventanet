package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;

public class VistaLogin extends VistaWeb {

    public VistaLogin(Page page) {
        super(page);
        page.navigate("http://localhost:8080/spring/login");
    }

    public String obtenerTextoDeLaBarraDeNavegacion() {
        page.locator("header.site-header a.logo span.brand").waitFor();
        return page.textContent("header.site-header a.logo span.brand");
    }

    public String obtenerMensajeDeError() {
        Locator error = page.locator("p.error");
        error.waitFor();
        return error.textContent();
    }

    public void escribirEMAIL(String email) {
        this.escribirEnElElemento("input[name='email']", email);
    }

    public void escribirClave(String clave) {
        this.escribirEnElElemento("input[name='password']", clave);
    }

    public void darClickEnIniciarSesion() {
        this.darClickEnElElemento("button.btn[type='submit']");
    }

    public void darClickEnRegistrarse() {
        this.darClickEnElElemento("a:has-text('Crear cuenta')");
    }
}
