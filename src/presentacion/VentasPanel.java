package presentacion;

import negocio.EmpresaTransporte;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.List;

public class VentasPanel extends JPanel {

    private final EmpresaTransporte empresa;
    private final CardLayout subCardLayout;
    private final JPanel subPanel;
    private static int ticketCounter = 1;

    public VentasPanel(EmpresaTransporte empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(Colores.FONDO_GENERAL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tabBar.setOpaque(false);

        JButton unTicketBtn = crearTabBtn("1 o mas tiquetes", true);
        JButton idaVueltaBtn = crearTabBtn("Ida y vuelta", false);

        subCardLayout = new CardLayout();
        subPanel = new JPanel(subCardLayout);
        subPanel.setOpaque(false);

        subPanel.add(crearPanelUnTicket(), "unTicket");
        subPanel.add(crearPanelIdaVuelta(), "idaVuelta");

        unTicketBtn.addActionListener(e -> {
            subCardLayout.show(subPanel, "unTicket");
            unTicketBtn.setBackground(Colores.AZUL_PRIMARIO);
            unTicketBtn.setForeground(Color.WHITE);
            idaVueltaBtn.setBackground(Colores.FONDO_SUPERFICIE);
            idaVueltaBtn.setForeground(Colores.TEXTO_PRIMARIO);
        });
        idaVueltaBtn.addActionListener(e -> {
            subCardLayout.show(subPanel, "idaVuelta");
            idaVueltaBtn.setBackground(Colores.AZUL_PRIMARIO);
            idaVueltaBtn.setForeground(Color.WHITE);
            unTicketBtn.setBackground(Colores.FONDO_SUPERFICIE);
            unTicketBtn.setForeground(Colores.TEXTO_PRIMARIO);
        });

        tabBar.add(unTicketBtn);
        tabBar.add(idaVueltaBtn);
        add(tabBar, BorderLayout.NORTH);

        JScrollPane mainScroll = new JScrollPane(subPanel);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(mainScroll, BorderLayout.CENTER);
    }

    private JButton crearTabBtn(String texto, boolean activo) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        btn.setBorder(new LineBorder(Colores.BORDE, 1));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(180, 35));
        if (activo) {
            btn.setBackground(Colores.AZUL_PRIMARIO);
            btn.setForeground(Color.WHITE);
        } else {
            btn.setBackground(Colores.FONDO_SUPERFICIE);
            btn.setForeground(Colores.TEXTO_PRIMARIO);
        }
        return btn;
    }

    private JComboBox<String> salidaComboUnTicket;
    private JComboBox<String> destinoComboUnTicket;
    private JPanel sillasGridUnTicket;
    private JSpinner cantidadSpinner;
    private JPanel pasajerosPanel;
    private List<PasajeroForm> pasajeroForms = new ArrayList<>();
    private int[] selectedBlock;
    private String selectedSalidaIdUnTicket;

    private JLabel resumenRuta, resumenSalida, resumenBus, resumenPuestos, resumenTotal;

    private JPanel crearPanelUnTicket() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        leftPanel.add(crearPasoSeleccionSalida());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(crearPanelCantidadYSillas());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(crearPanelDatosPasajeros());

        JPanel rightPanel = crearPanelResumen();

        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, rightPanel);
        split.setDividerLocation(520);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPasoSeleccionSalida() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Colores.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel titulo = new JLabel("Paso 1: Seleccionar salida");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Origen:"), gbc);
        gbc.gridx = 1;
        JTextField origenField = new JTextField("Cucuta");
        origenField.setEditable(false);
        panel.add(origenField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Destino:"), gbc);
        gbc.gridx = 1;
        destinoComboUnTicket = new JComboBox<>();
        cargarDestinosEnCombo(destinoComboUnTicket);
        panel.add(destinoComboUnTicket, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Salida:"), gbc);
        gbc.gridx = 1;
        salidaComboUnTicket = new JComboBox<>();
        cargarSalidasEnCombo(salidaComboUnTicket, null);
        destinoComboUnTicket.addActionListener(e -> cargarSalidasEnCombo(salidaComboUnTicket, (String) destinoComboUnTicket.getSelectedItem()));
        salidaComboUnTicket.addActionListener(e -> onSalidaUnTicketChanged());
        panel.add(salidaComboUnTicket, gbc);

        return panel;
    }

    private JPanel crearPanelCantidadYSillas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titulo = new JLabel("Paso 2: Seleccionar cantidad y puestos");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.setOpaque(false);
        top.add(new JLabel("Cantidad de puestos:"));
        cantidadSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 40, 1));
        cantidadSpinner.addChangeListener(e -> onCantidadChanged());
        top.add(cantidadSpinner);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        centerWrapper.setOpaque(false);

        sillasGridUnTicket = new JPanel();
        sillasGridUnTicket.setOpaque(false);
        centerWrapper.add(sillasGridUnTicket, BorderLayout.CENTER);

        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        leyenda.setOpaque(false);
        leyenda.add(crearLeyenda(Colores.ESTADO_VERDE, "Disponible"));
        leyenda.add(crearLeyenda(Colores.AZUL_PRIMARIO, "Seleccionada"));
        leyenda.add(crearLeyenda(Colores.FONDO_SUPERFICIE, "Ocupada"));
        centerWrapper.add(leyenda, BorderLayout.SOUTH);

        panel.add(titulo, BorderLayout.NORTH);
        panel.add(top, BorderLayout.NORTH);
        panel.add(centerWrapper, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDatosPasajeros() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titulo = new JLabel("Paso 3: Datos de los pasajeros");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(titulo, BorderLayout.NORTH);

        pasajerosPanel = new JPanel();
        pasajerosPanel.setLayout(new BoxLayout(pasajerosPanel, BoxLayout.Y_AXIS));
        pasajerosPanel.setOpaque(false);
        panel.add(new JScrollPane(pasajerosPanel), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colores.FONDO_SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("Resumen de compra");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        titulo.setForeground(Colores.TEXTO_PRIMARIO);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        resumenRuta = new JLabel("Ruta: --");
        resumenSalida = new JLabel("Salida: --");
        resumenBus = new JLabel("Bus: --");
        resumenPuestos = new JLabel("Puestos: --");
        panel.add(resumenRuta);
        panel.add(resumenSalida);
        panel.add(resumenBus);
        panel.add(resumenPuestos);
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(10));

        resumenTotal = new JLabel("Total: $0");
        resumenTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        resumenTotal.setForeground(Colores.TEXTO_PRIMARIO);
        panel.add(resumenTotal);
        panel.add(Box.createVerticalStrut(15));

        panel.add(new JLabel("Forma de pago:"));
        panel.add(new JComboBox<>(new String[]{"Efectivo", "Tarjeta debito", "Tarjeta credito"}));
        panel.add(Box.createVerticalStrut(15));

        JButton generarBtn = new JButton("Generar tiquetes");
        generarBtn.setBackground(Colores.AZUL_PRIMARIO);
        generarBtn.setForeground(Color.WHITE);
        generarBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        generarBtn.setFocusPainted(false);
        generarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generarBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        generarBtn.addActionListener(e -> generarUnTicket());
        panel.add(generarBtn);

        return panel;
    }

    private void cargarSalidasEnCombo(JComboBox<String> combo, String destino) {
        combo.removeAllItems();
        String[][] items;
        if (destino == null) {
            items = empresa.getSalidasProgramadasParaCombo();
        } else {
            items = empresa.getSalidasProgramadasPorDestino(destino);
        }
        for (String[] item : items) {
            combo.addItem(item[1]);
        }
        if (combo.getItemCount() == 0) {
            combo.addItem("-- No hay salidas disponibles --");
            combo.setEnabled(false);
        } else {
            combo.setEnabled(true);
        }
    }

    private void onSalidaUnTicketChanged() {
        String sel = (String) salidaComboUnTicket.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            selectedSalidaIdUnTicket = null;
            selectedBlock = null;
            rebuildSeatGridUnTicket();
            actualizarResumenUnTicket();
            return;
        }
        selectedSalidaIdUnTicket = sel.split(" —")[0].trim();
        selectedBlock = null;
        rebuildSeatGridUnTicket();
        onCantidadChanged();
        actualizarResumenUnTicket();
    }

    private void onCantidadChanged() {
        int cantidad = (Integer) cantidadSpinner.getValue();
        if (selectedSalidaIdUnTicket == null) return;
        int[] bloque = empresa.verificarPuestosConsecutivos(selectedSalidaIdUnTicket, cantidad);
        if (bloque != null) {
            selectedBlock = bloque;
            rebuildSeatGridUnTicket();
            rebuildPasajeroForms(cantidad);
        } else {
            selectedBlock = null;
            rebuildSeatGridUnTicket();
            pasajerosPanel.removeAll();
            pasajeroForms.clear();
            pasajerosPanel.revalidate();
            pasajerosPanel.repaint();
            JOptionPane.showMessageDialog(this, "No hay " + cantidad + " puestos consecutivos disponibles en esta salida.", "Sin cupo", JOptionPane.WARNING_MESSAGE);
        }
        actualizarResumenUnTicket();
    }

    private void rebuildSeatGridUnTicket() {
        sillasGridUnTicket.removeAll();
        if (selectedSalidaIdUnTicket == null) return;
        int[] estados = empresa.getEstadoPuestos(selectedSalidaIdUnTicket, selectedBlock);
        int cols = 4;
        int rows = (int) Math.ceil(estados.length / (double) cols);
        sillasGridUnTicket.setLayout(new GridLayout(rows, cols, 4, 4));

        for (int i = 0; i < estados.length; i++) {
            final int numPuesto = i + 1;
            JButton btn = new JButton(String.valueOf(numPuesto));
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setFont(new Font("SansSerif", Font.BOLD, 10));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (estados[i] == 2) {
                btn.setBackground(Colores.FONDO_SUPERFICIE);
                btn.setForeground(Colores.TEXTO_SECUNDARIO);
                btn.setEnabled(false);
            } else if (estados[i] == 1) {
                btn.setBackground(Colores.AZUL_PRIMARIO);
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> seleccionarBloqueDesde(numPuesto, estados));
            } else {
                btn.setBackground(Colores.ESTADO_VERDE);
                btn.setForeground(Colores.ESTADO_VERDE_TX);
                btn.addActionListener(e -> seleccionarBloqueDesde(numPuesto, estados));
            }
            sillasGridUnTicket.add(btn);
        }
        sillasGridUnTicket.revalidate();
        sillasGridUnTicket.repaint();
    }

    private void seleccionarBloqueDesde(int numPuesto, int[] estadosActuales) {
        int cantidad = (Integer) cantidadSpinner.getValue();
        boolean puede = true;
        for (int j = 0; j < cantidad; j++) {
            int idx = numPuesto - 1 + j;
            if (idx >= estadosActuales.length || estadosActuales[idx] == 2) {
                puede = false; break;
            }
        }
        if (puede) {
            selectedBlock = new int[cantidad];
            for (int j = 0; j < cantidad; j++) selectedBlock[j] = numPuesto + j;
            rebuildSeatGridUnTicket();
            rebuildPasajeroForms(cantidad);
            actualizarResumenUnTicket();
        } else {
            JOptionPane.showMessageDialog(this, "No hay suficientes puestos consecutivos desde esta posicion.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rebuildPasajeroForms(int cantidad) {
        pasajerosPanel.removeAll();
        pasajeroForms.clear();
        for (int i = 0; i < cantidad; i++) {
            int puesto = selectedBlock != null && i < selectedBlock.length ? selectedBlock[i] : (i + 1);
            PasajeroForm form = new PasajeroForm(puesto, empresa, pasajerosPanel);
            pasajeroForms.add(form);
            pasajerosPanel.add(form);
            pasajerosPanel.add(Box.createVerticalStrut(8));
        }
        pasajerosPanel.revalidate();
        pasajerosPanel.repaint();
    }

    private void generarUnTicket() {
        if (selectedSalidaIdUnTicket == null || selectedBlock == null) {
            JOptionPane.showMessageDialog(this, "Seleccione una salida y un bloque de puestos");
            return;
        }
        int cantidad = (Integer) cantidadSpinner.getValue();
        if (pasajeroForms.size() != cantidad) {
            JOptionPane.showMessageDialog(this, "Datos de pasajeros incompletos");
            return;
        }

        String[] cedulas = new String[cantidad];
        String[] nombres = new String[cantidad];
        String[] correos = new String[cantidad];
        String[] telefonos = new String[cantidad];
        for (int i = 0; i < cantidad; i++) {
            PasajeroForm form = pasajeroForms.get(i);
            cedulas[i] = form.cedulaField.getText().trim();
            nombres[i] = form.nombreField.getText().trim();
            correos[i] = form.correoField.getText().trim();
            telefonos[i] = form.telefonoField.getText().trim();
            if (cedulas[i].isEmpty() || nombres[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete cedula y nombre para el pasajero del puesto " + form.puesto);
                return;
            }
        }

        String[] resultados = empresa.generarTicketsFormateados(selectedSalidaIdUnTicket,
            selectedBlock, cedulas, nombres, correos, telefonos);
        if (resultados != null) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < resultados.length; i++) {
                if (i > 0) sb.append("\n\n");
                sb.append(resultados[i].replace("TQ-00001", "TQ-" + String.format("%05d", ticketCounter++)));
            }
            JOptionPane.showMessageDialog(this, sb.toString());
            limpiarFormularioUnTicket();
        } else {
            JOptionPane.showMessageDialog(this, "Verifique que la salida este PROGRAMADA y haya puestos disponibles.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormularioUnTicket() {
        selectedBlock = null;
        selectedSalidaIdUnTicket = null;
        pasajeroForms.clear();
        pasajerosPanel.removeAll();
        pasajerosPanel.revalidate();
        pasajerosPanel.repaint();
        cantidadSpinner.setValue(1);
        if (destinoComboUnTicket.getItemCount() > 0) destinoComboUnTicket.setSelectedIndex(0);
        if (salidaComboUnTicket.getItemCount() > 0) salidaComboUnTicket.setSelectedIndex(0);
        onSalidaUnTicketChanged();
        rebuildSeatGridUnTicket();
        actualizarResumenUnTicket();
    }

    private void actualizarResumenUnTicket() {
        if (selectedSalidaIdUnTicket == null) {
            resumenRuta.setText("Ruta: --");
            resumenSalida.setText("Salida: --");
            resumenBus.setText("Bus: --");
            resumenPuestos.setText("Puestos: --");
            resumenTotal.setText("Total: $0");
            return;
        }
        String[] info = empresa.getInfoSalida(selectedSalidaIdUnTicket);
        if (info == null || info[0].isEmpty()) return;
        resumenRuta.setText("Ruta: " + info[0]);
        resumenSalida.setText("Salida: " + info[1]);
        resumenBus.setText("Bus: " + info[2]);
        if (selectedBlock != null) {
            StringBuilder sb = new StringBuilder("Puestos: ");
            for (int p : selectedBlock) sb.append(p).append(" ");
            resumenPuestos.setText(sb.toString());
            float total = 0f;
            for (int i = 0; i < selectedBlock.length; i++) {
                float valor = empresa.getTarifaSalida(selectedSalidaIdUnTicket);
                PasajeroForm form = i < pasajeroForms.size() ? pasajeroForms.get(i) : null;
                if (form != null && !form.cedulaField.getText().trim().isEmpty()) {
                    String[] pasajeroInfo = empresa.getInfoPasajero(form.cedulaField.getText().trim());
                    if (pasajeroInfo != null && "true".equals(pasajeroInfo[4])) {
                        valor = valor * 0.9f;
                    }
                }
                total += valor;
            }
            resumenTotal.setText("Total: $" + String.format("%,.0f", total));
        } else {
            resumenPuestos.setText("Puestos: --");
            resumenTotal.setText("Total: $0");
        }
    }

    private JComboBox<String> idaCombo, vueltaCombo;
    private JPanel sillasGridIda, sillasGridVuelta;
    private JSpinner cantidadIdaSpinner, cantidadVueltaSpinner;
    private int[] selectedBlockIda, selectedBlockVuelta;
    private String selectedSalidaIdIda, selectedSalidaIdVuelta;
    private JLabel resumenIyVRuta, resumenIyVIda, resumenIyVVuelta, resumenIyVPuestos, resumenIyVTotal, validacionRutaLabel;

    private JPanel pasajerosIyVPanel;
    private List<PasajeroForm> pasajeroIyVForms = new ArrayList<>();

    private JPanel crearPanelIdaVuelta() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        leftPanel.add(crearPanelSeleccionIdaVuelta());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(crearPanelSillasIdaVuelta());
        leftPanel.add(Box.createVerticalStrut(10));
        leftPanel.add(crearPanelDatosPasajerosIyV());

        JPanel resumenPanel = crearPanelResumenIyV();

        JScrollPane leftScroll = new JScrollPane(leftPanel);
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(16);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftScroll, resumenPanel);
        split.setDividerLocation(520);
        split.setBorder(null);
        panel.add(split, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelSeleccionIdaVuelta() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Colores.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);

        JLabel titulo = new JLabel("Seleccionar salidas (ida y vuelta)");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(titulo, gbc);
        gbc.gridwidth = 1;

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Ida:"), gbc);
        gbc.gridx = 1;
        idaCombo = new JComboBox<>();
        cargarSalidasEnCombo(idaCombo, null);
        idaCombo.addActionListener(e -> onIdaChanged());
        panel.add(idaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Vuelta:"), gbc);
        gbc.gridx = 1;
        vueltaCombo = new JComboBox<>();
        cargarSalidasEnCombo(vueltaCombo, null);
        vueltaCombo.addActionListener(e -> onVueltaChanged());
        panel.add(vueltaCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        validacionRutaLabel = new JLabel(" ");
        validacionRutaLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
        panel.add(validacionRutaLabel, gbc);

        return panel;
    }

    private JPanel crearPanelSillasIdaVuelta() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setOpaque(false);

        JPanel idaPanel = new JPanel(new BorderLayout());
        idaPanel.setBackground(Colores.FONDO_TARJETA);
        idaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JPanel topIda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topIda.setOpaque(false);
        topIda.add(new JLabel("Cantidad:"));
        cantidadIdaSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 40, 1));
        cantidadIdaSpinner.addChangeListener(e -> onCantidadIdaChanged());
        topIda.add(cantidadIdaSpinner);
        idaPanel.add(topIda, BorderLayout.NORTH);
        sillasGridIda = new JPanel();
        sillasGridIda.setOpaque(false);
        idaPanel.add(sillasGridIda, BorderLayout.CENTER);
        JPanel leyIda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leyIda.setOpaque(false);
        leyIda.add(crearLeyenda(Colores.ESTADO_VERDE, "Libre"));
        leyIda.add(crearLeyenda(Colores.AZUL_PRIMARIO, "Sel."));
        leyIda.add(crearLeyenda(Colores.FONDO_SUPERFICIE, "Ocup."));
        idaPanel.add(leyIda, BorderLayout.SOUTH);

        JPanel vueltaPanel = new JPanel(new BorderLayout());
        vueltaPanel.setBackground(Colores.FONDO_TARJETA);
        vueltaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JPanel topVuelta = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topVuelta.setOpaque(false);
        topVuelta.add(new JLabel("Cantidad:"));
        cantidadVueltaSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 40, 1));
        cantidadVueltaSpinner.addChangeListener(e -> onCantidadVueltaChanged());
        topVuelta.add(cantidadVueltaSpinner);
        vueltaPanel.add(topVuelta, BorderLayout.NORTH);
        sillasGridVuelta = new JPanel();
        sillasGridVuelta.setOpaque(false);
        vueltaPanel.add(sillasGridVuelta, BorderLayout.CENTER);
        JPanel leyVuelta = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        leyVuelta.setOpaque(false);
        leyVuelta.add(crearLeyenda(Colores.ESTADO_VERDE, "Libre"));
        leyVuelta.add(crearLeyenda(Colores.AZUL_PRIMARIO, "Sel."));
        leyVuelta.add(crearLeyenda(Colores.FONDO_SUPERFICIE, "Ocup."));
        vueltaPanel.add(leyVuelta, BorderLayout.SOUTH);

        panel.add(idaPanel);
        panel.add(vueltaPanel);

        return panel;
    }

    private JPanel crearPanelDatosPasajerosIyV() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colores.FONDO_TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        JLabel titulo = new JLabel("Paso 3: Datos de los pasajeros (aplica para ida y vuelta)");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 12));
        panel.add(titulo, BorderLayout.NORTH);
        pasajerosIyVPanel = new JPanel();
        pasajerosIyVPanel.setLayout(new BoxLayout(pasajerosIyVPanel, BoxLayout.Y_AXIS));
        pasajerosIyVPanel.setOpaque(false);
        panel.add(new JScrollPane(pasajerosIyVPanel), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelResumenIyV() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colores.FONDO_SUPERFICIE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titulo = new JLabel("Resumen ida y vuelta");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        titulo.setForeground(Colores.TEXTO_PRIMARIO);
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(10));

        resumenIyVRuta = new JLabel("Ruta: --");
        resumenIyVIda = new JLabel("Ida: --");
        resumenIyVVuelta = new JLabel("Vuelta: --");
        resumenIyVPuestos = new JLabel("Puestos: --");
        panel.add(resumenIyVRuta);
        panel.add(resumenIyVIda);
        panel.add(resumenIyVVuelta);
        panel.add(resumenIyVPuestos);
        panel.add(Box.createVerticalStrut(5));
        panel.add(new JSeparator());
        panel.add(Box.createVerticalStrut(5));

        resumenIyVTotal = new JLabel("Total: $0");
        resumenIyVTotal.setFont(new Font("SansSerif", Font.BOLD, 15));
        resumenIyVTotal.setForeground(Colores.TEXTO_PRIMARIO);
        panel.add(new JLabel("Descuento 10% sobre total (ida + vuelta)"));
        panel.add(resumenIyVTotal);
        panel.add(Box.createVerticalStrut(15));

        JButton generarBtn = new JButton("Generar tiquetes ida y vuelta");
        generarBtn.setBackground(Colores.AZUL_PRIMARIO);
        generarBtn.setForeground(Color.WHITE);
        generarBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        generarBtn.setFocusPainted(false);
        generarBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generarBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        generarBtn.addActionListener(e -> generarIdaYVuelta());
        panel.add(generarBtn);

        return panel;
    }

    private void onIdaChanged() {
        String sel = (String) idaCombo.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            selectedSalidaIdIda = null;
            selectedBlockIda = null;
            rebuildSeatGridIyV(sillasGridIda, null, true);
            validarMismaRuta();
            actualizarResumenIyV();
            return;
        }
        selectedSalidaIdIda = sel.split(" —")[0].trim();
        selectedBlockIda = null;
        rebuildSeatGridIyV(sillasGridIda, selectedSalidaIdIda, true);
        onCantidadIdaChanged();
        validarMismaRuta();
        actualizarResumenIyV();
    }

    private void onVueltaChanged() {
        String sel = (String) vueltaCombo.getSelectedItem();
        if (sel == null || sel.startsWith("--")) {
            selectedSalidaIdVuelta = null;
            selectedBlockVuelta = null;
            rebuildSeatGridIyV(sillasGridVuelta, null, false);
            validarMismaRuta();
            actualizarResumenIyV();
            return;
        }
        selectedSalidaIdVuelta = sel.split(" —")[0].trim();
        selectedBlockVuelta = null;
        rebuildSeatGridIyV(sillasGridVuelta, selectedSalidaIdVuelta, false);
        onCantidadVueltaChanged();
        validarMismaRuta();
        actualizarResumenIyV();
    }

    private void validarMismaRuta() {
        if (selectedSalidaIdIda == null || selectedSalidaIdVuelta == null) {
            validacionRutaLabel.setText(" ");
            return;
        }
        String[] infoIda = empresa.getInfoSalida(selectedSalidaIdIda);
        String[] infoVuelta = empresa.getInfoSalida(selectedSalidaIdVuelta);
        if (infoIda != null && infoVuelta != null && !infoIda[0].isEmpty() && !infoVuelta[0].isEmpty()) {
            boolean esRetorno = infoIda[4].equals(infoVuelta[5]) && infoIda[5].equals(infoVuelta[4]);
            if (esRetorno) {
                validacionRutaLabel.setText("Ruta de retorno validada (" + infoIda[4] + " \u2194 " + infoIda[5] + ")");
                validacionRutaLabel.setForeground(Colores.ESTADO_VERDE_TX);
            } else {
                validacionRutaLabel.setText("Error: la vuelta debe ser la ruta inversa (" + infoIda[5] + " -> " + infoIda[4] + ")");
                validacionRutaLabel.setForeground(Colores.ESTADO_ROJO_TX);
            }
        }
    }

    private void onCantidadIdaChanged() {
        int cantidad = (Integer) cantidadIdaSpinner.getValue();
        if (selectedSalidaIdIda == null) return;
        int[] bloque = empresa.verificarPuestosConsecutivos(selectedSalidaIdIda, cantidad);
        if (bloque != null) {
            selectedBlockIda = bloque;
        } else {
            selectedBlockIda = null;
            JOptionPane.showMessageDialog(this, "No hay " + cantidad + " puestos consecutivos en la salida de ida.", "Sin cupo", JOptionPane.WARNING_MESSAGE);
        }
        rebuildSeatGridIyV(sillasGridIda, selectedSalidaIdIda, true);
        revisarFormulariosIyV();
        actualizarResumenIyV();
    }

    private void onCantidadVueltaChanged() {
        int cantidad = (Integer) cantidadVueltaSpinner.getValue();
        if (selectedSalidaIdVuelta == null) return;
        int[] bloque = empresa.verificarPuestosConsecutivos(selectedSalidaIdVuelta, cantidad);
        if (bloque != null) {
            selectedBlockVuelta = bloque;
        } else {
            selectedBlockVuelta = null;
            JOptionPane.showMessageDialog(this, "No hay " + cantidad + " puestos consecutivos en la salida de vuelta.", "Sin cupo", JOptionPane.WARNING_MESSAGE);
        }
        rebuildSeatGridIyV(sillasGridVuelta, selectedSalidaIdVuelta, false);
        revisarFormulariosIyV();
        actualizarResumenIyV();
    }

    private void revisarFormulariosIyV() {
        if (selectedBlockIda != null && selectedBlockIda.length > 0) {
            rebuildPasajeroIyVForms(selectedBlockIda.length);
        }
    }

    private void rebuildPasajeroIyVForms(int cantidad) {
        pasajerosIyVPanel.removeAll();
        pasajeroIyVForms.clear();
        for (int i = 0; i < cantidad; i++) {
            int puestoIda = selectedBlockIda != null && i < selectedBlockIda.length ? selectedBlockIda[i] : (i + 1);
            PasajeroForm form = new PasajeroForm(puestoIda, empresa, pasajerosIyVPanel);
            pasajeroIyVForms.add(form);
            pasajerosIyVPanel.add(form);
            pasajerosIyVPanel.add(Box.createVerticalStrut(8));
        }
        pasajerosIyVPanel.revalidate();
        pasajerosIyVPanel.repaint();
    }

    private void rebuildSeatGridIyV(JPanel grid, String salidaId, boolean esIda) {
        grid.removeAll();
        if (salidaId == null) return;
        int[] bloque = esIda ? selectedBlockIda : selectedBlockVuelta;
        int[] estados = empresa.getEstadoPuestos(salidaId, bloque);
        int cols = 4;
        int rows = (int) Math.ceil(estados.length / (double) cols);
        grid.setLayout(new GridLayout(rows, cols, 4, 4));

        for (int i = 0; i < estados.length; i++) {
            final int numPuesto = i + 1;
            JButton btn = new JButton(String.valueOf(numPuesto));
            btn.setPreferredSize(new Dimension(40, 40));
            btn.setFont(new Font("SansSerif", Font.BOLD, 10));
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (estados[i] == 2) {
                btn.setBackground(Colores.FONDO_SUPERFICIE);
                btn.setForeground(Colores.TEXTO_SECUNDARIO);
                btn.setEnabled(false);
            } else if (estados[i] == 1) {
                btn.setBackground(Colores.AZUL_PRIMARIO);
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    int cantidad = esIda ? (Integer) cantidadIdaSpinner.getValue() : (Integer) cantidadVueltaSpinner.getValue();
                    boolean puede = true;
                    int[] estadosActuales = empresa.getEstadoPuestos(salidaId, bloque);
                    for (int j = 0; j < cantidad; j++) {
                        int idx = numPuesto - 1 + j;
                        if (idx >= estadosActuales.length || estadosActuales[idx] == 2) {
                            puede = false; break;
                        }
                    }
                    if (puede) {
                        int[] nuevoBloque = new int[cantidad];
                        for (int j = 0; j < cantidad; j++) nuevoBloque[j] = numPuesto + j;
                        if (esIda) selectedBlockIda = nuevoBloque; else selectedBlockVuelta = nuevoBloque;
                        rebuildSeatGridIyV(grid, salidaId, esIda);
                        revisarFormulariosIyV();
                        actualizarResumenIyV();
                    }
                });
            } else {
                btn.setBackground(Colores.ESTADO_VERDE);
                btn.setForeground(Colores.ESTADO_VERDE_TX);
                btn.addActionListener(e -> {
                    int cantidad = esIda ? (Integer) cantidadIdaSpinner.getValue() : (Integer) cantidadVueltaSpinner.getValue();
                    boolean puede = true;
                    int[] estadosActuales = empresa.getEstadoPuestos(salidaId, bloque);
                    for (int j = 0; j < cantidad; j++) {
                        int idx = numPuesto - 1 + j;
                        if (idx >= estadosActuales.length || estadosActuales[idx] == 2) {
                            puede = false; break;
                        }
                    }
                    if (puede) {
                        int[] nuevoBloque = new int[cantidad];
                        for (int j = 0; j < cantidad; j++) nuevoBloque[j] = numPuesto + j;
                        if (esIda) selectedBlockIda = nuevoBloque; else selectedBlockVuelta = nuevoBloque;
                        rebuildSeatGridIyV(grid, salidaId, esIda);
                        revisarFormulariosIyV();
                        actualizarResumenIyV();
                    }
                });
            }
            grid.add(btn);
        }
        grid.revalidate();
        grid.repaint();
    }

    private void generarIdaYVuelta() {
        if (selectedSalidaIdIda == null || selectedSalidaIdVuelta == null || selectedBlockIda == null || selectedBlockVuelta == null) {
            JOptionPane.showMessageDialog(this, "Seleccione salidas de ida y vuelta y bloques de puestos");
            return;
        }

        int cantIda = selectedBlockIda.length;
        String[] cedulas = new String[cantIda];
        String[] nombres = new String[cantIda];
        String[] correos = new String[cantIda];
        String[] telefonos = new String[cantIda];
        for (int i = 0; i < cantIda; i++) {
            if (i >= pasajeroIyVForms.size()) { JOptionPane.showMessageDialog(this, "Datos de pasajeros incompletos"); return; }
            PasajeroForm form = pasajeroIyVForms.get(i);
            cedulas[i] = form.cedulaField.getText().trim();
            nombres[i] = form.nombreField.getText().trim();
            correos[i] = form.correoField.getText().trim();
            telefonos[i] = form.telefonoField.getText().trim();
            if (cedulas[i].isEmpty() || nombres[i].isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete cedula y nombre para el pasajero del puesto " + form.puesto);
                return;
            }
        }

        String resultado = empresa.ventaIdaYVueltaFormateada(
            selectedSalidaIdIda, selectedBlockIda, cedulas, nombres, correos, telefonos,
            selectedSalidaIdVuelta, selectedBlockVuelta);
        if (resultado != null && !resultado.startsWith("La salida") && !resultado.startsWith("No hay")
            && !resultado.startsWith("Algun puesto") && !resultado.startsWith("Error")) {
            JOptionPane.showMessageDialog(this, resultado);
            limpiarFormularioIyV();
        } else {
            JOptionPane.showMessageDialog(this, resultado != null ? resultado : "Error al generar tiquetes de ida y vuelta.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarFormularioIyV() {
        selectedBlockIda = null;
        selectedBlockVuelta = null;
        selectedSalidaIdIda = null;
        selectedSalidaIdVuelta = null;
        pasajeroIyVForms.clear();
        pasajerosIyVPanel.removeAll();
        pasajerosIyVPanel.revalidate();
        pasajerosIyVPanel.repaint();
        cantidadIdaSpinner.setValue(1);
        cantidadVueltaSpinner.setValue(1);
        if (idaCombo.getItemCount() > 0) idaCombo.setSelectedIndex(0);
        if (vueltaCombo.getItemCount() > 0) vueltaCombo.setSelectedIndex(0);
        onIdaChanged();
        onVueltaChanged();
        validacionRutaLabel.setText(" ");
        actualizarResumenIyV();
    }

    private void actualizarResumenIyV() {
        String[] infoIda = selectedSalidaIdIda != null ? empresa.getInfoSalida(selectedSalidaIdIda) : null;
        String[] infoVuelta = selectedSalidaIdVuelta != null ? empresa.getInfoSalida(selectedSalidaIdVuelta) : null;
        if (infoIda != null && !infoIda[0].isEmpty()) {
            resumenIyVRuta.setText("Ruta: " + infoIda[0]);
            resumenIyVIda.setText("Ida: " + infoIda[1]);
        } else {
            resumenIyVRuta.setText("Ruta: --");
            resumenIyVIda.setText("Ida: --");
        }
        if (infoVuelta != null && !infoVuelta[0].isEmpty()) {
            resumenIyVVuelta.setText("Vuelta: " + infoVuelta[1]);
        } else {
            resumenIyVVuelta.setText("Vuelta: --");
        }
        if (selectedBlockIda != null && selectedBlockVuelta != null && infoIda != null && infoVuelta != null) {
            StringBuilder sb = new StringBuilder("Puestos ida: ");
            for (int p : selectedBlockIda) sb.append(p).append(" ");
            sb.append("| vuelta: ");
            for (int p : selectedBlockVuelta) sb.append(p).append(" ");
            resumenIyVPuestos.setText(sb.toString());
            float total = 0f;
            for (int i = 0; i < selectedBlockIda.length; i++) {
                float base = empresa.getTarifaSalida(selectedSalidaIdIda);
                float totalAntes = base * 2f;
                total += totalAntes * 0.9f;
            }
            resumenIyVTotal.setText("Total: $" + String.format("%,.0f", total));
        } else {
            resumenIyVPuestos.setText("Puestos: --");
            resumenIyVTotal.setText("Total: $0");
        }
    }

    private JPanel crearLeyenda(Color color, String texto) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        p.setOpaque(false);
        JLabel cuadro = new JLabel("  ");
        cuadro.setOpaque(true);
        cuadro.setBackground(color);
        cuadro.setPreferredSize(new Dimension(14, 14));
        JLabel label = new JLabel(texto);
        label.setFont(new Font("SansSerif", Font.PLAIN, 10));
        label.setForeground(Colores.TEXTO_SECUNDARIO);
        p.add(cuadro);
        p.add(label);
        return p;
    }

    public void refreshData() {
        cargarDestinosEnCombo(destinoComboUnTicket);
        cargarSalidasEnCombo(salidaComboUnTicket, (String) destinoComboUnTicket.getSelectedItem());
        cargarSalidasEnCombo(idaCombo, null);
        cargarSalidasEnCombo(vueltaCombo, null);
        limpiarFormularioUnTicket();
        limpiarFormularioIyV();
    }

    private void cargarDestinosEnCombo(JComboBox<String> combo) {
        String selected = (String) combo.getSelectedItem();
        combo.removeAllItems();
        for (String[] item : empresa.getDestinosDisponibles()) {
            if (!"Cucuta".equalsIgnoreCase(item[0])) {
                combo.addItem(item[0]);
            }
        }
        if (selected != null && !"Cucuta".equalsIgnoreCase(selected)) combo.setSelectedItem(selected);
    }

    private static class PasajeroForm extends JPanel {
        final int puesto;
        final JTextField cedulaField;
        final JTextField nombreField;
        final JTextField correoField;
        final JTextField telefonoField;
        final JLabel tipoLabel;

        PasajeroForm(int puesto, EmpresaTransporte empresa, JPanel parent) {
            this.puesto = puesto;
            setLayout(new GridBagLayout());
            setBackground(new Color(248, 248, 248));
            setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Colores.BORDE, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
            ));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(2, 2, 2, 2);

            gbc.gridx = 0; gbc.gridy = 0;
            add(new JLabel("Puesto " + puesto + " | Cedula:"), gbc);
            gbc.gridx = 1;
            cedulaField = new JTextField(10);
            add(cedulaField, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Nombre:"), gbc);
            gbc.gridx = 1;
            nombreField = new JTextField(10);
            add(nombreField, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            add(new JLabel("Correo:"), gbc);
            gbc.gridx = 1;
            correoField = new JTextField(10);
            add(correoField, gbc);

            gbc.gridx = 0; gbc.gridy = 3;
            add(new JLabel("Telefono:"), gbc);
            gbc.gridx = 1;
            telefonoField = new JTextField(10);
            add(telefonoField, gbc);

            gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
            tipoLabel = new JLabel(" ");
            tipoLabel.setFont(new Font("SansSerif", Font.BOLD, 11));
            add(tipoLabel, gbc);

            cedulaField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    String cedula = cedulaField.getText().trim();
                    if (!cedula.isEmpty()) {
                        String[] info = empresa.getInfoPasajero(cedula);
                        if (info != null) {
                            nombreField.setText(info[1]);
                            correoField.setText(info[2]);
                            telefonoField.setText(info[3]);
                            nombreField.setEnabled(false);
                            if ("true".equals(info[4])) {
                                tipoLabel.setText("CLIENTE PREFERENCIAL -- 10% descuento");
                                tipoLabel.setForeground(Colores.ESTADO_VERDE_TX);
                            } else {
                                tipoLabel.setText("Cliente frecuente");
                                tipoLabel.setForeground(Colores.TEXTO_SECUNDARIO);
                            }
                        } else {
                            nombreField.setEnabled(true);
                            tipoLabel.setText("Nuevo cliente");
                            tipoLabel.setForeground(Colores.AZUL_MEDIO);
                        }
                    }
                }
            });
        }
    }
}
