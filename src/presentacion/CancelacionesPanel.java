package presentacion;

import negocio.EmpresaTransporte;
import negocio.Salida;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CancelacionesPanel extends JPanel {

    private final EmpresaTransporte empresa;
    private final JTextField idField;
    private final JLabel rutaLabel;
    private final JLabel fechaLabel;
    private final JLabel busLabel;
    private final JLabel ticketsLabel;
    private final JLabel estadoLabel;
    private final DefaultTableModel ticketsModel;
    private final JPanel alertaPanel;
    private final JRadioButton reprogramarRadio;
    private final JRadioButton reembolsarRadio;
    private final JButton confirmarBtn;
    private String idSalidaEncontrada;
    private String[] infoSalidaCache;

    public CancelacionesPanel(EmpresaTransporte empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout(10, 10));
        setBackground(Colores.FONDO_GENERAL);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel buscarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        buscarPanel.setOpaque(false);

        buscarPanel.add(new JLabel("ID de salida:"));
        idField = new JTextField(10);
        buscarPanel.add(idField);

        JComboBox<String> rutaCombo = new JComboBox<>();
        rutaCombo.addItem("Todas");
        for (String[] r : empresa.getRutasParaCombo()) {
            rutaCombo.addItem(r[0]);
        }
        buscarPanel.add(new JLabel("Ruta:"));
        buscarPanel.add(rutaCombo);

        JButton buscarBtn = new JButton("Buscar");
        buscarBtn.setBackground(Colores.AZUL_PRIMARIO);
        buscarBtn.setForeground(Color.WHITE);
        buscarBtn.setFocusPainted(false);
        buscarPanel.add(buscarBtn);

        add(buscarPanel, BorderLayout.NORTH);

        alertaPanel = new JPanel();
        alertaPanel.setBackground(Colores.ESTADO_ROJO);
        alertaPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.ESTADO_ROJO_TX, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        alertaPanel.setLayout(new BoxLayout(alertaPanel, BoxLayout.Y_AXIS));
        alertaPanel.setVisible(false);
        JLabel alertaTitulo = new JLabel("Salida encontrada");
        alertaTitulo.setFont(new Font("SansSerif", Font.BOLD, 13));
        alertaTitulo.setForeground(Colores.ESTADO_ROJO_TX);
        alertaPanel.add(alertaTitulo);
        JLabel alertaMsg = new JLabel("Al cambiar el estado se afectaran los tiquetes asociados.");
        alertaMsg.setFont(new Font("SansSerif", Font.PLAIN, 11));
        alertaMsg.setForeground(Colores.ESTADO_ROJO_TX);
        alertaPanel.add(alertaMsg);

        JPanel centralPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centralPanel.setOpaque(false);

        JPanel datosPanel = new JPanel();
        datosPanel.setLayout(new BoxLayout(datosPanel, BoxLayout.Y_AXIS));
        datosPanel.setBackground(Colores.FONDO_TARJETA);
        datosPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        datosPanel.add(new JLabel("Datos de la salida"));
        datosPanel.add(Box.createVerticalStrut(8));

        rutaLabel = new JLabel("Ruta: --");
        fechaLabel = new JLabel("Fecha y hora: --");
        busLabel = new JLabel("Bus asignado: --");

        datosPanel.add(rutaLabel);
        datosPanel.add(Box.createVerticalStrut(4));
        datosPanel.add(fechaLabel);
        datosPanel.add(Box.createVerticalStrut(4));
        datosPanel.add(busLabel);
        datosPanel.add(Box.createVerticalStrut(4));
        ticketsLabel = new JLabel("Tiquetes VIGENTES: 0");
        datosPanel.add(ticketsLabel);
        datosPanel.add(Box.createVerticalStrut(4));
        estadoLabel = new JLabel("Estado: --");
        estadoLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        datosPanel.add(estadoLabel);

        ticketsModel = new DefaultTableModel(new String[]{"Tiquete", "Pasajero", "Silla", "Estado"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable ticketsTable = new JTable(ticketsModel);
        ticketsTable.setRowHeight(28);

        JPanel ticketsContainer = new JPanel(new BorderLayout());
        ticketsContainer.setBackground(Colores.FONDO_TARJETA);
        ticketsContainer.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        ticketsContainer.add(new JLabel("Tiquetes afectados"), BorderLayout.NORTH);
        ticketsContainer.add(new JScrollPane(ticketsTable), BorderLayout.CENTER);

        centralPanel.add(datosPanel);
        centralPanel.add(ticketsContainer);

        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setOpaque(false);
        centerContainer.add(alertaPanel, BorderLayout.NORTH);
        centerContainer.add(centralPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        JPanel accionesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        accionesPanel.setBackground(Colores.FONDO_SUPERFICIE);
        accionesPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        reprogramarRadio = new JRadioButton("Reprogramar automaticamente (proxima salida despues de hoy)");
        reprogramarRadio.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reprogramarRadio.setOpaque(false);
        reembolsarRadio = new JRadioButton("Marcar como REEMBOLSADO");
        reembolsarRadio.setFont(new Font("SansSerif", Font.PLAIN, 12));
        reembolsarRadio.setOpaque(false);
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(reprogramarRadio);
        grupo.add(reembolsarRadio);
        reprogramarRadio.setSelected(true);

        accionesPanel.add(reprogramarRadio);
        accionesPanel.add(reembolsarRadio);

        JButton cancelarBtn = new JButton("Cancelar");
        confirmarBtn = new JButton("Confirmar cancelacion y generar reporte");
        confirmarBtn.setBackground(Colores.ESTADO_ROJO_TX);
        confirmarBtn.setForeground(Color.WHITE);
        confirmarBtn.setFocusPainted(false);

        accionesPanel.add(cancelarBtn);
        accionesPanel.add(confirmarBtn);
        add(accionesPanel, BorderLayout.SOUTH);

        buscarBtn.addActionListener(e -> buscarSalida());
        confirmarBtn.addActionListener(e -> confirmarCancelacion());
        cancelarBtn.addActionListener(e -> limpiarBusqueda());
    }

    private void buscarSalida() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese un ID de salida");
            return;
        }
        infoSalidaCache = empresa.getInfoSalidaParaCancelacion(id);
        if (infoSalidaCache == null || infoSalidaCache[0].isEmpty()) {
            JOptionPane.showMessageDialog(this, "Salida no encontrada", "Error", JOptionPane.ERROR_MESSAGE);
            alertaPanel.setVisible(false);
            return;
        }

        idSalidaEncontrada = id;
        alertaPanel.setVisible(true);
        rutaLabel.setText("Ruta: " + infoSalidaCache[0]);
        fechaLabel.setText("Fecha y hora: " + infoSalidaCache[1]);
        busLabel.setText("Bus asignado: " + infoSalidaCache[2]);
        String estado = infoSalidaCache[3];
        String activa = infoSalidaCache[4];
        if ("ACTIVA".equals(activa)) {
            estadoLabel.setText("Estado: " + estado + " (ACTIVA)");
            estadoLabel.setForeground(Colores.ESTADO_VERDE_TX);
        } else {
            estadoLabel.setText("Estado: " + estado + " (NO VIGENTE)");
            estadoLabel.setForeground(Colores.ESTADO_ROJO_TX);
        }

        ticketsModel.setRowCount(0);
        int vigentes = 0;
        for (Object[] row : empresa.getTicketsPorSalida(id)) {
            ticketsModel.addRow(row);
            if (Salida.PROGRAMADA.equals(row[3]) || Salida.EN_RUTA.equals(row[3]) || "VIGENTE".equals(row[3])) vigentes++;
        }
        ticketsLabel.setText("Tiquetes VIGENTES: " + vigentes);
    }

    private void confirmarCancelacion() {
        if (idSalidaEncontrada == null) return;
        boolean reembolsar = reembolsarRadio.isSelected();
        List<String> resultados = empresa.cancelarSalida(idSalidaEncontrada, reembolsar);

        StringBuilder encabezado = new StringBuilder();
        encabezado.append("CANCELACION DE SALIDA: ").append(idSalidaEncontrada).append("  (");
        if (infoSalidaCache != null && infoSalidaCache[0] != null) {
            encabezado.append(infoSalidaCache[0]);
        } else {
            encabezado.append(idSalidaEncontrada);
        }
        encabezado.append(") ").append(infoSalidaCache != null ? infoSalidaCache[1] : "")
                  .append("\n\nTiquetes procesados:\n");

        StringBuilder sb = new StringBuilder(encabezado);
        int reprogramados = 0;
        int reembolsadosCount = 0;

        for (String r : resultados) {
            sb.append("- ").append(r).append("\n");
            if (r.contains("REPROGRAMADO")) reprogramados++;
            else if (r.contains("REEMBOLSADO")) reembolsadosCount++;
        }

        sb.append("\nTOTAL REPROGRAMADOS: ").append(reprogramados).append("\n");
        sb.append("TOTAL REEMBOLSADOS: ").append(reembolsadosCount);

        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        JScrollPane scroll = new JScrollPane(textArea);
        scroll.setPreferredSize(new Dimension(600, 350));
        JOptionPane.showMessageDialog(this, scroll, "Reporte de cancelacion", JOptionPane.INFORMATION_MESSAGE);

        limpiarBusqueda();
    }

    private void limpiarBusqueda() {
        idField.setText("");
        alertaPanel.setVisible(false);
        rutaLabel.setText("Ruta: --");
        fechaLabel.setText("Fecha y hora: --");
        busLabel.setText("Bus asignado: --");
        ticketsLabel.setText("Tiquetes VIGENTES: 0");
        ticketsModel.setRowCount(0);
        estadoLabel.setText("Estado: --");
        estadoLabel.setForeground(Color.BLACK);
        idSalidaEncontrada = null;
        infoSalidaCache = null;
    }
}
