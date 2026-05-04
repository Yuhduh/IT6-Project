package it6.project;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */

import java.util.List;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.ListSelectionModel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Carl John
 */
public class ProductList extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger =
            java.util.logging.Logger.getLogger(ProductList.class.getName());

    private ProductDAO  productDAO;
    private CategoryDAO categoryDAO;
    private DefaultTableModel productTableModel;
    private int selectedProductId = -1;

    // ── Constructor ───────────────────────────────────────────────────────────

    public ProductList() {
        initComponents();
        setLocationRelativeTo(null);
        initializeBackend();
    }

    // ── Backend initialisation ────────────────────────────────────────────────

    private void initializeBackend() {
        productDAO  = new ProductDAO();
        categoryDAO = new CategoryDAO();

        productTableModel = (DefaultTableModel) jTable1.getModel();
        productTableModel.setRowCount(0);
        jTable1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        jTable1.getSelectionModel().addListSelectionListener(e -> {
            int viewRow = jTable1.getSelectedRow();
            if (viewRow >= 0) {
                int modelRow = jTable1.convertRowIndexToModel(viewRow);
                selectedProductId = (int) productTableModel.getValueAt(modelRow, 0);
            }
        });


        setupCrudMenu();
        setupActionsColumnClick();
        setupActionsColumnRenderer();
        loadProducts();
    }

    private void setupActionsColumnRenderer() {
        jTable1.setBackground(Color.WHITE);
        jTable1.setOpaque(true);
        jTable1.setRowHeight(40);
        jTable1.getColumnModel().getColumn(5).setMinWidth(220);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(240);
        jTable1.getColumnModel().getColumn(5).setMaxWidth(260);
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new ActionsCellRenderer());
    }

    private void setupActionsColumnClick() {
        jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = jTable1.rowAtPoint(e.getPoint());
                int col = jTable1.columnAtPoint(e.getPoint());
                if (row < 0 || col != 5) return;

                int modelRow = jTable1.convertRowIndexToModel(row);
                selectedProductId = (int) productTableModel.getValueAt(modelRow, 0);

                int cellX    = e.getX() - jTable1.getCellRect(row, col, true).x;
                int cellWidth = jTable1.getCellRect(row, col, true).width;
                int zone     = cellX * 3 / Math.max(cellWidth, 1);

                if      (zone == 0) sellSelectedProduct();
                else if (zone == 1) showEditProductDialog();
                else                deleteSelectedProduct();
            }
        });
    }


    private void setupCrudMenu() {
        JPopupMenu popupMenu = new JPopupMenu();

        JMenuItem addItem = new JMenuItem("Add Product");
        addItem.addActionListener(e -> { new AddProduct(this).setVisible(true); dispose(); });

        JMenuItem editItem   = new JMenuItem("Edit Selected Product");
        editItem.addActionListener(e -> showEditProductDialog());

        JMenuItem deleteItem = new JMenuItem("Delete Selected Product");
        deleteItem.addActionListener(e -> deleteSelectedProduct());

        JMenuItem sellItem   = new JMenuItem("Sell/Deduct Selected Product");
        sellItem.addActionListener(e -> sellSelectedProduct());

        JMenuItem refreshItem = new JMenuItem("Refresh");
        refreshItem.addActionListener(e -> loadProducts());

        popupMenu.add(addItem);
        popupMenu.add(editItem);
        popupMenu.add(deleteItem);
        popupMenu.add(sellItem);
        popupMenu.addSeparator();
        popupMenu.add(refreshItem);

        jTable1.setComponentPopupMenu(popupMenu);
        jPanel1.setComponentPopupMenu(popupMenu);
    }

    // ── Load / search ─────────────────────────────────────────────────────────

    /**
     * Loads all products via INNER JOIN (category name comes from the JOIN).
     */
    private void loadProducts() {
        productTableModel.setRowCount(0);
        for (Product p : productDAO.getAllProducts()) {
            productTableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),     // resolved via INNER JOIN in ProductDAO
                p.getQuantity(),
                String.format("%.2f", p.getPrice()),
                "Sell/Deduct | Edit | Delete"
            });
        }
    }

    public void refreshProductList() {
        loadProducts();
    }

    private void searchProducts() {
        String keyword = jTextField1.getText().trim();
        if (keyword.isEmpty() || "Search by product name or category....".equals(keyword)) {
            loadProducts();
            return;
        }

        productTableModel.setRowCount(0);
        for (Product p : productDAO.searchProducts(keyword)) {
            productTableModel.addRow(new Object[]{
                p.getId(),
                p.getName(),
                p.getCategory(),
                p.getQuantity(),
                String.format("%.2f", p.getPrice()),
                "actions"
            });
        }
    }

    // ── Actions cell renderer ─────────────────────────────────────────────────

    private static class ActionsCellRenderer extends JPanel implements TableCellRenderer {

        private final JLabel sellLabel   = createActionLabel(" Sell/Deduct ", new Color(255, 122, 0));
        private final JLabel editLabel   = createActionLabel(" Edit ",        new Color(52,  120, 246));
        private final JLabel deleteLabel = createActionLabel(" Delete ",      new Color(255,  59,  79));

        ActionsCellRenderer() {
            setLayout(new FlowLayout(FlowLayout.LEFT, 6, 4));
            setOpaque(true);
            add(sellLabel);
            add(editLabel);
            add(deleteLabel);
        }

        private static JLabel createActionLabel(String text, Color bg) {
            JLabel label = new JLabel(text);
            label.setOpaque(true);
            label.setForeground(Color.WHITE);
            label.setBackground(bg);
            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(bg.darker()),
                    BorderFactory.createEmptyBorder(4, 8, 4, 8)));
            return label;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    // ── CRUD dialogs ──────────────────────────────────────────────────────────

    /**
     * Edit dialog — uses a JComboBox for the category field,
     * pre-selected to the product's current category.
     */
    private void showEditProductDialog() {
        if (selectedProductId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }

        Product current = productDAO.getProductById(selectedProductId);
        if (current == null) {
            JOptionPane.showMessageDialog(this, "Product not found.");
            return;
        }

        // ── Name field ────────────────────────────────────────────────────────
        String name = JOptionPane.showInputDialog(this, "Edit product name:", current.getName());
        if (name == null || name.trim().isEmpty()) return;

        // ── Category combo box ────────────────────────────────────────────────
        List<Category> categories = categoryDAO.getAllCategories();
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        Category preSelected = null;
        for (Category cat : categories) {
            model.addElement(cat);
            if (cat.getId() == current.getCategoryId()) {
                preSelected = cat;
            }
        }

        JComboBox<Category> categoryCombo = new JComboBox<>(model);
        categoryCombo.setFont(new java.awt.Font("Segoe UI", 0, 14));
        if (preSelected != null) {
            categoryCombo.setSelectedItem(preSelected);
        }

        int catResult = JOptionPane.showConfirmDialog(
                this,
                new Object[]{"Select category:", categoryCombo},
                "Edit Category",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (catResult != JOptionPane.OK_OPTION) return;

        Category selectedCategory = (Category) categoryCombo.getSelectedItem();
        if (selectedCategory == null) return;

        // ── Price & quantity ──────────────────────────────────────────────────
        String priceInput    = JOptionPane.showInputDialog(this, "Edit price:",    current.getPrice());
        String quantityInput = JOptionPane.showInputDialog(this, "Edit quantity:", current.getQuantity());

        try {
            double price    = Double.parseDouble(priceInput);
            int    quantity = Integer.parseInt(quantityInput);

            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative.");
                return;
            }

            Product updated = new Product(
                selectedProductId,
                name.trim(),
                selectedCategory.getId(),
                selectedCategory.getName(),
                price,
                quantity
            );

            if (productDAO.updateProduct(updated)) {
                JOptionPane.showMessageDialog(this, "Product updated successfully.");
                loadProducts();
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid numeric input.");
        }
    }

    private void deleteSelectedProduct() {
        if (selectedProductId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
                this, "Delete selected product?", "Confirm Delete", JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            if (productDAO.deleteProduct(selectedProductId)) {
                JOptionPane.showMessageDialog(this, "Product deleted successfully.");
                selectedProductId = -1;
                loadProducts();
            }
        }
    }

    private void sellSelectedProduct() {
        if (selectedProductId <= 0) {
            JOptionPane.showMessageDialog(this, "Select a product first.");
            return;
        }

        String qtyInput = JOptionPane.showInputDialog(this, "Enter quantity to deduct:");
        if (qtyInput == null || qtyInput.trim().isEmpty()) return;

        try {
            int qtyToSell = Integer.parseInt(qtyInput);
            if (qtyToSell <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than zero.");
                return;
            }
            if (productDAO.sellProduct(selectedProductId, qtyToSell)) {
                JOptionPane.showMessageDialog(this, "Stock deducted successfully.");
                loadProducts();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid quantity input.");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel45 = new javax.swing.JPanel();
        jButton113 = new javax.swing.JButton();
        jButton114 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton115 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel46 = new javax.swing.JPanel();
        jButton116 = new javax.swing.JButton();
        jButton117 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jButton118 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();

        jPanel45.setBackground(new java.awt.Color(255, 255, 255));

        jButton113.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton113.setText("Product List");
        jButton113.addActionListener(this::jButton113jButton2ActionPerformed);

        jButton114.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton114.setText("Stock In");
        jButton114.addActionListener(this::jButton114jButton3ActionPerformed);

        jButton7.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton7.setText("Dashboard");
        jButton7.addActionListener(this::jButton7ActionPerformed);

        jButton115.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton115.setText("Stock Monitoring");
        jButton115.addActionListener(this::jButton115jButton5ActionPerformed);

        jButton8.setText("Reports");
        jButton8.addActionListener(this::jButton8ActionPerformed);

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton113, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton114, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton115, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton113, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton114, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton115, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        jLabel2.setText("Product List");

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Search Products");

        jTextField1.setForeground(new java.awt.Color(153, 153, 153));
        jTextField1.setText("Search by product name or category....");
        jTextField1.addActionListener(this::jTextField1ActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 866, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jLabel3)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(jLabel4))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(25, Short.MAX_VALUE))
        );

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Product Name", "Category", "Quantity", "Price", "Actions"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jButton1.setBackground(new java.awt.Color(0, 153, 255));
        jButton1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Add Product");

        jPanel46.setBackground(new java.awt.Color(255, 255, 255));

        jButton116.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton116.setText("Product List");
        jButton116.addActionListener(this::jButton116jButton2ActionPerformed);

        jButton117.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton117.setText("Stock In");
        jButton117.addActionListener(this::jButton117jButton3ActionPerformed);

        jButton9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton9.setText("Dashboard");
        jButton9.addActionListener(this::jButton9ActionPerformed);

        jButton118.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton118.setText("Stock Monitoring");
        jButton118.addActionListener(this::jButton118jButton5ActionPerformed);

        jButton10.setText("Reports");
        jButton10.addActionListener(this::jButton10ActionPerformed);

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton116, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton117, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(jButton118, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton116, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton117, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton9, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton118, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 925, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel46, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(23, 23, 23)
                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
        searchProducts();
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton113jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton113jButton2ActionPerformed
        ProductList productList = new ProductList();
        productList.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton113jButton2ActionPerformed

    private void jButton114jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton114jButton3ActionPerformed
        StockIn stockIn = new StockIn();
        stockIn.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton114jButton3ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton115jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton115jButton5ActionPerformed
        StockMonitoring stockMonitor = new StockMonitoring();
        stockMonitor.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton115jButton5ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        Reports report = new Reports();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton8ActionPerformed

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

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        Dashboard dashboard = new Dashboard();
        dashboard.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton118jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton118jButton5ActionPerformed
        StockMonitoring stockMonitor = new StockMonitoring();
        stockMonitor.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton118jButton5ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        Reports report = new Reports();
        report.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_jButton10ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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

        java.awt.EventQueue.invokeLater(() -> new ProductList().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton113;
    private javax.swing.JButton jButton114;
    private javax.swing.JButton jButton115;
    private javax.swing.JButton jButton116;
    private javax.swing.JButton jButton117;
    private javax.swing.JButton jButton118;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}