package com.tallerwebi.presentacion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.tallerwebi.dominio.entidades.Alto;
import com.tallerwebi.dominio.entidades.Ancho;
import com.tallerwebi.dominio.entidades.Cliente;
import com.tallerwebi.dominio.entidades.Color;
// TipoVentana import removed (duplicate)
import com.tallerwebi.dominio.entidades.Cotizacion;
import com.tallerwebi.dominio.entidades.CotizacionItem;
import com.tallerwebi.dominio.entidades.Localidad;
import com.tallerwebi.dominio.entidades.MaterialDePerfil;
import com.tallerwebi.dominio.entidades.Partido;
import com.tallerwebi.dominio.entidades.Presupuesto;
import com.tallerwebi.dominio.entidades.PresupuestoItem;
import com.tallerwebi.dominio.entidades.Producto;
import com.tallerwebi.dominio.entidades.TipoProducto;
import com.tallerwebi.dominio.entidades.TipoVentana;
import com.tallerwebi.dominio.enums.EstadoCotizacion;
import com.tallerwebi.dominio.excepcion.NoHayProductoExistente;
import com.tallerwebi.dominio.excepcion.ProductoExistente;
import com.tallerwebi.dominio.excepcion.UsuarioInexistenteException;
import com.tallerwebi.dominio.servicios.ServicioCotizacion;
import com.tallerwebi.dominio.servicios.ServicioProducto;
import com.tallerwebi.dominio.servicios.ServicioTablas;
import com.tallerwebi.dominio.servicios.ServicioTipoProducto;
import com.tallerwebi.dominio.servicios.ServicioTipoVentana;
import com.tallerwebi.dominio.servicios.ServicioUsuario;
import com.tallerwebi.presentacion.dto.PresupuestoItemRequest;
import com.tallerwebi.presentacion.dto.PresupuestoRequest;
import com.tallerwebi.presentacion.dto.UsuarioSesionDto;

@Controller
public class ControladorPresupuesto {

    private final ServicioTipoProducto servicioTipoProducto;
    private final ServicioTipoVentana servicioTipoVentana;
    private final ServicioTablas servicioTablas;
    private final ServicioProducto servicioProducto;
    private final ServicioCotizacion servicioCotizacion;
    private final ServicioUsuario servicioUsuario;
    private final com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto;

    public ControladorPresupuesto(ServicioTipoProducto servicioTipoProducto, ServicioTipoVentana servicioTipoVentana,
            ServicioTablas servicioTablas, ServicioProducto servicioProducto, ServicioCotizacion servicioCotizacion,
            ServicioUsuario servicioUsuario, com.tallerwebi.dominio.servicios.ServicioPresupuesto servicioPresupuesto) {
        this.servicioTipoProducto = servicioTipoProducto;
        this.servicioTipoVentana = servicioTipoVentana;
        this.servicioTablas = servicioTablas;
        this.servicioProducto = servicioProducto;
        this.servicioCotizacion = servicioCotizacion;
        this.servicioUsuario = servicioUsuario;
        this.servicioPresupuesto = servicioPresupuesto;
    }

    @GetMapping("/presupuestoBkp")
    public String mostrarFormulario(Model model) {
        // agregar tipos de producto al modelo para llenar el select
        model.addAttribute("tiposProducto", servicioTipoProducto.obtener());
        model.addAttribute("colores", servicioTablas.obtenerColores());
        model.addAttribute("materiales", servicioTablas.obtenerMateriales());
        model.addAttribute("altos", servicioTablas.obtenerAltos());
        model.addAttribute("anchos", servicioTablas.obtenerAnchos());
        model.addAttribute("provincias", servicioTablas.obtenerProvincias());

        // tipoVentana will be loaded dynamically based on the selected tipoProducto via
        // AJAX
        return "presupuesto";
        }

    /**
     * Endpoint utilizado por la vista para obtener las categorias (TipoVentana) de
     * un producto.
     * Retorna un JSON con lista de objetos {id,nombre}.
     */

    @GetMapping("/presupuesto/tipo-ventana")
    @ResponseBody
    public List<Map<String, Object>> obtenerTipoVentanaPorTipoDeProducto(
            @RequestParam("tipoProductoId") Long tipoProductoId) {
        List<TipoVentana> lista = servicioTipoVentana.obtenerPorIdTipoProducto(tipoProductoId);
        return lista.stream().map(tv -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", tv.getId());
            m.put("nombre", tv.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/presupuesto/provincia")
    @ResponseBody
    public List<Map<String, Object>> obtenerLocalidadesPorProvincia(@RequestParam("provinciaId") Long provinciaId) {
        List<Localidad> lista = servicioTablas.obtenerLocalidadesPorProvincia(provinciaId);
        return lista.stream().map(l -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id_localidad", l.getId_localidad());
            m.put("nombre", l.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @GetMapping("/presupuesto/localidad")
    @ResponseBody
    public List<Map<String, Object>> obtenerPartidosPorLocalidad(@RequestParam("localidadId") Long localidadId) {
        List<Partido> lista = servicioTablas.obtenerPartidosPorLocalidad(localidadId);
        return lista.stream().map(p -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id_partido", p.getId_partido());
            m.put("nombre", p.getNombre());
            return m;
        }).collect(Collectors.toList());
    }

    @PostMapping("/presupuesto")
    public String procesarFormulario(
            @RequestParam String tipoVentana,
            @RequestParam Double ancho,
            @RequestParam Double alto,
            @RequestParam String material,
            @RequestParam String vidrio,
            @RequestParam String color,
            @RequestParam(required = false) boolean premarco,
            @RequestParam(required = false) boolean barrotillos,
            Model model) {

        model.addAttribute("mensaje", "Tu solicitud fue enviada!");
        return "home";
    }

    @PostMapping(path = "/presupuesto/crear-desde-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<?> crearCotizacionDesdeForm(HttpServletRequest request) {
        // requiere usuario logueado
        UsuarioSesionDto u = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Debe iniciar sesión"));
        }

        // leer arrays generados por la vista
        String[] tiposProducto = request.getParameterValues("itemTipoProducto[]");
        String[] tiposVentana = request.getParameterValues("itemTipoVentana[]");
        String[] anchos = request.getParameterValues("itemAncho[]");
        String[] altos = request.getParameterValues("itemAlto[]");
        String[] materiales = request.getParameterValues("itemMaterial[]");
        String[] vidrios = request.getParameterValues("itemVidrio[]");
        String[] colores = request.getParameterValues("itemColor[]");

        int totalItems = (tiposProducto != null) ? tiposProducto.length : 0;

        Cotizacion cot = new Cotizacion();
        cot.setFechaCreacion(LocalDate.now());
        cot.setEstado(EstadoCotizacion.PENDIENTE);
        // set cliente desde sesión
        try {
            // buscar usuario completo
            var usuario = servicioUsuario.buscarPorMail(u.getUsername());
            if (usuario instanceof Cliente)
                cot.setCliente((Cliente) usuario);
        } catch (Exception e) {
            // si algo falla -> retornar error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "No se pudo resolver el usuario de sesión"));
        }

        List<Map<String, Object>> itemsCreados = new ArrayList<>();
        List<Map<String, Object>> errores = new ArrayList<>();

        // persistimos cotizacion primero (se pedía crear cotización y luego items)
        servicioCotizacion.guardar(cot);

        for (int i = 0; i < totalItems; i++) {
            try {
                Long tipoProductoId = Long.parseLong(tiposProducto[i]);
                Long tipoVentanaId = (tiposVentana != null && tiposVentana.length > i && tiposVentana[i] != null
                        && !tiposVentana[i].isEmpty()) ? Long.parseLong(tiposVentana[i]) : null;
                Long anchoId = (anchos != null && anchos.length > i && anchos[i] != null && !anchos[i].isEmpty())
                        ? Long.parseLong(anchos[i])
                        : null;
                Long altoId = (altos != null && altos.length > i && altos[i] != null && !altos[i].isEmpty())
                        ? Long.parseLong(altos[i])
                        : null;
                String material = (materiales != null && materiales.length > i) ? materiales[i] : null;
                String vidrio = (vidrios != null && vidrios.length > i) ? vidrios[i] : null;
                String color = (colores != null && colores.length > i) ? colores[i] : null;

                // intentar encontrar producto por combinacion de atributos
                Producto encontrado = servicioProducto.buscarConFiltros(tipoProductoId).stream()
                        .filter(p -> (tipoVentanaId == null
                                || (p.getTipoVentana() != null && p.getTipoVentana().getId().equals(tipoVentanaId)))
                                && (anchoId == null || (p.getAncho() != null && p.getAncho().getId().equals(anchoId)))
                                && (altoId == null || (p.getAlto() != null && p.getAlto().getId().equals(altoId)))
                                && (material == null || (p.getMaterialDePerfil() != null
                                        && p.getMaterialDePerfil().getNombre().equalsIgnoreCase(material)))
                                && (vidrio == null || (p.getTipoDeVidrio() != null
                                        && p.getTipoDeVidrio().getNombre().equalsIgnoreCase(vidrio)))
                                && (color == null
                                        || (p.getColor() != null && p.getColor().getNombre().equalsIgnoreCase(color))))
                        .findFirst().orElse(null);

                if (encontrado == null) {
                    errores.add(Map.of("index", i, "msg", "Producto no encontrado con esos atributos"));
                    continue; // PARCIAL: ignorar item inválido
                }

                CotizacionItem it = new CotizacionItem();
                it.setProducto(encontrado);
                it.setCantidad(1);
                it.setPrecioUnitario(encontrado.getPrecio());
                it.setCotizacion(cot);

                cot.getItems().add(it);
                itemsCreados.add(Map.of("index", i, "productoId", encontrado.getId()));

            } catch (Exception ex) {
                errores.add(Map.of("index", i, "msg", "Error interno procesando item"));
            }
        }

        // recalcular monto y actualizar
        cot.recalcularMontoTotal();
        servicioCotizacion.guardar(cot);

        Map<String, Object> resp = new HashMap<>();
        resp.put("cotizacionId", cot.getId());
        resp.put("itemsCreados", itemsCreados);
        resp.put("errores", errores);

        return ResponseEntity.ok(resp);
    }

    @PostMapping(path = "/presupuesto/crear", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crearPresupuestoJson(@RequestBody Presupuesto presupuesto, HttpServletRequest request) {
        // si hay usuario en sesión lo asignamos como cliente
        UsuarioSesionDto u = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        try {
            if (u != null) {
                var usuario = servicioUsuario.buscarPorMail(u.getUsername());
                if (usuario instanceof Cliente)
                    presupuesto.setCliente((Cliente) usuario);
            }
            Presupuesto creado = servicioPresupuesto.crearPresupuesto(presupuesto);
            return ResponseEntity.ok(java.util.Map.of("presupuestoId", creado.getId()));
        } catch (ProductoExistente pe) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(java.util.Map.of("error", pe.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "Error interno"));
        }
    }

    @PostMapping(path = "/presupuesto/crear-con-items", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> crearPresupuestoConItems(@RequestBody PresupuestoRequest req, HttpServletRequest request) {
        // require logged-in user for creating a presupuesto
        UsuarioSesionDto u = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (u == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Debe iniciar sesión para solicitar un presupuesto"));
        }

        Presupuesto p = new Presupuesto();
        p.setFechaCreacion(java.time.LocalDate.now());
        try {
            var usuario = servicioUsuario.buscarPorMail(u.getUsername());
            if (usuario instanceof Cliente)
                p.setCliente((Cliente) usuario);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "No se pudo resolver el usuario de sesión"));
        }

        java.util.List<Map<String, Object>> itemsCreados = new java.util.ArrayList<>();
        java.util.List<Map<String, Object>> errores = new java.util.ArrayList<>();

        // All-or-nothing validation: ensure each item has the visible fields completed
        if (req.items == null || req.items.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Debe agregar al menos un item antes de solicitar el presupuesto"));
        }

        java.util.List<Integer> missingIndexes = new java.util.ArrayList<>();
        int idxCheck = 0;
        for (PresupuestoItemRequest ir : req.items) {
            // visible/required fields: tipoProductoId, tipoVentanaId, anchoId, altoId,
            // materialId, vidrio (string), colorId
            boolean missing = (ir.tipoProductoId == null || ir.tipoVentanaId == null || ir.anchoId == null
                    || ir.altoId == null || ir.materialId == null || ir.vidrio == null || ir.vidrio.isBlank()
                    || ir.colorId == null);
            if (missing)
                missingIndexes.add(idxCheck);
            idxCheck++;
        }

        if (!missingIndexes.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(java.util.Map.of("error",
                    "Complete los campos visualizados antes de enviar", "missingIndexes", missingIndexes));
        }

        // Build items now that validation passed
        java.util.List<PresupuestoItem> items = new java.util.ArrayList<>();
        int idx = 0;
        for (PresupuestoItemRequest ir : req.items) {
            try {
                PresupuestoItem it = new PresupuestoItem();
                TipoProducto tp = servicioTipoProducto.obtenerPorId(ir.tipoProductoId);
                TipoVentana tv = servicioTipoVentana.obtener().stream().filter(t -> t.getId().equals(ir.tipoVentanaId))
                        .findFirst().orElse(null);
                Ancho ancho = servicioTablas.obtenerAnchos().stream().filter(a -> a.getId().equals(ir.anchoId))
                        .findFirst().orElse(null);
                Alto alto = servicioTablas.obtenerAltos().stream().filter(a -> a.getId().equals(ir.altoId)).findFirst()
                        .orElse(null);
                MaterialDePerfil mat = servicioTablas.obtenerMateriales().stream()
                        .filter(m -> m.getId().equals(ir.materialId)).findFirst().orElse(null);
                Color color = servicioTablas.obtenerColores().stream().filter(c -> c.getId().equals(ir.colorId))
                        .findFirst().orElse(null);

                // If any lookup fails unexpectedly, report and abort (all-or-nothing)
                if (tp == null || ancho == null || alto == null || mat == null || color == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(java.util.Map.of("error", "Referencias inválidas en los items"));
                }

                it.setTipoProducto(tp);
                it.setTipoVentana(tv);
                it.setAncho(ancho);
                it.setAlto(alto);
                it.setMaterial(mat);
                it.setColor(color);
                it.setPresupuesto(p);
                items.add(it);
                itemsCreados.add(java.util.Map.of("index", idx, "tipoProductoId", tp.getId()));
            } catch (Exception ex) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(java.util.Map.of("error", "Error interno procesando items"));
            }
            idx++;
        }

        p.setItems(items);

        try {
            Presupuesto creado = servicioPresupuesto.crearPresupuesto(p);
            Map<String, Object> resp = new HashMap<>();
            resp.put("presupuestoId", creado.getId());
            resp.put("itemsCreados", itemsCreados);
            resp.put("errores", errores);
            return ResponseEntity.ok(resp);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(java.util.Map.of("error", "No se pudo crear presupuesto"));
        }
    }

    /**
     * If someone opens the POST-only endpoint in a browser (GET), redirect to the
     * presupuesto form instead of returning a raw 405 page.
     */
    @GetMapping("/presupuesto/crear-con-items")
    public String redirectCrearConItemsGet() {
        return "redirect:/presupuesto";
    }

    @PostMapping(path = "/presupuesto/crear-form", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String crearPresupuestoDesdeForm(HttpServletRequest request) {
        // requiere usuario logueado
        UsuarioSesionDto u = (UsuarioSesionDto) request.getSession().getAttribute("usuarioLogueado");
        if (u == null) {
            // redirigir al form con error
            return "redirect:/presupuesto?error=not_logged_in";
        }

        // Dump incoming parameters for debugging
        try {
            System.out.println("parametros recibidos:");
            request.getParameterMap()
                    .forEach((k, v) -> System.out.println("  " + k + " = " + java.util.Arrays.toString(v)));
        } catch (Exception ex) {
            System.out.println("no se pudieron imprimir parametros: " + ex.getMessage());
        }

        // leer arrays generados por la vista
        String[] tiposProducto = request.getParameterValues("itemTipoProducto[]");
        String[] tiposVentana = request.getParameterValues("itemTipoVentana[]");
        String[] anchos = request.getParameterValues("itemAncho[]");
        String[] altos = request.getParameterValues("itemAlto[]");
        String[] materiales = request.getParameterValues("itemMaterial[]");
        String[] colores = request.getParameterValues("itemColor[]");

        Presupuesto p = new Presupuesto();
        p.setFechaCreacion(java.time.LocalDate.now());
        Cliente clienteSesion = null;
        try {
            var usuario = servicioUsuario.buscarPorMail(u.getUsername());
            if (usuario instanceof Cliente) {
                clienteSesion = (Cliente) usuario;
                p.setCliente(clienteSesion);
            }
        } catch (Exception e) {
            return "redirect:/presupuesto?error=user_lookup";
        }

        // Resolve optional location selects from the top form and set them on
        // Presupuesto
        try {
            String provinciaSel = request.getParameter("provincia");
            String localidadSel = request.getParameter("localidad");
            String partidoSel = request.getParameter("partido");

            // If provincia/localidad/partido are required in your business rules, enforce
            // here
            // For now require at least provincia and localidad to be present and resolvable
            if (provinciaSel == null || provinciaSel.isBlank()) {
                return "redirect:/presupuesto?error=missing_provincia";
            }

            if (provinciaSel != null && !provinciaSel.isBlank()) {
                try {
                    Long provId = Long.parseLong(provinciaSel);
                    var provincia = servicioTablas.obtenerProvincias().stream()
                            .filter(pr -> pr.getId_provincia().equals(provId)).findFirst().orElse(null);
                    if (provincia != null)
                        p.setProvincia(provincia);
                } catch (NumberFormatException nfe) {
                    /* ignore */ }
            }
            if (localidadSel != null && !localidadSel.isBlank() && p.getProvincia() != null) {
                try {
                    Long locId = Long.parseLong(localidadSel);
                    var localidades = servicioTablas.obtenerLocalidadesPorProvincia(p.getProvincia().getId_provincia());
                    var localidad = localidades.stream().filter(l -> l.getId_localidad().equals(locId)).findFirst()
                            .orElse(null);
                    if (localidad != null)
                        p.setLocalidad(localidad);
                } catch (NumberFormatException nfe) {
                    /* ignore */ }
            } else {
                // if provincia was set but localidad missing, show helpful info in logs
                System.out.println("[WARN] localidad param missing or provincia not set. provinciaSel=" + provinciaSel
                        + ", localidadSel=" + localidadSel);
                return "redirect:/presupuesto?error=missing_localidad";
            }

            // After attempting resolution, log what we found
            System.out.println("[DEBUG] provincia resuelta=" + (p.getProvincia() != null
                    ? p.getProvincia().getId_provincia() + ":" + p.getProvincia().getNombre()
                    : "null"));
            System.out.println("[DEBUG] localidad resuelta=" + (p.getLocalidad() != null
                    ? p.getLocalidad().getId_localidad() + ":" + p.getLocalidad().getNombre()
                    : "null"));
            if (p.getLocalidad() == null && p.getProvincia() != null) {
                var locs = servicioTablas.obtenerLocalidadesPorProvincia(p.getProvincia().getId_provincia());
                System.out
                        .println("[DEBUG] localidades disponibles para provincia " + p.getProvincia().getId_provincia()
                                + ": " + locs.stream().map(l -> l.getId_localidad().toString())
                                        .reduce((a, b) -> a + "," + b).orElse("(none)"));
                return "redirect:/presupuesto?error=localidad_not_found&provincia=" + provinciaSel + "&localidad="
                        + localidadSel;
            }

            if (partidoSel != null && !partidoSel.isBlank() && p.getLocalidad() != null) {
                try {
                    Long partId = Long.parseLong(partidoSel);
                    var partidos = servicioTablas.obtenerPartidosPorLocalidad(p.getLocalidad().getId_localidad());
                    var partido = partidos.stream().filter(pa -> pa.getId_partido().equals(partId)).findFirst()
                            .orElse(null);
                    if (partido != null)
                        p.setPartido(partido);
                } catch (NumberFormatException nfe) {
                    /* ignore */ }
            } else {
                // if partido not provided, try to auto-resolve to the first partido for the
                // selected localidad. The DB schema requires partido non-null, so attempt
                // automatic resolution; if none found, return an explanatory error.
                if (p.getLocalidad() != null) {
                    var partidos = servicioTablas.obtenerPartidosPorLocalidad(p.getLocalidad().getId_localidad());
                    if (partidos != null && !partidos.isEmpty()) {
                        p.setPartido(partidos.get(0));
                        System.out.println("[DEBUG] partido auto-resuelto=" + p.getPartido().getId_partido() + ":"
                                + p.getPartido().getNombre());
                    } else {
                        System.out.println("[WARN] No hay partidos asociados a la localidad " + p.getLocalidad().getId_localidad());
                        return "redirect:/presupuesto?error=missing_partido&provincia=" + provinciaSel + "&localidad=" + localidadSel;
                    }
                }
            }
        } catch (Exception ex) {
            // if resolution fails we continue; saving will fail later and be logged
            System.out.println("[WARN] no se pudieron resolver provincias/localidades/partidos: " + ex.getMessage());
        }

        int totalItems = (tiposProducto != null) ? tiposProducto.length : 0;
        java.util.List<PresupuestoItem> items = new java.util.ArrayList<>();

        for (int i = 0; i < totalItems; i++) {
            try {
                Long tipoProductoId = (tiposProducto != null && tiposProducto.length > i && tiposProducto[i] != null
                        && !tiposProducto[i].isEmpty()) ? Long.parseLong(tiposProducto[i]) : null;
                Long tipoVentanaId = (tiposVentana != null && tiposVentana.length > i && tiposVentana[i] != null
                        && !tiposVentana[i].isEmpty()) ? Long.parseLong(tiposVentana[i]) : null;
                Long anchoId = (anchos != null && anchos.length > i && anchos[i] != null && !anchos[i].isEmpty())
                        ? Long.parseLong(anchos[i])
                        : null;
                Long altoId = (altos != null && altos.length > i && altos[i] != null && !altos[i].isEmpty())
                        ? Long.parseLong(altos[i])
                        : null;
                Long materialId = (materiales != null && materiales.length > i && materiales[i] != null
                        && !materiales[i].isEmpty()) ? Long.parseLong(materiales[i]) : null;
                Long colorId = (colores != null && colores.length > i && colores[i] != null && !colores[i].isEmpty())
                        ? Long.parseLong(colores[i])
                        : null;

                // validar campos visibles: si faltan, ignorar o redirigir según regla (aquí
                // ignoramos)
                if (tipoProductoId == null || anchoId == null || altoId == null)
                    continue;

                PresupuestoItem it = new PresupuestoItem();
                TipoProducto tp = tipoProductoId != null ? servicioTipoProducto.obtenerPorId(tipoProductoId) : null;
                TipoVentana tv = tipoVentanaId != null
                        ? servicioTipoVentana.obtener().stream().filter(t -> t.getId().equals(tipoVentanaId))
                                .findFirst().orElse(null)
                        : null;
                Ancho ancho = anchoId != null
                        ? servicioTablas.obtenerAnchos().stream().filter(a -> a.getId().equals(anchoId)).findFirst()
                                .orElse(null)
                        : null;
                Alto alto = altoId != null
                        ? servicioTablas.obtenerAltos().stream().filter(a -> a.getId().equals(altoId)).findFirst()
                                .orElse(null)
                        : null;
                MaterialDePerfil mat = materialId != null
                        ? servicioTablas.obtenerMateriales().stream().filter(m -> m.getId().equals(materialId))
                                .findFirst().orElse(null)
                        : null;
                Color color = colorId != null
                        ? servicioTablas.obtenerColores().stream().filter(c -> c.getId().equals(colorId)).findFirst()
                                .orElse(null)
                        : null;

                it.setTipoProducto(tp);
                it.setTipoVentana(tv);
                it.setAncho(ancho);
                it.setAlto(alto);
                it.setMaterial(mat);
                it.setColor(color);
                // set the usuario on the item (DB requires usuario non-null)
                if (clienteSesion != null) {
                    it.setUsuario(clienteSesion);
                }
                it.setPresupuesto(p);
                items.add(it);
            } catch (Exception ex) {
                // ignorar item problemático
            }
        }

        p.setItems(items);

        try {
            System.out.println("[DEBUG] intentar crear presupuesto. clienteSesion="
                    + (clienteSesion != null ? clienteSesion.getEmail() : "null") + ", items=" + items.size());
            Presupuesto creado = servicioPresupuesto.crearPresupuesto(p);
            System.out.println("[DEBUG] presupuesto creado id=" + (creado != null ? creado.getId() : "null"));
            return "redirect:/presupuesto?success=1&presupuestoId=" + (creado != null ? creado.getId() : "")
                    + "&itemsCreated=" + items.size();
        } catch (Exception e) {
            System.out.println("[ERROR] fallo al crear presupuesto: " + e.getMessage());
            e.printStackTrace();
            // add diagnostic info to redirect so we can see what was null
            String provId = (p.getProvincia() != null) ? String.valueOf(p.getProvincia().getId_provincia()) : "null";
            String locId = (p.getLocalidad() != null) ? String.valueOf(p.getLocalidad().getId_localidad()) : "null";
            String partId = (p.getPartido() != null) ? String.valueOf(p.getPartido().getId_partido()) : "null";
            int itemCount = (p.getItems() != null) ? p.getItems().size() : 0;
            return "redirect:/presupuesto?error=save_failed&provincia=" + provId + "&localidad=" + locId + "&partido=" + partId + "&items=" + itemCount;
        }

    }

    @GetMapping("/presupuesto/{id}/items")
    public ModelAndView verItemsPresupuesto(@org.springframework.web.bind.annotation.PathVariable("id") Long id,
            HttpServletRequest request) {
        ModelMap datos = new ModelMap();
        try {
            System.out.println("[ControladorPresupuesto] verItemsPresupuesto id=" + id);
            Presupuesto p = servicioPresupuesto.obtenerPorId(id);
            if (p == null) {
                System.out.println("[ControladorPresupuesto] presupuesto nulo para id=" + id);
                datos.put("error", "Presupuesto no encontrado");
                return new ModelAndView("presupuesto_items", datos);
            }
            int itemCount = (p.getItems() == null) ? 0 : p.getItems().size();
            System.out.println("[ControladorPresupuesto] presupuesto encontrado id=" + p.getId() + ", items=" + itemCount);
            datos.put("presupuesto", p);
            datos.put("items", p.getItems() == null ? new java.util.ArrayList<>() : p.getItems());
            datos.put("fecha", p.getFechaCreacion() == null ? null : p.getFechaCreacion().toString());
            // Build product merge list: for each product in catalog, if it matches any item
            // of the presupuesto, group it by proveedor.
            try {
                List<Producto> allProducts = servicioProducto.obtener();
                java.util.List<java.util.Map<String, Object>> grupos = new java.util.ArrayList<>();
                java.util.Map<Long, java.util.Map<String, Object>> grupoPorProveedor = new java.util.LinkedHashMap<>();

                for (Producto prod : allProducts) {
                    if (prod == null || prod.getProveedor() == null) continue;
                    boolean matches = false;
                    if (p.getItems() != null) {
                        for (PresupuestoItem it : p.getItems()) {
                            /*// Match by tipoProducto (required) and then check optional attributes
                            if (it.getTipoProducto() == null || prod.getTipoProducto() == null) continue;
                            if (!prod.getTipoProducto().getId().equals(it.getTipoProducto().getId())) continue;
                            // tipoVentana

                             */
                            if (it.getTipoVentana() != null) {
                                if (prod.getTipoVentana() == null || !prod.getTipoVentana().getId().equals(it.getTipoVentana().getId()))
                                    continue;
                            }
                           /* // ancho
                            if (it.getAncho() != null) {
                                if (prod.getAncho() == null || !prod.getAncho().getId().equals(it.getAncho().getId())) continue;
                            }
                            // alto
                            if (it.getAlto() != null) {
                                if (prod.getAlto() == null || !prod.getAlto().getId().equals(it.getAlto().getId())) continue;
                            }
                                */
                            // material
                            if (it.getMaterial() != null) {
                                if (prod.getMaterialDePerfil() == null || !prod.getMaterialDePerfil().getId().equals(it.getMaterial().getId())) continue;
                            }
                            /*
                            // color
                            if (it.getColor() != null) {
                                if (prod.getColor() == null || !prod.getColor().getId().equals(it.getColor().getId())) continue;
                            }
 */
                            // if we reached this point, product matches this item
                            matches = true;
                            break;
                        }
                    }
                    if (!matches) continue;

                    Long provId = prod.getProveedor().getId();
                    java.util.Map<String, Object> grupo = grupoPorProveedor.get(provId);
                    if (grupo == null) {
                        grupo = new java.util.HashMap<>();
                        grupo.put("proveedorId", provId);
                        grupo.put("proveedorNombre", prod.getProveedor().getNombre());
                        grupo.put("productos", new java.util.ArrayList<java.util.Map<String, Object>>() );
                        grupoPorProveedor.put(provId, grupo);
                    }
                    @SuppressWarnings("unchecked")
                    java.util.List<java.util.Map<String, Object>> lista = (java.util.List<java.util.Map<String, Object>>) grupo.get("productos");
                    java.util.Map<String, Object> pd = new java.util.HashMap<>();
                    pd.put("id", prod.getId());
                    pd.put("nombre", prod.getNombre());
                    pd.put("precio", prod.getPrecio());
                    pd.put("stock", prod.getStock());
                    lista.add(pd);
                }

                // move to list preserving order
                grupoPorProveedor.values().forEach(v -> grupos.add(v));
                datos.put("productosPorProveedor", grupos);
            } catch (Exception ex) {
                System.out.println("[ControladorPresupuesto] fallo al buscar productos para merge: " + ex.getMessage());
            }
            return new ModelAndView("presupuesto_items", datos);
        } catch (Exception e) {
            System.out.println("[ControladorPresupuesto] error recuperando presupuesto id=" + id + ": " + e.getMessage());
            e.printStackTrace();
            datos.put("error", "Error al recuperar presupuesto");
            return new ModelAndView("presupuesto_items", datos);
        }
    }

    // tolerant mapping: sometimes requests to '/presupuestO' or different context path
    // or to '/presupuesto/{id}/items' via direct URL may not resolve; provide a simple
    // redirect endpoint with different casing/variations if necessary.
    @GetMapping("/presupuestO/{id}/items")
    public String redirectPresupuestoTypo(@org.springframework.web.bind.annotation.PathVariable("id") Long id) {
        return "redirect:/presupuesto/" + id + "/items";
    }
}