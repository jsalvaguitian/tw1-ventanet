package com.tallerwebi.punta_a_punta.vistas;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class VistaNuevoUsuario {

    private final Page page;

    public VistaNuevoUsuario(Page page) {
        this.page = page;
        this.page.navigate("http://localhost:8080/spring/nuevo-usuario");
    }

    public void escribirNombre(String nombre) {
        page.fill("input[name='nombre']", nombre);
    }

    public void escribirApellido(String apellido) {
        page.fill("input[name='apellido']", apellido);
    }

    public void escribirEMAIL(String email) {
        page.fill("input[name='email']", email);
    }

    public void escribirClave(String clave) {
        page.fill("input[name='password']", clave);
    }

    public void escribirConfirmarClave(String clave) {
        page.fill("input[name='confirmarPassword']", clave);
    }

    public void darClickEnRegistrarme() {
        page.click("button:has-text('Registrarme')");
    }

    public String obtenerMensajeDeError() {
        Locator error = page.locator("p.error");
        error.waitFor();
        return error.textContent();
    }
    
    public Page getPage() {
        return this.page;
    }
}
