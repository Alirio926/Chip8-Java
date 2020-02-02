/*
 * Copyright (C) 2019 Alirio Oliveira <https://github.com/Alirio926>
 *
 * Este software é livre: você pode redistribuí-lo e/ou
 * modificá-lo dentro dos termos da Licença Pública Geral GNU como
 * publicada pela Fundação do Software Livre (FSF); na versão 3 da
 * Licença, ou (a seu critério) qualquer versão posterior.
 *
 * Este programa é distribuído na esperança de que possa ser útil,
 * mas SEM NENHUMA GARANTIA; sem uma garantia implícita de ADEQUAÇÃO
 * a qualquer MERCADO ou APLICAÇÃO EM PARTICULAR. Veja a
 * Licença Pública Geral GNU para maiores detalhes.
 *
 * Você deve ter recebido uma cópia da Licença Pública Geral GNU junto
 * com este programa, Se não, veja <http://www.gnu.org/licenses/>.
 */

package main;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Alirio Oliveira <https://github.com/Alirio926>
 */

public class Emulador extends javax.swing.JFrame implements Runnable, KeyListener, ScreenCallback{

    /**
     * Creates new form Emulador
     */
    private final Chip8 cpu;
    //private static Tela screen;
    //private static ScreenCanvas screenCanvas;
    private BufferStrategy bufferStrategy;
    Graphics graphics;
    private int[][] gfx = new int[64][32];
    private static final int W = 64;
    private static final int H = 32;
    private int scale = 8;
    public boolean drawFlag;
    static int[] chip8_fontset = 
    { 
      0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
      0x20, 0x60, 0x20, 0x20, 0x70, // 1
      0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
      0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
      0x90, 0x90, 0xF0, 0x10, 0x10, // 4
      0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
      0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
      0xF0, 0x10, 0x20, 0x40, 0x40, // 7
      0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
      0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
      0xF0, 0x90, 0xF0, 0x90, 0x90, // A
      0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
      0xF0, 0x80, 0x80, 0x80, 0xF0, // C
      0xE0, 0x90, 0x90, 0x90, 0xE0, // D
      0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
      0xF0, 0x80, 0xF0, 0x80, 0x80  // F
    };
    
    private static Teclado teclado;
    private volatile boolean start = false;
    private volatile boolean oneStep = false;
    private volatile int cicle_per_second = 200;
    private volatile Thread thread = null;
    private static final Logger LOGGER = Logger.getLogger( Emulador.class.getName() );
    private int lastSP=0xFE;
    private javax.swing.table.DefaultTableModel tableModel;
    
    long startTime=0, elapsedTime=0, lastTime=0;
    
    public Emulador() {
        super();
	addKeyListener( this );        
       
        //screen = new Tela();
        teclado = new Teclado();
        initComponents();
                        
        tableModel = new DefaultTableModel();
        
        this.pack();
        
        if(thread == null)
            thread = new Thread(this, "Chip8");
               
        
        //screenCanvas = new ScreenCanvas();        
        //jPanel1.add(screen);
        screen.createBufferStrategy(3);
        cpu = new Chip8(this, teclado);
      
        
        thread.start();
    }
    private void paintScreen(){
        bufferStrategy = screen.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();
         for(int x=0;x<W;x++){
            for(int y=0;y<H;y++){
                if(gfx[x][y] == 1){
                    graphics.setColor(Color.WHITE);
                }else{
                    graphics.setColor(Color.BLACK);
                }
                graphics.fillRect(x*scale, (y*scale), scale, scale);
            }
        }
        bufferStrategy.show();
        graphics.dispose();
        setScreenDrawFlag(false);
        
    }
    private void printRegistradores(){
        tfV0.setText((cpu.V[0] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[0]).toUpperCase());
        tfV1.setText((cpu.V[1] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[1]).toUpperCase());
        tfV2.setText((cpu.V[2] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[2]).toUpperCase());
        tfV3.setText((cpu.V[3] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[3]).toUpperCase());
        tfV4.setText((cpu.V[4] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[4]).toUpperCase());
        tfV5.setText((cpu.V[5] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[5]).toUpperCase());
        tfV6.setText((cpu.V[6] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[6]).toUpperCase());
        tfV7.setText((cpu.V[7] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[7]).toUpperCase());
        tfV8.setText((cpu.V[8] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[8]).toUpperCase());
        tfV9.setText((cpu.V[9] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[9]).toUpperCase());
        V10.setText((cpu.V[10] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[10]).toUpperCase());
        V11.setText((cpu.V[11] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[11]).toUpperCase());
        V12.setText((cpu.V[12] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[12]).toUpperCase());
        V13.setText((cpu.V[13] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[13]).toUpperCase());
        V14.setText((cpu.V[14] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[14]).toUpperCase());
        V15.setText((cpu.V[15] > 16 ? "0x":"0x0")+Integer.toHexString(cpu.V[15]).toUpperCase());
        tfOpcode.setText(Integer.toHexString(cpu.OpCode).toUpperCase());
        tfPC.setText(Integer.toHexString(cpu.PC).toUpperCase());
        tfSP.setText(Integer.toHexString(cpu.SP).toUpperCase());
        tfI.setText(Integer.toHexString(cpu.I).toUpperCase());
        tableModel = (DefaultTableModel)tbStack.getModel();
        
        
        if(cpu.SP != lastSP){
            tableModel.setRowCount(0);
            
            for(int x = 0; x < 16; x++)
                tableModel.addRow(new Object[]{cpu.stack[x]});
            
            tbStack.setModel(tableModel);
            lastSP = cpu.SP;
        }
    }
    @Override
    public void run() {
        while(true){
            if(start){  
                elapsedTime = System.nanoTime() - startTime;
                if(elapsedTime != lastTime){
                    lbFreq.setText(Long.toString(elapsedTime/1000000)+" ms");
                    lastTime = elapsedTime;
                }
                startTime = System.nanoTime();
                cpu.fetch();
                cpu.execute();
                //if(screen.isDrawFlag())
                    //mainPanel.repaint();
                if(getScreenDrawFlag())
                    paintScreen();

                teclado.doTeclado();
                printRegistradores();
                try {
                    Thread.sleep(1000/cicle_per_second);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Emulador.class.getName()).log(Level.SEVERE, null, ex);
                }
                if(oneStep){
                    oneStep = false;
                    start = false;                    
                }
                    
            }
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

        jdSobre = new javax.swing.JDialog();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        screen = new java.awt.Canvas();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        tfV15 = new javax.swing.JLabel();
        tfV14 = new javax.swing.JLabel();
        tfV13 = new javax.swing.JLabel();
        tfV12 = new javax.swing.JLabel();
        tfV11 = new javax.swing.JLabel();
        lbl10 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        tfV7 = new javax.swing.JTextField();
        tfV8 = new javax.swing.JTextField();
        V10 = new javax.swing.JTextField();
        V11 = new javax.swing.JTextField();
        V12 = new javax.swing.JTextField();
        V13 = new javax.swing.JTextField();
        V14 = new javax.swing.JTextField();
        V15 = new javax.swing.JTextField();
        tfV5 = new javax.swing.JTextField();
        tfV2 = new javax.swing.JTextField();
        tfV0 = new javax.swing.JTextField();
        tfV3 = new javax.swing.JTextField();
        tfV4 = new javax.swing.JTextField();
        tfV1 = new javax.swing.JTextField();
        tfV6 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        tfV9 = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbStack = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        tfOpcode = new javax.swing.JTextField();
        tfPC = new javax.swing.JTextField();
        tfI = new javax.swing.JTextField();
        tfSP = new javax.swing.JTextField();
        btnIniciar = new javax.swing.JButton();
        btnReset = new javax.swing.JButton();
        btnParar = new javax.swing.JButton();
        btnStep = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jSlider2 = new javax.swing.JSlider();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        lbFreq = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu2 = new javax.swing.JMenu();
        miAbrir = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();

        jdSobre.setTitle("Sobre o programa");
        jdSobre.setAlwaysOnTop(true);
        jdSobre.setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        jdSobre.setResizable(false);

        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main/enoiz.gif"))); // NOI18N

        jLabel11.setText("<html><head> </head><body> <p> Criado por: <font color=\"blue\">Alirio -Squall</p></font><p>Versão 0.1 - 2019</p><p>Livre para uso.</p><p><a href=\"\">aliriofilho926@yahoo.com.br</a></p></body></html>");

        jLabel12.setFont(new java.awt.Font("Calibri", 1, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("CHIP8 Emulador");

        javax.swing.GroupLayout jdSobreLayout = new javax.swing.GroupLayout(jdSobre.getContentPane());
        jdSobre.getContentPane().setLayout(jdSobreLayout);
        jdSobreLayout.setHorizontalGroup(
            jdSobreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jdSobreLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 10, Short.MAX_VALUE)
                .addGroup(jdSobreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addContainerGap())
        );
        jdSobreLayout.setVerticalGroup(
            jdSobreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jdSobreLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jdSobreLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jdSobreLayout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("CHIP8 Emulador -by Squall");
        setResizable(false);

        jPanel1.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel1.setPreferredSize(new java.awt.Dimension(513, 264));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(screen, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(screen, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Registradores"));

        jLabel1.setText("V0");

        jLabel2.setText("V1");

        jLabel3.setText("V2");

        jLabel4.setText("V3");

        jLabel5.setText("V4");

        jLabel6.setText("V5");

        jLabel7.setText("V6");

        jLabel8.setText("V7");

        tfV15.setText("V15");

        tfV14.setText("V14");

        tfV13.setText("V13");

        tfV12.setText("V12");

        tfV11.setText("V11");

        lbl10.setText("V10");

        jLabel16.setText("V9");

        tfV7.setText("0xFF");
        tfV7.setEnabled(false);
        tfV7.setFocusable(false);
        tfV7.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV7.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV8.setText("0xFF");
        tfV8.setEnabled(false);
        tfV8.setFocusable(false);
        tfV8.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV8.setPreferredSize(new java.awt.Dimension(40, 30));

        V10.setText("0xFF");
        V10.setEnabled(false);
        V10.setFocusable(false);
        V10.setMinimumSize(new java.awt.Dimension(40, 30));
        V10.setPreferredSize(new java.awt.Dimension(40, 30));

        V11.setText("0xFF");
        V11.setEnabled(false);
        V11.setFocusable(false);
        V11.setMinimumSize(new java.awt.Dimension(40, 30));
        V11.setPreferredSize(new java.awt.Dimension(40, 30));

        V12.setText("0xFF");
        V12.setEnabled(false);
        V12.setFocusable(false);
        V12.setMinimumSize(new java.awt.Dimension(40, 30));
        V12.setPreferredSize(new java.awt.Dimension(40, 30));

        V13.setText("0xFF");
        V13.setEnabled(false);
        V13.setFocusable(false);
        V13.setMinimumSize(new java.awt.Dimension(40, 30));
        V13.setPreferredSize(new java.awt.Dimension(40, 30));

        V14.setText("0xFF");
        V14.setEnabled(false);
        V14.setFocusable(false);
        V14.setMinimumSize(new java.awt.Dimension(40, 30));
        V14.setPreferredSize(new java.awt.Dimension(40, 30));

        V15.setText("0xFF");
        V15.setEnabled(false);
        V15.setFocusable(false);
        V15.setMinimumSize(new java.awt.Dimension(40, 30));
        V15.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV5.setText("0xFF");
        tfV5.setEnabled(false);
        tfV5.setFocusable(false);
        tfV5.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV5.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV2.setText("0xFF");
        tfV2.setEnabled(false);
        tfV2.setFocusable(false);
        tfV2.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV2.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV0.setText("0xFF");
        tfV0.setEnabled(false);
        tfV0.setFocusable(false);
        tfV0.setMaximumSize(new java.awt.Dimension(40, 30));
        tfV0.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV0.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV3.setText("0xFF");
        tfV3.setEnabled(false);
        tfV3.setFocusable(false);
        tfV3.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV3.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV4.setText("0xFF");
        tfV4.setEnabled(false);
        tfV4.setFocusable(false);
        tfV4.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV4.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV1.setText("0xFF");
        tfV1.setEnabled(false);
        tfV1.setFocusable(false);
        tfV1.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV1.setPreferredSize(new java.awt.Dimension(40, 30));

        tfV6.setText("0xFF");
        tfV6.setEnabled(false);
        tfV6.setFocusable(false);
        tfV6.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV6.setPreferredSize(new java.awt.Dimension(40, 30));

        jLabel20.setText("V8");

        tfV9.setText("0xFF");
        tfV9.setEnabled(false);
        tfV9.setFocusable(false);
        tfV9.setMinimumSize(new java.awt.Dimension(40, 30));
        tfV9.setPreferredSize(new java.awt.Dimension(40, 30));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfV0, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfV1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfV2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfV3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfV4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(tfV5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(10, 10, 10)
                        .addComponent(tfV7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(10, 10, 10)
                        .addComponent(tfV9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V15, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfV6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(tfV8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(lbl10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(V12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(tfV0, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV1, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV3, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV4, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV5, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV6, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel7))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV7, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(tfV8, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tfV9, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V10, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbl10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V11, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfV11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V12, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfV12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V13, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfV13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V14, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfV14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(V15, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tfV15)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Variaveis"));

        tbStack.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null},
                {null}
            },
            new String [] {
                "Stack"
            }
        ));
        tbStack.setColumnSelectionAllowed(true);
        tbStack.setEnabled(false);
        tbStack.setFillsViewportHeight(true);
        tbStack.setFocusable(false);
        tbStack.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tbStack);

        jLabel9.setText("SP");

        jLabel17.setText("PC");

        jLabel18.setText("I");

        jLabel19.setText("Opcode");

        tfOpcode.setText("0xDXYN");
        tfOpcode.setEnabled(false);
        tfOpcode.setFocusable(false);

        tfPC.setText("0x25F");
        tfPC.setEnabled(false);
        tfPC.setFocusable(false);

        tfI.setText("0xF96");
        tfI.setEnabled(false);
        tfI.setFocusable(false);

        tfSP.setText("0x2");
        tfSP.setEnabled(false);
        tfSP.setFocusable(false);

        btnIniciar.setText("Iniciar");
        btnIniciar.setEnabled(false);
        btnIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnIniciarActionPerformed(evt);
            }
        });

        btnReset.setText("Reset");
        btnReset.setEnabled(false);
        btnReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetActionPerformed(evt);
            }
        });

        btnParar.setText("Pause");
        btnParar.setEnabled(false);
        btnParar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPararActionPerformed(evt);
            }
        });

        btnStep.setText("Step");
        btnStep.setEnabled(false);
        btnStep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnStepActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel19)
                            .addComponent(jLabel17)
                            .addComponent(jLabel18)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfSP, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfI, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfPC, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tfOpcode, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(btnIniciar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnReset, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnParar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnStep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(tfOpcode, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel17)
                            .addComponent(tfPC, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel18)
                            .addComponent(tfI, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(tfSP, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnIniciar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnReset)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnParar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnStep)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSlider1.setMaximum(8);
        jSlider1.setMinimum(1);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jSlider2.setMaximum(800);
        jSlider2.setMinimum(100);
        jSlider2.setPaintLabels(true);
        jSlider2.setPaintTicks(true);
        jSlider2.setValue(200);
        jSlider2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider2StateChanged(evt);
            }
        });

        jLabel13.setText("Scale x");

        jLabel14.setText("Speed");

        jLabel15.setText("8");

        jLabel21.setText("200");

        lbFreq.setText("00 ms");

        jMenu2.setText("Rom");

        miAbrir.setText("Abrir rom");
        miAbrir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                miAbrirActionPerformed(evt);
            }
        });
        jMenu2.add(miAbrir);

        jMenuItem1.setText("Fechar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem1);

        jMenuBar1.add(jMenu2);

        jMenu1.setText("Ajuda");

        jMenuItem2.setText("Sobre");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel13)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                                .addComponent(lbFreq, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 314, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(0, 0, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel13)
                                        .addComponent(jLabel15))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                    .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel14)
                                            .addComponent(jLabel21))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                            .addGap(8, 8, 8)
                                            .addComponent(lbFreq)))
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jSlider2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGap(18, 18, 18)
                            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void miAbrirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_miAbrirActionPerformed
        
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          this.setTitle("CHIP8 Emulador -by Squall    [ "+selectedFile.getName()+" ]");
        try {         
            start = false;
            //screen.clear();
            clearScreen();
            //mainPanel.repaint();
            cpu.resetChip8();
            cpu.loadFile(selectedFile); 
            start = true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Emulador.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Emulador.class.getName()).log(Level.SEVERE, null, ex);
        }
        btnIniciar.setEnabled(true);
        btnParar.setEnabled(true);
        btnStep.setEnabled(true);
        btnReset.setEnabled(true);
        }
        
    }//GEN-LAST:event_miAbrirActionPerformed

    private void btnIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnIniciarActionPerformed
        start = true;        
    }//GEN-LAST:event_btnIniciarActionPerformed

    private void btnPararActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPararActionPerformed
        start = !start;
    }//GEN-LAST:event_btnPararActionPerformed

    private void btnStepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStepActionPerformed
        oneStep = true;
        start = true;
    }//GEN-LAST:event_btnStepActionPerformed

    private void btnResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetActionPerformed
        //if(start){
            //screen.clear();
            clearScreen();
            cpu.resetChip8();
            lastSP = 0xfe;
            printRegistradores();
        
    }//GEN-LAST:event_btnResetActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        jdSobre.pack();
        jdSobre.setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged
        jLabel15.setText(Integer.toString(jSlider1.getValue()));
        
        bufferStrategy = screen.getBufferStrategy();
        graphics = bufferStrategy.getDrawGraphics();
         for(int x=0;x<W;x++){
            for(int y=0;y<H;y++){
                graphics.setColor(Color.WHITE);
                graphics.fillRect(x*scale, (y*scale), scale, scale);
            }
        }
        bufferStrategy.show();
        graphics.dispose();
        
        scale = jSlider1.getValue();
    }//GEN-LAST:event_jSlider1StateChanged

    private void jSlider2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider2StateChanged
        jLabel21.setText(Integer.toString(jSlider2.getValue()));
        cicle_per_second = jSlider2.getValue();
    }//GEN-LAST:event_jSlider2StateChanged
    @Override
    public void keyPressed(KeyEvent e) {
        teclado.KeyDown(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        teclado.KeyUp(e);
    }
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
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Emulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Emulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Emulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Emulador.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Emulador emu = new Emulador();
                emu.setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField V10;
    private javax.swing.JTextField V11;
    private javax.swing.JTextField V12;
    private javax.swing.JTextField V13;
    private javax.swing.JTextField V14;
    private javax.swing.JTextField V15;
    private javax.swing.JButton btnIniciar;
    private javax.swing.JButton btnParar;
    private javax.swing.JButton btnReset;
    private javax.swing.JButton btnStep;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JSlider jSlider2;
    private javax.swing.JDialog jdSobre;
    private javax.swing.JLabel lbFreq;
    private javax.swing.JLabel lbl10;
    private javax.swing.JMenuItem miAbrir;
    private java.awt.Canvas screen;
    private javax.swing.JTable tbStack;
    private javax.swing.JTextField tfI;
    private javax.swing.JTextField tfOpcode;
    private javax.swing.JTextField tfPC;
    private javax.swing.JTextField tfSP;
    private javax.swing.JTextField tfV0;
    private javax.swing.JTextField tfV1;
    private javax.swing.JLabel tfV11;
    private javax.swing.JLabel tfV12;
    private javax.swing.JLabel tfV13;
    private javax.swing.JLabel tfV14;
    private javax.swing.JLabel tfV15;
    private javax.swing.JTextField tfV2;
    private javax.swing.JTextField tfV3;
    private javax.swing.JTextField tfV4;
    private javax.swing.JTextField tfV5;
    private javax.swing.JTextField tfV6;
    private javax.swing.JTextField tfV7;
    private javax.swing.JTextField tfV8;
    private javax.swing.JTextField tfV9;
    // End of variables declaration//GEN-END:variables

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void clearScreen() {
        for(int h=0;h<H;h++){
            for(int w=0;w<W;w++){
                gfx[w][h]=0;
            }
        }
    }

    @Override
    public int getScreenPixel(int x, int y) {
        return gfx[x][y];        
    }

    @Override
    public void setScreenPixel(int x, int y) {
        gfx[x][y] ^= 1;
    }

    @Override
    public boolean getScreenDrawFlag() {
        return drawFlag;
    }

    @Override
    public void setScreenDrawFlag(boolean drawFlag) {
        this.drawFlag = drawFlag;
    }
}
