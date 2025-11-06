package com.tallerwebi.punta_a_punta;

import com.microsoft.playwright.*;
import com.tallerwebi.punta_a_punta.vistas.VistaLogin;
import com.tallerwebi.punta_a_punta.vistas.VistaNuevoUsuario;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class VistaLoginE2E {

    static Playwright playwright;
    static Browser browser;
    BrowserContext context;
    VistaLogin vistaLogin;

    @BeforeAll
    static void abrirNavegador() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false)
                .setSlowMo(200)
                .setExecutablePath(Paths.get("C:\\Program Files\\BraveSoftware\\Brave-Browser\\Application\\brave.exe")) // <-- uso de Paths.get
        );
    }

    @AfterAll
    static void cerrarNavegador() {
        playwright.close();
    }

    @BeforeEach
    void crearContextoYPagina() {
        context = browser.newContext();
        Page page = context.newPage();
        vistaLogin = new VistaLogin(page);
    }

    @AfterEach
    void cerrarContexto() {
        context.close();
    }

    @Test
    void deberiaMostrarVentanetEnNavbar() throws MalformedURLException {
        String texto = vistaLogin.obtenerTextoDeLaBarraDeNavegacion();
        assertThat("Ventanet", equalToIgnoringCase(texto));
    }

    @Test
    void loginInvalidoDaError() {
        vistaLogin.escribirEMAIL("damian@unlam.edu.ar");
        vistaLogin.escribirClave("unlam");
        vistaLogin.darClickEnIniciarSesion();
        String error = vistaLogin.obtenerMensajeDeError();
        assertThat("Error Usuario o clave incorrecta", equalToIgnoringCase(error));
    }

    @Test
    void loginValidoRedirigeADashboard() throws MalformedURLException {
        vistaLogin.escribirEMAIL("pedro.gomez@email.com");
        vistaLogin.escribirClave("Totoro1!");
        vistaLogin.darClickEnIniciarSesion();
        URL url = vistaLogin.obtenerURLActual();
        assertThat(url.getPath(), matchesPattern("^/spring/proveedor/dashboard-proveedor(?:;jsessionid=[^/\\s]+)?$"));
    }

    @Test
    void registrarUsuario() throws MalformedURLException {
        vistaLogin.darClickEnRegistrarse();
        VistaNuevoUsuario registro = new VistaNuevoUsuario(context.pages().get(0));

        registro.escribirNombre("Testx");
        registro.escribirApellido("Usernamex");
        registro.escribirEMAIL("testusernamex@mailinator.com");
        registro.escribirClave("Totoro1!");
        registro.escribirConfirmarClave("Totoro1!");
        registro.darClickEnRegistrarme();

        registro.getPage().waitForURL("**/spring/info-registro-resultado", new Page.WaitForURLOptions().setTimeout(10000));

        URL url = new URL(context.pages().get(0).url());
        assertThat(url.getPath(), matchesPattern("^/spring/info-registro-resultado(?:;jsessionid=[^/\\s]+)?$"));
    }

    @Test
    void registrarUsuarioExistenteDaError() {
        vistaLogin.darClickEnRegistrarse();
        VistaNuevoUsuario registro = new VistaNuevoUsuario(context.pages().get(0));

        registro.escribirNombre("Pedro");
        registro.escribirApellido("Gomez");
        registro.escribirEMAIL("pedro.gomez@email.com"); // usuario ya registrado
        registro.escribirClave("Totoro1!");
        registro.escribirConfirmarClave("Totoro1!");
        registro.darClickEnRegistrarme();

        registro.getPage().locator("p.error").waitFor(new Locator.WaitForOptions().setTimeout(10000));

        String error = registro.obtenerMensajeDeError();
        assertThat("El usuario ya existe", equalToIgnoringCase(error));
    }

    /*@Test
    void registrarUsuarioEIniciarSesion() throws MalformedURLException {
        vistaLogin.darClickEnRegistrarse();
        VistaNuevoUsuario registro = new VistaNuevoUsuario(context.pages().get(0));
        registro.escribirNombre("Test");
        registro.escribirApellido("Username");
        registro.escribirEMAIL("testusername@mailinator.com");
        registro.escribirClave("Totoro1!");
        registro.escribirConfirmarClave("Totoro1!");
        registro.darClickEnRegistrarme();

        // volver a login y probar login
        vistaLogin = new VistaLogin(context.pages().get(0));
        vistaLogin.escribirEMAIL("testusername@mailinator.com");
        vistaLogin.escribirClave("Totoro1!");
        vistaLogin.darClickEnIniciarSesion();
        URL url = vistaLogin.obtenerURLActual();
        assertThat(url.getPath(), matchesPattern("^/dashboard-proveedor(?:;jsessionid=[^/\\s]+)?$"));
    }*/
}