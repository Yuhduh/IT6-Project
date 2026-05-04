/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package it6.project;
/**
 *
 * @author Carl John
 */
public class Reports extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Reports.class.getName());
    private static final int LOW_STOCK_THRESHOLD = 10;

    private ProductDAO productDAO;
    private javax.swing.JLabel lblTotalProductsVal;
    private javax.swing.JLabel lblTotalStockVal;
    private javax.swing.JLabel lblInventoryValueVal;
    private javax.swing.JLabel lblLowStockVal;
    private InventoryChartPanel inventoryChartPanel;

    /**
     * Creates new form Reports
     */
    public Reports() {
        initComponents();
        this.setLocationRelativeTo(null);
        installScrollPane();
        productDAO = new ProductDAO();
        initReport();
        loadReportData();
        wireButtons();
    }

    private void installScrollPane() {
        javax.swing.JScrollPane reportScrollPane = new javax.swing.JScrollPane(jPanel4);
        reportScrollPane.setBorder(null);
        reportScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        reportScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        getContentPane().removeAll();
        getContentPane().setLayout(new java.awt.BorderLayout());
        getContentPane().add(reportScrollPane, java.awt.BorderLayout.CENTER);
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void initReport() {
        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setForeground(java.awt.Color.WHITE);
        jButton1.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        jButton1.setFocusPainted(false);
        jButton1.setBorderPainted(false);
        jButton1.setOpaque(true);
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(e -> printReport());

        lblTotalProductsVal = makeValueLabel("0", new java.awt.Color(30, 30, 30));
        lblTotalStockVal = makeValueLabel("0", new java.awt.Color(30, 30, 30));
        lblInventoryValueVal = makeValueLabel("₱0.00", new java.awt.Color(0, 128, 0));
        lblLowStockVal = makeValueLabel("0", new java.awt.Color(200, 0, 0));

        jPanel8.setLayout(null);
        jLabel7.setBounds(21, 21, 250, 28);
        jLabel14.setBounds(21, 55, 900, 16);
        jLabel8.setBounds(21, 77, 200, 22);
        jLabel9.setBounds(21, 107, 200, 22);
        jLabel10.setBounds(21, 137, 200, 22);
        jLabel11.setBounds(21, 167, 200, 22);

        lblTotalProductsVal.setBounds(600, 77, 300, 22);
        lblTotalStockVal.setBounds(600, 107, 300, 22);
        lblInventoryValueVal.setBounds(600, 137, 300, 22);
        lblLowStockVal.setBounds(600, 167, 300, 22);

        jPanel8.add(jLabel7);
        jPanel8.add(jLabel14);
        jPanel8.add(jLabel8);
        jPanel8.add(jLabel9);
        jPanel8.add(jLabel10);
        jPanel8.add(jLabel11);
        jPanel8.add(lblTotalProductsVal);
        jPanel8.add(lblTotalStockVal);
        jPanel8.add(lblInventoryValueVal);
        jPanel8.add(lblLowStockVal);
        javax.swing.JLabel chartTitle = new javax.swing.JLabel("Inventory by Category");
        chartTitle.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 18));
        chartTitle.setForeground(new java.awt.Color(31, 41, 55));
        chartTitle.setBounds(21, 210, 260, 24);
        jPanel8.add(chartTitle);

        inventoryChartPanel = new InventoryChartPanel();
        inventoryChartPanel.setBounds(21, 240, 910, 200);
        jPanel8.add(inventoryChartPanel);
        jPanel8.setPreferredSize(new java.awt.Dimension(952, 460));
    }

    private javax.swing.JLabel makeValueLabel(String text, java.awt.Color fg) {
        javax.swing.JLabel lbl = new javax.swing.JLabel(text, javax.swing.SwingConstants.RIGHT);
        lbl.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 15));
        lbl.setForeground(fg);
        return lbl;
    }

    private void wireButtons() {
        jButton2.addActionListener(e -> { new ProductList().setVisible(true); dispose(); });
        jButton3.addActionListener(e -> { new StockIn().setVisible(true); dispose(); });
        jButton4.addActionListener(e -> { new Dashboard().setVisible(true); dispose(); });
        jButton5.addActionListener(e -> { new StockMonitoring().setVisible(true); dispose(); });
    }

    private void loadReportData() {
        java.util.List<Product> allProducts = productDAO.getAllProducts();
        java.util.List<Product> lowStock = productDAO.getLowStockItems(LOW_STOCK_THRESHOLD);
        int totalProducts = productDAO.getTotalProducts();
        int totalItemsInStock = productDAO.getTotalItemsInStock();
        double totalInventoryValue = calculateInventoryValue(allProducts);
        int lowStockCount = lowStock.size();

        lblTotalProductsVal.setText(String.valueOf(totalProducts));
        lblTotalStockVal.setText(String.valueOf(totalItemsInStock));
        lblInventoryValueVal.setText(String.format("₱%.2f", totalInventoryValue));
        lblLowStockVal.setText(String.valueOf(lowStockCount));
        lblLowStockVal.setForeground(lowStockCount > 0 ? new java.awt.Color(200, 0, 0) : new java.awt.Color(0, 128, 0));

        javax.swing.table.DefaultTableModel allModel = (javax.swing.table.DefaultTableModel) jTable3.getModel();
        allModel.setRowCount(0);
        for (Product p : allProducts) {
            allModel.addRow(new Object[]{
                p.getName(),
                p.getCategory(),
                p.getQuantity(),
                String.format("₱%.2f", p.getPrice())
            });
        }
        jTable3.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                Object qty = table.getModel().getValueAt(row, 2);
                if (col == 2 && qty instanceof Integer && (Integer) qty < LOW_STOCK_THRESHOLD) {
                    setForeground(new java.awt.Color(200, 0, 0));
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                } else {
                    setForeground(isSelected ? table.getSelectionForeground() : java.awt.Color.BLACK);
                    setFont(getFont().deriveFont(java.awt.Font.PLAIN));
                }
                return this;
            }
        });

        java.util.Map<String, Integer> categoryTotals = new java.util.LinkedHashMap<>();
        for (Product product : allProducts) {
            String category = product.getCategory();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            }
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0) + product.getQuantity());
        }
        inventoryChartPanel.setData(categoryTotals);

        javax.swing.table.DefaultTableModel lowModel = (javax.swing.table.DefaultTableModel) jTable4.getModel();
        lowModel.setRowCount(0);
        for (Product p : lowStock) {
            lowModel.addRow(new Object[]{
                p.getName(),
                p.getCategory(),
                p.getQuantity(),
                "Only " + p.getQuantity() + " left"
            });
        }
        jTable4.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(
                    javax.swing.JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (col == 3 || col == 2) {
                    setForeground(new java.awt.Color(200, 0, 0));
                    setFont(getFont().deriveFont(java.awt.Font.BOLD));
                } else {
                    setForeground(isSelected ? table.getSelectionForeground() : java.awt.Color.BLACK);
                    setFont(getFont().deriveFont(java.awt.Font.PLAIN));
                }
                return this;
            }
        });

        jPanel8.revalidate();
        jPanel8.repaint();
    }

    private static final class InventoryChartPanel extends javax.swing.JPanel {

        private java.util.List<java.util.Map.Entry<String, Integer>> entries = java.util.Collections.emptyList();

        InventoryChartPanel() {
            setBackground(java.awt.Color.WHITE);
            setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(209, 213, 219)));
        }

        void setData(java.util.Map<String, Integer> categoryTotals) {
            java.util.List<java.util.Map.Entry<String, Integer>> nextEntries = new java.util.ArrayList<>(categoryTotals.entrySet());
            nextEntries.sort((left, right) -> java.lang.Integer.compare(right.getValue(), left.getValue()));
            entries = nextEntries;
            repaint();
        }

        @Override
        protected void paintComponent(java.awt.Graphics graphics) {
            super.paintComponent(graphics);

            java.awt.Graphics2D g2 = (java.awt.Graphics2D) graphics.create();
            try {
                g2.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                        java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();
                int leftPad = 150;
                int rightPad = 28;
                int topPad = 26;
                int bottomPad = 24;
                int chartWidth = Math.max(0, width - leftPad - rightPad);
                int chartHeight = Math.max(0, height - topPad - bottomPad);

                g2.setColor(new java.awt.Color(248, 250, 252));
                g2.fillRoundRect(0, 0, width, height, 18, 18);

                g2.setColor(new java.awt.Color(17, 24, 39));
                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
                g2.drawString("Stock levels grouped by product category", 18, 18);

                if (entries.isEmpty()) {
                    g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
                    g2.setColor(new java.awt.Color(107, 114, 128));
                    g2.drawString("No inventory data available.", 18, height / 2);
                    return;
                }

                int maxValue = 0;
                for (java.util.Map.Entry<String, Integer> entry : entries) {
                    maxValue = Math.max(maxValue, entry.getValue());
                }
                maxValue = Math.max(maxValue, 1);

                int rowHeight = Math.max(28, chartHeight / entries.size());
                int barHeight = Math.min(22, Math.max(14, rowHeight - 10));
                int y = topPad;

                java.awt.Color[] palette = new java.awt.Color[] {
                    new java.awt.Color(37, 99, 235),
                    new java.awt.Color(14, 165, 233),
                    new java.awt.Color(16, 185, 129),
                    new java.awt.Color(245, 158, 11),
                    new java.awt.Color(239, 68, 68),
                    new java.awt.Color(168, 85, 247)
                };

                g2.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 12));
                for (int i = 0; i < entries.size(); i++) {
                    java.util.Map.Entry<String, Integer> entry = entries.get(i);
                    int barWidth = (int) Math.round((entry.getValue() / (double) maxValue) * chartWidth);
                    int barY = y + ((rowHeight - barHeight) / 2);

                    g2.setColor(new java.awt.Color(55, 65, 81));
                    g2.drawString(entry.getKey(), 18, y + (rowHeight / 2) + 5);

                    g2.setColor(new java.awt.Color(226, 232, 240));
                    g2.fillRoundRect(leftPad, barY, chartWidth, barHeight, barHeight, barHeight);

                    g2.setColor(palette[i % palette.length]);
                    g2.fillRoundRect(leftPad, barY, Math.max(6, barWidth), barHeight, barHeight, barHeight);

                    g2.setColor(new java.awt.Color(17, 24, 39));
                    g2.drawString(String.valueOf(entry.getValue()), leftPad + Math.max(8, barWidth + 8), y + (rowHeight / 2) + 5);

                    y += rowHeight;
                    if (y > height - bottomPad) {
                        break;
                    }
                }
            } finally {
                g2.dispose();
            }
        }
    }

    private double calculateInventoryValue(java.util.List<Product> products) {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice() * p.getQuantity();
        }
        return total;
    }

    private java.util.Map<String, Integer> buildCategoryTotals(java.util.List<Product> products) {
        java.util.Map<String, Integer> categoryTotals = new java.util.LinkedHashMap<>();
        for (Product product : products) {
            String category = product.getCategory();
            if (category == null || category.trim().isEmpty()) {
                category = "Uncategorized";
            }
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0) + product.getQuantity());
        }
        return categoryTotals;
    }

    private String buildCategoryChartHtml(java.util.Map<String, Integer> categoryTotals) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='section'>");
        html.append("<h3>Inventory by Category</h3>");

        if (categoryTotals.isEmpty()) {
            html.append("<div class='restock'>No category data available.</div>");
            html.append("</div>");
            return html.toString();
        }

        int maxValue = 1;
        for (int value : categoryTotals.values()) {
            maxValue = Math.max(maxValue, value);
        }

        html.append("<div class='chart'>");
        for (java.util.Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
            int widthPercent = (int) Math.round((entry.getValue() * 100.0) / maxValue);
            html.append("<div class='chart-row'>")
                .append("<div class='chart-label'>").append(escapeHtml(entry.getKey())).append("</div>")
                .append("<div class='chart-track'><div class='chart-fill' style='width:")
                .append(widthPercent)
                .append("%;'></div></div>")
                .append("<div class='chart-value'>")
                .append(entry.getValue())
                .append("</div>")
                .append("</div>");
        }
        html.append("</div>");
        html.append("</div>");
        return html.toString();
    }

    private void printReport() {
        java.util.List<Product> allProducts = productDAO.getAllProducts();
        java.util.List<Product> lowStock = productDAO.getLowStockItems(LOW_STOCK_THRESHOLD);
        double totalValue = calculateInventoryValue(allProducts);
        String date = new java.text.SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(new java.util.Date());
        String html = buildReportHtml(allProducts, lowStock, totalValue, date);

        javax.swing.JEditorPane editorPane = new javax.swing.JEditorPane("text/html", html);
        editorPane.setSize(750, 1200);
        try {
            boolean printed = editorPane.print(
                new java.text.MessageFormat("Sari-Sari Store - Inventory Report"),
                new java.text.MessageFormat("Page {0}"),
                true,
                null,
                null,
                true
            );
            if (printed) {
                javax.swing.JOptionPane.showMessageDialog(this,
                    "Report printed successfully!",
                    "Print Success", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (java.awt.print.PrinterException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Printing failed: " + ex.getMessage(),
                "Print Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private String buildReportHtml(java.util.List<Product> allProducts, java.util.List<Product> lowStock,
            double totalValue, String generatedAt) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><style>");
        html.append("body{font-family:Segoe UI,Arial,sans-serif;padding:24px;color:#1f2937;}");
        html.append(".header{border-bottom:3px solid #2563eb;padding-bottom:14px;margin-bottom:18px;}");
        html.append(".brand{font-size:26px;font-weight:700;color:#111827;}");
        html.append(".title{font-size:20px;font-weight:600;color:#2563eb;margin-top:4px;}");
        html.append(".meta{color:#6b7280;font-size:12px;margin-top:6px;}");
        html.append(".section{margin-top:18px;}");
        html.append(".section h3{margin:0 0 10px 0;font-size:16px;color:#111827;}");
        html.append(".summary{width:100%;border-collapse:collapse;background:#f8fafc;border:1px solid #dbe4ef;}");
        html.append(".summary td{padding:10px 12px;border-bottom:1px solid #e5ebf2;}");
        html.append(".summary td:last-child{text-align:right;font-weight:700;}");
        html.append(".summary .value-green{color:#15803d;}");
        html.append(".summary .value-red{color:#b91c1c;}");
        html.append(".table{width:100%;border-collapse:collapse;}");
        html.append(".table th{background:#e8f0ff;color:#1e3a8a;text-align:left;padding:10px;border:1px solid #d5e1f2;}");
        html.append(".table td{padding:10px;border:1px solid #e5ebf2;}");
        html.append(".table tr:nth-child(even){background:#fafcff;}");
        html.append(".low{color:#b91c1c;font-weight:700;}");
        html.append(".restock{background:#fff1f2;border:1px solid #fecdd3;padding:12px;border-radius:8px;}");
        html.append(".chart{margin-top:8px;}");
        html.append(".chart-row{display:flex;align-items:center;gap:10px;margin:10px 0;}");
        html.append(".chart-label{width:170px;font-size:12px;color:#374151;font-weight:600;word-break:break-word;}");
        html.append(".chart-track{flex:1;background:#e5ebf2;border-radius:999px;height:16px;overflow:hidden;}");
        html.append(".chart-fill{height:16px;border-radius:999px;background:linear-gradient(90deg,#2563eb,#0ea5e9,#10b981);}");
        html.append(".chart-value{width:44px;text-align:right;font-size:12px;font-weight:700;color:#111827;}");
        html.append("</style></head><body>");

        html.append("<div class='header'>");
        html.append("<div class='brand'>Sari-Sari Store Inventory System</div>");
        html.append("<div class='title'>Inventory Report</div>");
        html.append("<div class='meta'>Generated: ").append(generatedAt).append("</div>");
        html.append("</div>");

        html.append("<div class='section'>");
        html.append("<h3>Inventory Summary</h3>");
        html.append("<table class='summary'>");
        html.append("<tr><td>Total Products</td><td>").append(allProducts.size()).append("</td></tr>");
        html.append("<tr><td>Total Items in Stock</td><td>").append(productDAO.getTotalItemsInStock()).append("</td></tr>");
        html.append("<tr><td>Total Inventory Value</td><td class='value-green'>&#8369;").append(String.format("%.2f", totalValue)).append("</td></tr>");
        html.append("<tr><td>Low Stock Items</td><td class='value-red'>").append(lowStock.size()).append("</td></tr>");
        html.append("</table>");
        html.append("</div>");

        html.append(buildCategoryChartHtml(buildCategoryTotals(allProducts)));

        html.append("<div class='section'>");
        html.append("<h3>All Products</h3>");
        html.append("<table class='table'>");
        html.append("<tr><th>Product Name</th><th>Category</th><th>Quantity</th><th>Price (&#8369;)</th></tr>");
        for (Product product : allProducts) {
            boolean low = product.getQuantity() < LOW_STOCK_THRESHOLD;
            html.append("<tr>")
                .append("<td>").append(escapeHtml(product.getName())).append("</td>")
                .append("<td>").append(escapeHtml(product.getCategory())).append("</td>")
                .append("<td class='").append(low ? "low" : "").append("'>").append(product.getQuantity()).append("</td>")
                .append("<td>&#8369;").append(String.format("%.2f", product.getPrice())).append("</td>")
                .append("</tr>");
        }
        html.append("</table>");
        html.append("</div>");

        html.append("<div class='section'>");
        html.append("<h3 style='color:#b91c1c;'>Items Need Restocking</h3>");
        if (lowStock.isEmpty()) {
            html.append("<div class='restock'>No items currently require restocking.</div>");
        } else {
            html.append("<div class='restock'>");
            html.append("<table class='table'>");
            html.append("<tr><th>Product Name</th><th>Category</th><th>Quantity Left</th></tr>");
            for (Product product : lowStock) {
                html.append("<tr>")
                    .append("<td><b>").append(escapeHtml(product.getName())).append("</b></td>")
                    .append("<td>").append(escapeHtml(product.getCategory())).append("</td>")
                    .append("<td class='low'>Only ").append(product.getQuantity()).append(" left</td>")
                    .append("</tr>");
            }
            html.append("</table>");
            html.append("</div>");
        }
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }

    private String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel46 = new javax.swing.JPanel();
        jButton116 = new javax.swing.JButton();
        jButton117 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton118 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jPanel21 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        jTable15 = new javax.swing.JTable();
        jPanel22 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jScrollPane16 = new javax.swing.JScrollPane();
        jTable16 = new javax.swing.JTable();
        jPanel23 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jScrollPane17 = new javax.swing.JScrollPane();
        jTable17 = new javax.swing.JTable();
        jPanel24 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane18 = new javax.swing.JScrollPane();
        jTable18 = new javax.swing.JTable();
        jPanel25 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        jScrollPane19 = new javax.swing.JScrollPane();
        jTable19 = new javax.swing.JTable();
        jPanel26 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        jScrollPane20 = new javax.swing.JScrollPane();
        jTable20 = new javax.swing.JTable();
        jPanel27 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        jScrollPane21 = new javax.swing.JScrollPane();
        jTable21 = new javax.swing.JTable();
        jPanel28 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        jScrollPane22 = new javax.swing.JScrollPane();
        jTable22 = new javax.swing.JTable();
        jPanel29 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane23 = new javax.swing.JScrollPane();
        jTable23 = new javax.swing.JTable();
        jPanel30 = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane24 = new javax.swing.JScrollPane();
        jTable24 = new javax.swing.JTable();
        jPanel31 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jScrollPane25 = new javax.swing.JScrollPane();
        jTable25 = new javax.swing.JTable();
        jPanel32 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        jScrollPane26 = new javax.swing.JScrollPane();
        jTable26 = new javax.swing.JTable();
        jPanel33 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jScrollPane27 = new javax.swing.JScrollPane();
        jTable27 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jPanel48 = new javax.swing.JPanel();
        jButton122 = new javax.swing.JButton();
        jButton123 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        jButton124 = new javax.swing.JButton();
        jButton15 = new javax.swing.JButton();
        jPanel8 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jButton6 = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(245, 250, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 35)); // NOI18N
        jLabel1.setText("Sari-Sari Store Inventory System");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(14, 14, 14))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton2.setText("Product List");

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton3.setText("Stock In");

        jButton4.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton4.setText("Dashboard");
        jButton4.addActionListener(this::jButton4ActionPerformed);

        jButton5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton5.setText("Stock Monitoring");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(15, 15, 15))
        );

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel2.setText("Stock Monitoring");

        jPanel5.setBackground(new java.awt.Color(255, 204, 204));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(153, 0, 51));
        jLabel3.setText("Low Stock Items (Below 10)");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane1)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(204, 255, 204));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 153, 0));
        jLabel4.setText("Well-Stock Items (10 or more)");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 930, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 925, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(46, Short.MAX_VALUE))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        jPanel46.setBackground(new java.awt.Color(255, 255, 255));

        jButton116.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton116.setText("Product List");
        jButton116.addActionListener(this::jButton116jButton2ActionPerformed);

        jButton117.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton117.setText("Stock In");
        jButton117.addActionListener(this::jButton117jButton3ActionPerformed);

        jButton10.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton10.setText("Dashboard");
        jButton10.addActionListener(this::jButton10ActionPerformed);

        jButton118.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton118.setText("Stock Monitoring");
        jButton118.addActionListener(this::jButton118jButton5ActionPerformed);

        jButton11.setText("Reports");
        jButton11.addActionListener(this::jButton11ActionPerformed);

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton116, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton117, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton118, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton116, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton117, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton118, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        jPanel21.setBackground(new java.awt.Color(255, 204, 204));

        jLabel24.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(153, 0, 51));
        jLabel24.setText("Low Stock Items (Below 10)");

        jTable15.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane15.setViewportView(jTable15);

        jPanel22.setBackground(new java.awt.Color(255, 204, 204));

        jLabel25.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(153, 0, 51));
        jLabel25.setText("Low Stock Items (Below 10)");

        jTable16.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane16.setViewportView(jTable16);

        jPanel23.setBackground(new java.awt.Color(255, 204, 204));

        jLabel26.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(153, 0, 51));
        jLabel26.setText("Low Stock Items (Below 10)");

        jTable17.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane17.setViewportView(jTable17);

        jPanel24.setBackground(new java.awt.Color(255, 204, 204));

        jLabel27.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(153, 0, 51));
        jLabel27.setText("Low Stock Items (Below 10)");

        jTable18.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane18.setViewportView(jTable18);

        jPanel25.setBackground(new java.awt.Color(255, 204, 204));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(153, 0, 51));
        jLabel28.setText("Low Stock Items (Below 10)");

        jTable19.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane19.setViewportView(jTable19);

        jPanel26.setBackground(new java.awt.Color(255, 204, 204));

        jLabel29.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(153, 0, 51));
        jLabel29.setText("Low Stock Items (Below 10)");

        jTable20.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane20.setViewportView(jTable20);

        jPanel27.setBackground(new java.awt.Color(255, 204, 204));

        jLabel30.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(153, 0, 51));
        jLabel30.setText("Low Stock Items (Below 10)");

        jTable21.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane21.setViewportView(jTable21);

        jPanel28.setBackground(new java.awt.Color(255, 204, 204));

        jLabel31.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(153, 0, 51));
        jLabel31.setText("Low Stock Items (Below 10)");

        jTable22.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane22.setViewportView(jTable22);

        jPanel29.setBackground(new java.awt.Color(255, 204, 204));

        jLabel32.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(153, 0, 51));
        jLabel32.setText("Low Stock Items (Below 10)");

        jTable23.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane23.setViewportView(jTable23);

        jPanel30.setBackground(new java.awt.Color(255, 204, 204));

        jLabel33.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(153, 0, 51));
        jLabel33.setText("Low Stock Items (Below 10)");

        jTable24.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane24.setViewportView(jTable24);

        jPanel31.setBackground(new java.awt.Color(255, 204, 204));

        jLabel34.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(153, 0, 51));
        jLabel34.setText("Low Stock Items (Below 10)");

        jTable25.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane25.setViewportView(jTable25);

        jPanel32.setBackground(new java.awt.Color(255, 204, 204));

        jLabel35.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(153, 0, 51));
        jLabel35.setText("Low Stock Items (Below 10)");

        jTable26.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane26.setViewportView(jTable26);

        jPanel33.setBackground(new java.awt.Color(255, 204, 204));

        jLabel36.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(153, 0, 51));
        jLabel36.setText("Low Stock Items (Below 10)");

        jTable27.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane27.setViewportView(jTable27);

        javax.swing.GroupLayout jPanel33Layout = new javax.swing.GroupLayout(jPanel33);
        jPanel33.setLayout(jPanel33Layout);
        jPanel33Layout.setHorizontalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel36, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane27)
        );
        jPanel33Layout.setVerticalGroup(
            jPanel33Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel33Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel36)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane27, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel32Layout = new javax.swing.GroupLayout(jPanel32);
        jPanel32.setLayout(jPanel32Layout);
        jPanel32Layout.setHorizontalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane26)
            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel32Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel33, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel32Layout.setVerticalGroup(
            jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel32Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel35)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane26, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel32Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel32Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel33, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel31Layout = new javax.swing.GroupLayout(jPanel31);
        jPanel31.setLayout(jPanel31Layout);
        jPanel31Layout.setHorizontalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane25)
            .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel31Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel31Layout.setVerticalGroup(
            jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel31Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel34)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane25, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel31Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel31Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel32, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel30Layout = new javax.swing.GroupLayout(jPanel30);
        jPanel30.setLayout(jPanel30Layout);
        jPanel30Layout.setHorizontalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane24)
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel30Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel30Layout.setVerticalGroup(
            jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel30Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel33)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane24, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel30Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel30Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel31, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel29Layout = new javax.swing.GroupLayout(jPanel29);
        jPanel29.setLayout(jPanel29Layout);
        jPanel29Layout.setHorizontalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane23)
            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel29Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel29Layout.setVerticalGroup(
            jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel29Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane23, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel29Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel29Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel30, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel28Layout = new javax.swing.GroupLayout(jPanel28);
        jPanel28.setLayout(jPanel28Layout);
        jPanel28Layout.setHorizontalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel31, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane22)
            .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel28Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel28Layout.setVerticalGroup(
            jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel28Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane22, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel28Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel28Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel29, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel27Layout = new javax.swing.GroupLayout(jPanel27);
        jPanel27.setLayout(jPanel27Layout);
        jPanel27Layout.setHorizontalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel30, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane21)
            .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel27Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel28, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel27Layout.setVerticalGroup(
            jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel27Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel30)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane21, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel27Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel27Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel28, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel26Layout = new javax.swing.GroupLayout(jPanel26);
        jPanel26.setLayout(jPanel26Layout);
        jPanel26Layout.setHorizontalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane20)
            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel26Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel26Layout.setVerticalGroup(
            jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel26Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel29)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane20, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel26Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel26Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel27, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel28, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane19)
            .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel25Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel28)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane19, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel25Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel26, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel24Layout = new javax.swing.GroupLayout(jPanel24);
        jPanel24.setLayout(jPanel24Layout);
        jPanel24Layout.setHorizontalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane18)
            .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel24Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel24Layout.setVerticalGroup(
            jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel24Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel27)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane18, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel24Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel24Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel23Layout = new javax.swing.GroupLayout(jPanel23);
        jPanel23.setLayout(jPanel23Layout);
        jPanel23Layout.setHorizontalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane17)
            .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel23Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel24, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel23Layout.setVerticalGroup(
            jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel23Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane17, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel23Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel23Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel24, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel22Layout = new javax.swing.GroupLayout(jPanel22);
        jPanel22.setLayout(jPanel22Layout);
        jPanel22Layout.setHorizontalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane16)
            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel22Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel22Layout.setVerticalGroup(
            jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel22Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel25)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane16, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel22Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel22Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel23, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel21Layout = new javax.swing.GroupLayout(jPanel21);
        jPanel21.setLayout(jPanel21Layout);
        jPanel21Layout.setHorizontalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane15)
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel21Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel21Layout.setVerticalGroup(
            jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel21Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
            .addGroup(jPanel21Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel21Layout.createSequentialGroup()
                    .addGap(0, 0, Short.MAX_VALUE)
                    .addComponent(jPanel22, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 0, Short.MAX_VALUE)))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel4.setBackground(new java.awt.Color(245, 250, 255));

        jPanel7.setBackground(new java.awt.Color(255, 255, 255));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 35)); // NOI18N
        jLabel5.setText("Sari-Sari Store Inventory System");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(jLabel5)
                .addContainerGap(476, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel5)
                .addGap(14, 14, 14))
        );

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel6.setText("Inventory Report");

        jPanel48.setBackground(new java.awt.Color(255, 255, 255));

        jButton122.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton122.setText("Product List");
        jButton122.addActionListener(this::jButton122jButton2ActionPerformed);

        jButton123.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton123.setText("Stock In");
        jButton123.addActionListener(this::jButton123jButton3ActionPerformed);

        jButton14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton14.setText("Dashboard");
        jButton14.addActionListener(this::jButton14ActionPerformed);

        jButton124.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton124.setText("Stock Monitoring");
        jButton124.addActionListener(this::jButton124jButton5ActionPerformed);

        jButton15.setText("Reports");
        jButton15.addActionListener(this::jButton15ActionPerformed);

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel48Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton122, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34)
                .addComponent(jButton123, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(32, 32, 32)
                .addComponent(jButton124, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(38, 38, 38)
                .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31))
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel48Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButton122, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton123, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton124, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );

        jPanel8.setBackground(new java.awt.Color(204, 255, 255));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel7.setText("Inventory Summary");

        jLabel8.setBackground(new java.awt.Color(102, 102, 102));
        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setText("Total Products: ");

        jLabel9.setBackground(new java.awt.Color(102, 102, 102));
        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(51, 51, 51));
        jLabel9.setText("Total Item in Stock:");

        jLabel10.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setText("Total Inventory Value:");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(51, 51, 51));
        jLabel11.setText("Low Stock Items:");

        jLabel14.setText("______________________________________________________________________________________________________________________________________________________________________________________________");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel14)
                .addGap(8, 8, 8)
                .addComponent(jLabel8)
                .addGap(30, 30, 30)
                .addComponent(jLabel9)
                .addGap(29, 29, 29)
                .addComponent(jLabel10)
                .addGap(29, 29, 29)
                .addComponent(jLabel11)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jButton1.setBackground(new java.awt.Color(51, 153, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Print Report");
        jButton1.addActionListener(this::jButton1ActionPerformed);

        jPanel9.setBackground(new java.awt.Color(204, 255, 204));

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Price"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane3.setViewportView(jTable3);
        if (jTable3.getColumnModel().getColumnCount() > 0) {
            jTable3.getColumnModel().getColumn(0).setResizable(false);
            jTable3.getColumnModel().getColumn(1).setResizable(false);
            jTable3.getColumnModel().getColumn(2).setResizable(false);
            jTable3.getColumnModel().getColumn(3).setResizable(false);
        }

        jLabel12.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(0, 102, 51));
        jLabel12.setText("All Products");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 940, Short.MAX_VALUE)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 189, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 286, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jPanel10.setBackground(new java.awt.Color(255, 204, 204));

        jLabel13.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(153, 0, 51));
        jLabel13.setText("Item Needs Restocking");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Product Name", "Category", "Quantity", "Status"
            }
        ));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 952, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 143, Short.MAX_VALUE))
        );

        jButton6.setBackground(new java.awt.Color(153, 255, 153));
        jButton6.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton6.setText("Download PDF");
        jButton6.addActionListener(this::jButton6ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel48, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel6)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 43, Short.MAX_VALUE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 1050, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 12, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        new Dashboard().setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton116jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton116jButton2ActionPerformed
        ProductList productList = new ProductList();
        productList.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton116jButton2ActionPerformed

    private void jButton117jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton117jButton3ActionPerformed
        StockIn stockIn = new StockIn();
        stockIn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton117jButton3ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton118jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton118jButton5ActionPerformed
        StockMonitoring stockMonitor = new StockMonitoring();
        stockMonitor.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton118jButton5ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        Reports report = new Reports();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton122jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton122jButton2ActionPerformed
        ProductList pl = new ProductList();
        pl.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton122jButton2ActionPerformed

    private void jButton123jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton123jButton3ActionPerformed
        StockIn si = new StockIn();
        si.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton123jButton3ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        Dashboard db = new Dashboard();
        db.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton14ActionPerformed

    private void jButton124jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton124jButton5ActionPerformed
        StockMonitoring sm = new StockMonitoring();
        sm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton124jButton5ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        Reports report = new Reports();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton15ActionPerformed

    private void downloadPdfReport() {
        javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
        fileChooser.setDialogTitle("Save Inventory Report as PDF");
        fileChooser.setSelectedFile(new java.io.File("Inventory_Report_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".pdf"));
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

        int result = fileChooser.showSaveDialog(this);
        if (result != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File targetFile = fileChooser.getSelectedFile();
        if (!targetFile.getName().toLowerCase(java.util.Locale.ROOT).endsWith(".pdf")) {
            targetFile = new java.io.File(targetFile.getParentFile(), targetFile.getName() + ".pdf");
        }

        try {
            writeSimplePdf(targetFile, buildPdfReportLines());
            javax.swing.JOptionPane.showMessageDialog(this,
                "PDF saved successfully at:\n" + targetFile.getAbsolutePath(),
                "Download Complete", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        } catch (java.io.IOException ex) {
            javax.swing.JOptionPane.showMessageDialog(this,
                "Failed to save PDF: " + ex.getMessage(),
                "Download Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private java.util.List<String> buildPdfReportLines() {
        java.util.List<Product> allProducts = productDAO.getAllProducts();
        java.util.List<Product> lowStock = productDAO.getLowStockItems(LOW_STOCK_THRESHOLD);
        java.util.Map<String, Integer> categoryTotals = buildCategoryTotals(allProducts);

        java.util.List<String> lines = new java.util.ArrayList<>();
        String generatedAt = new java.text.SimpleDateFormat("MMMM dd, yyyy hh:mm a").format(new java.util.Date());

        lines.add("SARI-SARI STORE INVENTORY SYSTEM");
        lines.add("Inventory Report");
        lines.add("Generated: " + generatedAt);
        lines.add(repeat('=', 74));
        lines.add("");
        lines.add(buildSectionHeading("INVENTORY SUMMARY"));
        lines.add(buildTableBorder(30, 22));
        lines.add(buildTableRow(new int[]{30, 22}, new boolean[]{false, true}, "Metric", "Value"));
        lines.add(buildTableBorder(30, 22));
        lines.add(buildTableRow(new int[]{30, 22}, new boolean[]{false, true}, "Total Products", String.valueOf(productDAO.getTotalProducts())));
        lines.add(buildTableRow(new int[]{30, 22}, new boolean[]{false, true}, "Total Items in Stock", String.valueOf(productDAO.getTotalItemsInStock())));
        lines.add(buildTableRow(new int[]{30, 22}, new boolean[]{false, true}, "Total Inventory Value", String.format("PHP %.2f", calculateInventoryValue(allProducts))));
        lines.add(buildTableRow(new int[]{30, 22}, new boolean[]{false, true}, "Low Stock Items", String.valueOf(lowStock.size())));
        lines.add(buildTableBorder(30, 22));
        lines.add("");
        lines.add(buildSectionHeading("INVENTORY BY CATEGORY"));
        lines.add(buildTableBorder(34, 14));
        lines.add(buildTableRow(new int[]{34, 14}, new boolean[]{false, true}, "Category", "Total Qty"));
        lines.add(buildTableBorder(34, 14));
        if (categoryTotals.isEmpty()) {
            lines.add(buildTableRow(new int[]{34, 14}, new boolean[]{false, true}, "No category data available.", ""));
        } else {
            for (java.util.Map.Entry<String, Integer> entry : categoryTotals.entrySet()) {
                lines.add(buildTableRow(new int[]{34, 14}, new boolean[]{false, true}, truncate(entry.getKey(), 34), String.valueOf(entry.getValue())));
            }
        }
        lines.add(buildTableBorder(34, 14));
        lines.add("");
        lines.add(buildSectionHeading("ALL PRODUCTS"));
        lines.add(buildTableBorder(24, 22, 6, 14));
        lines.add(buildTableRow(new int[]{24, 22, 6, 14}, new boolean[]{false, false, true, true}, "Product Name", "Category", "Qty", "Price"));
        lines.add(buildTableBorder(24, 22, 6, 14));
        for (Product product : allProducts) {
            lines.add(buildTableRow(new int[]{24, 22, 6, 14}, new boolean[]{false, false, true, true},
                truncate(product.getName(), 24),
                truncate(product.getCategory(), 22),
                String.valueOf(product.getQuantity()),
                String.format("PHP %.2f", product.getPrice())));
        }
        lines.add(buildTableBorder(24, 22, 6, 14));

        lines.add("");
        lines.add(buildSectionHeading("ITEMS NEED RESTOCKING"));
        if (lowStock.isEmpty()) {
            lines.add("  No items currently require restocking.");
        } else {
            lines.add(buildTableBorder(24, 22, 18));
            lines.add(buildTableRow(new int[]{24, 22, 18}, new boolean[]{false, false, false}, "Product Name", "Category", "Status"));
            lines.add(buildTableBorder(24, 22, 18));
            for (Product product : lowStock) {
                lines.add(buildTableRow(new int[]{24, 22, 18}, new boolean[]{false, false, false},
                    truncate(product.getName(), 24),
                    truncate(product.getCategory(), 22),
                    "Only " + product.getQuantity() + " left"));
            }
            lines.add(buildTableBorder(24, 22, 18));
        }

        return lines;
    }

    private String buildTableBorder(int... widths) {
        StringBuilder border = new StringBuilder();
        border.append('+');
        for (int width : widths) {
            border.append(repeat('-', width + 2)).append('+');
        }
        return border.toString();
    }

    private String buildSectionHeading(String title) {
        return title;
    }

    private String buildTableRow(int[] widths, String... values) {
        boolean[] alignments = new boolean[widths.length];
        if (widths.length > 0) {
            alignments[widths.length - 1] = true;
        }
        return buildTableRow(widths, alignments, values);
    }

    private String buildTableRow(int[] widths, boolean[] rightAlignments, String... values) {
        StringBuilder row = new StringBuilder();
        row.append('|');
        for (int index = 0; index < widths.length; index++) {
            String value = index < values.length ? values[index] : "";
            boolean rightAlign = index < rightAlignments.length && rightAlignments[index];
            row.append(' ').append(padCell(value, widths[index], rightAlign)).append(' ').append('|');
        }
        return row.toString();
    }

    private String padCell(String text, int width, boolean rightAlign) {
        String value = text == null ? "" : text;
        if (value.length() > width) {
            value = truncate(value, width);
        }
        if (rightAlign) {
            return String.format(java.util.Locale.ROOT, "%" + width + "s", value);
        }
        return String.format(java.util.Locale.ROOT, "%-" + width + "s", value);
    }

    private void writeSimplePdf(java.io.File file, java.util.List<String> lines) throws java.io.IOException {
        final int maxLinesPerPage = 42;
        final float pageWidth = 595f;
        final float pageHeight = 842f;
        final float leftMargin = 40f;
        final float topMargin = 40f;
        final float fontSize = 11f;
        final float leading = 14f;

        java.util.List<java.util.List<String>> pages = new java.util.ArrayList<>();
        for (int index = 0; index < lines.size(); index += maxLinesPerPage) {
            pages.add(lines.subList(index, Math.min(index + maxLinesPerPage, lines.size())));
        }
        if (pages.isEmpty()) {
            pages.add(java.util.Collections.singletonList("No report data available."));
        }

        java.util.List<byte[]> contentStreams = new java.util.ArrayList<>();
        for (java.util.List<String> pageLines : pages) {
            StringBuilder content = new StringBuilder();
            content.append("BT\n");
            content.append("/F1 ").append((int) fontSize).append(" Tf\n");
            content.append(leading).append(" TL\n");
            content.append(leftMargin).append(' ').append(pageHeight - topMargin).append(" Td\n");
            for (int lineIndex = 0; lineIndex < pageLines.size(); lineIndex++) {
                if (lineIndex > 0) {
                    content.append("T*\n");
                }
                content.append('(').append(escapePdfText(pageLines.get(lineIndex))).append(") Tj\n");
            }
            content.append("ET\n");
            contentStreams.add(content.toString().getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        }

        java.io.ByteArrayOutputStream pdf = new java.io.ByteArrayOutputStream();
        java.util.List<Integer> offsets = new java.util.ArrayList<>();
        pdf.write("%PDF-1.4\n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

        java.util.List<String> objects = new java.util.ArrayList<>();
        objects.add("1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n");

        StringBuilder pagesObject = new StringBuilder();
        pagesObject.append("2 0 obj\n<< /Type /Pages /Kids [");
        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            int pageObjectNumber = 5 + (pageIndex * 2);
            pagesObject.append(pageObjectNumber).append(" 0 R ");
        }
        pagesObject.append("] /Count ").append(pages.size()).append(" >>\nendobj\n");
        objects.add(pagesObject.toString());

        objects.add("3 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier >>\nendobj\n");
        objects.add("4 0 obj\n<< /Type /Font /Subtype /Type1 /BaseFont /Courier-Bold >>\nendobj\n");

        for (int pageIndex = 0; pageIndex < pages.size(); pageIndex++) {
            int pageObjectNumber = 5 + (pageIndex * 2);
            int contentObjectNumber = pageObjectNumber + 1;
            String pageObject = pageObjectNumber + " 0 obj\n"
                + "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 " + (int) pageWidth + " " + (int) pageHeight + "] "
                + "/Resources << /Font << /F1 3 0 R /F2 4 0 R >> >> /Contents " + contentObjectNumber + " 0 R >>\n"
                + "endobj\n";
            String contentObject = contentObjectNumber + " 0 obj\n"
                + "<< /Length " + contentStreams.get(pageIndex).length + " >>\nstream\n"
                + new String(contentStreams.get(pageIndex), java.nio.charset.StandardCharsets.ISO_8859_1)
                + "endstream\nendobj\n";
            objects.add(pageObject);
            objects.add(contentObject);
        }

        for (String object : objects) {
            offsets.add(pdf.size());
            pdf.write(object.getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        }

        int xrefStart = pdf.size();
        int totalObjects = objects.size() + 1;
        pdf.write(("xref\n0 " + totalObjects + "\n").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        pdf.write("0000000000 65535 f \n".getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        for (int offset : offsets) {
            pdf.write(String.format(java.util.Locale.ROOT, "%010d 00000 n \n", offset).getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));
        }
        pdf.write(("trailer\n<< /Size " + totalObjects + " /Root 1 0 R >>\nstartxref\n" + xrefStart + "\n%%EOF").getBytes(java.nio.charset.StandardCharsets.ISO_8859_1));

        try (java.io.FileOutputStream outputStream = new java.io.FileOutputStream(file)) {
            pdf.writeTo(outputStream);
        }
    }

    private String escapePdfText(String text) {
        return text
            .replace("\\", "\\\\")
            .replace("(", "\\(")
            .replace(")", "\\)");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, Math.max(0, maxLength - 3)) + "...";
    }

    private String repeat(char character, int count) {
        StringBuilder builder = new StringBuilder(count);
        for (int index = 0; index < count; index++) {
            builder.append(character);
        }
        return builder.toString();
    }

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        downloadPdfReport();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Reports().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton116;
    private javax.swing.JButton jButton117;
    private javax.swing.JButton jButton118;
    private javax.swing.JButton jButton122;
    private javax.swing.JButton jButton123;
    private javax.swing.JButton jButton124;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane16;
    private javax.swing.JScrollPane jScrollPane17;
    private javax.swing.JScrollPane jScrollPane18;
    private javax.swing.JScrollPane jScrollPane19;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane20;
    private javax.swing.JScrollPane jScrollPane21;
    private javax.swing.JScrollPane jScrollPane22;
    private javax.swing.JScrollPane jScrollPane23;
    private javax.swing.JScrollPane jScrollPane24;
    private javax.swing.JScrollPane jScrollPane25;
    private javax.swing.JScrollPane jScrollPane26;
    private javax.swing.JScrollPane jScrollPane27;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable15;
    private javax.swing.JTable jTable16;
    private javax.swing.JTable jTable17;
    private javax.swing.JTable jTable18;
    private javax.swing.JTable jTable19;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable20;
    private javax.swing.JTable jTable21;
    private javax.swing.JTable jTable22;
    private javax.swing.JTable jTable23;
    private javax.swing.JTable jTable24;
    private javax.swing.JTable jTable25;
    private javax.swing.JTable jTable26;
    private javax.swing.JTable jTable27;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    // End of variables declaration//GEN-END:variables
}
