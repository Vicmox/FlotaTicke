package negocio;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmpresaTransporte {

    private List<Bus> myBuses;
    private List<Ruta> myRutas;
    private List<Salida> mySalidas;
    private List<PasajeTicket> myTickets;
    private CajaVenta myCaja;
    private List<Empleado> myEmpleado;
    private List<Conductor> myConductores;
    private List<Pasajero> myPasajeros;
    private int secuencialSalida;
    private int secuencialRuta;

    public EmpresaTransporte() {
        this.myBuses = new ArrayList<>();
        this.myRutas = new ArrayList<>();
        this.mySalidas = new ArrayList<>();
        this.myTickets = new ArrayList<>();
        this.myEmpleado = new ArrayList<>();
        this.myConductores = new ArrayList<>();
        this.myPasajeros = new ArrayList<>();
        this.myCaja = new CajaVenta();
        this.secuencialSalida = 1;
        this.secuencialRuta = 9;
        cargarDatosBase();
    }

    public boolean crearBus(String placa, String estado, String tipo, int capacidad) {
        if (busExiste(placa)) {
            return false;
        }
        Bus bus;
        if ("EJECUTIVO".equalsIgnoreCase(tipo)) {
            bus = new BusTipoEjecutivo(placa, estado, capacidad);
        } else {
            bus = new BusTipoNormal(placa, estado, capacidad);
        }
        myBuses.add(bus);
        return true;
    }

    public boolean editarBus(String placa, String nuevoEstado) {
        Bus bus = getBusPorPlaca(placa);
        if (bus == null) {
            return false;
        }
        bus.setEstado(nuevoEstado);
        return true;
    }

    public boolean eliminarBus(String placa) {
        Bus bus = getBusPorPlaca(placa);
        if (bus == null) {
            return false;
        }
        myBuses.remove(bus);
        return true;
    }

    public boolean asignarConductorABus(String placa, String cedulaConductor) {
        Bus bus = getBusPorPlaca(placa);
        if (bus == null) {
            return false;
        }
        if (cedulaConductor == null || cedulaConductor.isEmpty()) {
            bus.setMyConductor(null);
            return true;
        }
        Conductor c = getConductorPorCedula(cedulaConductor);
        if (c == null) {
            return false;
        }
        bus.setMyConductor(c);
        return true;
    }

    public boolean crearRuta(String codigo, String origen, String destino, float tarifa) {
        if (codigo == null || codigo.isEmpty()) {
            codigo = generarIdRuta();
        }
        if (rutaExiste(codigo)) {
            return false;
        }
        myRutas.add(new Ruta(codigo, origen, destino, tarifa));
        return true;
    }

    public String generarIdRuta() {
        return "R" + String.format("%02d", secuencialRuta++);
    }

    public boolean editarRuta(String codigo, String nuevoOrigen, String nuevoDestino, float nuevaTarifa) {
        Ruta ruta = getRutaPorCodigo(codigo);
        if (ruta == null) {
            return false;
        }
        ruta.setOrigen(nuevoOrigen);
        ruta.setDestino(nuevoDestino);
        ruta.setTarifa(nuevaTarifa);
        return true;
    }

    public boolean eliminarRuta(String codigo) {
        Ruta ruta = getRutaPorCodigo(codigo);
        if (ruta == null) {
            return false;
        }
        myRutas.remove(ruta);
        return true;
    }

    public boolean crearSalida(String codigoRuta, String placaBus, LocalDateTime fecha) {
        Ruta ruta = getRutaPorCodigo(codigoRuta);
        Bus bus = getBusPorPlaca(placaBus);
        if (ruta == null || bus == null) {
            return false;
        }

        for (Salida s : mySalidas) {
            if (s.getMyBus().getPlaca().equals(placaBus) && s.getFecha().equals(fecha)) {
                return false;
            }
        }

        String idSalida = generarIdSalida();
        if (salidaExiste(placaBus, codigoRuta, idSalida)) {
            return false;
        }

        Salida salida = new Salida(idSalida, ruta, fecha, bus, Salida.PROGRAMADA);
        mySalidas.add(salida);
        return true;
    }

    public boolean editarSalida(String idSalida, String nuevoEstado) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return false;
        }

        if (Salida.EN_RUTA.equals(nuevoEstado)) {
            if (!salida.esSalidaEfectiva(myTickets)) {
                return false;
            }
        }
        salida.setEstado(nuevoEstado);
        return true;
    }

    public String editarSalidaCompleta(String idSalida, String codigoRuta, String placaBus, String fechaHoraStr) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return "Salida no encontrada";
        }
        Ruta ruta = getRutaPorCodigo(codigoRuta);
        if (ruta == null) {
            return "Ruta no encontrada";
        }
        Bus bus = getBusPorPlaca(placaBus);
        if (bus == null) {
            return "Bus no encontrado";
        }
        try {
            LocalDateTime fecha = LocalDateTime.parse(fechaHoraStr,
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            salida.setMyRuta(ruta);
            salida.setMyBus(bus);
            salida.setFecha(fecha);
            return "OK";
        } catch (Exception e) {
            return "Formato de fecha invalido. Use: dd/MM/yyyy HH:mm";
        }
    }

    public boolean crearConductor(String cedula, String nombre, String direccion, String correo, String telefono, float sueldo) {
        if (conductorExiste(cedula)) {
            return false;
        }
        myConductores.add(new Conductor(cedula, nombre, direccion, correo, telefono, sueldo));
        return true;
    }

    public boolean editarConductor(String cedula, String nuevoNombre, String nuevaDireccion, String nuevoCorreo, String nuevoTelefono, float nuevoSueldo) {
        Conductor c = getConductorPorCedula(cedula);
        if (c == null) {
            return false;
        }
        c.setNombre(nuevoNombre);
        c.setDireccion(nuevaDireccion);
        c.setCorreo(nuevoCorreo);
        c.setTelefono(nuevoTelefono);
        c.setSueldo(nuevoSueldo);
        return true;
    }

    public boolean eliminarConductor(String cedula) {
        Conductor c = getConductorPorCedula(cedula);
        if (c == null) {
            return false;
        }
        myConductores.remove(c);
        return true;
    }

    public boolean finalizarSalida(String idSalida) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return false;
        }
        if (!Salida.PROGRAMADA.equals(salida.getEstado()) && !Salida.EN_RUTA.equals(salida.getEstado())) {
            return false;
        }
        if (!salida.puedeFinalizarse()) {
            return false;
        }
        salida.setEstado(Salida.FINALIZADA);
        return true;
    }

    public boolean esSalidaEfectiva(String idSalida) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return false;
        }
        return salida.esSalidaEfectiva(myTickets);
    }

    public List<String> cancelarSalida(String idSalida, boolean reembolsar) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return new ArrayList<>();
        }

        salida.setEstado(Salida.CANCELADA);

        List<PasajeTicket> ticketsAfectados = new ArrayList<>();
        for (PasajeTicket t : myTickets) {
            if (t.getMySalida().getIdSalida().equals(idSalida) && PasajeTicket.VIGENTE.equals(t.getEstado())) {
                ticketsAfectados.add(t);
            }
        }

        Puesto[] puestosSalida = salida.getMyPuestos();
        List<String> resultados = new ArrayList<>();
        int tktIndex = 1;

        for (PasajeTicket ticket : ticketsAfectados) {
            int numPuesto = ticket.getPuesto();
            if (numPuesto >= 1 && numPuesto <= puestosSalida.length) {
                puestosSalida[numPuesto - 1].setMyPasajero(null);
            }

            StringBuilder r = new StringBuilder();
            String id = "TKT-" + String.format("%05d", tktIndex++);
            String pasajero = ticket.getMyPasajero().getCedula().length() > 6
                    ? ticket.getMyPasajero().getCedula().substring(0, 6) + "..."
                    : ticket.getMyPasajero().getCedula();

            if (reembolsar) {
                ticket.setEstado(PasajeTicket.REEMBOLSADO);
                myCaja.registrarReembolso(ticket.getValorPagar());
                r.append(id).append("  Pasajero ").append(pasajero)
                        .append("  Silla ").append(String.format("%02d", ticket.getPuesto()))
                        .append("  -> REEMBOLSADO");
            } else {
                String res = reprogramarTicketConResultado(ticket);
                r.append(id).append("  Pasajero ").append(pasajero)
                        .append("  Silla ").append(String.format("%02d", ticket.getPuesto()));
                if (res != null) {
                    r.append("  -> ").append(res);
                } else {
                    r.append("  -> REEMBOLSADO (sin cupo)");
                }
            }
            resultados.add(r.toString());
        }

        return resultados;
    }

    public int getVentasPorRuta(String codigoRuta) {
        return myCaja.getVentasPorRuta(myTickets, codigoRuta);
    }

    public int getVentasEnRango(LocalDateTime desde, LocalDateTime hasta) {
        return myCaja.getVentasEnRango(myTickets, desde, hasta);
    }

    public int[] verificarPuestosConsecutivos(String idSalida, int cantidad) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return null;
        }
        Puesto[] puestos = salida.getMyPuestos();
        for (int i = 0; i <= puestos.length - cantidad; i++) {
            boolean bloqueLibre = true;
            for (int j = 0; j < cantidad; j++) {
                if (!puestos[i + j].estaLibre()) {
                    bloqueLibre = false;
                    break;
                }
            }
            if (bloqueLibre) {
                int[] bloque = new int[cantidad];
                for (int j = 0; j < cantidad; j++) {
                    bloque[j] = i + j + 1;
                }
                return bloque;
            }
        }
        return null;
    }

    public int[] getEstadoPuestos(String idSalida, int[] puestosSeleccionados) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return new int[0];
        }
        Puesto[] puestos = salida.getMyPuestos();
        int[] estados = new int[puestos.length];
        boolean[] enBloque = new boolean[puestos.length];
        if (puestosSeleccionados != null) {
            for (int p : puestosSeleccionados) {
                if (p >= 1 && p <= puestos.length) {
                    enBloque[p - 1] = true;
                }
            }
        }
        for (int i = 0; i < puestos.length; i++) {
            if (!puestos[i].estaLibre()) {
                estados[i] = 2;
            } else if (enBloque[i]) {
                estados[i] = 1;
            } else {
                estados[i] = 0;
            }
        }
        return estados;
    }

    public String[] generarTicketsFormateados(String idSalida, int[] numerosPuestos,
            String[] cedulas, String[] nombres,
            String[] correos, String[] telefonos) {
        Pasajero[] pasajeros = new Pasajero[cedulas.length];
        for (int i = 0; i < cedulas.length; i++) {
            pasajeros[i] = buscarOCrearPasajero(cedulas[i], nombres[i], "", correos[i], telefonos[i]);
        }

        PasajeTicket[] tickets = crearTicketsInterno(idSalida, numerosPuestos, pasajeros, false);
        if (tickets == null) {
            return null;
        }

        float total = 0f;
        for (PasajeTicket t : tickets) {
            total += t.getValorPagar();
        }
        myCaja.registrarVenta(total);

        String[] resultados = new String[tickets.length];
        for (int i = 0; i < tickets.length; i++) {
            resultados[i] = formatearTicket(tickets[i], i + 1);
        }
        return resultados;
    }

    public String ventaIdaYVueltaFormateada(String idSalidaIda, int[] puestosIda,
            String[] cedulasIda, String[] nombresIda,
            String[] correosIda, String[] telefonosIda,
            String idSalidaVuelta, int[] puestosVuelta) {
        Salida salidaIda = getSalidaPorId(idSalidaIda);
        Salida salidaVuelta = getSalidaPorId(idSalidaVuelta);

        if (salidaIda == null || salidaVuelta == null) {
            return "Salida no encontrada";
        }
        if (!Salida.PROGRAMADA.equals(salidaIda.getEstado())) {
            return "La salida de ida no esta PROGRAMADA (estado: " + salidaIda.getEstado() + ")";
        }
        if (!Salida.PROGRAMADA.equals(salidaVuelta.getEstado())) {
            return "La salida de vuelta no esta PROGRAMADA (estado: " + salidaVuelta.getEstado() + ")";
        }

        if (!salidaIda.getMyRuta().getOrigen().equals(salidaVuelta.getMyRuta().getDestino())
                || !salidaIda.getMyRuta().getDestino().equals(salidaVuelta.getMyRuta().getOrigen())) {
            return "La vuelta debe ser la ruta inversa";
        }

        int[] bloqueIda = verificarPuestosConsecutivos(idSalidaIda, puestosIda.length);
        int[] bloqueVuelta = verificarPuestosConsecutivos(idSalidaVuelta, puestosVuelta.length);
        if (bloqueIda == null) {
            return "No hay puestos consecutivos en la salida de ida";
        }
        if (bloqueVuelta == null) {
            return "No hay puestos consecutivos en la salida de vuelta";
        }

        if (!todosPuestosLibres(salidaIda, puestosIda)) {
            return "Algun puesto de ida ya esta ocupado";
        }
        if (!todosPuestosLibres(salidaVuelta, puestosVuelta)) {
            return "Algun puesto de vuelta ya esta ocupado";
        }

        Pasajero[] pasajeros = new Pasajero[cedulasIda.length];
        for (int i = 0; i < cedulasIda.length; i++) {
            pasajeros[i] = buscarOCrearPasajero(cedulasIda[i], nombresIda[i], "",
                    correosIda[i], telefonosIda[i]);
        }

        PasajeTicket[] ticketsIda = crearTicketsInterno(idSalidaIda, puestosIda, pasajeros, true);
        if (ticketsIda == null) {
            return "Error al crear tickets de ida";
        }

        PasajeTicket[] ticketsVuelta = crearTicketsInterno(idSalidaVuelta, puestosVuelta, pasajeros, true);
        if (ticketsVuelta == null) {
            revertirTickets(ticketsIda);
            return "Error al crear tickets de vuelta. Los tickets de ida fueron revertidos.";
        }

        float totalIda = 0f;
        for (PasajeTicket t : ticketsIda) {
            totalIda += t.getValorPagar();
        }
        float totalVuelta = 0f;
        for (PasajeTicket t : ticketsVuelta) {
            totalVuelta += t.getValorPagar();
        }
        float totalGlobal = (totalIda + totalVuelta) * 0.9f;
        myCaja.registrarVenta(totalGlobal);

        StringBuilder sb = new StringBuilder();
        int num = 1;
        for (int i = 0; i < ticketsIda.length; i++) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append(formatearTicketIdaVuelta(ticketsIda[i], num++, true));
        }
        for (int i = 0; i < ticketsVuelta.length; i++) {
            if (sb.length() > 0) {
                sb.append("\n\n");
            }
            sb.append(formatearTicketIdaVuelta(ticketsVuelta[i], num++, false));
        }
        return sb.toString();
    }

    public String[][] getSalidasProgramadasParaCombo() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        List<String[]> items = new ArrayList<>();
        for (Salida s : mySalidas) {
            if (!Salida.PROGRAMADA.equals(s.getEstado())) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(s.getIdSalida()).append(" — ")
                    .append(s.getMyRuta().getOrigen()).append(" → ").append(s.getMyRuta().getDestino())
                    .append(" (").append(s.getFecha().format(fmt)).append(")");
            items.add(new String[]{s.getIdSalida(), sb.toString()});
        }
        return items.toArray(new String[0][]);
    }

    public String[][] getSalidasProgramadasPorDestino(String destino) {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        List<String[]> items = new ArrayList<>();
        for (Salida s : mySalidas) {
            if (!Salida.PROGRAMADA.equals(s.getEstado())) {
                continue;
            }
            if (destino != null && !s.getMyRuta().getDestino().equalsIgnoreCase(destino)) {
                continue;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(s.getIdSalida()).append(" — ")
                    .append(s.getMyRuta().getOrigen()).append(" → ").append(s.getMyRuta().getDestino())
                    .append(" (").append(s.getFecha().format(fmt)).append(")");
            items.add(new String[]{s.getIdSalida(), sb.toString()});
        }
        return items.toArray(new String[0][]);
    }

    public String[][] getDestinosDisponibles() {
        List<String[]> items = new ArrayList<>();
        for (Ruta r : myRutas) {
            if (!"Cúcuta".equalsIgnoreCase(r.getDestino())) {
                boolean existe = false;
                for (String[] item : items) {
                    if (item[0].equals(r.getDestino())) {
                        existe = true;
                        break;
                    }
                }
                if (!existe) {
                    items.add(new String[]{r.getDestino(), r.getDestino()});
                }
            }
        }
        return items.toArray(new String[0][]);
    }

    public String[][] getRutasParaCombo() {
        List<String[]> items = new ArrayList<>();
        for (Ruta r : myRutas) {
            StringBuilder sb = new StringBuilder();
            sb.append(r.getCodigo()).append(" — ").append(r.getOrigen())
                    .append(" → ").append(r.getDestino());
            items.add(new String[]{r.getCodigo(), sb.toString()});
        }
        return items.toArray(new String[0][]);
    }

    public String[][] getBusesDisponiblesParaCombo() {
        List<String[]> items = new ArrayList<>();
        for (Bus b : myBuses) {
            if (Bus.DISPONIBLE.equals(b.getEstado())) {
                StringBuilder sb = new StringBuilder();
                sb.append(b.getPlaca()).append(" (Cap: ").append(b.getCapacidad()).append(")");
                items.add(new String[]{b.getPlaca(), sb.toString()});
            }
        }
        return items.toArray(new String[0][]);
    }

    public String[][] getConductoresParaCombo() {
        List<String[]> items = new ArrayList<>();
        for (Conductor c : myConductores) {
            StringBuilder sb = new StringBuilder();
            sb.append(c.getCedula()).append(" - ").append(c.getNombre());
            items.add(new String[]{c.getCedula(), sb.toString()});
        }
        return items.toArray(new String[0][]);
    }

    public String[] getInfoSalida(String idSalida) {
        Salida s = getSalidaPorId(idSalida);
        if (s == null) {
            return new String[]{"", "", "", "", "", "", "", ""};
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM HH:mm");
        StringBuilder rutaInfo = new StringBuilder();
        rutaInfo.append(s.getMyRuta().getCodigo()).append(" — ")
                .append(s.getMyRuta().getOrigen()).append(" → ").append(s.getMyRuta().getDestino());
        StringBuilder fechaInfo = new StringBuilder();
        fechaInfo.append(s.getIdSalida()).append(" (").append(s.getFecha().format(fmt)).append(")");
        String tipo = s.getMyBus() instanceof BusTipoEjecutivo ? "Ejecutivo" : "Normal";
        StringBuilder busInfo = new StringBuilder();
        busInfo.append(s.getMyBus().getPlaca()).append(" (").append(tipo).append(")");
        return new String[]{
            rutaInfo.toString(),
            fechaInfo.toString(),
            busInfo.toString(),
            s.getMyRuta().getCodigo(),
            s.getMyRuta().getOrigen(),
            s.getMyRuta().getDestino(),
            s.getMyBus().getPlaca(),
            tipo
        };
    }

    public String[] getInfoSalidaParaCancelacion(String idSalida) {
        Salida s = getSalidaPorId(idSalida);
        if (s == null) {
            return new String[]{"", "", "", "", ""};
        }
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder rutaInfo = new StringBuilder();
        rutaInfo.append(s.getMyRuta().getCodigo()).append(" — ")
                .append(s.getMyRuta().getOrigen()).append(" → ").append(s.getMyRuta().getDestino());
        StringBuilder fechaInfo = new StringBuilder();
        fechaInfo.append(s.getFecha().format(fmt));
        String estado = s.getEstado();
        boolean activa = Salida.PROGRAMADA.equals(estado) || Salida.EN_RUTA.equals(estado);
        return new String[]{
            rutaInfo.toString(),
            fechaInfo.toString(),
            s.getMyBus().getPlaca(),
            estado,
            activa ? "ACTIVA" : "NO VIGENTE"
        };
    }

    public String[] getInfoPasajero(String cedula) {
        Pasajero p = getPasajeroPorCedula(cedula);
        if (p == null) {
            return null;
        }
        return new String[]{
            p.getCedula(),
            p.getNombre(),
            p.getCorreo(),
            p.getTelefono(),
            p.esPreferencial() ? "true" : "false",
            p.esPreferencial() ? "PREFERENCIAL" : "NO_FRECUENTE"
        };
    }

    public float getTarifaSalida(String idSalida) {
        Salida s = getSalidaPorId(idSalida);
        if (s == null) {
            return 0f;
        }
        return s.getMyRuta().getTarifa();
    }

    public float getTarifaRuta(String codigoRuta) {
        Ruta r = getRutaPorCodigo(codigoRuta);
        if (r == null) {
            return 0f;
        }
        return r.getTarifa();
    }

    public String getTipoBusSalida(String idSalida) {
        Salida s = getSalidaPorId(idSalida);
        if (s == null) {
            return "";
        }
        return s.getMyBus() instanceof BusTipoEjecutivo ? "EJECUTIVO" : "NORMAL";
    }

    public String[] getDatosBus(String placa) {
        Bus b = getBusPorPlaca(placa);
        if (b == null) {
            return new String[]{"", "", "", "", ""};
        }
        String tipo = b instanceof BusTipoEjecutivo ? "Ejecutivo" : "Normal";
        String conductorNombre = b.getMyConductor() != null ? b.getMyConductor().getNombre() : "";
        return new String[]{
            b.getPlaca(),
            tipo,
            String.valueOf(b.getCapacidad()),
            b.getEstado(),
            conductorNombre
        };
    }

    public String[] getDatosConductor(String cedula) {
        Conductor c = getConductorPorCedula(cedula);
        if (c == null) {
            return new String[]{"", "", "", "", ""};
        }
        return new String[]{
            c.getCedula(),
            c.getNombre(),
            c.getDireccion(),
            c.getCorreo(),
            c.getTelefono()
        };
    }

    public int contarTicketsVigentes() {
        int count = 0;
        for (PasajeTicket t : myTickets) {
            if (PasajeTicket.VIGENTE.equals(t.getEstado())) {
                count++;
            }
        }
        return count;
    }

    public int contarBusesDisponibles() {
        int count = 0;
        for (Bus b : myBuses) {
            if (Bus.DISPONIBLE.equals(b.getEstado())) {
                count++;
            }
        }
        return count;
    }

    public int contarTicketsVigentesPorSalida(String idSalida) {
        int count = 0;
        for (PasajeTicket t : myTickets) {
            if (t.getMySalida().getIdSalida().equals(idSalida)
                    && PasajeTicket.VIGENTE.equals(t.getEstado())) {
                count++;
            }
        }
        return count;
    }

    public Object[][] getOcupacionPorRuta() {
        Map<String, int[]> stats = new LinkedHashMap<>();
        for (Ruta r : myRutas) {
            stats.put(r.getCodigo(), new int[]{0, 0});
        }
        for (PasajeTicket t : myTickets) {
            if (PasajeTicket.VIGENTE.equals(t.getEstado())) {
                String cod = t.getMySalida().getMyRuta().getCodigo();
                int[] s = stats.get(cod);
                if (s != null) {
                    s[0]++;
                }
            }
        }
        for (Salida s : mySalidas) {
            String cod = s.getMyRuta().getCodigo();
            int[] st = stats.get(cod);
            if (st != null) {
                st[1] += s.getMyBus().getCapacidad();
            }
        }
        List<Object[]> rows = new ArrayList<>();
        for (Map.Entry<String, int[]> e : stats.entrySet()) {
            int vend = e.getValue()[0];
            int cap = e.getValue()[1];
            String pct = cap > 0 ? String.format("%.0f%%", (vend * 100.0 / cap)) : "0%";
            rows.add(new Object[]{e.getKey(), vend, cap, pct});
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getProximasSalidasProgramadas() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        List<Salida> programadas = new ArrayList<>();
        for (Salida s : mySalidas) {
            if (Salida.PROGRAMADA.equals(s.getEstado())) {
                programadas.add(s);
            }
        }
        programadas.sort((a, b) -> a.getFecha().compareTo(b.getFecha()));
        List<Object[]> rows = new ArrayList<>();
        for (Salida s : programadas) {
            rows.add(new Object[]{
                s.getFecha().format(fmt),
                s.getMyRuta().getCodigo(),
                s.getMyBus().getPlaca(),
                s.getEstado()
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getRutasTabla() {
        List<Object[]> rows = new ArrayList<>();
        for (Ruta r : myRutas) {
            StringBuilder rutaStr = new StringBuilder();
            rutaStr.append(r.getOrigen()).append(" → ").append(r.getDestino());
            rows.add(new Object[]{
                r.getCodigo(),
                rutaStr.toString(),
                String.format("$%,.0f", r.getTarifa()),
                "Editar"
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getBusesTabla() {
        List<Object[]> rows = new ArrayList<>();
        for (Bus b : myBuses) {
            String tipo = b instanceof BusTipoEjecutivo ? "Ejecutivo" : "Normal";
            String conductorNombre = b.getMyConductor() != null ? b.getMyConductor().getNombre() : "";
            rows.add(new Object[]{
                b.getPlaca(),
                tipo,
                b.getCapacidad(),
                b.getEstado(),
                conductorNombre
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getSalidasTabla() {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
        List<Object[]> rows = new ArrayList<>();
        for (Salida s : mySalidas) {
            int vendidos = s.totalPasajesVendidos(myTickets);
            rows.add(new Object[]{
                s.getIdSalida(),
                s.getMyRuta().getCodigo(),
                s.getFecha().format(fmtFecha),
                s.getFecha().format(fmtHora),
                s.getMyBus().getPlaca(),
                s.getEstado(),
                vendidos,
                "Editar"
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getSalidasFiltradasTabla(String estadoFiltro) {
        DateTimeFormatter fmtFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter fmtHora = DateTimeFormatter.ofPattern("HH:mm");
        List<Object[]> rows = new ArrayList<>();
        for (Salida s : mySalidas) {
            if (!s.getEstado().equals(estadoFiltro)) {
                continue;
            }
            int vendidos = s.totalPasajesVendidos(myTickets);
            rows.add(new Object[]{
                s.getIdSalida(),
                s.getMyRuta().getCodigo(),
                s.getFecha().format(fmtFecha),
                s.getFecha().format(fmtHora),
                s.getMyBus().getPlaca(),
                s.getEstado(),
                vendidos,
                "Editar"
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getConductoresTabla() {
        List<Object[]> rows = new ArrayList<>();
        for (Conductor c : myConductores) {
            rows.add(new Object[]{
                c.getCedula(),
                c.getNombre(),
                c.getCorreo(),
                c.getTelefono(),
                String.format("$%,.0f", c.getSueldo()),
                "Editar"
            });
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getTicketsPorSalida(String idSalida) {
        List<Object[]> rows = new ArrayList<>();
        for (PasajeTicket t : myTickets) {
            if (t.getMySalida().getIdSalida().equals(idSalida)) {
                rows.add(new Object[]{
                    "TKT-" + t.hashCode(),
                    t.getMyPasajero().getNombre(),
                    t.getPuesto(),
                    t.getEstado()
                });
            }
        }
        return rows.toArray(new Object[0][]);
    }

    public double getMontoCaja() {
        return myCaja.getMontoCaja();
    }

    public double getTotalVendido() {
        return myCaja.getTotalVendido();
    }

    public double getTotalReembolsado() {
        return myCaja.getTotalReembolsado();
    }

    public double getIngresoNeto() {
        return myCaja.getIngresoNeto();
    }

    public int getCapacidadTotalFlota() {
        int total = 0;
        for (Bus b : myBuses) {
            total += b.getCapacidad();
        }
        return total;
    }

    public Object[][] getReporteVentasPorRuta() {
        Map<String, int[]> stats = new LinkedHashMap<>();
        Map<String, Float> totalsRuta = new LinkedHashMap<>();
        for (Ruta r : myRutas) {
            stats.put(r.getCodigo(), new int[]{0, 0});
            totalsRuta.put(r.getCodigo(), 0f);
        }
        for (PasajeTicket t : myTickets) {
            String cod = t.getMySalida().getMyRuta().getCodigo();
            int[] s = stats.get(cod);
            if (s != null) {
                if (PasajeTicket.VIGENTE.equals(t.getEstado())) {
                    s[0]++;
                    totalsRuta.merge(cod, t.getValorPagar(), Float::sum);
                }
                if (PasajeTicket.REEMBOLSADO.equals(t.getEstado())) {
                    s[1]++;
                }
            }
        }
        List<Object[]> rows = new ArrayList<>();
        for (Map.Entry<String, int[]> entry : stats.entrySet()) {
            int vend = entry.getValue()[0];
            int reemb = entry.getValue()[1];
            int cap = 0;
            for (Salida sal : mySalidas) {
                if (sal.getMyRuta().getCodigo().equals(entry.getKey())) {
                    cap += sal.getMyBus().getCapacidad();
                }
            }
            String pct = cap > 0 ? String.format("%.0f%%", vend * 100.0 / cap) : "0%";
            float total = totalsRuta.getOrDefault(entry.getKey(), 0f);
            rows.add(new Object[]{entry.getKey(), vend, reemb,
                "$" + String.format("%,.0f", total), pct});
        }
        return rows.toArray(new Object[0][]);
    }

    public Object[][] getReporteVentasRango() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        List<Object[]> rows = new ArrayList<>();
        for (PasajeTicket t : myTickets) {
            rows.add(new Object[]{
                t.getMySalida().getFecha().format(fmt),
                t.getMySalida().getMyRuta().getCodigo(),
                PasajeTicket.VIGENTE.equals(t.getEstado()) ? 1 : 0,
                PasajeTicket.REEMBOLSADO.equals(t.getEstado()) ? 1 : 0,
                String.format("$%,.0f", t.getValorPagar())
            });
        }
        return rows.toArray(new Object[0][]);
    }

    private Pasajero buscarOCrearPasajero(String cedula, String nombre, String direccion,
            String correo, String telefono) {
        for (Pasajero p : myPasajeros) {
            if (p.getCedula().equals(cedula)) {
                return p;
            }
        }
        Pasajero nuevo = new Pasajero(cedula, nombre, direccion, "P-" + cedula, correo, telefono);
        myPasajeros.add(nuevo);
        return nuevo;
    }

    private boolean registrarPasajerosYSillas(String idSalida, int[] numerosPuestos, Pasajero[] pasajeros) {
        Salida salida = getSalidaPorId(idSalida);
        if (salida == null) {
            return false;
        }
        if (!Salida.PROGRAMADA.equals(salida.getEstado())) {
            return false;
        }

        Puesto[] puestos = salida.getMyPuestos();
        for (int num : numerosPuestos) {
            if (num < 1 || num > puestos.length) {
                return false;
            }
            if (!puestos[num - 1].estaLibre()) {
                return false;
            }
        }

        for (int i = 0; i < numerosPuestos.length; i++) {
            puestos[numerosPuestos[i] - 1].setMyPasajero(pasajeros[i]);
        }
        return true;
    }

    private PasajeTicket[] crearTicketsInterno(String idSalida, int[] numerosPuestos,
            Pasajero[] pasajeros, boolean idaYVuelta) {
        if (!registrarPasajerosYSillas(idSalida, numerosPuestos, pasajeros)) {
            return null;
        }

        Salida salida = getSalidaPorId(idSalida);
        float tarifaBase = salida.getMyRuta().getTarifa();
        PasajeTicket[] tickets = new PasajeTicket[numerosPuestos.length];

        for (int i = 0; i < numerosPuestos.length; i++) {
            Pasajero p = pasajeros[i];
            float valor = tarifaBase;
            if (p.esPreferencial()) {
                valor = tarifaBase * 0.9f;
            }
            PasajeTicket ticket = new PasajeTicket(salida, numerosPuestos[i], valor,
                    PasajeTicket.VIGENTE, p, idaYVuelta);
            tickets[i] = ticket;
            myTickets.add(ticket);
            p.incrementarPasajes();
        }

        return tickets;
    }

    private void revertirTickets(PasajeTicket[] tickets) {
        if (tickets == null) {
            return;
        }
        for (PasajeTicket t : tickets) {
            myTickets.remove(t);
            Salida s = t.getMySalida();
            if (s != null) {
                Puesto[] puestos = s.getMyPuestos();
                int num = t.getPuesto();
                if (num >= 1 && num <= puestos.length) {
                    puestos[num - 1].setMyPasajero(null);
                }
            }
        }
    }

    private String formatearTicket(PasajeTicket t, int numero) {
        Salida sal = t.getMySalida();
        Bus bus = sal.getMyBus();
        String tipoBus = bus instanceof BusTipoEjecutivo ? "EJECUTIVO" : "NORMAL";
        StringBuilder sb = new StringBuilder();
        sb.append("VENTA EXITOSA\n")
                .append("Tiquete: TQ-").append(String.format("%05d", numero)).append("\n")
                .append("Pasajero: ").append(t.getMyPasajero().getCedula())
                .append(" - ").append(t.getMyPasajero().getNombre()).append("\n")
                .append("Salida: ").append(sal.getIdSalida())
                .append(" (").append(sal.getMyRuta().getOrigen())
                .append(" -> ").append(sal.getMyRuta().getDestino()).append(") ")
                .append(sal.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n")
                .append("Bus: ").append(bus.getPlaca()).append(" (").append(tipoBus)
                .append(")  Capacidad: ").append(bus.getCapacidad()).append("\n")
                .append("Silla: ").append(t.getPuesto()).append("\n")
                .append("Valor pagado: $").append(String.format("%,.0f", t.getValorPagar())).append("\n")
                .append("Estado tiquete: VIGENTE");
        return sb.toString();
    }

    private String formatearTicketIdaVuelta(PasajeTicket t, int numero, boolean esIda) {
        Salida sal = t.getMySalida();
        Bus bus = sal.getMyBus();
        String tipoBus = bus instanceof BusTipoEjecutivo ? "EJECUTIVO" : "NORMAL";
        String tipoTicket = esIda ? "IDA" : "VUELTA";
        float valor = sal.getMyRuta().getTarifa();
        if (t.getMyPasajero().esPreferencial()) {
            valor = valor * 0.9f;
        }
        valor = valor * 0.9f;

        StringBuilder sb = new StringBuilder();
        sb.append("VENTA EXITOSA — ").append(tipoTicket).append("\n")
                .append("Tiquete: TQ-").append(String.format("%05d", numero)).append("\n")
                .append("Pasajero: ").append(t.getMyPasajero().getCedula())
                .append(" - ").append(t.getMyPasajero().getNombre()).append("\n")
                .append("Salida: ").append(sal.getIdSalida())
                .append(" (").append(sal.getMyRuta().getOrigen())
                .append(" -> ").append(sal.getMyRuta().getDestino()).append(") ")
                .append(sal.getFecha().format(DateTimeFormatter.ofPattern("dd//MM/yyyy HH:mm"))).append("\n")
                .append("Bus: ").append(bus.getPlaca()).append(" (").append(tipoBus)
                .append(")  Capacidad: ").append(bus.getCapacidad()).append("\n")
                .append("Silla: ").append(t.getPuesto()).append("\n")
                .append("Valor pagado: $").append(String.format("%,.0f", valor)).append("\n")
                .append("Estado tiquete: VIGENTE");
        return sb.toString();
    }

    private String reprogramarTicketConResultado(PasajeTicket ticket) {
        Salida salidaOriginal = ticket.getMySalida();
        Salida nuevaSalida = buscarSalidaProxima(salidaOriginal);

        if (nuevaSalida != null) {
            Puesto[] puestos = nuevaSalida.getMyPuestos();
            for (int i = 0; i < puestos.length; i++) {
                if (puestos[i].estaLibre()) {
                    puestos[i].setMyPasajero(ticket.getMyPasajero());
                    ticket.setMySalida(nuevaSalida);
                    ticket.setPuesto(i + 1);
                    StringBuilder sb = new StringBuilder();
                    sb.append("REPROGRAMADO a ").append(nuevaSalida.getIdSalida())
                            .append(" Silla ").append(String.format("%02d", i + 1));
                    return sb.toString();
                }
            }
        }

        ticket.setEstado(PasajeTicket.REEMBOLSADO);
        myCaja.registrarReembolso(ticket.getValorPagar());
        return null;
    }

    private Salida buscarSalidaProxima(Salida salidaCancelada) {
        String codigoRuta = salidaCancelada.getMyRuta().getCodigo();
        LocalDateTime fechaRef = salidaCancelada.getFecha();

        Salida candidata = null;
        for (Salida s : mySalidas) {
            if (s.getMyRuta().getCodigo().equals(codigoRuta)
                    && Salida.PROGRAMADA.equals(s.getEstado())
                    && s.getFecha().isAfter(fechaRef)) {

                for (Puesto p : s.getMyPuestos()) {
                    if (p.estaLibre()) {
                        if (candidata == null || s.getFecha().isBefore(candidata.getFecha())) {
                            candidata = s;
                        }
                        break;
                    }
                }
            }
        }
        return candidata;
    }

    private boolean todosPuestosLibres(Salida salida, int[] puestos) {
        Puesto[] arr = salida.getMyPuestos();
        for (int num : puestos) {
            if (num < 1 || num > arr.length || !arr[num - 1].estaLibre()) {
                return false;
            }
        }
        return true;
    }

    private String generarIdSalida() {
        int anio = LocalDateTime.now().getYear();
        return "SAL-" + anio + "-" + String.format("%03d", secuencialSalida++);
    }

    private boolean busExiste(String placa) {
        return getBusPorPlaca(placa) != null;
    }

    private boolean rutaExiste(String codigo) {
        return getRutaPorCodigo(codigo) != null;
    }

    private boolean salidaExiste(String placa, String codigoRuta, String idSalida) {
        for (Salida s : mySalidas) {
            if (s.getMyBus().getPlaca().equals(placa)
                    && s.getMyRuta().getCodigo().equals(codigoRuta)
                    && s.getIdSalida().equals(idSalida)) {
                return true;
            }
        }
        return false;
    }

    private boolean conductorExiste(String cedula) {
        return getConductorPorCedula(cedula) != null;
    }

    private Ruta getRutaPorCodigo(String codigo) {
        for (Ruta r : myRutas) {
            if (r.getCodigo().equals(codigo)) {
                return r;
            }
        }
        return null;
    }

    private Bus getBusPorPlaca(String placa) {
        for (Bus b : myBuses) {
            if (b.getPlaca().equals(placa)) {
                return b;
            }
        }
        return null;
    }

    private Salida getSalidaPorId(String idSalida) {
        for (Salida s : mySalidas) {
            if (s.getIdSalida().equals(idSalida)) {
                return s;
            }
        }
        return null;
    }

    private Conductor getConductorPorCedula(String cedula) {
        for (Conductor c : myConductores) {
            if (c.getCedula().equals(cedula)) {
                return c;
            }
        }
        return null;
    }

    private Pasajero getPasajeroPorCedula(String cedula) {
        for (Pasajero p : myPasajeros) {
            if (p.getCedula().equals(cedula)) {
                return p;
            }
        }
        return null;
    }

    private LocalDateTime parseFecha(String fechaHora) {
        return LocalDateTime.parse(fechaHora, DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    private void cargarDatosBase() {
        myRutas.add(new Ruta("R01", "Cúcuta", "Bucaramanga", 80000f));
        myRutas.add(new Ruta("R02", "Cúcuta", "Bogotá", 160000f));
        myRutas.add(new Ruta("R03", "Cúcuta", "Medellín", 180000f));
        myRutas.add(new Ruta("R04", "Cúcuta", "Cartagena", 220000f));

        myRutas.add(new Ruta("R05", "Bucaramanga", "Cúcuta", 80000f));
        myRutas.add(new Ruta("R06", "Bogotá", "Cúcuta", 160000f));
        myRutas.add(new Ruta("R07", "Medellín", "Cúcuta", 180000f));
        myRutas.add(new Ruta("R08", "Cartagena", "Cúcuta", 220000f));

        myBuses.add(new BusTipoNormal("KAA-101", "DISPONIBLE", 40));
        myBuses.add(new BusTipoEjecutivo("KBB-202", "DISPONIBLE", 30));
        myBuses.add(new BusTipoNormal("KCC-303", "DISPONIBLE", 40));
        myBuses.add(new BusTipoEjecutivo("KDD-404", "DISPONIBLE", 30));
        myBuses.add(new BusTipoNormal("KEE-505", "MANTENIMIENTO", 40));
        myBuses.add(new BusTipoNormal("KFF-606", "DISPONIBLE", 30));

        mySalidas.add(new Salida("S001", getRutaPorCodigo("R01"), parseFecha("15/03/2026 06:00"), getBusPorPlaca("KAA-101"), "PROGRAMADA"));
        mySalidas.add(new Salida("S002", getRutaPorCodigo("R01"), parseFecha("15/03/2026 14:00"), getBusPorPlaca("KBB-202"), "PROGRAMADA"));
        mySalidas.add(new Salida("S003", getRutaPorCodigo("R02"), parseFecha("16/03/2026 07:00"), getBusPorPlaca("KCC-303"), "PROGRAMADA"));
        mySalidas.add(new Salida("S004", getRutaPorCodigo("R02"), parseFecha("16/03/2026 20:00"), getBusPorPlaca("KDD-404"), "PROGRAMADA"));
        mySalidas.add(new Salida("S005", getRutaPorCodigo("R03"), parseFecha("17/03/2026 05:30"), getBusPorPlaca("KFF-606"), "PROGRAMADA"));
        mySalidas.add(new Salida("S006", getRutaPorCodigo("R03"), parseFecha("17/03/2026 18:00"), getBusPorPlaca("KAA-101"), "PROGRAMADA"));
        mySalidas.add(new Salida("S007", getRutaPorCodigo("R04"), parseFecha("18/03/2026 06:30"), getBusPorPlaca("KCC-303"), "PROGRAMADA"));
        mySalidas.add(new Salida("S008", getRutaPorCodigo("R04"), parseFecha("18/03/2026 19:30"), getBusPorPlaca("KBB-202"), "PROGRAMADA"));

        mySalidas.add(new Salida("S009", getRutaPorCodigo("R05"), parseFecha("16/03/2026 08:00"), getBusPorPlaca("KCC-303"), "PROGRAMADA"));
        mySalidas.add(new Salida("S010", getRutaPorCodigo("R05"), parseFecha("16/03/2026 16:00"), getBusPorPlaca("KBB-202"), "PROGRAMADA"));
        mySalidas.add(new Salida("S011", getRutaPorCodigo("R06"), parseFecha("17/03/2026 09:00"), getBusPorPlaca("KDD-404"), "PROGRAMADA"));
        mySalidas.add(new Salida("S012", getRutaPorCodigo("R06"), parseFecha("17/03/2026 22:00"), getBusPorPlaca("KAA-101"), "PROGRAMADA"));
        mySalidas.add(new Salida("S013", getRutaPorCodigo("R07"), parseFecha("18/03/2026 07:30"), getBusPorPlaca("KFF-606"), "PROGRAMADA"));
        mySalidas.add(new Salida("S014", getRutaPorCodigo("R08"), parseFecha("19/03/2026 08:00"), getBusPorPlaca("KDD-404"), "PROGRAMADA"));

        myConductores.add(new Conductor("1001", "Juan Pérez", "Calle 10 #5-20", "juan@copetran.com", "3001112233", 2500000f));
        myConductores.add(new Conductor("1002", "Carlos Gómez", "Av. 3 #12-45", "carlos@copetran.com", "3102223344", 2300000f));
        myConductores.add(new Conductor("1003", "Andrés López", "Cra. 7 #8-15", "andres@copetran.com", "3203334455", 2400000f));
    }
}
