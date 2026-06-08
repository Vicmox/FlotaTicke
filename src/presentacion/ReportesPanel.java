package presentacion;

import negocio.EmpresaTransporte;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportesPanel extends JPanel {

    private final EmpresaTransporte empresa;

    private JLabel totalesMontoCaja, totalesVendido, totalesReembolsado, totalesIngresoNeto;
    private JPanel totalesMetricasPanel;

    public ReportesPanel(EmpresaTransporte empresa) {
        this.empresa = empresa;
        setLayout(new BorderLayout());
        setBackground(Colores.FONDO_GENERAL);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));
        tabs.addTab("Ventas por ruta", crearPanelVentasPorRuta());
        JPanel totalesPanel = crearPanelTotalesDia();
        tabs.addTab("Totales del dia", totalesPanel);
        tabs.addTab("Ventas por mes / rango", crearPanelVentasRango());

        tabs.addChangeListener(e -> {
            actualizarTotalesDia();
        });

        add(tabs, BorderLayout.CENTER);
    }

    public void refreshData() {
        actualizarTotalesDia();
    }

    private JPanel crearPanelVentasPorRuta() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Colores.FONDO_GENERAL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setOpaque(false);

        SpinnerDateModel desdeModel = new SpinnerDateModel();
        JSpinner desdeSpinner = new JSpinner(desdeModel);
        desdeSpinner.setEditor(new JSpinner.DateEditor(desdeSpinner, "dd/MM/yyyy"));

        SpinnerDateModel hastaModel = new SpinnerDateModel();
        JSpinner hastaSpinner = new JSpinner(hastaModel);
        hastaSpinner.setEditor(new JSpinner.DateEditor(hastaSpinner, "dd/MM/yyyy"));

        JComboBox<String> rutaCombo = new JComboBox<>();
        rutaCombo.addItem("Todas");
        for (String[] r : empresa.getRutasParaCombo()) {
            rutaCombo.addItem(r[1]);
        }

        filtros.add(new JLabel("Desde:"));
        filtros.add(desdeSpinner);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(hastaSpinner);
        filtros.add(new JLabel("Ruta:"));
        filtros.add(rutaCombo);

        JButton generarBtn = new JButton("Generar");
        generarBtn.setBackground(Colores.AZUL_PRIMARIO);
        generarBtn.setForeground(Color.WHITE);
        generarBtn.setFocusPainted(false);
        filtros.add(generarBtn);

        panel.add(filtros, BorderLayout.NORTH);

        JPanel metricas = new JPanel(new GridLayout(1, 4, 10, 0));
        metricas.setOpaque(false);

        JLabel totalVendido = new JLabel("$0");
        JLabel reembolsado = new JLabel("$0");
        JLabel ingresoNeto = new JLabel("$0");
        JLabel ocupacion = new JLabel("0%");

        metricas.add(crearTarjetaSimple("Total vendido", totalVendido, Colores.ESTADO_VERDE_TX));
        metricas.add(crearTarjetaSimple("Reembolsado", reembolsado, Colores.ESTADO_ROJO_TX));
        metricas.add(crearTarjetaSimple("Ingreso neto", ingresoNeto, Colores.TEXTO_PRIMARIO));
        metricas.add(crearTarjetaSimple("Ocupacion promedio", ocupacion, Colores.ESTADO_AZUL_TX));

        DefaultTableModel model = new DefaultTableModel(new String[]{"Ruta", "Tiquetes", "Reembolsos", "Total neto", "Ocupacion %"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };

        generarBtn.addActionListener(e -> {
            totalVendido.setText(String.format("$%,.0f", empresa.getTotalVendido()));
            reembolsado.setText(String.format("$%,.0f", empresa.getTotalReembolsado()));
            ingresoNeto.setText(String.format("$%,.0f", empresa.getIngresoNeto()));
            int totalCap = empresa.getCapacidadTotalFlota();
            int totalVend = empresa.contarTicketsVigentes();
            ocupacion.setText(totalCap > 0 ? String.format("%.0f%%", totalVend * 100.0 / totalCap) : "0%");

            model.setRowCount(0);
            for (Object[] row : empresa.getReporteVentasPorRuta()) {
                model.addRow(row);
            }
        });
        JTable table = new JTable(model);
        table.setRowHeight(28);

        JButton exportarBtn = new JButton("Exportar");
        exportarBtn.addActionListener(e ->
            JOptionPane.showMessageDialog(panel, "Funcionalidad de exportacion proximamente disponible.")
        );

        panel.add(metricas, BorderLayout.CENTER);

        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.setOpaque(false);
        tableWrapper.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.setOpaque(false);
        tableContainer.add(new JScrollPane(table), BorderLayout.CENTER);
        tableContainer.add(exportarBtn, BorderLayout.SOUTH);
        tableWrapper.add(tableContainer, BorderLayout.CENTER);
        panel.add(tableWrapper, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelTotalesDia() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Colores.FONDO_GENERAL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        totalesMetricasPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        totalesMetricasPanel.setOpaque(false);

        totalesMontoCaja = new JLabel();
        totalesVendido = new JLabel();
        totalesReembolsado = new JLabel();
        totalesIngresoNeto = new JLabel();

        totalesMetricasPanel.add(crearTarjetaGrande("Monto en caja", "", Colores.AZUL_CLARO, Colores.AZUL_PRIMARIO, totalesMontoCaja));
        totalesMetricasPanel.add(crearTarjetaGrande("Total vendido", "", Colores.ESTADO_VERDE, Colores.ESTADO_VERDE_TX, totalesVendido));
        totalesMetricasPanel.add(crearTarjetaGrande("Reembolsado", "", Colores.ESTADO_ROJO, Colores.ESTADO_ROJO_TX, totalesReembolsado));
        totalesMetricasPanel.add(crearTarjetaGrande("Ingreso neto", "", new Color(200,230,200), new Color(30,100,30), totalesIngresoNeto));

        actualizarTotalesDia();

        panel.add(totalesMetricasPanel, BorderLayout.CENTER);

        JLabel fechaReporte = new JLabel("Reporte generado: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        fechaReporte.setFont(new Font("SansSerif", Font.PLAIN, 11));
        fechaReporte.setForeground(Colores.TEXTO_SECUNDARIO);
        fechaReporte.setHorizontalAlignment(SwingConstants.CENTER);
        fechaReporte.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        panel.add(fechaReporte, BorderLayout.SOUTH);

        return panel;
    }

    private void actualizarTotalesDia() {
        if (totalesMontoCaja != null) totalesMontoCaja.setText(String.format("$%,.0f", empresa.getMontoCaja()));
        if (totalesVendido != null) totalesVendido.setText(String.format("$%,.0f", empresa.getTotalVendido()));
        if (totalesReembolsado != null) totalesReembolsado.setText(String.format("$%,.0f", empresa.getTotalReembolsado()));
        if (totalesIngresoNeto != null) totalesIngresoNeto.setText(String.format("$%,.0f", empresa.getIngresoNeto()));
    }

    private JPanel crearPanelVentasRango() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Colores.FONDO_GENERAL);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filtros.setOpaque(false);

        SpinnerDateModel desdeModel = new SpinnerDateModel();
        JSpinner desdeSpinner = new JSpinner(desdeModel);
        desdeSpinner.setEditor(new JSpinner.DateEditor(desdeSpinner, "dd/MM/yyyy"));

        SpinnerDateModel hastaModel = new SpinnerDateModel();
        JSpinner hastaSpinner = new JSpinner(hastaModel);
        hastaSpinner.setEditor(new JSpinner.DateEditor(hastaSpinner, "dd/MM/yyyy"));

        filtros.add(new JLabel("Desde:"));
        filtros.add(desdeSpinner);
        filtros.add(new JLabel("Hasta:"));
        filtros.add(hastaSpinner);

        JButton generarBtn = new JButton("Generar");
        generarBtn.setBackground(Colores.AZUL_PRIMARIO);
        generarBtn.setForeground(Color.WHITE);
        generarBtn.setFocusPainted(false);
        filtros.add(generarBtn);

        panel.add(filtros, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new String[]{"Fecha", "Ruta", "Tiquetes vendidos", "Reembolsos", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(28);

        generarBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Object[] row : empresa.getReporteVentasRango()) {
                model.addRow(row);
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaSimple(String titulo, JLabel valor, Color colorTexto) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Colores.FONDO_TARJETA);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        tituloLabel.setForeground(Colores.TEXTO_SECUNDARIO);
        valor.setFont(new Font("SansSerif", Font.BOLD, 18));
        valor.setForeground(colorTexto);
        card.add(tituloLabel, BorderLayout.NORTH);
        card.add(valor, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearTarjetaGrande(String titulo, String valorInicial, Color bgColor, Color txColor, JLabel outLabel) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colores.BORDE, 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        JLabel tituloLabel = new JLabel(titulo, SwingConstants.CENTER);
        tituloLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tituloLabel.setForeground(txColor);
        outLabel.setText(valorInicial);
        outLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        outLabel.setForeground(txColor);
        outLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(tituloLabel, BorderLayout.NORTH);
        card.add(outLabel, BorderLayout.CENTER);
        return card;
    }
}
