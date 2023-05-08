package frame;

import lexico.Errores;
import lexico.Lexico;
import lexico.Token;
import resources.CargarRecursos;
import resources.Line;
import sintaxis.Sintaxis;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.LinkedList;

public class Frame extends JFrame{
    //Titulo
    private String title;
    //Rutas
    private final String iconPath="src/resources/images/icon.png";
    private final String compilarPath="src/resources/images/engrane.png";
    private final String excelIconPath="src/resources/images/excel.png";
    private final String dirPath="src/resources/images/carpeta.png";
    private final String excelLexicoPath="src/resources/matrizLexico.xlsx";
    private final String excelSintaxisPath="src/resources/matrizSintaxis.xlsx";
    //Icono
    private ImageIcon icon;
    private ImageIcon iconCompi;
    private ImageIcon iconExcel;
    private ImageIcon iconDir;
    //Numero de linea para TextArea
    private Line numLine;
    private final int [][] matrizLexico;
    private final int [][] matrizSintactica;
    //Instancias de clase
    private Lexico lexico;

    private int erroresLexico;
    private int erroresSintaxis;
    private DefaultTableModel mdTblErrores;
    private DefaultTableModel mdTblTipoErrores;
    private DefaultTableModel mdTblContadores;
    private LinkedList<Errores> erroresList =new LinkedList<>();
    private LinkedList<Token> tokenListSintaxis =new LinkedList<>();
    private Sintaxis sintaxis;
    private boolean compilo=false;


    //Constructor de la clase, define el titulo, icono y llama a InitComponents
    public Frame (final String title){
        this.title=title;

        initImages();
        initComponents();
        initComponentsPersonal();

        //Cargar excel de léxico
        matrizLexico=CargarRecursos.openExcelFileLexico(excelLexicoPath);
        matrizSintactica=CargarRecursos.openExcelFileSintaxis(excelSintaxisPath);

//        CargarRecursos.llenarContadores();
    }
    private void initImages(){
        BufferedImage imagen= CargarRecursos.compatibleImageTRANSLUCENT(iconPath);
        this.icon = new ImageIcon(imagen);
        imagen= CargarRecursos.compatibleImageTRANSLUCENT(compilarPath);
        this.iconCompi = new ImageIcon(imagen);
        imagen= CargarRecursos.compatibleImageTRANSLUCENT(excelIconPath);
        this.iconExcel = new ImageIcon(imagen);
        imagen= CargarRecursos.compatibleImageTRANSLUCENT(dirPath);
        this.iconDir = new ImageIcon(imagen);
    }
    private void initComponentsPersonal(){
        setTitle(title);
        setIconImage(icon.getImage());
        numLine=new Line(txtCodigo);

//        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        mdTblErrores=(DefaultTableModel) tblError.getModel();
        mdTblTipoErrores=(DefaultTableModel) tblTipoError.getModel();
        mdTblContadores=(DefaultTableModel) tblContadores.getModel();

        sclCodigo.setRowHeaderView(numLine);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        setVisible(true);

    }
    //Código autogenerado por arrastrar código en NetBeans
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        pnCodigo = new javax.swing.JPanel();
        pnErrores = new javax.swing.JPanel();
        lblErrores = new javax.swing.JLabel();
        sclError = new javax.swing.JScrollPane();
        tblError = new javax.swing.JTable();
        pnRight = new javax.swing.JPanel();
        sclTokens = new javax.swing.JScrollPane();
        tblTokens = new javax.swing.JTable();
        sclTipoError = new javax.swing.JScrollPane();
        tblTipoError = new javax.swing.JTable();
        lblFungirox = new javax.swing.JLabel();
        lblTokens = new javax.swing.JLabel();
        pnContadores = new javax.swing.JPanel();
        lblContadores = new javax.swing.JLabel();
        sclContadores = new javax.swing.JScrollPane();
        tblContadores = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        btnCompilar = new javax.swing.JButton();
        btnOpenFile = new javax.swing.JButton();
        btnExcel = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        sclCodigo = new javax.swing.JScrollPane();
        txtCodigo = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblErrores.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblErrores.setText("Errores");

        tblError.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                        "Token", "Descripción", "Lexema", "Tipo", "Linea"
                }
        ));
        sclError.setViewportView(tblError);
        if (tblError.getColumnModel().getColumnCount() > 0) {
            tblError.getColumnModel().getColumn(0).setMinWidth(80); //Token
            tblError.getColumnModel().getColumn(0).setMaxWidth(80); //
            tblError.getColumnModel().getColumn(3).setMinWidth(120);//Tipo
            tblError.getColumnModel().getColumn(3).setMaxWidth(120);//Tipo
            tblError.getColumnModel().getColumn(4).setMinWidth(45); //Linea
            tblError.getColumnModel().getColumn(4).setMaxWidth(45);
        }

        tblTipoError.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {"Lexico", null},
                        {"Sintaxis", null}
                },
                new String [] {
                        "Tipos", ""
                }
        ));

        sclTipoError.setViewportView(tblTipoError);
        if (tblTipoError.getColumnModel().getColumnCount() > 0) {
            tblTipoError.getColumnModel().getColumn(0).setResizable(false);
        }

        javax.swing.GroupLayout pnErroresLayout = new javax.swing.GroupLayout(pnErrores);
        pnErrores.setLayout(pnErroresLayout);
        pnErroresLayout.setHorizontalGroup(
                pnErroresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnErroresLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnErroresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(sclError, javax.swing.GroupLayout.PREFERRED_SIZE, 820, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblErrores))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(sclTipoError, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );
        pnErroresLayout.setVerticalGroup(
                pnErroresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnErroresLayout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblErrores)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(pnErroresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(sclTipoError, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(sclError, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        tblTokens.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                },
                new String [] {
                        "Estado", "Lexema", "Linea"
                }
        ));
        sclTokens.setViewportView(tblTokens);
        if (tblTokens.getColumnModel().getColumnCount() > 0) {
            tblTokens.getColumnModel().getColumn(0).setMinWidth(50);
            tblTokens.getColumnModel().getColumn(0).setMaxWidth(50);
            tblTokens.getColumnModel().getColumn(2).setMinWidth(50);
            tblTokens.getColumnModel().getColumn(2).setMaxWidth(50);
        }

        lblTokens.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTokens.setText("Tokens");

        lblContadores.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblContadores.setText("Contadores");

        tblContadores.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {"Postfix",null},
                        {"Binarios",null},
                        {"Control",null},
                        {"Matemáticos",null},
                        {"Exponentes",null},
                        {"Turno",null},
                        {"Relacionales",null},
                        {"Sin igualdad",null},
                        {"Lógicos",null},
                        {"Ternario",null},
                        {"Asignación",null},
                        {"Agrupamiento",null},
                        {"Reservadas",null},
                        {"Comentarios",null},
                        {"Cadenas",null},
                        {"Enteros",null},
                        {"Reales",null},
                        {"Booleans",null},
                        {"Nulls",null},
                        {"Identificadores",null},
                        {"Errores",null}
                },
                new String [] {
                        "Clasificación", ""
                }
        ));
        sclContadores.setViewportView(tblContadores);
        if (tblContadores.getColumnModel().getColumnCount() > 0) {
            tblContadores.getColumnModel().getColumn(0).setMinWidth(150);
            tblContadores.getColumnModel().getColumn(0).setMaxWidth(150);
//            tblContadores.getColumnModel().getColumn(1).setMinWidth(80);
//            tblContadores.getColumnModel().getColumn(1).setMaxWidth(80);
        }

        javax.swing.GroupLayout pnContadoresLayout = new javax.swing.GroupLayout(pnContadores);
        pnContadores.setLayout(pnContadoresLayout);
        pnContadoresLayout.setHorizontalGroup(
                pnContadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnContadoresLayout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(pnContadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblContadores)
                                        .addComponent(sclContadores, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        pnContadoresLayout.setVerticalGroup(
                pnContadoresLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnContadoresLayout.createSequentialGroup()
                                .addComponent(lblContadores)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sclContadores, javax.swing.GroupLayout.DEFAULT_SIZE, 459, Short.MAX_VALUE))
        );

        btnCompilar.setIcon(iconCompi);
        btnCompilar.setPreferredSize(new java.awt.Dimension(72, 50));
        btnCompilar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCompilarActionPerformed(evt);
            }
        });


        btnOpenFile.setIcon(iconDir);
        btnOpenFile.setPreferredSize(new java.awt.Dimension(72, 50));
        btnOpenFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btnOpenFileActionPerformed(evt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        jPanel3.add(btnOpenFile);

        jPanel3.add(btnCompilar);

        btnExcel.setIcon(iconExcel);
        btnExcel.setPreferredSize(new java.awt.Dimension(72, 50));
        jPanel3.add(btnExcel);
        btnExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnExcelActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnRightLayout = new javax.swing.GroupLayout(pnRight);
        pnRight.setLayout(pnRightLayout);
        pnRightLayout.setHorizontalGroup(
                pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnRightLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblTokens)
                                        .addComponent(sclTokens, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(pnContadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnRightLayout.setVerticalGroup(
                pnRightLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnRightLayout.createSequentialGroup()
                                .addComponent(lblTokens)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sclTokens)
                                .addContainerGap())
                        .addGroup(pnRightLayout.createSequentialGroup()
                                .addGap(25, 25, 25)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(pnContadores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addContainerGap())
        );

        lblCodigo.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblCodigo.setText("Código");

        txtCodigo.setColumns(20);
        txtCodigo.setRows(5);
        sclCodigo.setViewportView(txtCodigo);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(lblCodigo)
                                        .addComponent(sclCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblCodigo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(sclCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 550, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnCodigoLayout = new javax.swing.GroupLayout(pnCodigo);
        pnCodigo.setLayout(pnCodigoLayout);
        pnCodigoLayout.setHorizontalGroup(
                pnCodigoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnCodigoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnCodigoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(pnErrores, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(pnCodigoLayout.createSequentialGroup()
                                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(pnRight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnCodigoLayout.setVerticalGroup(
                pnCodigoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(pnCodigoLayout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(pnCodigoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(pnRight, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(pnErrores, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pnCodigo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(pnCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    private void btnCompilarActionPerformed(java.awt.event.ActionEvent evt){
        //Compilar

        switch (txtCodigo.getText()){
            case "":
            case " ":
            case "\n":
            case "\t":
                JOptionPane.showMessageDialog(null,"Escribe/abre tu codigo antes de compilar (o゜▽゜)o☆");
                break;
            default:
                if (compilo){
                    lexico.clean();
                    sintaxis.clean();
                    mdTblErrores.setRowCount(0);
                    lexico.setText(txtCodigo.getText()+" ");
                }
                else{
                    lexico=new Lexico(matrizLexico,txtCodigo.getText()+'\n',tblTokens/*,tblError*/,tblContadores,erroresList,tokenListSintaxis);
                    compilo=true;
                    sintaxis=new Sintaxis(matrizSintactica,erroresList,tokenListSintaxis);
                }
                lexico.compilar();
                erroresLexico=erroresList.size();
                sintaxis.analize();
                erroresSintaxis=erroresList.size()-erroresLexico;
                llenarTablaErrores();
        }


//        txtCodigo.get
    }

    private void llenarTablaErrores(){
        for(int i = 0; i< erroresList.size(); i++){
            mdTblErrores.addRow(erroresList.get(i).getRow());
        }
        mdTblContadores.setValueAt(erroresList.size(),20,1);
        mdTblTipoErrores.setValueAt(erroresLexico,0,1);
        mdTblTipoErrores.setValueAt(erroresSintaxis,1,1);
    }

    private void btnExcelActionPerformed(java.awt.event.ActionEvent evt){
//        String palabrasReservadas=CargarRecursos.openFile("src/resources/palabrasReservadas.txt");
//        imprimirMap(palabrasReservadas);
        if(compilo){
            JFileChooser fileChooser=new JFileChooser();
            fileChooser.setDialogTitle("Save Excel File");
            fileChooser.setFileFilter(new FileNameExtensionFilter("Excel files (*.xlsx)", "xlsx"));
            int r=fileChooser.showSaveDialog(null);//Select file to open
            String path="";

            if(r==JFileChooser.APPROVE_OPTION){
                path=fileChooser.getSelectedFile().getAbsolutePath();
                lexico.writeExcel(path);
            }
        }
        else
            JOptionPane.showMessageDialog(null,"Primero debes Compilar (o゜▽゜)o☆");
    }

    private void btnOpenFileActionPerformed(java.awt.event.ActionEvent evt) throws IOException {
        if(compilo)
            lexico.clean();
        JFileChooser fileChooser=new JFileChooser();
        fileChooser.setDialogTitle("Open File");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        int r=fileChooser.showOpenDialog(null);//Select file to open
        String path="",contenido="";

        if(r==JFileChooser.APPROVE_OPTION){
            path=fileChooser.getSelectedFile().getAbsolutePath();
            contenido=CargarRecursos.openFile(path);
//            contenido=new String(Files.readAllBytes(Paths.get(path)),StandardCharsets.UTF_8);

            txtCodigo.setText(contenido);
        }

    }

    private void imprimirMap(String contenido) {
        String palabras[];
        palabras=separarString(",",contenido);
        /*
         * SECCIONES POR STRING CADA UNA
         */
        for (int i=0;i<palabras.length;i++){
            System.out.println("put(\""+palabras[i]+"\","+(i+1)+");");
        }
    }
    private String[] separarString(String caracter,String cadena){
        return cadena.split(caracter);
    }
    //Objetos
    private javax.swing.JButton btnExcel;
    private javax.swing.JButton btnCompilar;
    private javax.swing.JButton btnOpenFile;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblFungirox;
    private javax.swing.JLabel lblContadores;
    private javax.swing.JLabel lblErrores;
    private javax.swing.JLabel lblTokens;
    private javax.swing.JPanel pnCodigo;
    private javax.swing.JPanel pnContadores;
    private javax.swing.JPanel pnErrores;
    private javax.swing.JPanel pnRight;
    private javax.swing.JScrollPane sclContadores;
    private javax.swing.JScrollPane sclError;
    private javax.swing.JScrollPane sclTokens;
    private javax.swing.JScrollPane sclCodigo;
    private javax.swing.JTable tblContadores;
    private javax.swing.JTable tblError;
    private javax.swing.JTable tblTokens;
    private javax.swing.JTextArea txtCodigo;
    private javax.swing.JTable tblTipoError;
    private javax.swing.JScrollPane sclTipoError;
}
